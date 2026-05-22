package com.ponteshop.util;

import com.ponteshop.entity.Store;
import com.ponteshop.exception.InvalidStoreUrlException;
import java.net.URI;
import java.net.URISyntaxException;

public final class StoreUrlValidator {
    private StoreUrlValidator() {
    }

    public static void requireBelongsToStore(String url, Store store) {
        String productHost = host(url);
        String storeHost = host(store.getBaseUrl());
        if (productHost == null || storeHost == null || !sameDomain(productHost, storeHost)) {
            throw new InvalidStoreUrlException("URL não pertence à loja " + store.getName());
        }
    }

    private static boolean sameDomain(String productHost, String storeHost) {
        String product = stripWww(productHost);
        String store = stripWww(storeHost);
        return product.equals(store) || product.endsWith("." + store);
    }

    private static String stripWww(String host) {
        return host != null && host.startsWith("www.") ? host.substring(4) : host;
    }

    private static String host(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            URI uri = new URI(value.trim());
            String host = uri.getHost();
            return host == null ? null : host.toLowerCase();
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
