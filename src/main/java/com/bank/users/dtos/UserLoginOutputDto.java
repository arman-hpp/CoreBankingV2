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
public class UserLoginOutputDto extends BaseDto {
    private String username;
    private String accessToken;
    private String refreshToken;
    private Integer refreshTokenExpiration;
}