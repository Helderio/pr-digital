package com.ponteshop.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ponteshop.dto.PriceBreakdownDto;
import com.ponteshop.dto.VariantOptionDto;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonMapper {
    private static final TypeReference<List<String>> STR_LIST = new TypeReference<>() {};
    private static final TypeReference<List<VariantOptionDto>> VARIANT_LIST = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public List<String> toStringList(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, STR_LIST);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public String fromStringList(List<String> list) {
        if (list == null) return "[]";
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    public List<VariantOptionDto> toVariantList(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, VARIANT_LIST);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public String fromVariantList(List<VariantOptionDto> list) {
        if (list == null) return "[]";
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    public PriceBreakdownDto toBreakdown(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, PriceBreakdownDto.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String fromBreakdown(PriceBreakdownDto dto) {
        if (dto == null) return null;
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (Exception e) {
            return null;
        }
    }

    public String toJson(Object any) {
        try {
            return objectMapper.writeValueAsString(any);
        } catch (Exception e) {
            return "{}";
        }
    }
}

