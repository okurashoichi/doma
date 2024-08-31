package org.seasar.doma.jdbc.criteria.statement;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.seasar.doma.jdbc.BatchResult;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.Sql;
import org.seasar.doma.jdbc.SqlKind;
import org.seasar.doma.jdbc.command.Command;
import org.seasar.doma.jdbc.criteria.context.InsertSettings;
import org.seasar.doma.jdbc.criteria.metamodel.EntityMetamodel;
import org.seasar.doma.jdbc.query.DuplicateKeyType;

public class EntityqlBatchInsertStatement<ENTITY>
    extends AbstractStatement<EntityqlBatchInsertStatement<ENTITY>, BatchResult<ENTITY>> {

  static final EmptySql EMPTY_SQL = new EmptySql(SqlKind.BATCH_INSERT);
  private final EntityMetamodel<ENTITY> entityMetamodel;
  private final List<ENTITY> entities;
  private final InsertSettings settings;
  private DuplicateKeyType duplicateKeyType = DuplicateKeyType.EXCEPTION;

  public EntityqlBatchInsertStatement(
      Config config,
      EntityMetamodel<ENTITY> entityMetamodel,
      List<ENTITY> entities,
      InsertSettings settings) {
    super(Objects.requireNonNull(config));
    this.entityMetamodel = Objects.requireNonNull(entityMetamodel);
    this.entities = Objects.requireNonNull(entities);
    this.settings = Objects.requireNonNull(settings);
  }

  public EntityqlBatchInsertIntermediate<ENTITY> onDuplicateKeyUpdate() {
    this.duplicateKeyType = DuplicateKeyType.UPDATE;
    return new EntityqlBatchInsertIntermediate<>(
        config, entityMetamodel, entities, settings, duplicateKeyType);
  }

  public EntityqlBatchInsertIntermediate<ENTITY> onDuplicateKeyIgnore() {
    this.duplicateKeyType = DuplicateKeyType.IGNORE;
    return new EntityqlBatchInsertIntermediate<>(
        config, entityMetamodel, entities, settings, duplicateKeyType);
  }

  /**
   * {@inheritDoc}
   *
   * @throws org.seasar.doma.jdbc.UniqueConstraintException if an unique constraint is violated
   * @throws org.seasar.doma.jdbc.JdbcException if a JDBC related error occurs
   */
  @SuppressWarnings("EmptyMethod")
  @Override
  public BatchResult<ENTITY> execute() {
    return super.execute();
  }

  @Override
  protected Command<BatchResult<ENTITY>> createCommand() {
    EntityqlBatchInsertTerminate<ENTITY> terminate =
        new EntityqlBatchInsertTerminate<>(
            config, entityMetamodel, entities, settings, duplicateKeyType, Collections.emptyList());
    return terminate.createCommand();
  }

  @Override
  public Sql<?> asSql() {
    if (entities.isEmpty()) {
      return EMPTY_SQL;
    }
    return super.asSql();
  }
}
