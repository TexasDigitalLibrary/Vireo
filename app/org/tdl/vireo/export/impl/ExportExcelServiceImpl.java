package org.tdl.vireo.export.impl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.tdl.vireo.error.ErrorLog;
import org.tdl.vireo.export.ChunkStream;
import org.tdl.vireo.export.ExportExcel;
import org.tdl.vireo.export.ExportService;
import org.tdl.vireo.export.Packager;
import org.tdl.vireo.job.JobManager;
import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.job.JobStatus;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.search.SearchDirection;
import org.tdl.vireo.search.SearchFilter;
import org.tdl.vireo.search.SearchOrder;
import org.tdl.vireo.search.Searcher;
import org.tdl.vireo.security.SecurityContext;

import play.Logger;
import play.db.jpa.JPA;
import play.jobs.Job;

/**
 * Implementation of the export service interface.
 * 
 * @author <a href=mailto:gad.krumholz@austin.utexas.edu>Gad Krumholz</a>
 */
public class ExportExcelServiceImpl implements ExportService {

    public final static String MIME_TYPE = "application/vnd.ms-excel";
    public final static int BUFFER_SIZE = 10; // Each chunk may be big.

    // The repositories
    public PersonRepository personRepo;
    public SubmissionRepository subRepo;
    public ErrorLog errorLog;

    // The searcher used to find submissions in a batch.
    public Searcher searcher;

    // The security context, who's logged in.
    public SecurityContext context;

    // Maintains job metadata
    public JobManager jobManager;

    /**
     * @param searcher
     *            Set the searcher used for identify batch of submissions to be processed.
     */
    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    /**
     * @param repo
     *            The person repository
     */
    public void setPersonRepository(PersonRepository repo) {
        this.personRepo = repo;
    }

    /**
     * @param repo
     *            The submission repository
     */
    public void setSubmissionRepository(SubmissionRepository repo) {
        this.subRepo = repo;
    }

    /**
     * @param errorLog
     *            The error log
     */
    public void setErrorLog(ErrorLog errorLog) {
        this.errorLog = errorLog;
    }

    /**
     * @param context
     *            The security context managing who is currently logged in.
     */
    public void setSecurityContext(SecurityContext context) {
        this.context = context;
    }

