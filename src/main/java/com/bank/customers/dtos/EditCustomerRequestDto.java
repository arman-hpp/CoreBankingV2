package com.bank.customers.dtos;

import com.bank.core.dtos.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "DTO for editing an existing customer")
public class EditCustomerRequestDto extends BaseDto {
    @Schema(description = "ID of the customer to be edited", example = "5")
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Schema(description = "First name of the customer", example = "Arman")
    private String firstName;

    @NotBlank
    @Size(max = 50)
    @Schema(description = "Last name of the customer", example = "Hasanpour")
    private String lastName;

    @Size(max = 255)
    @Schema(description = "Customer address", example = "Tehran, Vanak Square")
    private String address;
}
