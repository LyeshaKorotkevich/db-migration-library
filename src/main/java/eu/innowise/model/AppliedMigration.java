package eu.innowise.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AppliedMigration {

    private String version;
    private String description;
    private int checksum;
    private LocalDateTime installedOn;
}
