package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.validation.AttachmentTypeValidator;

import edu.tamu.framework.model.BaseOrderedEntity;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint( columnNames = { "name", "field_predicate_id" } ) } )
public class AttachmentType extends BaseOrderedEntity {


    @Column(nullable = false, length = 255)
    private String name;

    @OneToOne(cascade = DETACH, orphanRemoval = true, optional = false)
    private FieldPredicate fieldPredicate;

    public AttachmentType() {
        setModelValidator(new AttachmentTypeValidator());
    }

    /**
     * Create a new JpaDocumentTypeImpl
     *
     * @param name
     *            The name of the new document type.
     */
    public AttachmentType(String name) {
        this();
        setName(name);
    }

    public AttachmentType(String name, FieldPredicate fieldPredicate) {
        this(name);
        setFieldPredicate(fieldPredicate);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the fieldPredicate
     */
    public FieldPredicate getFieldPredicate() {
        return fieldPredicate;
    }

    /**
     * @param fieldPredicate the predicate to set
     */
    public void setFieldPredicate(FieldPredicate fieldPredicate) {
        this.fieldPredicate = fieldPredicate;
    }
}
