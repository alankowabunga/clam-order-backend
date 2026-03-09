package com.project.clamorderbackend.presentation.controller;

import com.project.clamorderbackend.application.dto.*;
import com.project.clamorderbackend.application.usecase.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for product operations
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductUseCase productUseCase;

    /**
     * Get all active products
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts() {
        return ResponseEntity.ok(productUseCase.getActiveProducts());
    }
}
