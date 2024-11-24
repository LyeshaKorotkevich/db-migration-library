package eu.innowise.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Migration {

    private String version;
    private String description;
    private int checksum;

    private List<String> sqlStatements;
}
