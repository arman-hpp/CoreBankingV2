package com.bank.core.dtos.filters;

import com.bank.core.dtos.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FilterSortDto extends BaseDto {
    private Sort.Direction direction;
    private String[] props;
}
