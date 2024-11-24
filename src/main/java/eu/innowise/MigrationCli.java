package eu.innowise;

import lombok.extern.slf4j.Slf4j;

/**
 * Command Line Interface (CLI) for interacting with the migration tool.
 * This class allows users to execute migration-related commands directly from the command line.
 */
@Slf4j
public class MigrationCli {

    public static void main(String[] args) {
        if (args.length == 0) {
            log.info("To use CLI write: java -jar migration-library.jar <command>");
            log.info("Available commands: migrate, rollback, status");
            return;
        }

        String command = args[0].toLowerCase();
        switch (command) {
            case "migrate":
                MigrationTool.migrate();
                break;
            case "rollback":
                if (args.length < 2) {
                    log.error("The 'rollback' command requires a version argument.");
                    System.out.println("Error: Please provide a version for rollback. Usage: rollback <version>");
                    return;
                }
                String version = args[1];
                MigrationTool.rollback(version);
                break;
            case "status":
                MigrationTool.showStatus();
                break;
            default:
                log.info("Unknown command: {}", command);
                log.info("Available commands: migrate, rollback, status");
        }
    }
}

