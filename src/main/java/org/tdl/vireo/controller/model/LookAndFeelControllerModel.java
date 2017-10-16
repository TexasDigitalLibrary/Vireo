package org.tdl.vireo.controller.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.tdl.vireo.controller.LookAndFeelController;

/**
 * Model used to validate deserialized {@link LookAndFeelController} objects coming from the front-end
 *
 * @author gad
 *
 */
public class LookAndFeelControllerModel {

    @NotEmpty
    private String setting;

    private String fileType;

    public LookAndFeelControllerModel() {

    }

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
