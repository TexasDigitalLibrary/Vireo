package org.tdl.vireo.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import edu.tamu.weaver.context.SpringContext;
import edu.tamu.weaver.validation.model.ValidatingOrderedBaseEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.tdl.vireo.model.response.Views;
import org.tdl.vireo.model.validation.ControlledVocabularyValidator;
import org.tdl.vireo.service.EntityControlledVocabularyService;

@Entity
@Configurable
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ControlledVocabulary extends ValidatingOrderedBaseEntity {

    final static Logger logger = LoggerFactory.getLogger(ControlledVocabulary.class);

    @JsonView(Views.SubmissionIndividual.class)
    @Column(nullable = false, unique = true)
    private String name;

    @JsonView(Views.SubmissionIndividual.class)
    @OneToMany(cascade = { ALL }, fetch = LAZY, mappedBy = "controlledVocabulary", orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size=1000)
    @OrderBy("controlledVocabulary ASC, name ASC")
    private List<VocabularyWord> dictionary;

    @JsonView(Views.SubmissionIndividual.class)
    @Column(nullable = false)
    private Boolean isEntityProperty;

    public ControlledVocabulary() {
        setModelValidator(new ControlledVocabularyValidator());
        setIsEntityProperty(false);
        setDictionary(new ArrayList<VocabularyWord>());
    }

    public ControlledVocabulary(String name) {
        this();
        setName(name);
    }

    public ControlledVocabulary(String name, Boolean isEntityProperty) {
        this(name);
        setIsEntityProperty(isEntityProperty);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns either a set of vocabulary words of the controlled vocabulary or a set composed of a unique list of an entities property. 
     * This is done lazily by requesting the EntityControlledVocabularyService bean through a static method of SpringContext. From the bean, 
     * calling the getControlledVocabulary method providing the entityName and name of the controlled vocabulary. This name is also the 
     * property name of the entity.
     *
     * @return the values
     */
    public List<VocabularyWord> getDictionary() {
        List<VocabularyWord> values = new ArrayList<VocabularyWord>();
        if (!getIsEntityProperty()) {
            values.addAll(dictionary);
        } else {
            EntityControlledVocabularyService entityControlledVocabularyService = SpringContext.bean(EntityControlledVocabularyService.class);
            try {
                values.addAll(entityControlledVocabularyService.getControlledVocabularyWords(name));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return values;
    }

    /**
     * @param values
     *            the values to set
     */
    public void setDictionary(List<VocabularyWord> values) {
        if (!getIsEntityProperty()) {
            dictionary = values;
        }
    }

    /**
     *
     * @param value
     */
    public void addValue(VocabularyWord value) {
        if (!getIsEntityProperty() && !dictionary.contains(value)) {
            dictionary.add(value);
        }
    }

    /**
     *
     * @param value
     */
    public void removeValue(VocabularyWord value) {
        if (!getIsEntityProperty()) {
            dictionary.remove(value);
        }
    }

    /**
     * @return the isEntityProperty
     */
    public Boolean getIsEntityProperty() {
        return isEntityProperty;
    }

    /**
     * @param isEntityProperty
     *            the isEntityProperty to set
     */
    public void setIsEntityProperty(Boolean isEntityProperty) {
        this.isEntityProperty = isEntityProperty;
    }

}
