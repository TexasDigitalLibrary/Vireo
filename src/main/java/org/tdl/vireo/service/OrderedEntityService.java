package org.tdl.vireo.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.repository.JpaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tdl.vireo.enums.EmbargoGuarantor;
import org.tdl.vireo.model.BaseOrderedEntity;
import org.tdl.vireo.model.Embargo;

@Service
public class OrderedEntityService {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass()); 
    
    private static final String POSITION_COLUMN_NAME = "position";

    private static final Long one = new Long(1);

    @PersistenceContext
    private EntityManager entityManager;
    
    @SuppressWarnings("unchecked")
    private void swap(Class<?> clazz, Long here, Long there, String whereProp, Object whereVal) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Object> update = (CriteriaUpdate<Object>) cb.createCriteriaUpdate(clazz);
        Root<?> e = update.from((Class<Object>) clazz);
        Path<Long> path = e.get(POSITION_COLUMN_NAME);
        update.set(path, there);
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(e.get(POSITION_COLUMN_NAME), here));
        if (whereProp != null && whereVal != null) {
            predicates.add(cb.equal(e.get(whereProp), whereVal));
        }
        update.where(predicates.toArray(new Predicate[] {}));
        entityManager.createQuery(update).executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public Object findByPosition(Class<?> clazz, Long position) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery();
        Root<?> e = query.from((Class<Object>) clazz);
        query.select(e).distinct(true);
        query.where(cb.equal(e.get(POSITION_COLUMN_NAME), position));
        return entityManager.createQuery(query).getSingleResult();
    }

    public synchronized void reorder(Class<?> clazz, Long src, Long dest) {
        this.reorder(clazz, src, dest, null, null);
    }
    
    /**
     * 
     * @param clazz
     * @param src
     * @param dest
     */
    @SuppressWarnings("unchecked")
    public synchronized void reorder(Class<?> clazz, Long src, Long dest, String whereProp, Object whereVal) {

        swap(clazz, src, Long.MAX_VALUE, whereProp, whereVal);
        // increment/decrement position as necessary
        {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaUpdate<Object> update = (CriteriaUpdate<Object>) cb.createCriteriaUpdate(clazz);
            Root<?> e = update.from((Class<Object>) clazz);
            Path<Long> path = e.get(POSITION_COLUMN_NAME);
            if (src > dest) {
                update.set(path, cb.sum(path, one));
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(cb.greaterThanOrEqualTo(path, dest));
                predicates.add(cb.lessThan(path, src));
                if (whereProp != null && whereVal != null) {
                    predicates.add(cb.equal(e.get(whereProp), whereVal));
                }
                update.where(predicates.toArray(new Predicate[] {}));
                entityManager.createQuery(update).executeUpdate();
            } else if (src < dest) {
                update.set(path, cb.sum(path, -one));
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(cb.greaterThan(path, src));
                predicates.add(cb.lessThanOrEqualTo(path, dest));
                if (whereProp != null && whereVal != null) {
                    predicates.add(cb.equal(e.get(whereProp), whereVal));
                }
                update.where(predicates.toArray(new Predicate[] {}));
                entityManager.createQuery(update).executeUpdate();
            } else {
                // do nothing
            }
        }
        swap(clazz, Long.MAX_VALUE, dest, whereProp, whereVal);

        entityManager.clear();
        entityManager.flush();
    }

    public synchronized void sort(Class<?> clazz, String property) {
        this.sort(clazz, property, null, null);
    }

    /**
     * SELECT * FROM ${clazz} WHERE ${whereProp} = ${whereVal} ORDER BY ${property} ASC
     * 
     * for every row(boe) that is of {@link BaseOrderedEntity}:
     * 
     * boe.setOrder(i+1) -- where i is the index of the position in the result-list from the SELECT statement above as we iterate through that list.
     * 
     * @param clazz
     *            -- the entity class
     * @param property
     *            -- the property to order by (ex. "duration" for {@link Embargo})
     * @param whereProp
     *            -- the property to filter by (ex. "guarantor" for {@link Embargo})
     * @param whereVal
     *            -- the property value to filter by (ex. {@link EmbargoGuarantor}.DEFAULT for guarantor for {@link Embargo})
     */
    @SuppressWarnings("unchecked")
    public synchronized void sort(Class<?> clazz, String property, String whereProp, Object whereVal) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> cq = cb.createQuery();
        Root<?> e = cq.from((Class<Object>) clazz);
        cq.multiselect(e); // select the whole object, so we can get a BasedOrderedEntity-capable cast later TODO: maybe we only need id and order?
        if (whereProp != null && whereVal != null) {
            cq.where(cb.equal(e.get(whereProp), whereVal));
        }
        cq.orderBy(cb.asc(e.get(property)));
        List<Object> orderedResults = entityManager.createQuery(cq).getResultList();
        for (int i = 0; i < orderedResults.size(); i++) {
            if (orderedResults.get(i) instanceof BaseOrderedEntity) {
                BaseOrderedEntity boe = (BaseOrderedEntity) orderedResults.get(i); //cast it safely
                boe.setPosition((long) i + 1); // i+1 because i starts at 0, order positions start at 1
                entityManager.persist(boe); // persist the new order
            } else {
                String err = "Could not sort [" + clazz.getName() + "]! It doesn't extend " + BaseOrderedEntity.class.getName() + "!";
                logger.error(err);
                break;
            }
        }
    }
    
    public synchronized void remove(Object repo, Class<?> clazz, Long position) {
        this.remove(repo, clazz, position, null, null);
    }
    
    @SuppressWarnings("unchecked")
    public synchronized void remove(Object repo, Class<?> clazz, Long position, String whereProp, Object whereVal) {
        Long id = ((BaseOrderedEntity) findByPosition(clazz, position)).getId(); 
        ((JpaRepository<Object, Long>) repo).delete(id);
        {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaUpdate<Object> update = (CriteriaUpdate<Object>) cb.createCriteriaUpdate(clazz);
            Root<?> e = update.from((Class<Object>) clazz);
            Path<Long> path = e.get(POSITION_COLUMN_NAME);
            update.set(path, cb.sum(path, -one));
            List<Predicate> predicates = new ArrayList<Predicate>();
            predicates.add(cb.greaterThan(path, position));
            if (whereProp != null && whereVal != null) {
                predicates.add(cb.equal(e.get(whereProp), whereVal));
            }
            update.where(predicates.toArray(new Predicate[] {}));
            entityManager.createQuery(update).executeUpdate();
        }
    }
}
