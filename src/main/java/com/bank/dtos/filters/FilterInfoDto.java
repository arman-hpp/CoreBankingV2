package com.bank.dtos.filters;

import com.bank.dtos.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FilterInfoDto extends BaseDto {
    private ArrayList<BaseFilterDto> filters;
    private FilerPageDto filterPage;
    private FilterSortDto filterSort;
}
