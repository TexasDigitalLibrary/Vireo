package org.tdl.vireo.search.impl;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.search.SearchOrder;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * This will export the current FilterTab results as an Excel spreadsheet.
 *
 * @author william_mckinney@harvard.edu
 *
 */
public class ExcelExport {

    public List<Submission> submissions = null;
    public List<SearchOrder> order = null;
    public XSSFWorkbook wb = null;
    public String sheetName = "vireo-export";
    public SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    /**
     *
     * @param submissions The list of submission viewed on the filter tab
     * @param order The search order
     */
    public ExcelExport(List<Submission> submissions, List<SearchOrder> order) {

        this.submissions = submissions;
        this.order = order;

    }

    /**
     *
     * @return The Excel workbook file (xssf format only)
     */
    public XSSFWorkbook getWorkbook() {

        wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet(sheetName);
        Row header = sheet.createRow(0);

        int i = 1; // row counter

        for (Submission sub : submissions) {

            Row row = sheet.createRow(i);
            int j = 0; // cell counter

            for (org.tdl.vireo.search.SearchOrder column : order) {

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
                            row.createCell(j).setCellValue(StringUtils.join(sub.getDocumentSubjects(),";"));
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
                        for (org.tdl.vireo.model.CommitteeMember member : sub.getCommitteeMembers()) {
                            cm.append(member.getFormattedName(org.tdl.vireo.model.NameFormat.LAST_FIRST));
                            if (member.getRoles().size() > 0) {
                                cm.append(" (").append(member.getFormattedRoles()).append(")");
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
                        if (sub.getProgram() !=null)
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
                        if (sub.getEmbargoType() != null)
                            row.createCell(j).setCellValue(sub.getEmbargoType().getName());
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
                        int actions = 0;
                        for (org.tdl.vireo.model.CustomActionValue action : sub.getCustomActions()) {
                            if (action.getValue()) {
                                actions++;
                            }
                        }
                        if (actions > 0)
                            row.createCell(j).setCellValue(Integer.toString(actions));
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

                }
            }
            i++;
        }
        return wb;
    }

    /**
     *
     * @return  The list of submission
     */
    public List<Submission> getSubmissions() {
        return submissions;
    }

    /**
     *
     * @param submissions  The list of submissions
     */
    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

}
