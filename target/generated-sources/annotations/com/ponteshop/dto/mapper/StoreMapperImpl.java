package com.ponteshop.dto.mapper;

import com.ponteshop.dto.StoreDto;
import com.ponteshop.entity.Store;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-18T10:26:11+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 26.0.2 (Arch Linux)"
)
@Component
public class StoreMapperImpl implements StoreMapper {

    @Override
    public StoreDto toDto(Store store) {
        if ( store == null ) {
            return null;
        }

        StoreDto.StoreDtoBuilder storeDto = StoreDto.builder();

        storeDto.id( store.getId() );
        storeDto.name( store.getName() );
        storeDto.slug( store.getSlug() );
        storeDto.baseUrl( store.getBaseUrl() );
        storeDto.logoUrl( store.getLogoUrl() );
        storeDto.country( store.getCountry() );
        storeDto.displayOrder( store.getDisplayOrder() );

        return storeDto.build();
    }
}
