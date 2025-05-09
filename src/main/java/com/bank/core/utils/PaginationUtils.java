package com.bank.core.utils;

import com.bank.core.dtos.PaginationRequestDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public final class PaginationUtils {
    public static PageRequest toPageRequest(PaginationRequestDto paginationDto) {
        var direction = "ASC".equals(paginationDto.getDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(
                paginationDto.getPageNumber(),
                paginationDto.getPageSize(),
                Sort.by(direction, paginationDto.getSortBy()));
    }
}
