package com.ponteshop.dto.mapper;

import com.ponteshop.dto.StoreDto;
import com.ponteshop.entity.Store;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StoreMapper {
    StoreDto toDto(Store store);
}

