package com.avnzor.oms_backend.common.persistence;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Maps MySQL {@code TINYINT} columns from legacy schemas. */
@Target(FIELD)
@Retention(RUNTIME)
@JdbcTypeCode(SqlTypes.TINYINT)
public @interface LegacyTinyInt {
}
