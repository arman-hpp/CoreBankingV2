package com.bank.core.dtos.filters;

import com.bank.core.dtos.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FilerPageDto extends BaseDto {
    private Integer page;
    private Integer size;
}
