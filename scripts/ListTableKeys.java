import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ListTableKeys {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://127.0.0.1:3306/avnzor?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String[] tables = {"sma_shopify_orders", "sma_supplier_inventory"};
        try (Connection c = DriverManager.getConnection(url, "root", "");
             Statement s = c.createStatement()) {
            for (String table : tables) {
                System.out.println("=== " + table + " ===");
                try (ResultSet rs = s.executeQuery(
                        "SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_KEY, EXTRA "
                                + "FROM information_schema.COLUMNS "
                                + "WHERE TABLE_SCHEMA='avnzor' AND TABLE_NAME='" + table + "' "
                                + "ORDER BY ORDINAL_POSITION")) {
                    while (rs.next()) {
                        System.out.printf("%s | %s | key=%s | extra=%s%n",
                                rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
                    }
                }
            }
        }
    }
}
