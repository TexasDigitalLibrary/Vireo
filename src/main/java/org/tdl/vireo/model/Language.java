package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Language extends BaseOrderedEntity {

    @Column(unique = true, nullable = false)
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
     * @param name
     */
    public Language(String name, Integer order) {
        setName(name);
        setOrder(order);
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
