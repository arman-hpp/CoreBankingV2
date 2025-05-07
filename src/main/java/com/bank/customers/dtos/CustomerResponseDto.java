package com.bank.customers.dtos;

import com.bank.core.dtos.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "DTO representing customer information returned by the API")
public class CustomerResponseDto extends BaseDto {
    @Schema(description = "Unique identifier of the customer", example = "5")
    private Long id;

    @Schema(description = "First name of the customer", example = "Arman")
    private String firstName;

    @Schema(description = "Last name of the customer", example = "Hasanpour")
    private String lastName;

    @Schema(description = "Customer address", example = "Tehran, Vanak Square")
    private String address;
}
