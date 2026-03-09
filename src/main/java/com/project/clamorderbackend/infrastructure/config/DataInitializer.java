package com.project.clamorderbackend.infrastructure.config;

import com.project.clamorderbackend.domain.entity.Product;
import com.project.clamorderbackend.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Data initialization for seeding initial products
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        // Only initialize if database is empty
        if (productRepository.count() == 0) {
            log.info("Initializing products...");
            
            List<Product> products = List.of(
                Product.builder()
                        .publicId("p1")
                        .name("大")
                        .pricePerCatty(170)
                        .description("約40顆/斤")
                        .isLimited(false)
                        .isActive(true)
                        .build(),
                Product.builder()
                        .publicId("p2")
                        .name("特大")
                        .pricePerCatty(200)
                        .description("約30顆/斤")
                        .isLimited(false)
                        .isActive(true)
                        .build(),
                Product.builder()
                        .publicId("p3")
                        .name("冬季限定款")
                        .pricePerCatty(220)
                        .description("約23顆/斤")
                        .isLimited(true)
                        .stockRemaining(50)
                        .isActive(true)
                        .build()
            );
            
            productRepository.saveAll(products);
            log.info("Initialized {} products", products.size());
        }
    }
}
