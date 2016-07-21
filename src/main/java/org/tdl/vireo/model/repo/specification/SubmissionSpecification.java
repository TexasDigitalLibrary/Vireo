package org.tdl.vireo.model.repo.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.tdl.vireo.model.SubmissionViewColumn;

public class SubmissionSpecification<Submission> implements Specification<Submission> {
	
    List<SubmissionViewColumn> submissionViewColums;
    
    public SubmissionSpecification(List<SubmissionViewColumn> submissionViewColums) {
        this.submissionViewColums = submissionViewColums;
    }
    
    @Override
    public Predicate toPredicate(Root<Submission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
    	
        List<Predicate> filterPredicates = new ArrayList<Predicate>();
        
        for(SubmissionViewColumn submissionViewColumn : submissionViewColums) {
            for(String filter : submissionViewColumn.getFilters()) {
                
                System.out.println(filter);
                
                if(submissionViewColumn.getPath().size() > 0) {
                    Path<Object> path = null;
                    for(String property : submissionViewColumn.getPath()) {
                        if(path == null) {
                            path = root.get(property);
                        }
                        else {
                            path = path.get(property);
                        }
                    }
                    
                    filterPredicates.add(cb.equal(path, filter));                    
                }
                
            }
        }
        
        return cb.and((Predicate[]) filterPredicates.toArray());
    }
}