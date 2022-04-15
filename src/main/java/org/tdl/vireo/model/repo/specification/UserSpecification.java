package org.tdl.vireo.model.repo.specification;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class UserSpecification<E> extends AbstractSpecification<E> {

    private static final long serialVersionUID = 6697995180045425460L;

    public UserSpecification(Map<String, String[]> filters) {
        super(filters);
    }

    @Override
    protected void toPredicateDefaultQueryOrderBy(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        query.orderBy(cb.desc(root.get("id")));
    }
}
