package com.ponteshop.dto.specialrequest;

import com.ponteshop.enums.SpecialRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialRequestStatusPatchRequest {
    @NotNull
    private SpecialRequestStatus status;
}
