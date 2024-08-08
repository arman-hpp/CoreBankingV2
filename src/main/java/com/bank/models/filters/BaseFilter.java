package com.bank.models.filters;

import com.bank.enums.filters.FilterComparators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BaseFilter {
    private String name;

    private FilterComparators comparator;

    private Object value;

    private Object value2;
}