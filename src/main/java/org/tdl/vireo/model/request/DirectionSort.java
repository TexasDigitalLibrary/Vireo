package org.tdl.vireo.model.request;

import org.springframework.data.domain.Sort;

public class DirectionSort {

    private String property;

    private Sort.Direction direction;

    public DirectionSort() {

    }

    public DirectionSort(String property, Sort.Direction direction) {
        this();
        this.property = property;
        this.direction = direction;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Sort.Direction getDirection() {
        return direction;
    }

    public void setDirection(Sort.Direction direction) {
        this.direction = direction;
    }

}
