# Vireo 4
Vireo 4 is a turnkey Electronic Thesis and Dissertation (ETD) Management System.

## Compiling and creating the zip and war packages
```bash
$ mvn clean package
```
If all compile-time tests pass, you should have both a `vireo-4.0.x-SNAPSHOT.war` and a `Vireo-4.0.x-SNAPSHOT-install.zip` in the `target/` directory.

## Installing Zip Package to filesystem
Unzip package into preferred directory (or any directory you choose):
```bash
$ cd /opt/vireo
$ unzip ~/Vireo-4.0.x-SNAPSHOT-install.zip
```

### Directory Structure of installed package
```bash
/opt/vireo$ ls
drwxr-xr-x 2 root root 4096 Nov 11 11:54 attachments
drwxr-xr-x 2 root root 4096 Oct  2 15:36 conf
drwxr-xr-x 5 root root 4096 Nov 11 11:54 webapp
```
* attachments -- where pdf's will be uploaded to
* conf -- where the external config files reside
* webapp -- the extracted WAR file

## Installing WAR Package in Tomcat 7
Copy war file into Tomcat 7 webapps directory (your location may vary -- this is an example):
```bash
$ cd /var/lib/tomcat7/webapps
$ cp ~/vireo-4.0.x-SNAPSHOT.war vireo.war
```

## Running as a stand-alone Spring Boot application
```bash
java -jar target/vireo-4.0.x-SNAPSHOT.war
```

## Configuring
There is an external `application.properties` file under the `conf` directory that you can modify to override the values in the built-in `application.properties`.

**NOTE: The `conf` directory is only available if deployed from the ZIP package.**

**NOTE: If you need an external configuration file to the WAR file, you'll need to put a `conf` directory in the same directory as the WAR file (whether running inside tomcat or as stand-alone Spring Boot application).**

**You should override the database config and the spring secret key.**
