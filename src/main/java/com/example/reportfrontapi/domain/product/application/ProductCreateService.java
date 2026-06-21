package com.example.reportfrontapi.domain.product.application;

import com.example.reportfrontapi.domain.gift.model.GiftInventory;
import com.example.reportfrontapi.domain.gift.repository.GiftInventoryRepository;
import com.example.reportfrontapi.domain.product.controller.dto.CodeLoadCreateRequest;
import com.example.reportfrontapi.domain.product.controller.dto.GiftUpdateCreateRequest;
import com.example.reportfrontapi.domain.product.controller.dto.ProductCreateRequest;
import com.example.reportfrontapi.domain.product.model.Product;
import com.example.reportfrontapi.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductCreateService {

    private final ProductRepository productRepository;
    private final GiftInventoryRepository giftInventoryRepository;

    // 운영자: 상품 등록.
    @Transactional
    public Long create(ProductCreateRequest request) {
        Product product = productRepository.save(
                Product.of(request.name(), request.brand(), request.imageUrl(), request.pointCost()));
        return product.getProductId();
    }

    // 운영자: 상품 정보 수정. (모든 필드 수정 가능)
    @Transactional
    public void update(Long productId, ProductCreateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
        product.update(request.name(), request.brand(), request.imageUrl(), request.pointCost());
    }

    // 운영자: 기프티콘 수정. (유효기간만 변경 가능)
    @Transactional
    public void updateCode(Long productId, Long giftInventoryId, GiftUpdateCreateRequest request) {
        GiftInventory inventory = giftInventoryRepository.findById(giftInventoryId)
                .filter(gi -> gi.getProductId().equals(productId))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Gift inventory not found: " + giftInventoryId + " for product: " + productId));
        inventory.changeValidUntil(request.validUntil());
    }

    // 운영자: 코드 재고 적재. 적재된 코드 수를 반환.
    @Transactional
    public int addCodes(Long productId, CodeLoadCreateRequest request) {
        productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        for (CodeLoadCreateRequest.CodeItem item : request.codes()) {
            giftInventoryRepository.save(
                    GiftInventory.of(productId, item.code(), item.barcodeImageUrl(), item.validUntil()));
        }
        return request.codes().size();
    }
}
