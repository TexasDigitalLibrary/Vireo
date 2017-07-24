-- Temporary function to help remove the old unique constraint (name) in embargo_type and email_template that Hibernate 4.2 won't remove even with jpa.ddl=update
-- It needs to be a function because EXECUTE cannot have a sub-query within it.
CREATE FUNCTION schema_upgrade_v2_to_v3() RETURNS VOID AS $$
DECLARE
   _constraint_name TEXT;
BEGIN

    FOR _constraint_name IN
         SELECT constraint_name
         FROM information_schema.key_column_usage
         WHERE table_name = 'embargo_type'
         AND column_name = 'name'
    LOOP
        EXECUTE 'ALTER TABLE embargo_type DROP CONSTRAINT ' || _constraint_name;
    END LOOP;

    FOR _constraint_name IN
         SELECT constraint_name
         FROM information_schema.key_column_usage
         WHERE table_name = 'email_template'
         AND column_name = 'name'
    LOOP
        EXECUTE 'ALTER TABLE email_template DROP CONSTRAINT ' || _constraint_name;
    END LOOP;

END;
$$
language plpgsql;
SELECT schema_upgrade_v2_to_v3();
DROP FUNCTION schema_upgrade_v2_to_v3();
