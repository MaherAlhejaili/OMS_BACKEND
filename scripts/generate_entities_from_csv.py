#!/usr/bin/env python3
"""Generate JPA entities from information_schema CSV export."""

import csv
import re
from collections import defaultdict
from pathlib import Path

CSV_PATH = Path(r"c:\Users\ASUS\Downloads\sql (13).csv")
ROOT = Path(r"c:\Users\ASUS\IdeaProjects\OMS_BACKEND\src\main\java\com\avnzor\oms_backend")

TABLE_CONFIG = {
    "sma_addresses": ("customers", "Address"),
    "sma_companies": ("customers", "Company"),
    "sma_employees": ("employees", "Employee"),
    "sma_products": ("products", "Product"),
    "sma_purchase_orders": ("purchases", "PurchaseOrder"),
    "sma_purchase_order_items": ("purchases", "PurchaseOrderItem"),
    "sma_purchase_order_shelving": ("purchases", "PurchaseOrderShelving"),
    "sma_purchase_order_shelving_items": ("purchases", "PurchaseOrderShelvingItem"),
    "sma_sales": ("sales", "Sale"),
    "sma_sale_items": ("sales", "SaleItem"),
    "sma_sales_jobs": ("sales", "SalesJob"),
    "sma_sale_status_history": ("sales", "SaleStatusHistory"),
    "sma_shopify_orders": ("sales", "ShopifyOrder"),
    "sma_shipment_creations": ("shipping", "ShipmentCreationLog"),
    "sma_supplier_orders": ("suppliers", "SupplierOrder"),
    "sma_supplier_inventory": ("suppliers", "SupplierInventory"),
    "sma_warehouse_locations": ("warehouse", "WarehouseLocation"),
    "sma_warehouse_users": ("auth", "WarehouseWorker"),
}


def snake_to_camel(name: str) -> str:
    parts = name.split("_")
    return parts[0] + "".join(p.capitalize() for p in parts[1:])


def parse_decimal(column_type: str):
    match = re.match(r"decimal\((\d+),(\d+)\)", column_type)
    if match:
        return int(match.group(1)), int(match.group(2))
    return None


def parse_varchar_length(column_type: str):
    match = re.match(r"varchar\((\d+)\)", column_type)
    if match:
        return int(match.group(1))
    return None


def normalize_column_type(column_type: str) -> str:
    column_type = column_type.lower().strip()
    if column_type.startswith("int") and "unsigned" in column_type:
        return "int unsigned"
    if column_type.startswith("bigint") and "unsigned" in column_type:
        return "bigint unsigned"
    if column_type.startswith("tinyint(1)"):
        return "tinyint(1)"
    if column_type.startswith("tinyint"):
        return "tinyint"
    if column_type.startswith("smallint"):
        return "smallint"
    if column_type.startswith("int(") or column_type == "int":
        return "int"
    if column_type.startswith("bigint(") or column_type == "bigint":
        return "bigint"
    if column_type.startswith("decimal"):
        return column_type.split()[0] if " " in column_type else column_type
    return column_type


