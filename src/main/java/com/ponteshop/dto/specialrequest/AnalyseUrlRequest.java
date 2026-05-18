package com.ponteshop.dto.specialrequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyseUrlRequest {
    @NotBlank
    private String url;

    @NotNull
    private Integer storeId;
}
