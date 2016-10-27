package me.exrates.model.dto.mobileApiDto;

import java.util.List;

/**
 * Created by OLEG on 01.09.2016.
 */
public class ListResponseWrapper<T extends List> {
    private T array;

    public ListResponseWrapper(T array) {
        this.array = array;
    }

    public T getArray() {
        return array;
    }

    public void setArray(T array) {
        this.array = array;
    }
}
