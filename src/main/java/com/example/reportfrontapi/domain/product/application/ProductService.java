package com.example.reportfrontapi.domain.product.application;

import com.example.reportfrontapi.domain.gift.GiftInventory;
import com.example.reportfrontapi.domain.gift.repository.GiftInventoryRepository;
import com.example.reportfrontapi.domain.product.Product;
import com.example.reportfrontapi.domain.product.application.dto.CodeLoadRequest;
import com.example.reportfrontapi.domain.product.application.dto.GiftInventoryResponse;
import com.example.reportfrontapi.domain.product.application.dto.GiftUpdateRequest;
import com.example.reportfrontapi.domain.product.application.dto.ProductCreateRequest;
import com.example.reportfrontapi.domain.product.application.dto.ProductResponse;
import com.example.reportfrontapi.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final GiftInventoryRepository giftInventoryRepository;

    // 노출 상품 목록 + 상품별 미사용(AVAILABLE)/처리중(RESERVED) 코드 수량.
    public List<ProductResponse> findAll() {
        return productRepository.findAllActive().stream()
                .map(p -> ProductResponse.from(p,
                        giftInventoryRepository.countAvailable(p.getProductId()),
                        giftInventoryRepository.countReserved(p.getProductId())))
                .toList();
    }

    // 운영자: 상품의 기프티콘 재고 목록(상태/유효기간). 지급완료(ISSUED)는 제외.
    public List<GiftInventoryResponse> findCodes(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        return giftInventoryRepository.findByProductIdExcludingIssued(productId).stream()
                .map(GiftInventoryResponse::from)
                .toList();
    }

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

    // 운영자: 기프티콘 수정. (유효기간만 변경 가능)
    @Transactional
    public void updateCode(Long productId, Long giftInventoryId, GiftUpdateRequest request) {
        GiftInventory inventory = giftInventoryRepository.findById(giftInventoryId)
                .filter(gi -> gi.getProductId().equals(productId))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Gift inventory not found: " + giftInventoryId + " for product: " + productId));
        inventory.changeValidUntil(request.validUntil());
    }

    // 운영자: 코드 재고 적재. 적재된 코드 수를 반환.
    @Transactional
    public int addCodes(Long productId, CodeLoadRequest request) {
        productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        for (CodeLoadRequest.CodeItem item : request.codes()) {
            giftInventoryRepository.save(
                    GiftInventory.of(productId, item.code(), item.barcodeImageUrl(), item.validUntil()));
        }
        return request.codes().size();
    }
}
