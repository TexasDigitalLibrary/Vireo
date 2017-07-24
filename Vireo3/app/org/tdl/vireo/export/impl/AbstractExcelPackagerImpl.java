package org.tdl.vireo.export.impl;

import java.util.List;

import org.tdl.vireo.export.ExportExcel;
import org.tdl.vireo.export.ExportPackage;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.search.SearchOrder;

/**
 * Abstract packager implementation.
 * 
 * Specifically used when using the {@link ExportExcel} package type
 * Extends {@link AbstractPackagerImpl}
 * 
 * @author <a href=mailto:gad.krumholz@austin.utexas.edu>Gad Krumholz</a>
 */
public abstract class AbstractExcelPackagerImpl extends AbstractPackagerImpl {
    
	/**
	 * Generates an {@link ExportExcel} object for {@link ExportExcelServiceImpl}
	 * @param submission - The ETD submission to export
	 * @param columns - The columns to export into the Excel workbook
	 * @return - {@link ExportExcel} object
	 * @throws Exception - if generateExcelPackage was not Overriden
	 */
    public ExportExcel generateExcelPackage(Submission submission, List<SearchOrder> columns) throws Exception {
        throw new Exception("generateExcelPackage needs to be overriden!");
    }
}
