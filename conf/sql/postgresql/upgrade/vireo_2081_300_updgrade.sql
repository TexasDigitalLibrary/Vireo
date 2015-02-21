-- Create a temporary function to help remove the old unique constraint in embargo_type that Hibernate 4.2 won't update with jpa.ddl=update
-- It will also upgrade the embargo_type schema (adds the 'guarantor' column)
CREATE OR REPLACE FUNCTION remove_unique_constraint_embargo_type() RETURNS TEXT AS $$
DECLARE
   _con TEXT;
BEGIN

_con := (SELECT constraint_name FROM information_schema.constraint_table_usage
      WHERE table_name = 'embargo_type'
      AND constraint_name LIKE 'uk%'
      LIMIT 1);

   EXECUTE '
      ALTER TABLE embargo_type DROP CONSTRAINT ' || _con;

   EXECUTE '
      ALTER TABLE embargo_type
      ADD guarantor integer DEFAULT 0 NOT NULL';

RETURN _con;
END;
$$
language plpgsql;
-- Create a temporary function to help add a new unique constraint in embargo_type that Hibernate 4.2 won't update with jpa.ddl=update
CREATE OR REPLACE FUNCTION add_unique_constraint_embargo_type(_con TEXT) RETURNS TEXT AS $$
BEGIN
    EXECUTE '
      ALTER TABLE ONLY embargo_type
      ADD CONSTRAINT ' || quote_ident(_con) || ' UNIQUE (name, guarantor)';
RETURN _con;
END;
$$
language plpgsql;
-- Use the original name of the uk_% key to create a new one
SELECT add_unique_constraint_embargo_type((SELECT remove_unique_constraint_embargo_type()));
-- Cleanup our temporary functions
DROP FUNCTION remove_unique_constraint_embargo_type();
DROP FUNCTION add_unique_constraint_embargo_type(_con TEXT);
