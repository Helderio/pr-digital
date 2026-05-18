package com.ponteshop.dto.mapper;

import com.ponteshop.dto.ProductAdminDto;
import com.ponteshop.dto.ProductDetailDto;
import com.ponteshop.dto.ProductSummaryDto;
import com.ponteshop.entity.Product;
import com.ponteshop.util.JsonMapper;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-18T10:26:14+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 26.0.2 (Arch Linux)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Autowired
    private JsonMapper jsonMapper;
    @Autowired
    private StoreMapper storeMapper;

    @Override
    public ProductSummaryDto toSummary(Product product, JsonMapper jsonMapper) {
        if ( product == null && jsonMapper == null ) {
            return null;
        }

        ProductSummaryDto.ProductSummaryDtoBuilder productSummaryDto = ProductSummaryDto.builder();

        if ( product != null ) {
            productSummaryDto.id( product.getId() );
            productSummaryDto.store( storeMapper.toDto( product.getStore() ) );
            productSummaryDto.name( jsonMapper.toJson( product.getName() ) );
            productSummaryDto.category( jsonMapper.toJson( product.getCategory() ) );
            productSummaryDto.priceEur( product.getPriceEur() );
            productSummaryDto.priceAoa( product.getPriceAoa() );
        }
        productSummaryDto.images( jsonMapper.toStringList(product.getImages()) );

        return productSummaryDto.build();
    }

    @Override
    public ProductDetailDto toDetail(Product product, JsonMapper jsonMapper) {
        if ( product == null && jsonMapper == null ) {
            return null;
        }

        ProductDetailDto.ProductDetailDtoBuilder productDetailDto = ProductDetailDto.builder();

        if ( product != null ) {
            productDetailDto.id( product.getId() );
            productDetailDto.store( storeMapper.toDto( product.getStore() ) );
            productDetailDto.sourceUrl( jsonMapper.toJson( product.getSourceUrl() ) );
            productDetailDto.name( jsonMapper.toJson( product.getName() ) );
            productDetailDto.description( jsonMapper.toJson( product.getDescription() ) );
            productDetailDto.category( jsonMapper.toJson( product.getCategory() ) );
            productDetailDto.priceEur( product.getPriceEur() );
            productDetailDto.priceAoa( product.getPriceAoa() );
            productDetailDto.status( product.getStatus() );
            productDetailDto.lastSyncedAt( product.getLastSyncedAt() );
            productDetailDto.createdAt( product.getCreatedAt() );
        }
        productDetailDto.images( jsonMapper.toStringList(product.getImages()) );
        productDetailDto.variants( jsonMapper.toVariantList(product.getVariants()) );
        productDetailDto.priceBreakdown( jsonMapper.toBreakdown(product.getPriceBreakdown()) );

        return productDetailDto.build();
    }

    @Override
    public ProductAdminDto toAdmin(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductAdminDto.ProductAdminDtoBuilder productAdminDto = ProductAdminDto.builder();

        productAdminDto.store( storeMapper.toDto( product.getStore() ) );
        productAdminDto.id( product.getId() );
        productAdminDto.sourceUrl( jsonMapper.toJson( product.getSourceUrl() ) );
        productAdminDto.name( jsonMapper.toJson( product.getName() ) );
        productAdminDto.category( jsonMapper.toJson( product.getCategory() ) );
        productAdminDto.priceEur( product.getPriceEur() );
        productAdminDto.priceAoa( product.getPriceAoa() );
        productAdminDto.importedBy( product.getImportedBy() );
        productAdminDto.status( product.getStatus() );
        productAdminDto.lastSyncedAt( product.getLastSyncedAt() );
        productAdminDto.createdAt( product.getCreatedAt() );

        return productAdminDto.build();
    }
}
