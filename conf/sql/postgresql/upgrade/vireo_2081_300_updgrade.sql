-- DO function to help remove the old unique constraint (name) in embargo_type that Hibernate 4.2 won't update even with jpa.ddl=update
-- It will also upgrade the embargo_type schema (adds the 'guarantor' column)
-- It will lastly add the new UNIQUE CONSTRAINT against both (name, guarantor)
DO $$
DECLARE
   _constraint_name TEXT;
BEGIN

   _constraint_name := (SELECT constraint_name
                        FROM information_schema.key_column_usage
                        WHERE table_name = 'embargo_type'
                        AND constraint_name LIKE 'uk%'
                        AND column_name = 'name'
                        LIMIT 1); -- there should only be 1

   EXECUTE '
      ALTER TABLE embargo_type DROP CONSTRAINT ' || _constraint_name;

   EXECUTE '
      ALTER TABLE embargo_type
      ADD guarantor integer DEFAULT 0 NOT NULL';

   EXECUTE '
      ALTER TABLE ONLY embargo_type
      ADD CONSTRAINT ' || _constraint_name || ' UNIQUE (name, guarantor)';

END;
$$
language plpgsql;
