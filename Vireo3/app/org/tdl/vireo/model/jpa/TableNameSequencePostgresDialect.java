package org.tdl.vireo.model.jpa;

import java.util.Properties;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.type.Type;

/**
 * This class is in the public domain. It was posted the user burtbeckwith on
 * the grails forum:
 * http://grails.1312388.n4.nabble.com/One-hibernate-sequence-is
 * -used-for-all-Postgres-tables-td1351722.html
 * 
 * Creates a sequence per table instead of the default behavior of one sequence.
 * 
 * From <a
 * href="http://www.hibernate.org/296.html">http://www.hibernate.org/296.
 * html</a>
 * 
 * @author Burt
 */
public class TableNameSequencePostgresDialect extends PostgreSQLDialect {

	/**
	 * Get the native identifier generator class.
	 * 
	 * @return TableNameSequenceGenerator.
	 */
	@Override
	public Class<?> getNativeIdentifierGeneratorClass() {
		return TableNameSequenceGenerator.class;
	}

	/**
	 * Creates a sequence per table instead of the default behavior of one
	 * sequence.
	 */
	public static class TableNameSequenceGenerator extends SequenceGenerator {

		/**
		 * {@inheritDoc} If the parameters do not contain a
		 * {@link SequenceGenerator#SEQUENCE} name, we assign one based on the
		 * table name.
		 */
		@Override
		public void configure(final Type type, final Properties params,
				final Dialect dialect) {
			if (params.getProperty(SEQUENCE) == null
					|| params.getProperty(SEQUENCE).length() == 0) {
				String tableName = params
						.getProperty(PersistentIdentifierGenerator.TABLE);
				if (tableName != null) {
					params.setProperty(SEQUENCE, "seq_" + tableName);
				}
			}
			super.configure(type, params, dialect);
		}
	}
}
