package org.tdl.vireo.controller.model;

import javax.persistence.Entity;

import org.hibernate.validator.constraints.NotEmpty;
import org.tdl.vireo.aspect.AppControllerAspect;
import org.tdl.vireo.controller.UserController;
import org.tdl.vireo.model.BaseEntity;

/**
 * Model used to validate deserialized {@link UserController} objects coming from the front-end
 * 
 * Not persisted as an {@link Entity}, used as transient model -- needs to extend {@link BaseEntity} because of {@link AppControllerAspect} casting during validation
 * 
 * @author gad
 *
 */
public class UserControllerModel extends BaseEntity {

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