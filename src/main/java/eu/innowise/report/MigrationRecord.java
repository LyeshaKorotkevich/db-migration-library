package eu.innowise.report;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MigrationRecord {

    private int installedRank;
    private String version;
    private String description;
    private int checksum;
    private boolean success;
    private LocalDateTime installedOn;
}