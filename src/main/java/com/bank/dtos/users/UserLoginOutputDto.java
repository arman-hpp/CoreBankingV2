package com.bank.dtos.users;

import com.bank.dtos.BaseDto;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserLoginOutputDto extends BaseDto {
    public String username;
    public String accessToken;
    public String refreshToken;
    public Integer refreshTokenExpiration;
}