def map_column(column_name: str, column_type: str, extra: str):
    extra = extra or ""
    column_type = normalize_column_type(column_type)
    annotations = []
    java_type = "String"
    imports = set()

    if column_type.startswith("decimal"):
        spec = parse_decimal(column_type)
        java_type = "BigDecimal"
        imports.add("java.math.BigDecimal")
        if spec:
            annotations.append(f'@Column(name = "{column_name}", precision = {spec[0]}, scale = {spec[1]})')
        else:
            annotations.append(f'@Column(name = "{column_name}")')
    elif column_type == "float":
        java_type = "Float"
        annotations.append(f'@Column(name = "{column_name}")')
    elif column_type == "double":
        java_type = "Double"
        annotations.append(f'@Column(name = "{column_name}")')
    elif column_type == "smallint":
        java_type = "Integer"
        imports.add("com.avnzor.oms_backend.common.persistence.LegacySmallInt")
        annotations.append("@LegacySmallInt")
        annotations.append(f'@Column(name = "{column_name}")')
    elif column_type in ("int", "int unsigned"):
    elif column_type in ("bigint", "bigint unsigned"):
        java_type = "Long"
        annotations.append(f'@Column(name = "{column_name}")')
    elif column_type == "tinyint(1)":
        java_type = "Boolean"
        imports.add("com.avnzor.oms_backend.common.persistence.LegacyBitFlag")
        annotations.append("@LegacyBitFlag")
        annotations.append(f'@Column(name = "{column_name}")')
    elif column_type.startswith("tinyint"):
        java_type = "Integer"
        imports.add("com.avnzor.oms_backend.common.persistence.LegacyTinyInt")
        annotations.append("@LegacyTinyInt")
        annotations.append(f'@Column(name = "{column_name}")')
    elif column_type == "date":
        java_type = "LocalDate"
        imports.add("java.time.LocalDate")
        annotations.append(f'@Column(name = "{column_name}")')
    elif column_type.startswith("datetime") or column_type.startswith("timestamp"):
        java_type = "LocalDateTime"
        imports.add("java.time.LocalDateTime")
        annotations.append(f'@Column(name = "{column_name}")')
    elif column_type == "mediumtext":
        java_type = "String"
        annotations.append(f'@Column(name = "{column_name}", columnDefinition = "MEDIUMTEXT")')
    elif column_type == "longtext":
        java_type = "String"
        imports.add("org.hibernate.annotations.JdbcTypeCode")
        imports.add("org.hibernate.type.SqlTypes")
        annotations.append(f'@Column(name = "{column_name}")')
        annotations.append("@JdbcTypeCode(SqlTypes.LONGVARCHAR)")
    elif column_type == "text":
        java_type = "String"
        annotations.append(f'@Column(name = "{column_name}", columnDefinition = "TEXT")')
    elif column_type.startswith("varchar"):
        length = parse_varchar_length(column_type)
        java_type = "String"
        if length:
            annotations.append(f'@Column(name = "{column_name}", length = {length})')
        else:
            annotations.append(f'@Column(name = "{column_name}")')
    else:
        java_type = "String"
        annotations.append(f'@Column(name = "{column_name}")')

    if "stored generated" in extra.lower():
        last = annotations[-1]
        if last.startswith("@Column(") and last.endswith(")"):
            inner = last[len("@Column("):-1]
            annotations[-1] = f"@Column({inner}, insertable = false, updatable = false)"

    field_name = snake_to_camel(column_name)
    if column_name == "is_default":
        field_name = "defaultAddress"
    elif column_name == "is_active":
        field_name = "active"
    elif column_name == "is_reserved":
        field_name = "reserved"
    elif column_name == "is_external":
        field_name = "external"
    elif column_name == "is_transfer":
        field_name = "transfer"
    elif column_name == "details" and column_name != "details_ar":
        pass  # keep details for product highlights handled below

    return field_name, java_type, annotations, imports


def generate_entity(table_name: str, rows: list[dict]) -> str:
    module, class_name = TABLE_CONFIG[table_name]
    package = f"com.avnzor.oms_backend.{module}.entity"

    fields = []
    all_imports = {
        "jakarta.persistence.Column",
        "jakarta.persistence.Entity",
        "jakarta.persistence.GeneratedValue",
        "jakarta.persistence.GenerationType",
        "jakarta.persistence.Id",
        "jakarta.persistence.Table",
        "lombok.Getter",
        "lombok.Setter",
    }

    for row in rows:
        column_name = row["COLUMN_NAME"]
        column_type = row["COLUMN_TYPE"]
        extra = row.get("EXTRA", "")

        field_name, java_type, annotations, imports = map_column(column_name, column_type, extra)
        all_imports.update(imports)

        if column_name == "id":
            fields.append("")
            fields.append("    @Id")
            fields.append("    @GeneratedValue(strategy = GenerationType.IDENTITY)")
            fields.append(f"    private {java_type} id;")
            continue

        fields.append("")
        for ann in annotations:
            fields.append(f"    {ann}")
        fields.append(f"    private {java_type} {field_name};")

    import_lines = sorted(all_imports)
    body = "\n".join(fields)

    return f"""package {package};

{chr(10).join(f"import {i};" for i in import_lines)}

@Entity
@Table(name = "{table_name}")
@Getter
@Setter
public class {class_name} {{
{body}
}}
"""


def main():
    tables = defaultdict(list)
    with CSV_PATH.open(newline="", encoding="utf-8-sig") as f:
        reader = csv.DictReader(f)
        for row in reader:
            tables[row["TABLE_NAME"]].append(row)

    for table_name, rows in tables.items():
        if table_name not in TABLE_CONFIG:
            continue
        module, class_name = TABLE_CONFIG[table_name]
        out_dir = ROOT / module / "entity"
        out_dir.mkdir(parents=True, exist_ok=True)
        content = generate_entity(table_name, rows)
        out_file = out_dir / f"{class_name}.java"
        out_file.write_text(content, encoding="utf-8")
        print(f"Wrote {out_file}")


if __name__ == "__main__":
    main()
