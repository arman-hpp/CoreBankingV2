package com.bank.dtos.filters;

import com.bank.dtos.BaseDto;
import com.bank.enums.FilterComparators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BaseFilterDto extends BaseDto {
    private String name;

    private FilterComparators comparator;

    private Object value;

    private Object value2;
}
