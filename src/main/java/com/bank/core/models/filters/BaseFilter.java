package com.bank.core.models.filters;

import com.bank.core.enums.FilterComparators;
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