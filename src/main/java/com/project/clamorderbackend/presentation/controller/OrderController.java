package com.project.clamorderbackend.presentation.controller;

import com.project.clamorderbackend.application.dto.*;
import com.project.clamorderbackend.application.usecase.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for order operations
 */
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderUseCase orderUseCase;

    /**
     * Calculate order price
     */
    @PostMapping("/calculate")
    public ResponseEntity<OrderCalculateResponse> calculatePrice(
            @Valid @RequestBody OrderCalculateRequest request) {
        return ResponseEntity.ok(orderUseCase.calculatePrice(request));
    }

    /**
     * Submit order
     */
    @PostMapping("/submit")
    public ResponseEntity<OrderSubmitResponse> submitOrder(
            @Valid @RequestBody OrderSubmitRequest request) {
        return ResponseEntity.ok(orderUseCase.submitOrder(request));
    }
}
