package org.tdl.vireo.model.repo.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.tdl.vireo.model.SubmissionListColumn;

public class SubmissionSpecification<E> implements Specification<E> {
    
    List<SubmissionListColumn> submissionListColums;
    
    public SubmissionSpecification(List<SubmissionListColumn> submissionListColums) {
        this.submissionListColums = submissionListColums;
    }
    
    @Override
    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        
        List<Order> _orders = new ArrayList<Order>();
        
        List<Expression<?>> _groupings = new ArrayList<Expression<?>>();

        Predicate combinedPrediacte = null;
        
        Join<?, ?> join = root.join("fieldValues", JoinType.LEFT);

        for (SubmissionListColumn submissionListColumn : submissionListColums) {
            Path<?> path = null;

            if (submissionListColumn.getValuePath().size() > 0) {

                if (submissionListColumn.getPredicate() != null) {

                    path = join.get("value");

                    Path<?> predicatePath = join.get("fieldPredicate").get("value");

                    combinedPrediacte = cb.and(cb.equal(predicatePath, submissionListColumn.getPredicate()));

                    if (submissionListColumn.getSortOrder() > 0) {
                        _groupings.add(predicatePath);
                    }
                } else {

                    for (String property : submissionListColumn.getValuePath()) {
                        if (path == null) {
                            path = root.get(property);
                        } else {
                            path = path.get(property);
                        }
                    }

                    combinedPrediacte = cb.and(cb.isNotNull(path));
                }

                for (String filter : submissionListColumn.getFilters()) {
                    combinedPrediacte = cb.and(cb.equal(path, filter));
                }

                switch (submissionListColumn.getSort()) {
                    case ASC: _orders.add(cb.asc(path)); break;
                    case DESC: _orders.add(cb.desc(path)); break;
                    default: break;
                }

            }
        }

        _groupings.add(root.get("id"));

        query.groupBy(_groupings).orderBy(_orders);
        
        return combinedPrediacte;
    }
}