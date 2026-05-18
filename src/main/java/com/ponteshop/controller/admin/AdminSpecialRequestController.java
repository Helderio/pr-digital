package com.ponteshop.controller.admin;

import com.ponteshop.dto.ProductDetailDto;
import com.ponteshop.dto.specialrequest.SpecialRequestAdminDto;
import com.ponteshop.dto.specialrequest.SpecialRequestStatusPatchRequest;
import com.ponteshop.enums.SpecialRequestStatus;
import com.ponteshop.service.SpecialRequestService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/special-requests")
public class AdminSpecialRequestController {

    private final SpecialRequestService specialRequestService;

    @GetMapping
    public Page<SpecialRequestAdminDto> list(
        @RequestParam(required = false) SpecialRequestStatus status,
        @RequestParam(required = false) Integer storeId,
        Pageable pageable
    ) {
        return specialRequestService.listAdmin(status, storeId, pageable);
    }

    @GetMapping("/{id}")
    public SpecialRequestAdminDto get(@PathVariable UUID id) {
        return specialRequestService.getAdmin(id);
    }

    @PatchMapping("/{id}/status")
    public SpecialRequestAdminDto updateStatus(
        @PathVariable UUID id,
        @Valid @RequestBody SpecialRequestStatusPatchRequest body
    ) {
        return specialRequestService.updateStatusAdmin(id, body.getStatus());
    }

    @PostMapping("/{id}/publish-to-catalog")
    public ProductDetailDto publishToCatalog(@PathVariable UUID id) {
        return specialRequestService.publishToCatalog(id);
    }
}
