-- Temporary function to help remove the old unique constraint (name) in embargo_type and email_template that Hibernate 4.2 won't remove even with jpa.ddl=update
-- It needs to be a function because EXECUTE cannot have a sub-query within it.
CREATE FUNCTION cleanup_upgrade_v2_to_v3() RETURNS VOID AS $$
DECLARE
   _constraint_name TEXT;
BEGIN

    FOR _constraint_name IN
         SELECT constraint_name
         FROM information_schema.key_column_usage
         WHERE table_name = 'submission'
         AND column_name = 'embargotype_id'
    LOOP
        EXECUTE 'ALTER TABLE submission DROP CONSTRAINT ' || _constraint_name;
    END LOOP;

    ALTER TABLE submission DROP COLUMN embargotype_id;
END;
$$
language plpgsql;
SELECT cleanup_upgrade_v2_to_v3();
DROP FUNCTION cleanup_upgrade_v2_to_v3();
