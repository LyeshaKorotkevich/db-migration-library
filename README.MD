# **Database Migration Library**

This library helps manage database migrations, including applying, rolling back, and checking migration statuses.

## **Features**

- Reading and sorting migration files.
- Applying migrations.
- Rolling back migrations to a specific version.
- Retrieving the current database version.
- Logging all migration processes.
- Generating reports on successful/failed migrations in JSON format.
- Running the library as a standalone command-line tool (CLI).
- Supports migrations for popular databases:
    - PostgreSQL
    - MySQL
    - H2

## **Requirements**

- Java 17 or higher.
- PostgreSQL, MySQL, or H2 database.
- Gradle for building and managing dependencies.

## **Setup Instructions**

### 1. **Clone the Repository**

Clone the repository to your local machine:

```bash
git clone https://github.com/LyeshaKorotkevich/db-migration-library.git
```
### 2. **Library Configuration**
Configuration is done through the application.properties file. In this file, you need to specify the database connection parameters and the location of migration files.

Example application.properties configuration:
```bash
db.driver-class-name=org.postgresql.Driver  # Change to the appropriate driver for your database
db.username=username
db.password=password
db.url=jdbc:postgresql://localhost:5438/database  # Replace with your database URL

migration.folder=path/to/your/migration/files
#Report settings (optional)
report.output.path=path/to/reports  # By default, reports are saved in the "reports" folder  
```
You can also use docker-compose to quickly set up your database if needed.

### 3. **Running the CLI Utility**
Before using the CLI utility, ensure that your application.properties file is configured. Then, build the project using Gradle:

```bash
gradle fatJar
```
After building, navigate to the build/libs folder and run the following command:

```bash
java -jar migration-library-1.0-SNAPSHOT.jar [command]
```
CLI commands:
```bash
migrate — Apply migrations.
rollback [version] — Rollback migrations to a specified version.
status — Check the current migration status.
```
### 4. **Adding the Library as a Dependency**
To use the library in another project, you need to publish it to your local Maven repository. Run the following command:
```bash
gradle publishToMavenLocal
```
After that, you can add the dependency in your project:
```groovy
implementation 'eu.innowise:migration-library:1.0-SNAPSHOT'
```
Make sure to configure the application.properties file for your project as well.

Example usage:
```java
package com.senla;

import eu.innowise.MigrationTool;

public class Main {
    public static void main(String[] args) {
        MigrationTool.migrate();
        MigrationTool.showStatus();
    }
}
```
# Notes
Migration File Naming:  
Migration files should follow a specific naming convention to ensure they are applied in the correct order.  
The versioning format for migration filenames should follow the pattern:   
V1__description.sql, V1.1__description.sql, V1_1__description.sql.  
For rollbacks, use the format U1__description.sql (e.g., U1__rollback_description.sql).

Supported Databases:  
This library supports PostgreSQL, MySQL, and H2. The correct driver and database connection details must be configured in the application.properties file.