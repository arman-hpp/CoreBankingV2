package com.bank.dtos.users;

import com.bank.dtos.BaseDto;
import com.bank.enums.users.UserTypes;
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
