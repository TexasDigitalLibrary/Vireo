package org.tdl.vireo.controller.model;

import javax.persistence.Entity;

import org.hibernate.validator.constraints.NotEmpty;
import org.tdl.vireo.aspect.AppControllerAspect;

import edu.tamu.framework.model.BaseEntity;
import edu.tamu.framework.model.ValidatingBase;

/**
 * Model used to validate deserialized {@link LookAndFeelController} objects coming from the front-end
 * 
 * Not persisted as an {@link Entity}, used as transient model -- needs to extend {@link BaseEntity} because of {@link AppControllerAspect} casting during validation
 * 
 * @author gad
 *
 */
public class LookAndFeelControllerModel extends ValidatingBase {

    @NotEmpty
    private String setting;

    private String fileType;

    public LookAndFeelControllerModel() { }

    /**
     * @return the setting
     */
    public String getSetting() {
        return setting;
    }

    /**
     * @param setting
     *            the setting to set
     */
    public void setSetting(String setting) {
        this.setting = setting;
    }

    /**
     * @return the fileType
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * @param fileType
     *            the fileType to set
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
