# Autopsy Backend System
Server side of the Autopsy application utilising the Sleuth Kit (TSK) library.

## Prerequisites
- **Open JDK 22**: Download and install the required JDK version from [OpenJDK 22](https://jdk.java.net/22/).
- **PostgreSQL**: Download and install PostgreSQL from [PostgreSQL Official Website](https://www.postgresql.org/download/).

# Setup Instructions

To run the backend system on your local machine, follow these steps:

1. **Clone the Repository**  

3. **Install Required JAR Files**  
   Two `.jar` files are provided in the project in the `setup` folder. These must be installed into the project directory using the following commands:

   - **sleuthkit-4.12.1.jar**

     **Windows**:
     ```bash
     mvn install:install-file ^
     -Dfile="{PROJECT_DIR_PATH}/setup/sleuthkit-4.12.1.jar" ^
     -DgroupId="org.sleuthkit" ^
     -DartifactId=sleuthkit ^
     -Dversion="4.12.1" ^
     -Dpackaging=jar ^
     -DgeneratePom=true
     ```

     **MacOS/Linux**:
     ```bash
     mvn install:install-file \
     -Dfile={PROJECT_DIR_PATH}/setup/sleuthkit-4.12.1.jar \
     -DgroupId=org.sleuthkit \
     -DartifactId=sleuthkit \
     -Dversion=4.12.1 \
     -Dpackaging=jar \
     -DgeneratePom=true
     ```

   - **org-sleuthkit-autopsy-core.jar**

     **Windows**:
     ```bash
     mvn install:install-file ^
     -Dfile="{PROJECT_DIR_PATH}/setup/org-sleuthkit-autopsy-core.jar" ^
     -DgroupId="org.sleuthkit" ^
     -DartifactId=autopsy-core ^
     -Dversion="4.21.0" ^
     -Dpackaging=jar ^
     -DgeneratePom=true
     ```

     **MacOS/Linux**:
     ```bash
     mvn install:install-file \
     -Dfile={PROJECT_DIR_PATH}/setup/org-sleuthkit-autopsy-core.jar \
     -DgroupId=org.sleuthkit \
     -DartifactId=autopsy-core \
     -Dversion=4.21.0 \
     -Dpackaging=jar \
     -DgeneratePom=true
     ```

4. **Install Dependencies**  
   Run the following command to make sure everything is installed:
   ```bash
   mvn install -Dmaven.test.skip=true

5. **Install Lombok Plugin**

6. **Set VM Options**
   Add the following VM options to the run configuration:
   ```
   --add-opens
   java.base/java.lang.ref=ALL-UNNAMED
   -Dnetbeans.user={PROJECT_DIR_PATH}/netbeans_userdir
  
7. **Configure PostgreSQL**
   Ensure PostgreSQL is installed, and update the connection details in the `application.properties` file:
   ```
   spring.datasource.url=jdbc:postgresql:{db_connection_string}
   spring.datasource.username=your_username
   spring.datasource.password=your_password

8. **Configure Case Directory**
   In the `application.properties` file, update the path for storing cases:
   ```
   case.baseDir={Default_dir_path_to_store_the_cases}
9. **Run the Project**
   Once the above steps are complete, run the project.
