package org.tdl.vireo.model.simple;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Immutable;
import org.tdl.vireo.model.FieldPredicate;

@Entity
@Immutable
@Table(name = "field_predicate")
public class SimpleFieldPredicate implements Serializable {

    @Transient
    private static final long serialVersionUID = 7752694484720316614L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private Long id;

    @Column(insertable = false, updatable = false, nullable = false, unique = true)
    private String value;

    @Column(insertable = false, updatable = false, nullable = false, unique = false)
    private Boolean documentTypePredicate;

    public static FieldPredicate toFieldPredicate(SimpleFieldPredicate simpleFieldPredicate) {
        if (simpleFieldPredicate == null) {
            return null;
        }

        FieldPredicate fieldPredicate = new FieldPredicate();

        fieldPredicate.setId(simpleFieldPredicate.getId());
        fieldPredicate.setValue(simpleFieldPredicate.getValue());
        fieldPredicate.setDocumentTypePredicate(simpleFieldPredicate.getDocumentTypePredicate());

        return fieldPredicate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getDocumentTypePredicate() {
        return documentTypePredicate;
    }

    public void setDocumentTypePredicate(Boolean documentTypePredicate) {
        this.documentTypePredicate = documentTypePredicate;
    }

}
