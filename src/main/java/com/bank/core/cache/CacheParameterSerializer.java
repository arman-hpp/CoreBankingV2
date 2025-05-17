package com.bank.core.cache;

import com.bank.core.dtos.PaginationRequestDto;
import com.bank.core.dtos.filters.FilterInfoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Serializes method parameters into strings for use in cache key generation.
 * This class handles various parameter types, including simple types, custom DTOs,
 * and complex objects, converting them into consistent string representations.
 */
public record CacheParameterSerializer(ObjectMapper objectMapper) {
    /**
     * Serializes an array of parameters into a single string
     *
     * @param params the parameters to serialize
     * @return a string representation of the serialized parameters
     */
    public String serialize(Object @NotNull ... params) {
        return Arrays.stream(params)
                .map(this::serializeObject)
                .collect(Collectors.joining("-"));
    }

    private String serializeObject(Object param) {
        if (param == null) {
            return "null";
        }
        if (isSimpleType(param)) {
            return param.toString();
        }
        if (param instanceof PaginationRequestDto paginationDto) {
            return serializePaginationDto(paginationDto);
        }
        if (param instanceof FilterInfoDto filterInfo) {
            return serializeFilterInfo(filterInfo);
        }
        return serializeToJson(param);
    }

    private boolean isSimpleType(Object param) {
        return param instanceof String || param instanceof Number || param instanceof Boolean;
    }

    private String serializePaginationDto(PaginationRequestDto paginationDto) {
        return String.format("page=%d,size=%d", paginationDto.getPageNumber(), paginationDto.getPageSize());
    }

    private String serializeFilterInfo(FilterInfoDto filterInfo) {
        return DigestUtils.md5Hex(serializeToJson(filterInfo));
    }

    private String serializeToJson(Object param) {
        try {
            return objectMapper.writeValueAsString(param);
        } catch (JsonProcessingException e) {
            return String.format("objHash=%s", param.hashCode());
        }
    }
}