package org.tdl.vireo.model.repo.specification;

import java.util.Map;

public class UserSpecification<E> extends AbstractSpecification<E> {

    private static final long serialVersionUID = 6697995180045425460L;

    public UserSpecification(Map<String, String[]> filters) {
        super(filters);
    }

}
