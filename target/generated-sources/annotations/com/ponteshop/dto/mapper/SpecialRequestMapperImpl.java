package com.ponteshop.dto.mapper;

import com.ponteshop.dto.specialrequest.SpecialRequestAdminDto;
import com.ponteshop.dto.specialrequest.SpecialRequestDto;
import com.ponteshop.entity.SpecialRequest;
import com.ponteshop.entity.Store;
import com.ponteshop.entity.User;
import com.ponteshop.util.JsonMapper;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-22T19:42:49+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 26.0.2 (Arch Linux)"
)
@Component
public class SpecialRequestMapperImpl implements SpecialRequestMapper {

    @Autowired
    private JsonMapper jsonMapper;

    @Override
    public SpecialRequestDto toDto(SpecialRequest sr, JsonMapper jsonMapper) {
        if ( sr == null && jsonMapper == null ) {
            return null;
        }

        SpecialRequestDto.SpecialRequestDtoBuilder specialRequestDto = SpecialRequestDto.builder();

        if ( sr != null ) {
            specialRequestDto.storeId( srStoreId( sr ) );
            specialRequestDto.storeSlug( jsonMapper.toJson( srStoreSlug( sr ) ) );
            specialRequestDto.id( sr.getId() );
            specialRequestDto.sourceUrl( jsonMapper.toJson( sr.getSourceUrl() ) );
            specialRequestDto.detectedName( jsonMapper.toJson( sr.getDetectedName() ) );
            specialRequestDto.detectedDescription( jsonMapper.toJson( sr.getDetectedDescription() ) );
            specialRequestDto.detectedPriceEur( sr.getDetectedPriceEur() );
            specialRequestDto.calculatedPriceAoa( sr.getCalculatedPriceAoa() );
            specialRequestDto.selectedVariant( jsonMapper.toJson( sr.getSelectedVariant() ) );
            specialRequestDto.status( sr.getStatus() );
            specialRequestDto.cartItemId( sr.getCartItemId() );
            specialRequestDto.productId( sr.getProductId() );
            specialRequestDto.createdAt( sr.getCreatedAt() );
            specialRequestDto.updatedAt( sr.getUpdatedAt() );
        }
        specialRequestDto.detectedImages( jsonMapper.toStringList(sr.getDetectedImages()) );
        specialRequestDto.detectedVariants( jsonMapper.toVariantList(sr.getDetectedVariants()) );
        specialRequestDto.priceBreakdown( jsonMapper.toBreakdown(sr.getPriceBreakdown()) );

        return specialRequestDto.build();
    }

    @Override
    public SpecialRequestAdminDto toAdminDto(SpecialRequest sr, JsonMapper jsonMapper) {
        if ( sr == null && jsonMapper == null ) {
            return null;
        }

        SpecialRequestAdminDto.SpecialRequestAdminDtoBuilder specialRequestAdminDto = SpecialRequestAdminDto.builder();

        if ( sr != null ) {
            specialRequestAdminDto.userId( srUserId( sr ) );
            specialRequestAdminDto.userName( jsonMapper.toJson( srUserName( sr ) ) );
            specialRequestAdminDto.userEmail( jsonMapper.toJson( srUserEmail( sr ) ) );
            specialRequestAdminDto.userPhone( jsonMapper.toJson( srUserPhone( sr ) ) );
            specialRequestAdminDto.storeId( srStoreId( sr ) );
            specialRequestAdminDto.storeName( jsonMapper.toJson( srStoreName( sr ) ) );
            specialRequestAdminDto.storeSlug( jsonMapper.toJson( srStoreSlug( sr ) ) );
            specialRequestAdminDto.id( sr.getId() );
            specialRequestAdminDto.sourceUrl( jsonMapper.toJson( sr.getSourceUrl() ) );
            specialRequestAdminDto.detectedName( jsonMapper.toJson( sr.getDetectedName() ) );
            specialRequestAdminDto.detectedDescription( jsonMapper.toJson( sr.getDetectedDescription() ) );
            specialRequestAdminDto.detectedPriceEur( sr.getDetectedPriceEur() );
            specialRequestAdminDto.calculatedPriceAoa( sr.getCalculatedPriceAoa() );
            specialRequestAdminDto.selectedVariant( jsonMapper.toJson( sr.getSelectedVariant() ) );
            specialRequestAdminDto.status( sr.getStatus() );
            specialRequestAdminDto.cartItemId( sr.getCartItemId() );
            specialRequestAdminDto.productId( sr.getProductId() );
            specialRequestAdminDto.createdAt( sr.getCreatedAt() );
            specialRequestAdminDto.updatedAt( sr.getUpdatedAt() );
        }
        specialRequestAdminDto.detectedImages( jsonMapper.toStringList(sr.getDetectedImages()) );
        specialRequestAdminDto.detectedVariants( jsonMapper.toVariantList(sr.getDetectedVariants()) );
        specialRequestAdminDto.priceBreakdown( jsonMapper.toBreakdown(sr.getPriceBreakdown()) );

        return specialRequestAdminDto.build();
    }

    private Integer srStoreId(SpecialRequest specialRequest) {
        if ( specialRequest == null ) {
            return null;
        }
        Store store = specialRequest.getStore();
        if ( store == null ) {
            return null;
        }
        Integer id = store.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String srStoreSlug(SpecialRequest specialRequest) {
        if ( specialRequest == null ) {
            return null;
        }
        Store store = specialRequest.getStore();
        if ( store == null ) {
            return null;
        }
        String slug = store.getSlug();
        if ( slug == null ) {
            return null;
        }
        return slug;
    }

    private UUID srUserId(SpecialRequest specialRequest) {
        if ( specialRequest == null ) {
            return null;
        }
        User user = specialRequest.getUser();
        if ( user == null ) {
            return null;
        }
        UUID id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String srUserName(SpecialRequest specialRequest) {
        if ( specialRequest == null ) {
            return null;
        }
        User user = specialRequest.getUser();
        if ( user == null ) {
            return null;
        }
        String name = user.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String srUserEmail(SpecialRequest specialRequest) {
        if ( specialRequest == null ) {
            return null;
        }
        User user = specialRequest.getUser();
        if ( user == null ) {
            return null;
        }
        String email = user.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }

    private String srUserPhone(SpecialRequest specialRequest) {
        if ( specialRequest == null ) {
            return null;
        }
        User user = specialRequest.getUser();
        if ( user == null ) {
            return null;
        }
        String phone = user.getPhone();
        if ( phone == null ) {
            return null;
        }
        return phone;
    }

    private String srStoreName(SpecialRequest specialRequest) {
        if ( specialRequest == null ) {
            return null;
        }
        Store store = specialRequest.getStore();
        if ( store == null ) {
            return null;
        }
        String name = store.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
