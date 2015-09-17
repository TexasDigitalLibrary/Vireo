package org.tdl.vireo.export;

/**
 * A runtime expetion that occured during depositing. This is basicaly like any
 * other undeclared runtime exception except that we store the field which is
 * probably in error. This allows the UI to provide better help to the user in
 * debugging the connection issues.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class DepositException extends RuntimeException {

	/**
	 * The field in probably causing the error.
	 */
	public FIELD field = null;

	public DepositException(String message) {
		super(message);
	}

	public DepositException(String message, Throwable cause) {
		super(message, cause);
	}

	public DepositException(Throwable cause) {
		super(cause);
	}

	public DepositException(FIELD field, String message) {
		super(message);
		this.field = field;
	}

	public DepositException(FIELD field, String message, Throwable cause) {
		super(message, cause);
		this.field = field;
	}

	public DepositException(FIELD field, Throwable cause) {
		super(cause);
		this.field = field;
	}

	/**
	 * @return The field which is probably causing the error.
	 */
	public FIELD getField() {
		return field;
	}

	/**
	 * @param field
	 *            A deposit field.
	 * @return True if this field is causing the error, otherwise false.
	 */
	public boolean isField(FIELD field) {
		return this.field == field;
	}

	/**
	 * A list of possible deposit fields.
	 * 
	 */
	public static enum FIELD {
		REPOSITORY, AUTHENTICATION, COLLECTION, OTHER
	}
}
