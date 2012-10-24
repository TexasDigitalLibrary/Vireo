package org.tdl.vireo.model.jpa;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.StringType;
import org.hibernate.usertype.UserType;

import play.Play;
import play.db.Model.BinaryField;
import play.exceptions.UnexpectedException;
import play.libs.Codec;
import play.libs.IO;

/**
 * This is a slight update upon the default Play "blob" datatype. The HashedBlob
 * will hash the data files out into sub directories instead of leaving them all
 * in one directory.
 * 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public class HashedBlob implements BinaryField, UserType {

    private String UUID;
    private String type;
    private File file;

    public HashedBlob() {}

    private HashedBlob(String UUID, String type) {
        this.UUID = UUID;
        this.type = type;
    }
    
    public InputStream get() {
        if(exists()) {
            try {
                return new FileInputStream(getFile());
            } catch(Exception e) {
                throw new UnexpectedException(e);
            }
        }
        return null;
    }
    
    public void set(InputStream is, String type) {
        this.UUID = Codec.UUID();
        this.type = type;
        
        // Make sure the hash directory exists.
        getFile().getParentFile().mkdirs();        
        IO.write(is, getFile());
    }

    public long length() {
        return getFile().length();
    }

    public String type() {
        return type;
    }

    public boolean exists() {
        return UUID != null && getFile().exists();
    }

	/**
	 * This is the one method that is different from the original Blob type.
	 * 
	 * A hash directory is calculated and set as the parent folder for the file.
	 * 
	 * @return A file pointer to the data.
	 */
	public File getFile() {
	if (file == null) {
			String UUID = getUUID();

			String name1 = UUID.substring(0, 2);
			String name2 = UUID.substring(2, 4);
			String name3 = UUID.substring(4, 6);
			String name4 = UUID.substring(6, 8);

			File hashDir = new File(getStore(), name1 + File.separator + name2
					+ File.separator + name3 + File.separator + name4);

			file = new File(hashDir, UUID);
		}
		return file;
	}
    
    public String getUUID()  {
        return UUID;
    }

    //

    public int[] sqlTypes() {
        return new int[] {Types.VARCHAR};
    }

    public Class returnedClass() {
        return HashedBlob.class;
    }

    private static boolean equal(Object a, Object b) {
      return a == b || (a != null && a.equals(b));
    }

    public boolean equals(Object o, Object o1) throws HibernateException {
        if(o instanceof HashedBlob && o1 instanceof HashedBlob) {
            return equal(((HashedBlob)o).UUID, ((HashedBlob)o1).UUID) &&
                    equal(((HashedBlob)o).type, ((HashedBlob)o1).type);
        }
        return equal(o, o1);
    }

    public int hashCode(Object o) throws HibernateException {
        return o.hashCode();
    }

    public Object nullSafeGet(ResultSet resultSet, String[] names, Object o) throws HibernateException, SQLException {
       	String val = (String) StringType.INSTANCE.get(resultSet, names[0]);

        if(val == null || val.length() == 0 || !val.contains("|")) {
            return new HashedBlob();
        }
        return new HashedBlob(val.split("[|]")[0], val.split("[|]")[1]);
    }

    public void nullSafeSet(PreparedStatement ps, Object o, int i) throws HibernateException, SQLException {
         if(o != null) {
            ps.setString(i, ((HashedBlob)o).UUID + "|" + ((HashedBlob)o).type);
        } else {
            ps.setNull(i, Types.VARCHAR);
        }
    }

    public Object deepCopy(Object o) throws HibernateException {
        if(o == null) {
            return null;
        }
        return new HashedBlob(((HashedBlob)o).UUID, ((HashedBlob)o).type);
    }

    public boolean isMutable() {
        return true;
    }

    public Serializable disassemble(Object o) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object assemble(Serializable srlzbl, Object o) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object replace(Object o, Object o1, Object o2) throws HibernateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //

    public static String getUUID(String dbValue) {
       return dbValue.split("[|]")[0];
    }

    public static File getStore() {
        String name = Play.configuration.getProperty("attachments.path", "attachments");
        File store = null;
        if(new File(name).isAbsolute()) {
            store = new File(name);
        } else {
            store = Play.getFile(name);
        }
        if(!store.exists()) {
            store.mkdirs();
        }
        return store;
    }
    
}
