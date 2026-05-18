package com.ponteshop.dto.mapper;

import com.ponteshop.dto.ProductAdminDto;
import com.ponteshop.dto.ProductDetailDto;
import com.ponteshop.dto.ProductSummaryDto;
import com.ponteshop.entity.Product;
import com.ponteshop.util.JsonMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {JsonMapper.class, StoreMapper.class})
public interface ProductMapper {
    @Mapping(target = "images", expression = "java(jsonMapper.toStringList(product.getImages()))")
    ProductSummaryDto toSummary(Product product, JsonMapper jsonMapper);

    @Mapping(target = "images", expression = "java(jsonMapper.toStringList(product.getImages()))")
    @Mapping(target = "variants", expression = "java(jsonMapper.toVariantList(product.getVariants()))")
    @Mapping(target = "priceBreakdown", expression = "java(jsonMapper.toBreakdown(product.getPriceBreakdown()))")
    ProductDetailDto toDetail(Product product, JsonMapper jsonMapper);

    @Mapping(target = "store", source = "store")
    ProductAdminDto toAdmin(Product product);
}

