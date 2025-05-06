package com.bank.users.dtos;

import com.bank.core.dtos.BaseDto;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class UserLoginInputDto extends BaseDto {
    @NonNull
    private String username;
    private String password;
    private String captchaToken;
    private String captchaAnswer;
}
