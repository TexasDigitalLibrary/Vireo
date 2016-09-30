package org.tdl.vireo.model.repo.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    private Set<String> allColumnSearchFilters;

    private List<SubmissionListColumn> allSubmissionListColumns;

    public SubmissionSpecification(List<SubmissionListColumn> allSubmissionListColumns, Set<String> allColumnSearchFilters) {
        this.allSubmissionListColumns = allSubmissionListColumns;
        this.allColumnSearchFilters = allColumnSearchFilters;
    }

    @Override
    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        Predicate predicate = null;

        List<Order> _orders = new ArrayList<Order>();

        List<Expression<?>> _groupings = new ArrayList<Expression<?>>();

        List<Predicate> _groupPredicates = new ArrayList<Predicate>();

        List<Predicate> _filterPredicates = new ArrayList<Predicate>();

        SetJoin<Submission, FieldValue> fieldValueJoinSet = null;

        _groupings.add(root.get("id"));

        for (SubmissionListColumn submissionListColumn : allSubmissionListColumns) {
            Path<?> path = null;
            if (submissionListColumn.getValuePath().size() > 0) {
                if (submissionListColumn.getPredicate() != null) {
                    if (submissionListColumn.getSortOrder() > 0 || submissionListColumn.getFilters().size() > 0 || submissionListColumn.getVisible()) {
                        SetJoin<Submission, FieldValue> join = root.joinSet("fieldValues", JoinType.LEFT);
                        path = join.get("value");

                        Path<?> predicatePath = join.get("fieldPredicate").get("value");
                        _groupings.add(predicatePath);

                        _groupPredicates.add(cb.equal(predicatePath, submissionListColumn.getPredicate()));

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
                    _filterPredicates.add(cb.like(path.as(String.class), "%" + filter + "%"));
                }

                for (String filter : allColumnSearchFilters) {
                    if (submissionListColumn.getPredicate() != null) {
                        if (fieldValueJoinSet == null) {
                            fieldValueJoinSet = root.joinSet("fieldValues");
                        }
                        _filterPredicates.add(cb.like(fieldValueJoinSet.get("value").as(String.class), "%" + filter + "%"));
                    } else {
                        _filterPredicates.add(cb.like(path.as(String.class), "%" + filter + "%"));
                    }
                }

                switch (submissionListColumn.getSort()) {
                case ASC:
                    _orders.add(cb.asc(path));
                    break;
                case DESC:
                    _orders.add(cb.desc(path));
                    break;
                default:
                    break;
                }
            }
        }

        if (_filterPredicates.size() == 0) {
            predicate = cb.and(cb.and(_groupPredicates.toArray(new Predicate[_groupPredicates.size()])));
        } else {
            predicate = cb.and(cb.and(_groupPredicates.toArray(new Predicate[_groupPredicates.size()])), cb.or(_filterPredicates.toArray(new Predicate[_filterPredicates.size()])));
        }

        query.groupBy(_groupings).orderBy(_orders);

        return predicate;
    }

}
