package com.bank.core.dtos;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(name = "PaginationRequest", description = "Generic pagination parameters for paginated API endpoints")
public class PaginationRequestDto extends BaseDto {
    @Min(value = 0, message = "Page number must be non-negative")
    @Parameter(description = "Page number (zero-based)", example = "0")
    @Schema(defaultValue = "0", minimum = "0")
    private int pageNumber;

    @Parameter(description = "Page size", example = "10")
    @Positive(message = "Page size must be positive")
    @Max(value = 100, message = "Page size must not exceed 100")
    @Schema(defaultValue = "10", minimum = "1")
    private int pageSize;

    @Parameter(description = "Sort by field", example = "Id")
    @Schema(defaultValue = "Id")
    private String sortBy = "Id";

    @Parameter(description = "Sort direction", example = "ASC")
    @Schema(allowableValues = {"ASC", "DESC"}, defaultValue = "ASC")
    private String direction = "ASC";
}
