-- DO function to help remove the old unique constraint (name) in embargo_type that Hibernate 4.2 won't remove even with jpa.ddl=update
-- It needs to be a function because EXECUTE cannot have a sub-query within it.

USE `vireo`;
DROP procedure IF EXISTS `remove_unique_key_constraint`;
DELIMITER $$
USE `vireo`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `remove_unique_key_constraint`(IN `dbName` varchar(255), IN `tableName` varchar(255), IN `columnName` varchar(255))
BEGIN
        DECLARE done BOOLEAN DEFAULT FALSE;
        DECLARE statement TEXT DEFAULT '';
        DECLARE cur1 CURSOR FOR SELECT CONCAT('ALTER TABLE \`', tableName, '\` DROP INDEX ', t.constraint_name, ';')
                from information_schema.table_constraints t
                left join information_schema.key_column_usage k ON k.table_name = t.table_name AND k.constraint_name = t.constraint_name
                where t.constraint_schema = `dbName` AND t.table_name = `tableName` AND k.column_name = `columnName`;
        DECLARE CONTINUE HANDLER FOR NOT FOUND SET done := TRUE;
		
        OPEN cur1;

        read_loop: LOOP
                FETCH cur1 INTO statement;
                IF done THEN
                        LEAVE read_loop;
                END IF;
		SELECT statement INTO @stmt;
                PREPARE stmt1 FROM @stmt;
		EXECUTE stmt1;
		DEALLOCATE PREPARE stmt1;
        END LOOP read_loop;

        CLOSE cur1;

END$$
DELIMITER ;
CALL remove_unique_key_constraint('vireo', 'embargo_type', 'name');
CALL remove_unique_key_constraint('vireo', 'email_template', 'name');
DROP procedure IF EXISTS `remove_unique_key_constraint`;
