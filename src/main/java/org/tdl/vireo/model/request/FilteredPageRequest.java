package org.tdl.vireo.model.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
        PageRequest pageRequest;
        if (orders.isEmpty()) {
            pageRequest = new PageRequest(pageNumber > 0 ? pageNumber - 1 : 0, pageSize > 0 ? pageSize : 10);
        } else {
            pageRequest = new PageRequest(pageNumber > 0 ? pageNumber - 1 : 0, pageSize > 0 ? pageSize : 10, new Sort(orders));
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
