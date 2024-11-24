package eu.innowise.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

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
