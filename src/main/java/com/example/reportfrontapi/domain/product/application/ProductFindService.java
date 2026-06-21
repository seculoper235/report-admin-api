package com.example.reportfrontapi.domain.product.application;

import com.example.reportfrontapi.domain.gift.repository.GiftInventoryRepository;
import com.example.reportfrontapi.domain.product.controller.dto.GiftInventoryFindResponse;
import com.example.reportfrontapi.domain.product.controller.dto.ProductFindResponse;
import com.example.reportfrontapi.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductFindService {

    private final ProductRepository productRepository;
    private final GiftInventoryRepository giftInventoryRepository;

    // 노출 상품 목록 + 상품별 미사용(AVAILABLE)/처리중(RESERVED) 코드 수량.
    public List<ProductFindResponse> findAll() {
        return productRepository.findAllActive().stream()
                .map(p -> ProductFindResponse.from(p,
                        giftInventoryRepository.countAvailable(p.getProductId()),
                        giftInventoryRepository.countReserved(p.getProductId())))
                .toList();
    }

    // 운영자: 상품의 기프티콘 재고 목록(상태/유효기간). 지급완료(ISSUED)는 제외.
    public List<GiftInventoryFindResponse> findCodes(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        return giftInventoryRepository.findByProductIdExcludingIssued(productId).stream()
                .map(GiftInventoryFindResponse::from)
                .toList();
    }
}
