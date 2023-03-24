package org.tdl.vireo.model.simple;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Immutable;
import org.tdl.vireo.model.FieldValue;

@Entity
@Immutable
@Table(name = "field_value")
public class SimpleFieldValue implements Serializable {

    @Transient
    private static final long serialVersionUID = -6043769326274667441L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private Long id;

    @Column(insertable = false, updatable = false, columnDefinition = "text", nullable = true)
    private String value;

    @Column(insertable = false, updatable = false, nullable = true)
    private String identifier;

    @Column(insertable = false, updatable = false, nullable = true)
    private String definition;

    @Transient
    private List<String> contacts;

    @Immutable
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private SimpleFieldPredicate fieldPredicate;

    public static FieldValue toFieldValue(SimpleFieldValue simpleFieldValue) {
        if (simpleFieldValue == null) {
            return null;
        }

        FieldValue fieldValue = new FieldValue();

        fieldValue.setId(simpleFieldValue.getId());
        fieldValue.setContacts(simpleFieldValue.getContacts());
        fieldValue.setDefinition(simpleFieldValue.getDefinition());
        fieldValue.setFieldPredicate(SimpleFieldPredicate.toFieldPredicate(simpleFieldValue.getFieldPredicate()));
        fieldValue.setIdentifier(simpleFieldValue.getIdentifier());
        fieldValue.setValue(simpleFieldValue.getValue());

        return fieldValue;
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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    public SimpleFieldPredicate getFieldPredicate() {
        return fieldPredicate;
    }

    public void setFieldPredicate(SimpleFieldPredicate fieldPredicate) {
        this.fieldPredicate = fieldPredicate;
    }

}
