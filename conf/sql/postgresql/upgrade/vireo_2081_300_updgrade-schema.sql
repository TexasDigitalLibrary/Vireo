-- DO function to help remove the old unique constraint (name) in embargo_type that Hibernate 4.2 won't remove even with jpa.ddl=update
-- It needs to be a function because EXECUTE cannot have a sub-query within it.
DO $$
DECLARE
   _constraint_name TEXT;
BEGIN

   _constraint_name := (SELECT constraint_name
                        FROM information_schema.key_column_usage
                        WHERE table_name = 'embargo_type'
                        AND column_name = 'name'
                        LIMIT 1); -- there should only be 1

   EXECUTE 'ALTER TABLE embargo_type DROP CONSTRAINT ' || _constraint_name;

END;
$$
language plpgsql;
