package org.tdl.vireo.proquest.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.tdl.vireo.proquest.ProquestDegree;
import org.tdl.vireo.proquest.ProquestLanguage;
import org.tdl.vireo.proquest.ProquestSubject;
import org.tdl.vireo.proquest.ProquestVocabularyRepository;

import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

/**
 * Controlled vocabulary repository for proquest values. This repository reads
 * values at start up from excel spreedsheets for each value. The spreadsheets
 * should be two columns, column A is the code, and column B is the description.
 * The first row is always discarded as a header.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public class ProquestExcelVocabularyRepositoryImpl implements
		ProquestVocabularyRepository {

	// Internal list of all subjects
	public static List<ProquestSubject> subjects = new ArrayList<ProquestSubject>();
	
	// Internal list of all languages
	public static List<ProquestLanguage> languages = new ArrayList<ProquestLanguage>();

	// Internal list of all degrees
	public static List<ProquestDegree> degrees = new ArrayList<ProquestDegree>();
	
	private ProquestExcelVocabularyRepositoryImpl() {
		// Only spring can instantiate this class.
	}
	
	/**
	 * Set the subjects vocabulary based upon the injected excel spreadsheet.
	 * The spreadsheet should consist of two columns; where column A is a
	 * subject code and column B is the description for that code. The first row
	 * of the spreadsheet is ignored as a header. Any previously defined
	 * subjects will be removed.
	 * 
	 * @param resource
	 *            A file reference to an excel spread sheet (either hssf or xssf
	 *            format).
	 */
	public void setSubjects(Resource resource) throws IOException {
		
		File file = resource.getFile();
		
		if (file == null) {
			throw new IllegalArgumentException("Unable to load the ProQuest controlled vocabulary for subjects because the file was invalid.");
		}
		if (!file.exists()) {
			throw new IllegalArgumentException("Unable to load the ProQuest controlled vocabulary for subjects because the path '"+file.getPath()+"' does not exist.");
		}
		if (!file.canRead()) {
			throw new IllegalArgumentException("Unable to load the ProQuest controlled vocabulary for subjects because the path '"+file.getPath()+"' is not readable.");
		}
		
		List<List<String>> rows = _readSpreadsheet(file);

		// Ignore the heading row
		if (rows.size() > 0)
			rows.remove(0);

		// Read each remaining row as a subject.
		subjects.clear();
		for(List<String> row : rows) {
			String code = row.get(0);
			String description = row.get(1);

			ProquestSubject subject = new ProquestSubjectImpl(code.intern(),description.intern());
			subjects.add(subject);
		}
		
		Logger.debug("Loaded %d ProQuest controlled vocabulary for subjects from the spreadsheet: '%s'.", subjects.size(), file.getPath());
	}
	
	/**
	 * Set the language vocabulary based upon the injected excel spreadsheet.
	 * The spreadsheet should consist of two columns; where column A is a
	 * language code and column B is the description for that code. The first row
	 * of the spreadsheet is ignored as a header. Any previously defined
	 * languages will be removed.
	 * 
	 * @param resource
	 *            A file reference to an excel spread sheet (either hssf or xssf
	 *            format).
	 */
	public void setLanguages(Resource resource) throws IOException {
		
		File file = resource.getFile();
		
		if (file == null) {
			throw new IllegalArgumentException("Unable to load the proquest controlled vocabulary for languages because the file was invalid.");
		}
		if (!file.exists()) {
			throw new IllegalArgumentException("Unable to load the proquest controlled vocabulary for languages because the path '"+file.getPath()+"' does not exist.");
		}
		if (!file.canRead()) {
			throw new IllegalArgumentException("Unable to load the proquest controlled vocabulary for languages because the path '"+file.getPath()+"' is not readable.");
		}
		
		List<List<String>> rows = _readSpreadsheet(file);

		// Ignore the heading row
		if (rows.size() > 0)
			rows.remove(0);

		// Read each remaining row as a language.
		languages.clear();
		for(List<String> row : rows) {
			String code = row.get(0);
			String description = row.get(1);

			ProquestLanguage language = new ProquestLanguageImpl(code.intern(),description.intern());
			languages.add(language);
		}
		
		Logger.debug("Loaded %d ProQuest controlled vocabulary for languages from the spreadsheet: '%s'.", languages.size(), file.getPath());
	}
	
	/**
	 * Set the degree vocabulary based upon the injected excel spreadsheet.
	 * The spreadsheet should consist of two columns; where column A is a
	 * degree code and column B is the description for that code. The first row
	 * of the spreadsheet is ignored as a header. Any previously defined
	 * degree will be removed.
	 * 
	 * @param resource
	 *            A file reference to an excel spread sheet (either hssf or xssf
	 *            format).
	 */
	public void setDegrees(Resource resource) throws IOException {
		
		File file = resource.getFile();
		
		if (file == null) {
			throw new IllegalArgumentException("Unable to load the proquest controlled vocabulary for degrees because the file was invalid.");
		}
		if (!file.exists()) {
			throw new IllegalArgumentException("Unable to load the proquest controlled vocabulary for degrees because the path '"+file.getPath()+"' does not exist.");
		}
		if (!file.canRead()) {
			throw new IllegalArgumentException("Unable to load the proquest controlled vocabulary for degrees because the path '"+file.getPath()+"' is not readable.");
		}
		
		List<List<String>> rows = _readSpreadsheet(file);

		// Ignore the heading row
		if (rows.size() > 0)
			rows.remove(0);

		// Read each remaining row as a degree.
		degrees.clear();
		for(List<String> row : rows) {
			String code = row.get(0);
			String description = row.get(1);

			ProquestDegree degree = new ProquestDegreeImpl(code.intern(),description.intern());
			degrees.add(degree);
		}
		
		Logger.debug("Loaded %d ProQuest controlled vocabulary for degrees from the spreadsheet: '%s'.", degrees.size(), file.getPath());

	}
	
	@Override
	public List<ProquestSubject> findAllSubjects() {
		return new ArrayList<ProquestSubject>(subjects);
	}

	@Override
	public ProquestSubject findSubjectByCode(String code) {
		
		if (code == null)
			return null;
		
		for (ProquestSubject subject : subjects) {
			if (code.equals(subject.getCode())) {
				return subject;
			}
		}
		
		return null;
	}

	@Override
	public ProquestSubject findSubjectByDescription(String description) {
		
		if (description == null)
			return null;
		
		for (ProquestSubject subject : subjects) {
			if (description.equals(subject.getDescription())) {
				return subject;
			}
		}
		
		return null;
	}
	
	
	@Override
	public List<ProquestLanguage> findAllLanguages() {
		return new ArrayList<ProquestLanguage>(languages);
	}

	@Override
	public ProquestLanguage findLanguageByCode(String code) {
		if (code == null)
			return null;
		
		for (ProquestLanguage language : languages) {
			if (code.equals(language.getCode())) {
				return language;
			}
		}
		
		return null;
	}

	@Override
	public ProquestLanguage findLanguageByDescription(String description) {
		if (description == null)
			return null;
		
		for (ProquestLanguage language : languages) {
			if (description.equals(language.getDescription())) {
				return language;
			}
		}
		
		return null;
	}
	
	@Override
	public List<ProquestDegree> findAllDegrees() {
		return new ArrayList<ProquestDegree>(degrees);
	}

	@Override
	public ProquestDegree findDegreeByCode(String code) {
		if (code == null)
			return null;
		
		for (ProquestDegree degree : degrees) {
			if (code.equals(degree.getCode())) {
				return degree;
			}
		}
		
		return null;
	}

	@Override
	public ProquestDegree findDegreeByDescription(String description) {
		if (description == null)
			return null;
		
		for (ProquestDegree degree : degrees) {
			if (description.equals(degree.getDescription())) {
				return degree;
			}
		}
		
		return null;
	}
	
	/**
	 * Internal method to retrieve the contents of an excel spreadsheet. The
	 * contents of the spread sheet will be returned as a simple List of Lists
	 * corresponding to the rows and cells of the spreadsheet. This method does
	 * not handle multiple worksheets or any data types other than string.
	 * 
	 * @param file
	 *            The file to read.
	 * @return The data read.
	 */
	protected List<List<String>> _readSpreadsheet(File file) throws IOException {

		// Read Workbook
		Workbook wb;
		InputStream is = new FileInputStream(file);
		try {
			String extension = FilenameUtils.getExtension(file.getName());
			if ("xlsx".equals(extension)) {
				// Newer XML office format
				// TODO: This appears to cause memory problems? I guess for now use the old format? Perhaps in a newer version of apache-poi this will work better.
				wb = new XSSFWorkbook(is);
			} else {
				// Older office 97 binary format
				wb = new HSSFWorkbook(is);
			}

			// Read the data
			List<List<String>> rowsOut = new ArrayList<List<String>>();

			Sheet sheet = wb.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			while (rows.hasNext()) {
				Row row = rows.next();

				List<String> cellsOut = new ArrayList<String>();
				Iterator<Cell> cells = row.cellIterator();
				while (cells.hasNext()) {
					Cell cell = cells.next();
					String value = cell.getRichStringCellValue().getString();
					cellsOut.add(value.trim());
				}

				rowsOut.add(cellsOut);
			}
			
			return rowsOut;

		} finally {
			wb = null;
			is.close();
			is = null;
			System.gc();
		}
	}


}
