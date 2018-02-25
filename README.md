    1. REPOSITORY

        - https://github.com/marcusviniciusfs/decora.git;

    2. BUILD

        - Execute mvn install to build it;
        - The distribution zip will be provided in target/decorawebservice-<VERSION>-bin.zip directory;

    3. INSTALLATION REQUIREMENTS

        - Linux or Windows;
        - Java 1.8 or superior (Oracle ou OpenJDK);
        - Postgre Database;

    4. INSTALLATION

        - There are two options to create the database: manually following the steps 'a', 'b', 'c', 'd' and 'e' or automatically;

        - If you prefer the second option, go to 'e' step configuring the database properties and than execute the command
          java -jar decorawebservice-<VERSION>.jar with the parameter -m at the prompt command and the application will uses the
          liquibase plugin to create a database with default configuration. Ex: <java -jar decorawebservice-<VERSION>.jar -m>

        a. Unzip the distribution zip into a directory the systemUser's choice;

        b. Create a database at chosen BD;

        c. Execute the creation script of the BD schema available at directory <sql/create.sql>;

        d. Execute the specific script available at directory <sql/postgre.sql>;

        e. Configure the following options: Connection URL, Driver JDBC, User and Password. Sample:

            - hibernate.connection.url=jdbc:postgresql://127.0.0.1/decora
            - hibernate.connection.username=postgres
            - hibernate.connection.password=postgres
            - hibernate.connection.driver_class=org.postgresql.Driver

    5. INITIALIZATION

        - Execute in the root installation directory: <java -jar decorawebservice-<VERSION>.jar start>