package org.tdl.vireo.export.impl;

import static controllers.FilterTab.SUBMISSION;
import static controllers.FilterTab.getDefaultColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.tdl.vireo.export.ExportExcel;
import org.tdl.vireo.export.ExportPackage;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.EmbargoGuarantor;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.search.SearchOrder;
import org.tdl.vireo.services.StringVariableReplacement;

import play.modules.spring.Spring;

/**
 * This will export the current FilterTab results as an Excel spreadsheet.
 * 
 * @author william_mckinney@harvard.edu
 * @author <a href=mailto:gad.krumholz@austin.utexas.edu>Gad Krumholz</a>
 */
public class ExcelPackagerImpl extends AbstractExcelPackagerImpl {

	/* Spring injected paramaters */
	public List<AttachmentType> attachmentTypes = new ArrayList<AttachmentType>();
	public Boolean aggregated = false;

	/* Global statics */
	public static final String sheetName = "vireo-export";
	public static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

	/* Global Dynamics */
	public LinkedHashMap<String, Properties> attachmentAttributes = new LinkedHashMap<String, Properties>();

	/**
	 * This function is used by Unit Test to export a {@link XSSFWorkbook} with all of the submissions as separate rows
	 * 
	 * This is needed because the Unit Test does not run through the ExportService (which is the iterator of {@link List} {@link Submission})
	 * 
	 * @param submissions
	 *            - list of submissions
	 * @param columns
	 *            - the columns to include in the sheet
	 * @return - The Excel workbook file (xssf format only)
	 */
	public XSSFWorkbook testWorkbook(List<Submission> submissions, List<SearchOrder> columns) {

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName);
		XSSFRow header = sheet.createRow(0);

		int i = 1; // row counter

