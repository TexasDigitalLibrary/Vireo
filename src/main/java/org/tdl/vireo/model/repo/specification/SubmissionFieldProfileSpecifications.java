package org.tdl.vireo.model.repo.specification;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.SubmissionFieldProfile;

import edu.tamu.weaver.data.model.BaseEntity;
import edu.tamu.weaver.validation.validators.BaseModelValidator;

public class SubmissionFieldProfileSpecifications {

    public static Specification<SubmissionFieldProfile> existing(FieldProfile fieldProfile) {
        return new Specification<SubmissionFieldProfile>() {

            private static final long serialVersionUID = -1220545498320954688L;

            private final static String ID_NAME = "id";

            @Override
            public Predicate toPredicate(Root<SubmissionFieldProfile> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                List<Field> fields = getAllFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (include(field)) {
                        try {
                            Class<?> type = field.getType();
                            String name = field.getName();
                            Object value = field.get(fieldProfile);
                            if (value == null) {
                                predicates.add(cb.isNull(root.get(name)));
                            } else {
                                if (isEntity(type)) {
                                    predicates.add(cb.equal(root.join(name).get(ID_NAME).as(Long.class), ((BaseEntity) value).getId()));
                                } else {
                                    predicates.add(cb.equal(root.get(name).as(type), type.cast(value)));
                                }
                            }
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }

            private List<Field> getAllFields() {
                return getAllFields(SubmissionFieldProfile.class);
            }

            private List<Field> getAllFields(Class<?> type) {
                List<Field> fields = new ArrayList<Field>();
                for (Class<?> c = type; c != null; c = c.getSuperclass()) {
                    fields.addAll(Arrays.asList(c.getDeclaredFields()));
                }
                return fields;
            }

            private boolean include(Field field) {
                return isNotId(field) && isNotValidator(field.getType()) && isNotClassCollection(field.getType());
            }

            private boolean isNotId(Field field) {
                return field.getAnnotation(Id.class) == null;
            }

            private boolean isNotValidator(Class<?> c) {
                // TODO: figure out why this has to be reversed!
                return !c.isAssignableFrom(BaseModelValidator.class);
            }

            private boolean isNotClassCollection(Class<?> c) {
                return !(Collection.class.isAssignableFrom(c) || Map.class.isAssignableFrom(c));
            }

            private boolean isEntity(Class<?> c) {
                return BaseEntity.class.isAssignableFrom(c);
            }

        };
    }

}
