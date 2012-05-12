package org.tdl.vireo.model;

/**
 * This class represents the document types which may be awarded by Vireo. An
 * example of document types are "Dissertation", "Thesis", or "Record of Study"
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public interface DocumentType extends AbstractOrderedModel {

	/**
	 * @return The name of this document type
	 */
	public String getName();

	/**
	 * @param name
	 *            The new name of this document type
	 */
	public void setName(String name);

	/**
	 * @return The degree level associated with this document type
	 */
	public DegreeLevel getLevel();

	/**
	 * 
	 * @param level
	 *            The new degree level associated with this document type
	 */
	public void setLevel(DegreeLevel level);

}
