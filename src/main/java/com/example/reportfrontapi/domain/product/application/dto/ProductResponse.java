package com.example.reportfrontapi.domain.product.application.dto;

import com.example.reportfrontapi.domain.product.Product;

public record ProductResponse(
        Long productId,
        String name,
        String brand,
        String imageUrl,
        Integer pointCost,
        long stock,        // 미사용(AVAILABLE) 코드 재고 수량
        long processing    // 처리중(RESERVED) 코드 수량
) {
    public static ProductResponse from(Product product, long stock, long processing) {
        return new ProductResponse(
                product.getProductId(),
                product.getName(),
                product.getBrand(),
                product.getImageUrl(),
                product.getPointCost(),
                stock,
                processing);
    }
}
