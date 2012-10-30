
How To Generate Schemas
=======================

Play is based upon the Java Persistence API (JPA) for short. This is a standard
Java-based method for defining database schemas, and interacting with them.
This means that the schemas is not defined by the SQL found in these files 
instead it's is spread across the series of JPA-based java classes. Each of the
JPA-based model classes contains a set of annotations (the @ things) to specify
how data should be represented in the database. These instructions walk through
generating schema definitions for the various database implementations 
supported.

Repeat the following steps for each database implementation:

1. Make sure the database module is installed.

    play deps --sync --%test
    
2. Update the application.conf

  Modify the application.conf for the following fields to point to a live
  implementation of the database.
  
    db = postgres://localhost/vireo
    db.user = user
    db.pass = password
    jpa.ddl = create
    
3. Export the database schema

  Run the following command to generate a clean definition of the database 
  schema and save the output.
  
    play db:export --create
