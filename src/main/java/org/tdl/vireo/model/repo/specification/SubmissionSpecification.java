package org.tdl.vireo.model.repo.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.tdl.vireo.model.SubmissionListColumn;

public class SubmissionSpecification<Submission> implements Specification<Submission> {
	
    List<SubmissionListColumn> submissionListColums;
    
    public SubmissionSpecification(List<SubmissionListColumn> submissionListColums) {
        this.submissionListColums = submissionListColums;
    }
    
    @Override
    public Predicate toPredicate(Root<Submission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
    	
        List<Predicate> filterPredicates = new ArrayList<Predicate>();
        
        for(SubmissionListColumn submissionListColumn : submissionListColums) {
            for(String filter : submissionListColumn.getFilters()) {
                
                System.out.println(filter);
                
                if(submissionListColumn.getValuePath().size() > 0) {
                    Path<Object> path = null;
                    for(String property : submissionListColumn.getValuePath()) {
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