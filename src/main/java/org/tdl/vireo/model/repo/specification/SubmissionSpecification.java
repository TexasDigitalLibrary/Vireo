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
    @SuppressWarnings("unchecked")
    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> _predicates = new ArrayList<Predicate>();

        List<Order> _orders = new ArrayList<Order>();

        List<Expression<?>> _groupings = new ArrayList<Expression<?>>();

        _groupings.add(root.get("id"));

        int i = 0;

        for (SubmissionListColumn submissionListColumn : submissionListColums) {
            Path<?> path = null;
            if (submissionListColumn.getValuePath().size() > 0) {
                if (submissionListColumn.getPredicate() != null) {
                    if (submissionListColumn.getSortOrder() > 0 || submissionListColumn.getFilters().size() > 0) {
                        Join<?, ?> join = (Join<?, ?>) root.join("fieldValues", JoinType.LEFT).alias("fv" + i);
                        path = join.get("value");
                        Path<?> predicatePath = join.get("fieldPredicate").get("value");
                        _groupings.add(predicatePath);
                        _predicates.add(cb.equal(predicatePath, submissionListColumn.getPredicate()));
                        i++;
                    }
                } else {
                    for (String property : submissionListColumn.getValuePath()) {
                        if (path == null) {
                            path = root.get(property);
                        } else {
                            path = path.get(property);
                        }
                    }
                }

                for (String filter : submissionListColumn.getFilters()) {
                    _predicates.add(cb.like((Expression<String>) path, "%" + filter + "%"));
                }

                switch (submissionListColumn.getSort()) {
                    case ASC: _orders.add(cb.asc(path)); break;
                    case DESC: _orders.add(cb.desc(path)); break;
                    default:  break;
                }

            }
        }

        query.groupBy(_groupings).orderBy(_orders);

        return cb.and(_predicates.toArray(new Predicate[_predicates.size()]));
    }
}