		for (Submission sub : submissions) {
			XSSFRow row = sheet.createRow(i);
			processWorkbookRow(header, row, sub, columns);
			i++;
		}
		return wb;
	}

	/**
	 * Takes {@link XSSFWorkbook} argument, a single {@link Submission}, and a {@link List} of {@link SearchOrder}.
	 * 
	 * Creates a new {@link XSSFSheet} in the {@link XSSFWorkbook} and adds a header {@link XSSFRow} and a data {@link XSSFRow}.
	 * 
	 * Passes the header row, the data row, the submission, and the {@link SearchOrder} {@link List} to processWorkbookRow()
	 * 
	 * @param wb
	 *            - the workbook to modify in-place
	 * @param sub
	 *            - the submission to add to the workbook
	 * @param columns
	 *            - the columns to include in the sheet
	 */
	public XSSFWorkbook writeWorkbook(Submission sub, List<SearchOrder> columns) {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName);
		XSSFRow header = sheet.createRow(0);
		XSSFRow row = sheet.createRow(1);
		processWorkbookRow(header, row, sub, columns);
		return wb;
	}

	private void processWorkbookRow(XSSFRow header, XSSFRow row, Submission sub, List<SearchOrder> SearchOrderList) {
		int j = 0; // cell counter

		for (SearchOrder column : SearchOrderList) {

			switch (column) {

			case ID:
				header.createCell(j).setCellValue("ID");
				if (null != sub.getId())
					row.createCell(j).setCellValue(sub.getId());
				j++;
				break;

			case STUDENT_EMAIL:
				header.createCell(j).setCellValue("Student email");
				if (null != sub.getSubmitter() && null != sub.getSubmitter().getEmail())
					row.createCell(j).setCellValue(sub.getSubmitter().getEmail());
				j++;
				break;

			case STUDENT_NAME:
				header.createCell(j).setCellValue("Student name");
				row.createCell(j).setCellValue(sub.getStudentFormattedName(org.tdl.vireo.model.NameFormat.LAST_FIRST_MIDDLE_BIRTH));
				j++;
				break;

			case STUDENT_ID:
				header.createCell(j).setCellValue("Student ID");
				if (sub.getSubmitter() != null && sub.getSubmitter().getInstitutionalIdentifier() != null)
					row.createCell(j).setCellValue(sub.getSubmitter().getInstitutionalIdentifier());
				j++;
				break;

			case STATE:
				header.createCell(j).setCellValue("Status");
				if (null != sub.getState())
					row.createCell(j).setCellValue(sub.getState().getDisplayName());
				j++;
				break;

			case ASSIGNEE:
				header.createCell(j).setCellValue("Assignee");
				if (null != sub.getAssignee())
					row.createCell(j).setCellValue(sub.getAssignee().getFormattedName(org.tdl.vireo.model.NameFormat.FIRST_LAST));
				j++;
				break;

			case DOCUMENT_TITLE:
				header.createCell(j).setCellValue("Title");
				if (null != sub.getDocumentTitle())
					row.createCell(j).setCellValue(sub.getDocumentTitle());
				j++;
				break;

			case DOCUMENT_ABSTRACT:
				header.createCell(j).setCellValue("Abstract");
				if (null != sub.getDocumentAbstract())
					row.createCell(j).setCellValue(sub.getDocumentAbstract());
				j++;
				break;

			case DOCUMENT_SUBJECTS:
				header.createCell(j).setCellValue("Subjects");
				if (null != sub.getDocumentSubjects())
					row.createCell(j).setCellValue(StringUtils.join(sub.getDocumentSubjects(), ";"));
				j++;
				break;

			case DOCUMENT_LANGUAGE:
				header.createCell(j).setCellValue("Language");
				if (null != sub.getDocumentLanguage())
					row.createCell(j).setCellValue(sub.getDocumentLanguage());
				j++;
				break;

			case PUBLISHED_MATERIAL:
				header.createCell(j).setCellValue("Published material");
				if (null != sub.getPublishedMaterial())
					row.createCell(j).setCellValue("Yes - " + sub.getPublishedMaterial());
				j++;
				break;

			case PRIMARY_DOCUMENT:
				header.createCell(j).setCellValue("Primary document");
				if (null != sub.getPrimaryDocument())
					row.createCell(j).setCellValue(sub.getPrimaryDocument().getName());
				j++;
				break;

			case GRADUATION_DATE:
				header.createCell(j).setCellValue("Graduation date");
				StringBuilder sb = new StringBuilder();
				String monthName = null;
				if (sub.getGraduationMonth() != null && sub.getGraduationMonth() >= 0 && sub.getGraduationMonth() <= 11)
					monthName = new java.text.DateFormatSymbols().getMonths()[sub.getGraduationMonth()];
				if (sub.getGraduationYear() != null)
					sb.append(sub.getGraduationYear());
				if (monthName != null)
					sb.append(" ").append(monthName);
				row.createCell(j).setCellValue(sb.toString());
				j++;
				break;

			case DEFENSE_DATE:
				header.createCell(j).setCellValue("Defense date");
				if (sub.getDefenseDate() != null)
					row.createCell(j).setCellValue(sdf.format(sub.getDefenseDate()));
				j++;
				break;

			case SUBMISSION_DATE:
				header.createCell(j).setCellValue("Submission date");
				if (sub.getSubmissionDate() != null)
					row.createCell(j).setCellValue(sdf.format(sub.getSubmissionDate()));
				j++;
				break;

			case LICENSE_AGREEMENT_DATE:
				header.createCell(j).setCellValue("License agreement date");
				if (sub.getLicenseAgreementDate() != null)
					row.createCell(j).setCellValue(sdf.format(sub.getLicenseAgreementDate()));
				j++;
				break;

			case APPROVAL_DATE:
				header.createCell(j).setCellValue("Approval date");
				if (sub.getApprovalDate() != null)
					row.createCell(j).setCellValue(sdf.format(sub.getApprovalDate()));
				j++;
				break;

			case COMMITTEE_APPROVAL_DATE:
				header.createCell(j).setCellValue("Committee approval date");
				if (sub.getCommitteeApprovalDate() != null)
					row.createCell(j).setCellValue(sdf.format(sub.getCommitteeApprovalDate()));
				j++;
				break;

			case COMMITTEE_EMBARGO_APPROVAL_DATE:
				header.createCell(j).setCellValue("Committee embargo approval date");
				if (sub.getCommitteeEmbargoApprovalDate() != null)
					row.createCell(j).setCellValue(sdf.format(sub.getCommitteeEmbargoApprovalDate()));
				j++;
				break;

			case COMMITTEE_MEMBERS:
				header.createCell(j).setCellValue("Committee members");
				StringBuilder cm = new StringBuilder();
				int i = 0;
				for (i = 0; i < sub.getCommitteeMembers().size(); i++) {
					CommitteeMember member = sub.getCommitteeMembers().get(i);
					cm.append(member.getFormattedName(NameFormat.LAST_FIRST));
					if (member.getRoles().size() > 0) {
						cm.append(" (").append(member.getFormattedRoles()).append(")");
					}
					if ((i + 1) < sub.getCommitteeMembers().size()) {
						cm.append(";");
					}
				}
				row.createCell(j).setCellValue(cm.toString());
				j++;
				break;

			case COMMITTEE_CONTACT_EMAIL:
				header.createCell(j).setCellValue("Committee contact email");
				if (sub.getCommitteeContactEmail() != null)
					row.createCell(j).setCellValue(sub.getCommitteeContactEmail());
				j++;
				break;

			case DEGREE:
				header.createCell(j).setCellValue("Degree");
				if (sub.getDegree() != null)
					row.createCell(j).setCellValue(sub.getDegree());
				j++;
				break;

			case DEGREE_LEVEL:
				header.createCell(j).setCellValue("Degree level");
				if (sub.getDegreeLevel() != null)
					row.createCell(j).setCellValue(sub.getDegreeLevel().name());
				j++;
				break;

			case PROGRAM:
				header.createCell(j).setCellValue("Program");
				if (sub.getProgram() != null)
					row.createCell(j).setCellValue(sub.getProgram());
				j++;
				break;

			case COLLEGE:
				header.createCell(j).setCellValue("College");
				if (sub.getCollege() != null)
					row.createCell(j).setCellValue(sub.getCollege());
				j++;
				break;

			case DEPARTMENT:
				header.createCell(j).setCellValue("Department");
				if (sub.getDepartment() != null)
					row.createCell(j).setCellValue(sub.getDepartment());
				j++;
				break;

			case MAJOR:
				header.createCell(j).setCellValue("Major");
				if (sub.getMajor() != null)
					row.createCell(j).setCellValue(sub.getMajor());
				j++;
				break;

			case EMBARGO_TYPE:
				header.createCell(j).setCellValue("Embargo type");
				String sEmbargos = "";
				List<EmbargoType> embargoTypes = sub.getEmbargoTypes();
				for (int k = 0; k < embargoTypes.size(); k++) {
					EmbargoType embargoType = embargoTypes.get(k);
					sEmbargos += embargoType.getName() + (embargoType.getGuarantor() != EmbargoGuarantor.DEFAULT ? " (" + embargoType.getGuarantor().name() + ")" : "") + ((k + 1) < embargoTypes.size() ? ";" : "");
				}
				row.createCell(j).setCellValue(sEmbargos);
				j++;
				break;

			case DOCUMENT_TYPE:
				header.createCell(j).setCellValue("Document type");
				if (sub.getDocumentType() != null)
					row.createCell(j).setCellValue(sub.getDocumentType());
				j++;
				break;

			case UMI_RELEASE:
				header.createCell(j).setCellValue("UMI release");
				if (sub.getUMIRelease() != null) {
					if (sub.getUMIRelease()) {
						row.createCell(j).setCellValue("Yes");
					} else {
						row.createCell(j).setCellValue("No");
					}
				}
				j++;
				break;

			case CUSTOM_ACTIONS:
				header.createCell(j).setCellValue("Custom actions");
				String sActions = "";
				SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
				List<CustomActionDefinition> customActions = settingRepo.findAllCustomActionDefinition();
				for (int k = 0; k < customActions.size(); k++) {
					CustomActionDefinition systemAction = customActions.get(k);
					boolean found = false;
					for (org.tdl.vireo.model.CustomActionValue subAction : sub.getCustomActions()) {
						if (systemAction.equals(subAction.getDefinition())) {
							sActions += "☑ " + subAction.getDefinition().getLabel();
							found = true;
						}
					}
					if (!found) {
						sActions += "☐ " + systemAction.getLabel();
					}
					if ((k + 1) < customActions.size()) {
						sActions += '\n';
					}
				}
				row.createCell(j).setCellValue(sActions);
				j++;
				break;

			case DEPOSIT_ID:
				header.createCell(j).setCellValue("Deposit ID");
				if (sub.getDepositId() != null)
					row.createCell(j).setCellValue(sub.getDepositId());
				j++;
				break;

			case REVIEWER_NOTES:
				header.createCell(j).setCellValue("Reviewer notes");
				if (sub.getReviewerNotes() != null)
					row.createCell(j).setCellValue(sub.getReviewerNotes());
				j++;
				break;
			case DOCUMENT_KEYWORDS:
				header.createCell(j).setCellValue("Document Keywords");
				if (sub.getDocumentKeywords() != null)
					row.createCell(j).setCellValue(sub.getDocumentKeywords());
				j++;
				break;
			case LAST_EVENT_ENTRY:
				header.createCell(j).setCellValue("Last Event Entry");
				if (sub.getLastLogEntry() != null)
					row.createCell(j).setCellValue(sub.getLastLogEntry());
				j++;
				break;
			case LAST_EVENT_TIME:
				header.createCell(j).setCellValue("Last Event Time");
				if (sub.getLastLogDate() != null)
					row.createCell(j).setCellValue(sub.getLastLogDate());
				j++;
				break;
			case ORCID:
				header.createCell(j).setCellValue("ORCID");
				if (sub.getOrcid() != null)
					row.createCell(j).setCellValue(sub.getOrcid());
				j++;
				break;
			}
		}
	}

	/* Spring injected paramaters */
	/**
	 * (OPITONAL) Set the attachment types which will be included in the package. Since not all attachments should be deposited, this allows the package to filter which files to include. They must be the exact name (all uppercase) of types listed in the AttachmentType enum.
	 * 
	 * If no types are specified then no attachments will be included.
	 * 
	 * @param attachmentTypeNames
	 *            List of attachment types to include.
	 */
	public void setAttachmentTypeNames(LinkedHashMap<String, Properties> attachmentTypeNames) {

		this.attachmentTypes = new ArrayList<AttachmentType>();
		this.attachmentAttributes = new LinkedHashMap<String, Properties>();

		if (attachmentTypeNames != null) {
			this.attachmentAttributes = attachmentTypeNames;
			for (String name : attachmentTypeNames.keySet()) {
				AttachmentType type = AttachmentType.valueOf(name);
				this.attachmentTypes.add(type);
			}
		}
	}

	/**
	 * (OPTIONAL) Inject whether we're aggregating Excel data into one file, no file attachments
	 * 
	 * @param filtered
	 *            Boolean of whether we're aggregating Excel data into one file, no file attachments
	 */
	public void setAggregated(Boolean aggregated) {
		this.aggregated = aggregated;
	}

	@Override
	public String getExportServiceBeanName() {
		String ret = "ExportService";
		if (aggregated) {
			ret = "ExportExcelService";
		}
		return ret;
	}

	/**
	 * This is used for ExportExcel implementations
	 * 
	 * @param submission
	 *            - the {@link Submission}
	 * @param columns
	 *            - the {@link SearchOrder} columns to export
	 * @return - the {@link ExportExcel} object
	 */
	@Override
	public ExportExcel generateExcelPackage(Submission submission, List<SearchOrder> columns) {
		// make sure we have columns to export
		if (columns.size() == 0) {
			columns = getDefaultColumns(SUBMISSION);
		}
		XSSFWorkbook wbook = writeWorkbook(submission, columns);
		return new ExcelWorkbookPackage(submission, wbook);
	}

	/**
	 * This is used for ExportPackage implementations
	 * 
	 * @param submission
	 *            - the {@link Submission}
	 * @return - the {@link ExportPackage} object
	 */
	@Override
	public ExportPackage generatePackage(Submission submission) {
		if (attachmentTypes.size() == 0) {
			throw new IllegalArgumentException("Unable to generate package because not attachment types have been defined.");
		}

		// Check that we have everything that we need.
		if (submission == null || submission.getId() == null)
			throw new IllegalArgumentException("Unable to generate a package because the submission is null, or has not been persisted.");

		try {

			// Set String replacement parameters
			Map<String, String> parameters = new HashMap<String, String>();
			parameters = StringVariableReplacement.setParameters(submission);

			File pkg = null;
			List<Attachment> actionLogAttachments = new ArrayList<Attachment>();

			pkg = File.createTempFile("template-export-", ".dir");

			// The package has more than one file, so export as a directory.
			pkg.delete();
			pkg.mkdir();

			File xl = new File(pkg.getPath(), "data.xlsx");
			if (xl.exists() || xl.isDirectory()) {
				xl.delete();
				xl.createNewFile();
			}
			XSSFWorkbook wbook = writeWorkbook(submission, Arrays.asList(SearchOrder.values()));
			FileOutputStream os = new FileOutputStream(xl);
			wbook.write(os);
			os.close();

			// Add all the attachments
			List<Attachment> attachments = submission.getAttachments();
			List<Attachment> actionLogs = submission.getAttachmentsByType(AttachmentType.ACTIONLOG);
			// add them to this list to delete the temp files later
			actionLogAttachments.addAll(actionLogs);
			// add them to this list to add them to export
			attachments.addAll(actionLogs);
			for (Attachment attachment : attachments) {
				// Do we include this type?
				if (!attachmentTypes.contains(attachment.getType()))
					continue;

				/*
				 * The string substitution only works on items we can retrieve from the submission so we have to get the file name for each attachment here in the attachment loop.
				 */
				String shortFileName = attachment.getName().replaceAll("." + FilenameUtils.getExtension(attachment.getName()), "");

				String fileName = attachment.getName();

				// Customize Attachment Name
				if (attachmentAttributes.get(attachment.getType().name()).get("customName") != null) {
					fileName = attachmentAttributes.get(attachment.getType().name()).get("customName") + "." + FilenameUtils.getExtension(attachment.getName());
					fileName = fileName.replace("{FILE_NAME}", shortFileName);
					fileName = StringVariableReplacement.applyParameterSubstitution(fileName, parameters);
				}

				// Check for Custom Directory Structure.
				String pkgPath = pkg.getPath();

				if (attachmentAttributes.get(attachment.getType().name()).get("directory") != null) {
					String dirName = (String) attachmentAttributes.get(attachment.getType().name()).get("directory");
					dirName = dirName.replace("{FILE_NAME}", shortFileName);
					dirName = StringVariableReplacement.applyParameterSubstitution(dirName, parameters);
					pkgPath = pkgPath + File.separator + dirName;
				}

				File exportFile = new File(pkgPath, fileName);

				FileUtils.copyFile(attachment.getFile(), exportFile);
			}// End for loop

			// Create the actual package!
			return new ExcelFilePackage(submission, pkg, null, actionLogAttachments);
		} catch (Exception ioe) {
			throw new RuntimeException("Unable to generate package", ioe);
		}
	}

	/**
	 * The package interface.
	 * 
	 * This is the class that represents the actual package. It contains the file we've built along with some basic metadata.
	 * 
	 */
	private static class ExcelFilePackage implements ExportPackage {

		// Members
		public final Submission submission;
		public final File file;
		public final String entryName;
		private final List<Attachment> actionLogAttachments;

		public ExcelFilePackage(Submission submission, File file, String entryName, List<Attachment> actionLogAttachments) {
			this.submission = submission;
			this.file = file;
			this.entryName = entryName;
			this.actionLogAttachments = actionLogAttachments;
		}

		@Override
		public Submission getSubmission() {
			return submission;
		}

		@Override
		public String getMimeType() {
			return null;
		}

		@Override
		public String getFormat() {
			return "Excel Spreadsheet";
		}

		@Override
		public File getFile() {
			return file;
		}

		@Override
		public String getEntryName() {
			return entryName;
		}

		@Override
		public void delete() {
			if (file != null && file.exists()) {
				if (file.isDirectory()) {
					try {
						FileUtils.deleteDirectory(file);
					} catch (IOException ioe) {
						throw new RuntimeException("Unable to cleanup export package: " + file.getAbsolutePath(), ioe);
					}
				} else {
					file.delete();
				}
			}
			// clean up tempoarary action logs too
			if (actionLogAttachments != null && actionLogAttachments.size() > 0) {
				for (Attachment actionLogAttachment : actionLogAttachments) {
					File actionLogFile = actionLogAttachment.getFile();
					if (actionLogFile.isDirectory()) {
						try {
							FileUtils.deleteDirectory(actionLogFile);
						} catch (IOException ioe) {
							throw new RuntimeException("Unable to cleanup export package: " + actionLogFile.getAbsolutePath(), ioe);
						}
					} else {
						actionLogFile.delete();
					}
				}
			}
		}
	}

	private static class ExcelWorkbookPackage implements ExportExcel {
		// Members
		public final Submission submission;
		public final XSSFWorkbook workbook;

		public ExcelWorkbookPackage(Submission submission, XSSFWorkbook workbook) {
			this.submission = submission;
			this.workbook = workbook;
		}

		@Override
		public Submission getSubmission() {
			return submission;
		}

		@Override
		public String getMimeType() {
			return null;
		}

		@Override
		public String getFormat() {
			return "Excel Spreadsheet";
		}

		@Override
		public XSSFWorkbook getWorkbook() {
			return workbook;
		}
	}
}
