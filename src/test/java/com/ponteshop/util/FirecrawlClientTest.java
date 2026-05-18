package com.ponteshop.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ponteshop.config.FirecrawlApiProperties;
import com.ponteshop.exception.ImportFailedException;
import java.math.BigDecimal;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FirecrawlClientTest {

    private MockWebServer server;
    private FirecrawlClient client;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        FirecrawlApiProperties props = new FirecrawlApiProperties();
        props.setKey("test-key");
        props.setUrl(server.url("/v2").toString().replaceAll("/+$", ""));
        props.setTimeoutSeconds(5);
        client = new FirecrawlClient(props, new ObjectMapper());
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    void scrapeProduct_parsesExtractBlock() {
        String body = """
            {
              "success": true,
              "data": {
                "extract": {
                  "name": "Ténis",
                  "priceEur": 49.99,
                  "description": "Casual",
                  "category": "moda",
                  "brand": "X",
                  "images": ["https://img/a.png"],
                  "variants": [{"type": "size", "values": ["40", "41"]}]
                }
              }
            }
            """;
        server.enqueue(new MockResponse().setBody(body).addHeader("Content-Type", "application/json"));

        ProductExtraction ex = client.scrapeProduct("https://loja.pt/p/1");

        assertThat(ex.name()).isEqualTo("Ténis");
        assertThat(ex.priceEur()).isEqualByComparingTo(new BigDecimal("49.99"));
        assertThat(ex.images()).containsExactly("https://img/a.png");
        assertThat(ex.variants()).hasSize(1);
        assertThat(ex.variants().getFirst().getType()).isEqualTo("size");
        assertThat(ex.variants().getFirst().getValues()).containsExactly("40", "41");
    }

    @Test
    void scrapeProduct_throwsWhenSuccessFalse() {
        server.enqueue(new MockResponse().setBody("{\"success\":false}").addHeader("Content-Type", "application/json"));

        assertThatThrownBy(() -> client.scrapeProduct("https://loja.pt/p"))
            .isInstanceOf(ImportFailedException.class);
    }
}
