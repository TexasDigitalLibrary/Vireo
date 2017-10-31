package org.tdl.vireo.controller.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.tdl.vireo.controller.UserController;

/**
 * Model used to validate deserialized {@link UserController} objects coming from the front-end
 *
 * @author gad
 *
 */
public class UserControllerModel {

    @NotEmpty
    private String settingValue;

    public UserControllerModel() {
    }

    /**
     * @return the settingValue
     */
    public String getSettingValue() {
        return settingValue;
    }

    /**
     * @param settingValue
     *            the settingValue to set
     */
    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

}
