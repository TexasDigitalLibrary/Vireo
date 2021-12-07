package org.tdl.vireo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface HibernateWorkaround {

    @JsonIgnore
    public default String getHandler() {
        return null;
    }

    @JsonIgnore
    public default String getHibernateLazyInitializer() {
        return null;
    }

}
