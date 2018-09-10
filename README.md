[![Waffle.io - Columns and their card count](https://badge.waffle.io/TexasDigitalLibrary/Vireo.png?columns=all)](https://waffle.io/TexasDigitalLibrary/Vireo?utm_source=badge) [![Build Status](https://travis-ci.org/TexasDigitalLibrary/Vireo.svg?branch=master)](https://travis-ci.org/TexasDigitalLibrary/Vireo)

# Vireo 4

Vireo is a turnkey Electronic Thesis and Dissertation (ETD) Management System.  Starting with the 4.x release, Vireo offers fully customizable workflows and controlled vocabularies.  

The software is presently in a Beta release.  If you would like to help with testing or development, GitHub issues and pull requests are encouraged!

## Building

Vireo build is done with [Maven](https://maven.apache.org/). The build is configured with [pom.xml](https://github.com/TexasDigitalLibrary/Vireo/blob/master/pom.xml) and [package.json](https://github.com/TexasDigitalLibrary/Vireo/blob/master/package.json). There are several command line arguments that can be used when packaging Vireo 4. 

* ```-Dproduction``` will package production ready. **Required for Tomcat deployment or running as a jar**
* ```-DskipTests``` will skip tests.
* ```-Dassets.uri=file:/opt/vireo/``` will configure where to store assets.
* ```-Dconfig.uri=file:/var/vireo/config/``` will configure the external configuration directory for the WAR packaged application.
* ```-Dspring.config.location=file:/var/vireo/config/``` will configure the external configuration directory for development using `spring-boot:run`.

**Ending trailing slash is required for spring.config.location**

The external configuration directory is where an application.properties file can be added to override default properties. When packaging the application define `config.uri`, which will template context.xml file with the `spring.config.location` system variable for container deployment. 

When running for development define `spring.config.location` to externalize the configuration. If running for development and using an external configuration, do not define `assets.uri`. It will have to be configured manually in the external application.properties.

**External configuration is recommended for production deployment**

### Development

```bash
$ mvn clean package
```

or run for development

```bash
$ mvn clean spring-boot:run
```

or run for development with external configuration

```bash
$ mvn clean spring-boot:run -Dspring.config.location=file:/var/vireo/config/
```

or run for development with external assets

```bash
$ mvn clean spring-boot:run -Dassets.uri=file:/var/vireo/
```

or run as production

```bash
$ mvn clean spring-boot:run -Dproduction
```

**Building or running with -Dproduction will change the index.html template at src/main/resources/templates/index.html**

### Production

```bash
$ mvn clean package -DskipTests -Dproduction -Dassets.uri=file:/opt/vireo/ -Dconfig.uri=file:/opt/vireo/config/
```

If build succeeds, you should have both a `vireo-4.0.0-SNAPSHOT.war` and a `vireo-4.0.0-SNAPSHOT-install.zip` in the `target/` directory. When building for production required static assets are copied into the packaged war file and the index.html template is optimized for production. For development a symlink is used to allow the application to access required static assets.

#### Apache Reverse Proxy Config

```
LoadModule rewrite_module modules/mod_rewrite.so
LoadModule proxy_module modules/mod_proxy.so
LoadModule proxy_http_module modules/mod_proxy_http.so
LoadModule proxy_wstunnel_module modules/mod_proxy_wstunnel.so

<VirtualHost *:80>
  ServerName localhost

  ProxyPreserveHost On
  ProxyPass / http://127.0.0.1:8080/
  ProxyPassReverse / http://127.0.0.1:8080/
  ProxyRequests Off
  RewriteEngine on
  RewriteCond %{HTTP:UPGRADE} ^WebSocket$ [NC]
  RewriteCond %{HTTP:CONNECTION} Upgrade$ [NC]
  RewriteRule .* ws://localhost:8080%{REQUEST_URI} [P]

</VirtualHost>
```

## Testing

### Server

```bash
$ mvn clean test
```

### Client

```bash
$ npm run test
```

### Server and Client

```bash
$ mvn clean test -Dclient
```

### e2e

```bash
$ mvn clean spring-boot:run
$ npm run protractor
```

## Deploying from Zip Package

Unzip package into preferred directory (or any directory you choose):

```bash
$ cd /opt/vireo
$ unzip vireo-4.0.0-SNAPSHOT-install.zip
```

### Directory Structure of installed package

```bash
drwxrwxrwx  6 root  root  204 Sep  3 11:20 config
drwxr-xr-x  2 root  root   68 Sep  3 11:19 logs
drwxrwxrwx  7 root  root  238 Sep  3 11:20 webapp
```

* config - where the external config files reside
* logs - where vireo log files are stored
* webapp - the extracted WAR file


### Configure application

Currently, in order to have Tomcat know where the external configuration directory is, `webapp/META-INF/context.xml` will have to be updated. Skip step 1 if package built defining `config.uri`.

1) Update [context.xml](https://github.com/TexasDigitalLibrary/Vireo/blob/master/src/main/WEB-INF/context.xml) to set external configuration directory

```bash
<?xml version="1.0" encoding="UTF-8"?>
<Context>
  <Parameter name="spring.config.location" value="file:/opt/vireo/config" />
</Context>
```

2) Update [application.properties](https://github.com/TexasDigitalLibrary/Vireo/blob/master/src/main/resources/application.properties)


### Deploy to Tomcat

Easiest way to deploy from extracted zip file is to create a symlink in Tomcat webapps directory to the Vireo webapp directory.

```
ln -s /opt/vireo/webapp /opt/tomcat/webapps/vireo
```

or as root

```
ln -s /opt/vireo/webapp /opt/tomcat/webapps/ROOT
```

## Deploying WAR Package in Tomcat

Copy war file into Tomcat webapps directory (your location may vary -- this is an example):

```bash
$ cp ~/vireo-4.0.0-SNAPSHOT.war /usr/local/tomcat/webapps/vireo.war
```

or as root:

```bash
$ cp ~/vireo-4.0.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
```

**if not specifying assets.uri during build the assets will be stored under the vireo webapp's classpath, /opt/tomcat/webapps/vireo/WEB-INF/classes**

**if not specifying config.uri during build the application.properties will be under the Vireo webapp's classpath, /opt/tomcat/webapps/vireo/WEB-INF/classes/application.properties**

**if deplyoed from default WAR package and would like to externalize the config, you will have to edit /opt/tomcat/webapps/vireo/META-INF/context.xml***


## Running WAR as a stand-alone Spring Boot application

```bash
java -jar target/vireo-4.0.0-SNAPSHOT.war
```

## Configuring

* [application.properties](https://github.com/TexasDigitalLibrary/Vireo/blob/master/src/main/resources/application.properties)
  * application configurations

* [theme-defaults.json](https://github.com/TexasDigitalLibrary/Vireo/blob/master/src/main/resources/theme-defaults.json)
  * default theme settings

* [favicon.ico](https://github.com/TexasDigitalLibrary/Vireo/blob/master/src/main/resources/favicon.ico)
  * browser favicon

**You should configure the following properties for production.**

| **Property** | **Type** | **Description** | **Example** |
| :----------- | :------- | :-------------- | :---------- |
| spring.datasource.platform | string | database platform, support h2, mysql, postgresql | postgresql |
| spring.datasource.url | string | URL to database | jdbc:postgresql://localhost:5432/vireo |
| spring.datasource.driverClassName | string | database driver class | org.postgresql.Driver |
| spring.jpa.database-platform | string | database platform dialect | org.hibernate.dialect.PostgreSQLDialect |
| spring.datasource.username | string | database username | vireo |
| spring.datasource.password | string | database password | vireo |
| spring.jpa.hibernate.ddl-auto | string | database schema initialization | update |
| app.url | string | URL to Vireo 4 | https://vireo.tdl.org |
| app.authority.admins | string | list of email addresses for initial administrators | admin@tdl.org |
| auth.security.jwt.secret | string | used to encrypt JWT | verysecretsecret |
| auth.security.jwt.issuer | string | issuer of JWT | vireo.tdl.org |
| auth.security.jwt.duration | string | JWT duration in minutes | 15 |
| app.security.secret | string |  used to encrypt confidential database entries and registration tokens | verysecretsecret |
| app.security.allow-access | string | CORS origins allowed | https://vireo.tdl.org |
| app.email.host | string | smtp relay host | relay.tdl.org |
| app.email.from | string | email address from | noreply@tdl.org |
| app.email.replyTo | string | email address replyTo | admin@tdl.org |
| app.reporting.address | string | email address to report issues | issues@tdl.org |
