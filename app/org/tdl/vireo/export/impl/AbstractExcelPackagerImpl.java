package org.tdl.vireo.export.impl;

import org.tdl.vireo.export.ExportExcel;
import org.tdl.vireo.export.ExportPackage;
import org.tdl.vireo.model.Submission;

/**
 * Abstract packager implementation.
 * 
 * Specifically used when using the {@link ExportExcel} package type
 * Extends {@link AbstractPackagerImpl}
 * 
 * @author Gad Krumholz ( gad.krumholz@austin.utexas.edu )
 */
public abstract class AbstractExcelPackagerImpl extends AbstractPackagerImpl {
    
    public ExportExcel generateExcelPackage(Submission submission) throws Exception {
        throw new Exception("generateExcelPackage needs to be overriden!");
    }
}
