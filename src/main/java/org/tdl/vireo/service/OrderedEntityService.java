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
import org.tdl.vireo.enums.EmbargoGuarantor;
import org.tdl.vireo.model.BaseOrderedEntity;
import org.tdl.vireo.model.Embargo;

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

    /**
     * TODO: THIS NEEDS TO findbyid not findbyorder!
     * @param clazz
     * @param src
     * @param dest
     */
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
                boe.setOrder(i + 1); // i+1 because i starts at 0, order positions start at 1
                entityManager.persist(boe); // persist the new order
            } else {
                String err = "Could not sort [" + clazz.getName() + "]! It doesn't extend " + BaseOrderedEntity.class.getName() + "!";
                logger.error(err);
                break;
            }
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
