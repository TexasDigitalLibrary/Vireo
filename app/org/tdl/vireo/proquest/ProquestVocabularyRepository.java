package org.tdl.vireo.proquest;

import java.util.List;

/**
 * Repository of all ProQuest / UMI controlled vocabulary. This repository can
 * be used to translate between the code and description of various proquest
 * vocabularies, or just simply retrieve a list of all the possible values.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public interface ProquestVocabularyRepository {

	/**
	 * @return A list of all subjects
	 */
	public List<ProquestSubject> findAllSubjects();

	/**
	 * @param code
	 *            The code to search for
	 * @return The subject found, or null if not found.
	 */
	public ProquestSubject findSubjectByCode(String code);

	/**
	 * @param description
	 *            The description to search for
	 * @return The subject found, or null if not found.
	 */
	public ProquestSubject findSubjectByDescription(String description);

	/**
	 * @return A list of all languages
	 */
	public List<ProquestLanguage> findAllLanguages();

	/**
	 * @param code
	 *            The code to search for
	 * @return The language found, or null if not found.
	 */
	public ProquestLanguage findLanguageByCode(String code);

	/**
	 * @param description
	 *            The description to search for
	 * @return The language found, or null if not found.
	 */
	public ProquestLanguage findLanguageByDescription(String description);
	
	
	/**
	 * @return A list of all degrees
	 */
	public List<ProquestDegree> findAllDegrees();

	/**
	 * @param code
	 *            The code to search for
	 * @return The degree found, or null if not found.
	 */
	public ProquestDegree findDegreeByCode(String code);

	/**
	 * @param description
	 *            The description to search for
	 * @return The degree found, or null if not found.
	 */
	public ProquestDegree findDegreeByDescription(String description);

}
