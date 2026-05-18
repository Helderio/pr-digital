package com.ponteshop.controller;

import com.ponteshop.dto.StoreDto;
import com.ponteshop.dto.mapper.StoreMapper;
import com.ponteshop.service.StoreService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreController {
    private final StoreService storeService;
    private final StoreMapper storeMapper;

    @GetMapping
    public List<StoreDto> list() {
        return storeService.listActive().stream().map(storeMapper::toDto).toList();
    }
}

