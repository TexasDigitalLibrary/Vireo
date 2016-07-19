package org.tdl.vireo.model;

import org.tdl.vireo.enums.Sort;

public class SubmissionViewColumn {
    
    private String label;
    
    private Sort sort;
    
    private String[] path;
    
    public SubmissionViewColumn(String label, Sort sort, String... path) {
        this.label = label;
        this.sort = sort;
        this.path = path;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the sort
     */
    public Sort getSort() {
        return sort;
    }

    /**
     * @param sort the sort to set
     */
    public void setSort(Sort sort) {
        this.sort = sort;
    }

    /**
     * @return the path
     */
    public String[] getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String[] path) {
        this.path = path;
    }
    
}
