package org.tdl.vireo.model.repo.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;

import org.springframework.data.jpa.domain.Specification;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionListColumn;

public class SubmissionSpecification<E> implements Specification<E> {

    List<SubmissionListColumn> submissionListColums;

    public SubmissionSpecification(List<SubmissionListColumn> submissionListColums) {
        this.submissionListColums = submissionListColums;
    }
    
    // TODO: determine how to handle date range on filter

    @Override
    @SuppressWarnings("unchecked")
    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Order> _orders = new ArrayList<Order>();
        
        List<Predicate> _groupPredicates = new ArrayList<Predicate>();

        List<Predicate> _columnFilterPredicates = new ArrayList<Predicate>();

        List<Expression<?>> _groupings = new ArrayList<Expression<?>>();

        _groupings.add(root.get("id"));

        int i = 0;

        for (SubmissionListColumn submissionListColumn : submissionListColums) {
            Path<?> path = null;
            if (submissionListColumn.getValuePath().size() > 0) {
                if (submissionListColumn.getPredicate() != null) {
                    if (submissionListColumn.getSortOrder() > 0 || submissionListColumn.getFilters().size() > 0) {
                        SetJoin<Submission, FieldValue> join = root.joinSet("fieldValues", JoinType.LEFT);
                        join.alias("fv" + i);
                        path = join.get("value");
                        Path<?> predicatePath = join.get("fieldPredicate").get("value");
                        _groupings.add(predicatePath);
                        _groupPredicates.add(cb.equal(predicatePath, submissionListColumn.getPredicate()));
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
                    _columnFilterPredicates.add(cb.like((Expression<String>) path, "%" + filter + "%"));
                }

                switch (submissionListColumn.getSort()) {
                    case ASC: _orders.add(cb.asc(path)); break;
                    case DESC: _orders.add(cb.desc(path)); break;
                    default:  break;
                }
            }
        }

        query.groupBy(_groupings).orderBy(_orders);
        
        Predicate returnPredicate = null;
        
        if(_columnFilterPredicates.size() == 0) {
            returnPredicate = cb.and(_groupPredicates.toArray(new Predicate[_groupPredicates.size()]));
        }
        else {
            returnPredicate = cb.and(cb.and(_groupPredicates.toArray(new Predicate[_groupPredicates.size()])), cb.or(_columnFilterPredicates.toArray(new Predicate[_columnFilterPredicates.size()])));
        }
        
        return returnPredicate;
    }
}