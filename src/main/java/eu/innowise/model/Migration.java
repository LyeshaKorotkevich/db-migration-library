package eu.innowise.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * Represents a migration, that should be applied, with SQL statements to be executed.
 * This class extends from {@link BaseMigration} and adds the SQL statements
 */
@Getter
@ToString(exclude = "sqlStatements")
@EqualsAndHashCode(callSuper = true)
public class Migration extends BaseMigration{

    @JsonIgnore
    private final List<String> sqlStatements;

    public Migration(String version, String description, int checksum, List<String> sqlStatements) {
        super(version, description, checksum);
        this.sqlStatements = sqlStatements;
    }
}
