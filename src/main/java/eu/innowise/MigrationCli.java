package eu.innowise;

import eu.innowise.migration.MigrationFileReader;
import eu.innowise.migration.MigrationManager;
import eu.innowise.model.AppliedMigration;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MigrationCli {

    public static void main(String[] args) {
        if (args.length == 0) {
            log.info("To use CLI write: java -jar your-library.jar <command>");
            log.info("Available commands: migrate, rollback, status");
            return;
        }

        String command = args[0].toLowerCase();
        switch (command) {
            case "migrate":
                MigrationTool.run();
                break;
            case "rollback":
                //performRollback();
                break;
            case "status":
                showStatus();
                break;
            default:
                log.info("Unknown command: {}", command);
                log.info("Available commands: migrate, rollback, status");
        }
    }

    private static void showStatus() {
        try {
            MigrationManager migrationManager = new MigrationManager(new MigrationFileReader());
            List<AppliedMigration> appliedMigrations = migrationManager.getAppliedMigrations();

            if (appliedMigrations.isEmpty()) {
                System.out.println("No migrations have been applied yet.");
                return;
            }

            System.out.println("\nDATABASE MIGRATION STATUS");
            System.out.println("==========================");
            System.out.printf("%-10s %-30s %-10s %-20s%n", "Version", "Description", "Checksum", "Installed On");
            System.out.println("--------------------------------------------------------------------------------");

            for (AppliedMigration migration : appliedMigrations) {
                System.out.printf("%-10s %-30s %-10d %-20s%n",
                        migration.getVersion(),
                        migration.getDescription(),
                        migration.getChecksum(),
                        migration.getInstalledOn());
            }

            String currentVersion = appliedMigrations.get(appliedMigrations.size() - 1).getVersion();
            System.out.println("\nCurrent version: " + currentVersion);

            log.info("Status command executed successfully.");
        } catch (Exception e) {
            log.error("Failed to retrieve migration status.", e);
            System.err.println("An error occurred while retrieving migration status. Check logs for details.");
        }
    }
}

