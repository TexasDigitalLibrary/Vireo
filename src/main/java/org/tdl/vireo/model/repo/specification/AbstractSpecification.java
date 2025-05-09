package org.tdl.vireo.model.repo.specification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public abstract class AbstractSpecification<E> implements Specification<E> {

    private static final long serialVersionUID = 415433340112106015L;

    protected Map<String, String[]> filters;

    public AbstractSpecification(Map<String, String[]> filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        PredicateBuilder builder = new PredicateBuilder();

        for (Map.Entry<String, String[]> entry : filters.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            for (String value : values) {
                builder.addPredicate(key, cb.like(cb.lower(root.get(key).as(String.class)), "%" + value.toLowerCase() + "%"));
            }
        }

        return builder.build(cb);
    }

    private class PredicateBuilder {

        private final Map<String, List<Predicate>> predicates;

        public PredicateBuilder() {
            this.predicates = new HashMap<String, List<Predicate>>();
        }

        public void addPredicate(String key, Predicate predicate) {
            List<Predicate> predicates = getPredicates(key);
            predicates.add(predicate);
            this.predicates.put(key, predicates);
        }

        public List<Predicate> getPredicates(String key) {
            Optional<List<Predicate>> potentialPredicates = Optional.ofNullable(this.predicates.get(key));
            return potentialPredicates.isPresent() ? potentialPredicates.get() : new ArrayList<Predicate>();
        }

        public Predicate build(CriteriaBuilder cb) {
            List<Predicate> columnPredicates = new ArrayList<Predicate>();
            for (List<Predicate> predicates : this.predicates.values()) {
                columnPredicates.add(cb.or(predicates.toArray(new Predicate[predicates.size()])));
            }
            return cb.and(columnPredicates.toArray(new Predicate[columnPredicates.size()]));
        }

    }

}
