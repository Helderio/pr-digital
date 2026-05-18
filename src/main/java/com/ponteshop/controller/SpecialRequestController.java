package com.ponteshop.controller;

import com.ponteshop.dto.specialrequest.AnalyseUrlRequest;
import com.ponteshop.dto.specialrequest.SpecialRequestDto;
import com.ponteshop.security.SecurityUtil;
import com.ponteshop.service.SpecialRequestService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/special-requests")
public class SpecialRequestController {

    private final SpecialRequestService specialRequestService;
    private final SecurityUtil securityUtil;

    @PostMapping("/analyse")
    public SpecialRequestDto analyse(@Valid @RequestBody AnalyseUrlRequest body) {
        UUID userId = securityUtil.requireCurrentUserId();
        return specialRequestService.analyseUrl(body.getUrl(), body.getStoreId(), userId);
    }

    @GetMapping
    public List<SpecialRequestDto> listMine() {
        return specialRequestService.listMine(securityUtil.requireCurrentUserId());
    }

    @GetMapping("/{id}")
    public SpecialRequestDto getMine(@PathVariable UUID id) {
        return specialRequestService.getMine(securityUtil.requireCurrentUserId(), id);
    }
}
