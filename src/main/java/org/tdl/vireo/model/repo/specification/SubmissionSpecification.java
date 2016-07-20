package org.tdl.vireo.model.repo.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class SubmissionSpecification<Submission> implements Specification<Submission> {
	
    // TODO: add list of filters
    
    public SubmissionSpecification() {

    }
    
    @Override
    public Predicate toPredicate(Root<Submission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
    	
        // TODO: filter
        
        return null;
    }
}