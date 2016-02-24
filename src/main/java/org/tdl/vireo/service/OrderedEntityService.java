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

import org.springframework.stereotype.Service;

@Service
public class OrderedEntityService {
	
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
	public void reorder(Class<?> clazz, Integer from, Integer to) {		
		swap(clazz, from, Integer.MAX_VALUE);
		// increment/decrement order as necessary
		{
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaUpdate<Object> update = (CriteriaUpdate<Object>) cb.createCriteriaUpdate(clazz);
			Root<?> e = update.from((Class<Object>) clazz);
			Path<Integer> path = e.get("order");
			if (from > to) {
				update.set(path, cb.sum(path, one));
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(cb.greaterThanOrEqualTo(path, to));
				predicates.add(cb.lessThan(path, from));
				update.where(predicates.toArray(new Predicate[] {}));
				entityManager.createQuery(update).executeUpdate();
			} else if (from < to) {
				update.set(path, cb.sum(path, -one));
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(cb.greaterThan(path, from));
				predicates.add(cb.lessThanOrEqualTo(path, to));
				update.where(predicates.toArray(new Predicate[] {}));
				entityManager.createQuery(update).executeUpdate();
			} else {
				// do nothing
			}
		}
		swap(clazz, Integer.MAX_VALUE, to);
	}

	@SuppressWarnings("unchecked")
	public void remove(Class<?> clazz, Integer order) {
		delete(clazz, order);
		// decrement order after order...doh!
		//TODO: refactor to remove ambiguity between intended value order and column order
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
