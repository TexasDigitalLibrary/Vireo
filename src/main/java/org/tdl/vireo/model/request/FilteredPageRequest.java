package org.tdl.vireo.model.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.specification.UserSpecification;

public class FilteredPageRequest {

    private int pageNumber;

    private int pageSize;

    private List<DirectionSort> sort;

    private Map<String, String[]> filters;

    public FilteredPageRequest() {
        sort = new ArrayList<DirectionSort>();
        filters = new HashMap<String, String[]>();
    }

    @JsonIgnore
    public UserSpecification<User> getUserSpecification() {
        return new UserSpecification<User>(filters);
    }

    @JsonIgnore
    public PageRequest getPageRequest() {
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        sort.forEach(sort -> {
            orders.add(new Sort.Order(sort.getDirection(), sort.getProperty()));
        });
        orders.add(new Sort.Order(Sort.Direction.ASC, "id"));
        PageRequest pageRequest;
        if (orders.isEmpty()) {
            pageRequest = PageRequest.of(pageNumber > 0 ? pageNumber - 1 : 0, pageSize > 0 ? pageSize : 10);
        } else {
            pageRequest = PageRequest.of(pageNumber > 0 ? pageNumber - 1 : 0, pageSize > 0 ? pageSize : 10, Sort.by(orders));
        }

        return pageRequest;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<DirectionSort> getSort() {
        return sort;
    }

    public void setSort(List<DirectionSort> sort) {
        this.sort = sort;
    }

    public Map<String, String[]> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, String[]> filters) {
        this.filters = filters;
    }

}
