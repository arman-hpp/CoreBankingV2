package com.bank.users.dtos;

import com.bank.core.dtos.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserChangePasswordInputDto extends BaseDto {
    private Long id;
    private String oldPassword;
    private String newPassword;
    private String repeatNewPassword;
}
