package com.ponteshop.util;

import com.ponteshop.entity.Store;
import com.ponteshop.exception.InvalidStoreUrlException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StoreUrlValidatorTest {
    @Test
    void requireBelongsToStore_acceptsSameDomainWithDifferentPath() {
        Store store = Store.builder()
            .name("Zara Portugal")
            .baseUrl("https://www.zara.com/pt")
            .build();

        assertThatCode(() -> StoreUrlValidator.requireBelongsToStore(
            "https://www.zara.com/pt/pt/casaco-p012345.html",
            store
        )).doesNotThrowAnyException();
    }

    @Test
    void requireBelongsToStore_rejectsDifferentDomain() {
        Store store = Store.builder()
            .name("Zara Portugal")
            .baseUrl("https://www.zara.com/pt")
            .build();

        assertThatThrownBy(() -> StoreUrlValidator.requireBelongsToStore(
            "https://example.com/product",
            store
        )).isInstanceOf(InvalidStoreUrlException.class);
    }
}
