package com.project.clamorderbackend.application.usecase;

import com.project.clamorderbackend.application.dto.*;
import com.project.clamorderbackend.application.mapper.OrderMapper;
import com.project.clamorderbackend.domain.entity.*;
import com.project.clamorderbackend.domain.repository.*;
import com.project.clamorderbackend.domain.service.*;
import com.project.clamorderbackend.domain.valueobject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case for product operations
 */
@Service
@RequiredArgsConstructor
public class ProductUseCase {

    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    /**
     * Get all active products
     */
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByIsActiveTrue().stream()
                .map(orderMapper::toProductResponse)
                .collect(Collectors.toList());
    }
}
