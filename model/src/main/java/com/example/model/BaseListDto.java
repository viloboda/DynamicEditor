package com.example.model;

import java.util.List;

public class BaseListDto<T> {

    private List<T> items;

    public BaseListDto(List<T> items) {
        this.items = items;
    }

    public T getFirstItem() {
        return items.get(0);
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    public List<T> getItems() {
        return items;
    }

    public void addItems(List<T> items) {
        this.items.addAll(items);
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
