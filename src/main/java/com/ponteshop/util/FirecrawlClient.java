package com.ponteshop.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ponteshop.config.FirecrawlApiProperties;
import com.ponteshop.dto.VariantOptionDto;
import com.ponteshop.exception.ImportFailedException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FirecrawlClient {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final Pattern DECIMAL_TEXT = Pattern.compile("(\\d+[\\d\\s.,]*)");

    private final FirecrawlApiProperties properties;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient;

    public FirecrawlClient(FirecrawlApiProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        int sec = Math.max(1, properties.getTimeoutSeconds());
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(sec, TimeUnit.SECONDS)
            .readTimeout(sec, TimeUnit.SECONDS)
            .writeTimeout(sec, TimeUnit.SECONDS)
            .callTimeout(sec, TimeUnit.SECONDS)
            .build();
    }

    public ProductExtraction scrapeProduct(String url) {
        if (properties.getKey() == null || properties.getKey().isBlank()) {
            throw new ImportFailedException("Firecrawl API key não configurada");
        }
        String base = properties.getUrl() == null || properties.getUrl().isBlank()
            ? "https://api.firecrawl.dev/v2"
            : properties.getUrl().replaceAll("/+$", "");
        String endpoint = base + "/scrape";

        try {
            String body = buildScrapeBody(url);
            Request request = new Request.Builder()
                .url(endpoint)
                .header("Authorization", "Bearer " + properties.getKey())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(body, JSON))
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                String respBody = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    log.warn("Firecrawl HTTP {}: {}", response.code(), respBody);
                    throw new ImportFailedException("Firecrawl respondeu com erro HTTP " + response.code());
                }
                return parseExtract(respBody);
            }
        } catch (ImportFailedException e) {
            throw e;
        } catch (IOException e) {
            log.warn("Firecrawl request failed", e);
            throw new ImportFailedException("Falha de rede ao contactar Firecrawl", e);
        } catch (Exception e) {
            log.warn("Firecrawl parse failed", e);
            throw new ImportFailedException("Resposta Firecrawl inválida", e);
        }
    }

    private String buildScrapeBody(String url) throws IOException {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("url", url);
        ArrayNode formats = root.putArray("formats");
        formats.add("extract");
        ObjectNode extract = root.putObject("extract");
        extract.set("schema", buildSchema());
        return objectMapper.writeValueAsString(root);
    }

    private ObjectNode buildSchema() {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        ObjectNode props = objectMapper.createObjectNode();
        props.set("name", propString("Nome completo do produto"));
        props.set("priceEur", propNumber("Preço em euros, só o número"));
        props.set("description", propString("Descrição curta em português, máx 100 palavras"));
        props.set("category", propString("Uma de: moda, tech, casa, saude, livros, brinquedos, outro"));
        props.set("brand", propString("Marca do produto"));
        props.set("images", propStringArray("URLs das imagens do produto"));
        props.set("variants", variantArraySchema());
        schema.set("properties", props);
        ArrayNode req = schema.putArray("required");
        req.add("name");
        req.add("priceEur");
        return schema;
    }

    private ObjectNode propString(String description) {
        ObjectNode n = objectMapper.createObjectNode();
        n.put("type", "string");
        n.put("description", description);
        return n;
    }

    private ObjectNode propNumber(String description) {
        ObjectNode n = objectMapper.createObjectNode();
        n.put("type", "number");
        n.put("description", description);
        return n;
    }

    private ObjectNode propStringArray(String description) {
        ObjectNode n = objectMapper.createObjectNode();
        n.put("type", "array");
        ObjectNode items = objectMapper.createObjectNode();
        items.put("type", "string");
        n.set("items", items);
        n.put("description", description);
        return n;
    }

    private ObjectNode variantArraySchema() {
        ObjectNode variantItem = objectMapper.createObjectNode();
        variantItem.put("type", "object");
        ObjectNode vprops = objectMapper.createObjectNode();
        vprops.set("type", propString("Tipo de variante, ex: size, color"));
        ObjectNode values = objectMapper.createObjectNode();
        values.put("type", "array");
        ObjectNode vitems = objectMapper.createObjectNode();
        vitems.put("type", "string");
        values.set("items", vitems);
        vprops.set("values", values);
        variantItem.set("properties", vprops);

        ObjectNode variants = objectMapper.createObjectNode();
        variants.put("type", "array");
        variants.set("items", variantItem);
        variants.put("description", "Variantes disponíveis");
        return variants;
    }

    private ProductExtraction parseExtract(String json) throws IOException {
        JsonNode root = objectMapper.readTree(json);
        if (!root.path("success").asBoolean(false)) {
            throw new ImportFailedException("Firecrawl indicou success=false");
        }
        JsonNode extract = root.path("data").path("extract");
        if (extract.isMissingNode() || extract.isNull()) {
            extract = root.path("data").path("json");
        }
        String name = text(extract, "name");
        BigDecimal priceEur = decimal(extract, "priceEur");
        if (name == null || name.isBlank()) {
            throw new ImportFailedException("Extração sem nome de produto");
        }
        if (priceEur == null || priceEur.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ImportFailedException("Extração sem preço EUR válido");
        }
        String description = text(extract, "description");
        String category = text(extract, "category");
        String brand = text(extract, "brand");
        List<String> images = readStringList(extract.path("images"));
        List<VariantOptionDto> variants = readVariants(extract.path("variants"));
        return new ProductExtraction(
            name,
            priceEur,
            description,
            category,
            brand,
            images,
            variants
        );
    }

    private static String text(JsonNode n, String field) {
        JsonNode v = n.path(field);
        return v.isMissingNode() || v.isNull() ? null : v.asText(null);
    }

    private static BigDecimal decimal(JsonNode n, String field) {
        JsonNode v = n.path(field);
        if (v.isMissingNode() || v.isNull()) {
            return null;
        }
        if (v.isNumber()) {
            return v.decimalValue();
        }
        if (!v.isTextual()) {
            return null;
        }
        String raw = v.asText("");
        Matcher matcher = DECIMAL_TEXT.matcher(raw);
        if (!matcher.find()) {
            return null;
        }
        String text = matcher.group(1).replace(" ", "");
        int comma = text.lastIndexOf(',');
        int dot = text.lastIndexOf('.');
        if (comma >= 0 && dot >= 0) {
            text = comma > dot ? text.replace(".", "").replace(',', '.') : text.replace(",", "");
        } else if (comma >= 0) {
            text = text.replace(',', '.');
        }
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private List<String> readStringList(JsonNode arr) {
        if (!arr.isArray()) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        for (JsonNode n : arr) {
            if (n.isTextual()) {
                out.add(n.asText());
            }
        }
        return out;
    }

    private List<VariantOptionDto> readVariants(JsonNode arr) {
        if (!arr.isArray()) {
            return List.of();
        }
        List<VariantOptionDto> out = new ArrayList<>();
        for (JsonNode n : arr) {
            String type = text(n, "type");
            List<String> values = readStringList(n.path("values"));
            if (type != null && !values.isEmpty()) {
                out.add(VariantOptionDto.builder().type(type).values(values).build());
            }
        }
        return out;
    }
}
