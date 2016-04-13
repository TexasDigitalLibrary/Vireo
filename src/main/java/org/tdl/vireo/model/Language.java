package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class Language extends BaseOrderedEntity {

    @Column(unique = true, nullable = false)
    @NotEmpty
    private String name;
    
    public Language() { }
    
    /**
     * 
     * @param name
     */
    public Language(String name) {
        this.name = name;
    }
    
    /**
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
}