    /**
     * @param jobManager
     *            The manager which maintains metadata about jobs.
     */
    public void setJobManager(JobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Override
    public ChunkStream export(Packager packager, SearchFilter filter) {

        if (packager == null)
            throw new IllegalArgumentException("A packager is required");

        if (filter == null)
            throw new IllegalArgumentException("A search filter is required");

        ChunkStreamImpl stream = new ChunkStreamImpl(MIME_TYPE, "attachment; filename=" + packager.getBeanName() + ".xlsx", BUFFER_SIZE);

        new ExportJob(packager, filter, stream).now();

        return stream;
    }

    /**
     * Background job to export
     */
    public class ExportJob extends Job {

        // Member fields
        public final Packager packager;
        public final SearchFilter filter;
        public final OutputStream out;
        public final Long personId;

        // Metadata about this job
        public final JobMetadata meta;

        /**
         * Construct a new export job.
         * 
         * @param packager
         *            The packager which will generate content.
         * @param filter
         *            The filter to select submissions.
         * @param out
         *            The output stream where to send the export too.
         */
        public ExportJob(Packager packager, SearchFilter filter, OutputStream out) {
            this.packager = packager;
            this.filter = filter;
            this.out = out;

            if (context.getPerson() != null) {

                if (!context.isReviewer())
                    throw new SecurityException("Not authorized to preform export operation.");

                this.personId = context.getPerson().getId();
            } else {

                if (!context.isAuthorizationActive())
                    throw new SecurityException("Not authorized to preform export operation.");

                this.personId = null;
            }

            // Register the job's metadata
            meta = jobManager.register("Download " + packager.getDisplayName(), context.getPerson());
            meta.setJob(this);
            meta.setStatus(JobStatus.READY);
        }

        /**
         * Run the back ground job.
         * 
         * Iterate through the submissions and stream each one into the a xlsx workbook.
         * 
         * The archive will be buffered, and sent directly to the ChunkStream to be transmitted to the browser.
         * 
         * This means we never have the complete archive on the server at any one time.
         * 
         * @throws Exception
         */
        public void doJob() throws Exception {

            try {
                meta.setStatus(JobStatus.RUNNING);

                if (personId != null) {
                    Person person = personRepo.findPerson(personId);
                    if (person == null)
                        throw new IllegalStateException("Unable to complete deposit job because person no longer exists.");

                    // Log the person in for this job.
                    context.login(person);
                } else {
                    // Assume we're running as a background admin process.
                    context.turnOffAuthorization();
                }

                // Figure out how many submissions total we are exporting
                long[] subIds = searcher.submissionSearch(filter, SearchOrder.ID, SearchDirection.ASCENDING);
                meta.getProgress().total = subIds.length;
                meta.getProgress().completed = 0;

                // Start processing workbooks
                BufferedOutputStream bos = new BufferedOutputStream(out);
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet wbkSheet = workbook.createSheet(ExcelPackagerImpl.sheetName);

                // Iterate over all the items, adding each one to the export.
                int i = 1;
                for (long subId : subIds) {
                    Submission sub = subRepo.findSubmission(subId);
                    if (sub != null && packager instanceof AbstractExcelPackagerImpl) {
                        ExportExcel pkg = ((AbstractExcelPackagerImpl) packager).generateExcelPackage(sub, filter.getColumns());
                        XSSFSheet pkgSheet = pkg.getWorkbook().getSheet(ExcelPackagerImpl.sheetName);

                        // if this is the first submission, copy over its header (row 0)
                        if (i == 1) {
                            // create workbook heading row (0)
                            XSSFRow wbHeading = wbkSheet.createRow(0);
                            // get package heading row (0)
                            XSSFRow pkgHeading = pkgSheet.getRow(0);
                            // copy over row 0 to row 0
                            copyRow(pkgHeading, wbHeading);

                            // create workbook data row (1)
                            XSSFRow wbRow = wbkSheet.createRow(1);
                            // get package data row (1)
                            XSSFRow pkgRow = pkgSheet.getRow(1);
                            // copy over row 1 to row 1
                            copyRow(pkgRow, wbRow);
                        } else {
                            // create workbook data row (i)
                            XSSFRow wbRow = wbkSheet.createRow(i);
                            // get package data row (1)
                            XSSFRow pkgRow = pkgSheet.getRow(1);
                            // copy over row 1 to row i
                            copyRow(pkgRow, wbRow);
                        }
                        i++;
                    } else {
                        throw new Exception("Wrong packager type being used for this class or submission is null");
                    }

                    // Immediately save the transaction
                    JPA.em().getTransaction().commit();
                    JPA.em().clear();
                    JPA.em().getTransaction().begin();

                    // Don't let memory get out of control
                    System.gc();

                    meta.getProgress().completed++;
                }
                // write workbook to BufferedOutputStream
                workbook.write(bos);
            } catch (RuntimeException re) {
                Logger.fatal(re, "Unexepcted exception while exporting items. Aborted.");
                meta.setMessage(re.toString());
                meta.setStatus(JobStatus.FAILED);

                errorLog.logError(re, meta);

                throw re;

            } catch (IOException ioe) {
                Logger.error(ioe, "Unexpected expection while exporting items. Aborted.");
                meta.setMessage(ioe.toString());
                meta.setStatus(JobStatus.FAILED);

                errorLog.logError(ioe, meta);

                throw ioe;

            } finally {
                // Clean up the security context
                if (personId != null) {
                    context.logout();
                } else {
                    context.restoreAuthorization();
                }
            }
            meta.setStatus(JobStatus.SUCCESS);
            meta.setJob(null);
        }

        /**
         * Copies an {@link XSSFRow} source to an {@link XSSFRow} destination, {@link XSSFCell} by {@link XSSFCell}
         * 
         * @param source - the source row
         * @param dest - the destination row
         */
        private void copyRow(XSSFRow source, XSSFRow dest) {
            for (Iterator it = source.cellIterator(); it.hasNext();) {
                XSSFCell cell = (XSSFCell) it.next();
                XSSFCell wbCell = dest.createCell(cell.getColumnIndex());
                copyCell(cell, wbCell);
            }
        }

        /**
         * Copies an {@link XSSFCell}'s value to another {@link XSSFCell}'s value
         * 
         * @param source - the source cell
         * @param dest -  the destination cell
         */
        private void copyCell(XSSFCell source, XSSFCell dest) {
            switch (source.getCellType()) {
            case XSSFCell.CELL_TYPE_BLANK:
                dest.setCellValue(source.getRawValue());
                break;
            case XSSFCell.CELL_TYPE_BOOLEAN:
                dest.setCellValue(source.getBooleanCellValue());
                break;
            case XSSFCell.CELL_TYPE_ERROR:
                dest.setCellValue(source.getErrorCellValue());
                break;
            case XSSFCell.CELL_TYPE_FORMULA:
                dest.setCellValue(source.getCellFormula());
                break;
            case XSSFCell.CELL_TYPE_NUMERIC:
                dest.setCellValue(source.getNumericCellValue());
                break;
            case XSSFCell.CELL_TYPE_STRING:
                dest.setCellValue(source.getStringCellValue());
                break;
            }
        }
    }
}
