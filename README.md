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

        - If you prefer the second option, after 'a' step go to 'e' step to configure database properties and than execute the command
          java -jar decorawebservice-<VERSION>.jar with -m parameter at the prompt command and the application will uses the
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

        - Execute in the root installation directory: <java -jar decorawebservice-<VERSION>.jar start> to
          initialize the application;

        - For Basic Authentication a root user is provided: Set the Authorization KEY at http client with VALUE: <Basic cm9vdEBkZWNvcmEuY29tOnNlbmhh>;

        - Propritary KEY <X-Roles> provided for user roles with acceptable VALUES <administrador> and <search>

        - By default the services will be available at:

          * HTTP CRUD Services (POST, GET (Retrieve All)):  127.0.0.1:8080/service/user/
          * HTTP CRUD Services (GET, DELETE ):              127.0.0.1:8080/service/user/{id}

          * Search (QueryParams: name, email, address, phone, orderBy, startPage, pageSize):    127.0.0.1:8080/filter/user/findby?$PARAM=$VALUE

          * Login:      127.0.0.1:8080/auth/login/


