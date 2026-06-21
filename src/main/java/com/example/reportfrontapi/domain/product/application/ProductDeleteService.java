package com.example.reportfrontapi.domain.product.application;

import com.example.reportfrontapi.domain.gift.repository.GiftInventoryRepository;
import com.example.reportfrontapi.domain.product.model.Product;
import com.example.reportfrontapi.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductDeleteService {

    private final ProductRepository productRepository;
    private final GiftInventoryRepository giftInventoryRepository;

    // 운영자: 상품 삭제. 재고(AVAILABLE)와 처리중(RESERVED)이 모두 0일 때만 가능.
    @Transactional
    public void delete(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        long available = giftInventoryRepository.countAvailable(productId);
        long reserved = giftInventoryRepository.countReserved(productId);
        if (available > 0 || reserved > 0) {
            throw new IllegalStateException(
                    "재고 또는 처리중인 기프티콘이 남아 있어 삭제할 수 없습니다. (재고 " + available + ", 처리중 " + reserved + ")");
        }
        productRepository.delete(product);
    }
}
