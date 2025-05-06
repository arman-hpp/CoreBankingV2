package com.bank.users.dtos;

import com.bank.core.dtos.BaseDto;
import com.bank.users.enums.UserTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EditUserInputDto extends BaseDto {
    private Long id;
    private String username;
    private UserTypes userType;
}
