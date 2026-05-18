package com.ponteshop.service;

import com.ponteshop.entity.Store;
import com.ponteshop.repository.StoreRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

    public List<Store> listActive() {
        return storeRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }
}

