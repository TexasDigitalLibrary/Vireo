package org.tdl.vireo.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderedEntityService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final Integer one = new Integer(1);

	@PersistenceContext
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	private void swap(Class<?> clazz, Integer here, Integer there) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();		
        CriteriaUpdate<Object> update = (CriteriaUpdate<Object>) cb.createCriteriaUpdate(clazz);
		Root<?> e = update.from((Class<Object>) clazz);
		Path<Integer> path = e.<Integer> get("order");
		update.set(path, there);
		update.where(cb.equal(e.get("order"), here));
		entityManager.createQuery(update).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	private void delete(Class<?> clazz, Integer order) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaDelete<Object> delete = (CriteriaDelete<Object>) cb.createCriteriaDelete(clazz);
		Root<?> e = delete.from((Class<Object>) clazz);
		delete.where(cb.equal(e.get("order"), order));
		entityManager.createQuery(delete).executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public Object findByOrder(Class<?> clazz, Integer order) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery();
        Root<?> e = query.from((Class<Object>) clazz);
        Path<Integer> path = e.get("order");
        query.select(path).distinct(true);
        query.where(cb.equal(path, order));
        return entityManager.createQuery(query).getSingleResult();
    }

	@SuppressWarnings("unchecked")
	public synchronized void reorder(Class<?> clazz, Integer src, Integer dest) {
		swap(clazz, src, Integer.MAX_VALUE);
		// increment/decrement order as necessary
		{
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaUpdate<Object> update = (CriteriaUpdate<Object>) cb.createCriteriaUpdate(clazz);
			Root<?> e = update.from((Class<Object>) clazz);
			Path<Integer> path = e.get("order");
			if (src > dest) {
				update.set(path, cb.sum(path, one));
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(cb.greaterThanOrEqualTo(path, dest));
				predicates.add(cb.lessThan(path, src));
				update.where(predicates.toArray(new Predicate[] {}));
				entityManager.createQuery(update).executeUpdate();
			} else if (src < dest) {
				update.set(path, cb.sum(path, -one));
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(cb.greaterThan(path, src));
				predicates.add(cb.lessThanOrEqualTo(path, dest));
				update.where(predicates.toArray(new Predicate[] {}));
				entityManager.createQuery(update).executeUpdate();
			} else {
				// do nothing
			}
		}
		swap(clazz, Integer.MAX_VALUE, dest);
	}
	
	@SuppressWarnings("unchecked")
	private void setOrder(Class<?> clazz, String property, Object propValue, Integer newOrder) {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Object> update = (CriteriaUpdate<Object>) cb.createCriteriaUpdate(clazz);
        Root<?> e = update.from((Class<Object>) clazz);
        update.set(e.get("order"), newOrder);
        update.where(cb.equal(e.get(property), propValue));
        entityManager.createQuery(update).executeUpdate();
	}
	
    public synchronized void sort(Class<?> clazz, String property){
	    this.sort(clazz, property, null, null);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void sort(Class<?> clazz, String property, String whereProp, String whereVal) {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery();
        Root<?> e = query.from((Class<Object>) clazz);
        query.multiselect(e.get(property));
        if(whereProp != null && whereVal != null) {
            Path<Integer> path = e.get(whereProp);
            query.where(cb.equal(path, whereVal));
        }
        query.orderBy(cb.asc(e.get(property)));
        List<Object> orderedResults = entityManager.createQuery(query).getResultList();
        for(int i = 0; i < orderedResults.size(); i++) {
            setOrder(clazz, property, orderedResults.get(i), i + 1);            
        }
	}

	@SuppressWarnings("unchecked")
	public synchronized void remove(Class<?> clazz, Integer order) {
		delete(clazz, order);
		{
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaUpdate<Object> update = (CriteriaUpdate<Object>) cb.createCriteriaUpdate(clazz);
			Root<?> e = update.from((Class<Object>) clazz);
			Path<Integer> path = e.get("order");
			update.set(path, cb.sum(path, -one));
			update.where(cb.greaterThan(path, order));
			entityManager.createQuery(update).executeUpdate();
		}
	}

}
