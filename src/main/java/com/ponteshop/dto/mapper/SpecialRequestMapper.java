package com.ponteshop.dto.mapper;

import com.ponteshop.dto.specialrequest.SpecialRequestAdminDto;
import com.ponteshop.dto.specialrequest.SpecialRequestDto;
import com.ponteshop.entity.SpecialRequest;
import com.ponteshop.util.JsonMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {JsonMapper.class})
public interface SpecialRequestMapper {

    @Mapping(target = "storeId", source = "sr.store.id")
    @Mapping(target = "storeSlug", source = "sr.store.slug")
    @Mapping(target = "detectedImages", expression = "java(jsonMapper.toStringList(sr.getDetectedImages()))")
    @Mapping(target = "detectedVariants", expression = "java(jsonMapper.toVariantList(sr.getDetectedVariants()))")
    @Mapping(target = "priceBreakdown", expression = "java(jsonMapper.toBreakdown(sr.getPriceBreakdown()))")
    SpecialRequestDto toDto(SpecialRequest sr, JsonMapper jsonMapper);

    @Mapping(target = "userId", source = "sr.user.id")
    @Mapping(target = "userName", source = "sr.user.name")
    @Mapping(target = "userEmail", source = "sr.user.email")
    @Mapping(target = "userPhone", source = "sr.user.phone")
    @Mapping(target = "storeId", source = "sr.store.id")
    @Mapping(target = "storeName", source = "sr.store.name")
    @Mapping(target = "storeSlug", source = "sr.store.slug")
    @Mapping(target = "detectedImages", expression = "java(jsonMapper.toStringList(sr.getDetectedImages()))")
    @Mapping(target = "detectedVariants", expression = "java(jsonMapper.toVariantList(sr.getDetectedVariants()))")
    @Mapping(target = "priceBreakdown", expression = "java(jsonMapper.toBreakdown(sr.getPriceBreakdown()))")
    SpecialRequestAdminDto toAdminDto(SpecialRequest sr, JsonMapper jsonMapper);
}
