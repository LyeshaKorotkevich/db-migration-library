package eu.innowise.utils;

import eu.innowise.model.Migration;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(setterPrefix = "with")
public class MigrationTestData {

    @Builder.Default
    private String version = "1";

    @Builder.Default
    private String description = "Create_table";

    @Builder.Default
    private int checksum = 1;

    @Builder.Default
    private List<String> sqlStatements = new ArrayList<>();

    public Migration getMigration() {
        return new Migration(
               version,
               description,
               checksum,
               sqlStatements
        );
    }
}
