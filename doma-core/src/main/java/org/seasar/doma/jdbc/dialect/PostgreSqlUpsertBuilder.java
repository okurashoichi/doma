package org.seasar.doma.jdbc.dialect;

import java.util.List;
import java.util.Map;
import org.seasar.doma.internal.jdbc.sql.PreparedSqlBuilder;
import org.seasar.doma.jdbc.InParameter;
import org.seasar.doma.jdbc.entity.EntityPropertyType;
import org.seasar.doma.jdbc.entity.EntityType;
import org.seasar.doma.jdbc.query.DuplicateKeyType;
import org.seasar.doma.jdbc.query.UpsertBuilder;
import org.seasar.doma.jdbc.query.UpsertBuilderSupport;
import org.seasar.doma.jdbc.query.UpsertContext;

public class PostgreSqlUpsertBuilder implements UpsertBuilder {
  private final PreparedSqlBuilder buf;
  private final EntityType<?> entityType;
  private final DuplicateKeyType duplicateKeyType;
  private final List<EntityPropertyType<?, ?>> insertPropertyTypes;
  private final List<EntityPropertyType<?, ?>> updatePropertyTypes;
  private final List<EntityPropertyType<?, ?>> keys;
  private final UpsertBuilderSupport upsertBuilderSupport;
  private final Map<EntityPropertyType<?, ?>, InParameter<?>> values;

  public PostgreSqlUpsertBuilder(UpsertContext context) {
    this.buf = context.buf;
    this.entityType = context.entityType;
    this.duplicateKeyType = context.duplicateKeyType;
    this.keys = context.keys;
    this.insertPropertyTypes = context.insertPropertyTypes;
    this.updatePropertyTypes = context.updatePropertyTypes;
    this.values = context.propertyValuePairs;
    UpsertBuilderSupport.DefaultUpsertAliasManager aliasManager =
        new UpsertBuilderSupport.DefaultUpsertAliasManager();
    this.upsertBuilderSupport =
        new UpsertBuilderSupport(context.naming, context.dialect, aliasManager);
  }

  @Override
  public void build() {
    buf.appendSql("insert into ");
    tableNameAndAlias(entityType);
    buf.appendSql(" (");
    for (EntityPropertyType<?, ?> targetPropertyType : insertPropertyTypes) {
      column(targetPropertyType);
      buf.appendSql(", ");
    }
    buf.cutBackSql(2);
    buf.appendSql(") values (");
    for (EntityPropertyType<?, ?> targetPropertyType : insertPropertyTypes) {
      param(targetPropertyType, values);
      buf.appendSql(", ");
    }
    buf.cutBackSql(2);
    buf.appendSql(") on conflict (");
    for (EntityPropertyType<?, ?> key : keys) {
      column(key);
      buf.appendSql(", ");
    }
    buf.cutBackSql(2);
    buf.appendSql(")");
    if (duplicateKeyType == DuplicateKeyType.IGNORE) {
      buf.appendSql(" do nothing");
    } else if (duplicateKeyType == DuplicateKeyType.UPDATE) {
      buf.appendSql(" do update set ");
      for (EntityPropertyType<?, ?> p : updatePropertyTypes) {
        column(p);
        buf.appendSql(" = ");
        updateParam(p);
        buf.appendSql(", ");
      }
      buf.cutBackSql(2);
    }
  }

  private void tableNameAndAlias(EntityType<?> entityType) {
    String sql =
        this.upsertBuilderSupport.table(
            entityType, UpsertBuilderSupport.TableNameType.NAME_AS_ALIAS);
    buf.appendSql(sql);
  }

  private void column(EntityPropertyType<?, ?> propertyType) {
    String sql = this.upsertBuilderSupport.column(propertyType);
    buf.appendSql(sql);
  }

  private void param(
      EntityPropertyType<?, ?> propertyType, Map<EntityPropertyType<?, ?>, InParameter<?>> values) {
    InParameter<?> param = this.upsertBuilderSupport.param(propertyType, values);
    buf.appendParameter(param);
  }

  private void updateParam(EntityPropertyType<?, ?> propertyType) {
    String sql = this.upsertBuilderSupport.updateParam(propertyType);
    buf.appendSql(sql);
  }
}
