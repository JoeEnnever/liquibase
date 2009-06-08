package liquibase.change.core;

import liquibase.database.Database;
import liquibase.statement.AddForeignKeyConstraintStatement;
import liquibase.statement.SqlStatement;
import liquibase.change.AbstractChange;
import liquibase.change.ChangeMetaData;
import liquibase.change.Change;

import java.sql.DatabaseMetaData;

/**
 * Adds a foreign key constraint to an existing column.
 */
public class AddForeignKeyConstraintChange extends AbstractChange {
    private String baseTableSchemaName;
    private String baseTableName;
    private String baseColumnNames;

    private String referencedTableSchemaName;
    private String referencedTableName;
    private String referencedColumnNames;

    private String constraintName;

    private Boolean deferrable;
    private Boolean initiallyDeferred;

    private Integer updateRule;
    private Integer deleteRule;

    public AddForeignKeyConstraintChange() {
        super("addForeignKeyConstraint", "Add Foreign Key Constraint", ChangeMetaData.PRIORITY_DEFAULT);
    }

    public String getBaseTableSchemaName() {
        return baseTableSchemaName;
    }

    public void setBaseTableSchemaName(String baseTableSchemaName) {
        this.baseTableSchemaName = baseTableSchemaName;
    }

    public String getBaseTableName() {
        return baseTableName;
    }

    public void setBaseTableName(String baseTableName) {
        this.baseTableName = baseTableName;
    }

    public String getBaseColumnNames() {
        return baseColumnNames;
    }

    public void setBaseColumnNames(String baseColumnNames) {
        this.baseColumnNames = baseColumnNames;
    }

    public String getReferencedTableSchemaName() {
        return referencedTableSchemaName;
    }

    public void setReferencedTableSchemaName(String referencedTableSchemaName) {
        this.referencedTableSchemaName = referencedTableSchemaName;
    }

    public String getReferencedTableName() {
        return referencedTableName;
    }

    public void setReferencedTableName(String referencedTableName) {
        this.referencedTableName = referencedTableName;
    }

    public String getReferencedColumnNames() {
        return referencedColumnNames;
    }

    public void setReferencedColumnNames(String referencedColumnNames) {
        this.referencedColumnNames = referencedColumnNames;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    public Boolean getDeferrable() {
        return deferrable;
    }

    public void setDeferrable(Boolean deferrable) {
        this.deferrable = deferrable;
    }

    public Boolean getInitiallyDeferred() {
        return initiallyDeferred;
    }

    public void setInitiallyDeferred(Boolean initiallyDeferred) {
        this.initiallyDeferred = initiallyDeferred;
    }

//    public Boolean getDeleteCascade() {
//        return deleteCascade;
//    }

    public void setDeleteCascade(Boolean deleteCascade) {
        if (deleteCascade != null && deleteCascade) {
            setOnDelete("CASCADE");
        }
    }

    public void setUpdateRule(Integer rule) {
        this.updateRule = rule;
    }

    public Integer getUpdateRule() {
        return this.updateRule;
    }

    public void setDeleteRule(Integer rule) {
        this.deleteRule = rule;
    }

    public Integer getDeleteRule() {
        return this.deleteRule;
    }

    public void setOnDelete(String onDelete) {
        if (onDelete != null && onDelete.equalsIgnoreCase("CASCADE")) {
            setDeleteRule(DatabaseMetaData.importedKeyCascade);
        } else if (onDelete != null && onDelete.equalsIgnoreCase("SET NULL")) {
            setDeleteRule(DatabaseMetaData.importedKeySetNull);
        } else if (onDelete != null && onDelete.equalsIgnoreCase("SET DEFAULT")) {
            setDeleteRule(DatabaseMetaData.importedKeySetDefault);
        } else if (onDelete != null && onDelete.equalsIgnoreCase("RESTRICT")) {
            setDeleteRule(DatabaseMetaData.importedKeyRestrict);
        } else if (onDelete == null || onDelete.equalsIgnoreCase("NO ACTION")){
            setDeleteRule(DatabaseMetaData.importedKeyNoAction);
        } else {
            throw new RuntimeException("Unknown onDelete action: "+onDelete);
        }
    }

    public void setOnUpdate(String onUpdate) {
        if (onUpdate != null && onUpdate.equalsIgnoreCase("CASCADE")) {
            setUpdateRule(DatabaseMetaData.importedKeyCascade);
        } else  if (onUpdate != null && onUpdate.equalsIgnoreCase("SET NULL")) {
            setUpdateRule(DatabaseMetaData.importedKeySetNull);
        } else if (onUpdate != null && onUpdate.equalsIgnoreCase("SET DEFAULT")) {
            setUpdateRule(DatabaseMetaData.importedKeySetDefault);
        } else if (onUpdate != null && onUpdate.equalsIgnoreCase("RESTRICT")) {
            setUpdateRule(DatabaseMetaData.importedKeyRestrict);
        } else if (onUpdate == null || onUpdate.equalsIgnoreCase("NO ACTION")) {
            setUpdateRule(DatabaseMetaData.importedKeyNoAction);
        } else {
            throw new RuntimeException("Unknown onUpdate action: "+onUpdate);
        }
    }

    public SqlStatement[] generateStatements(Database database) {

        boolean deferrable = false;
        if (getDeferrable() != null) {
            deferrable = getDeferrable();
        }

        boolean initiallyDeferred = false;
        if (getInitiallyDeferred() != null) {
            initiallyDeferred = getInitiallyDeferred();
        }

        return new SqlStatement[]{
                new AddForeignKeyConstraintStatement(getConstraintName(),
                        getBaseTableSchemaName() == null ? database.getDefaultSchemaName() : getBaseTableSchemaName(),
                        getBaseTableName(),
                        getBaseColumnNames(),
                        getReferencedTableSchemaName() == null ? database.getDefaultSchemaName() : getReferencedTableSchemaName(),
                        getReferencedTableName(),
                        getReferencedColumnNames())
                        .setDeferrable(deferrable)
                        .setInitiallyDeferred(initiallyDeferred)
                        .setUpdateRule(updateRule)
                        .setDeleteRule(deleteRule)
        };
    }

    @Override
    protected Change[] createInverses() {
        DropForeignKeyConstraintChange inverse = new DropForeignKeyConstraintChange();
        inverse.setBaseTableSchemaName(getBaseTableSchemaName());
        inverse.setBaseTableName(getBaseTableName());
        inverse.setConstraintName(getConstraintName());

        return new Change[]{
                inverse
        };
    }

    public String getConfirmationMessage() {
        return "Foreign key contraint added to " + getBaseTableName() + " (" + getBaseColumnNames() + ")";
    }
}