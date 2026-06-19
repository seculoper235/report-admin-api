package com.example.reportfrontapi.domain.product.controller;

import com.example.reportfrontapi.common.response.ApiResponse;
import com.example.reportfrontapi.domain.product.application.ProductService;
import com.example.reportfrontapi.domain.product.application.dto.CodeLoadRequest;
import com.example.reportfrontapi.domain.product.application.dto.GiftInventoryResponse;
import com.example.reportfrontapi.domain.product.application.dto.GiftUpdateRequest;
import com.example.reportfrontapi.domain.product.application.dto.ProductCreateRequest;
import com.example.reportfrontapi.domain.product.application.dto.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 운영자 전용(ADMIN). SecurityConfig에서 /api/admin/** 은 ROLE_ADMIN 으로 제한.
 */
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    // 등록된 전체 상품 목록 + 상품별 재고 수량.
    @GetMapping
    public ApiResponse<List<ProductResponse>> findAll() {
        return ApiResponse.success(productService.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Long> create(@Valid @RequestBody ProductCreateRequest request) {
        return ApiResponse.success(productService.create(request));
    }

    // 상품 정보 수정(모든 필드).
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ProductCreateRequest request) {
        productService.update(id, request);
        return ApiResponse.success(null);
    }

    // 상품 삭제. 재고(AVAILABLE)/처리중(RESERVED)이 모두 0일 때만 가능.
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.success(null);
    }

    // 상품의 기프티콘 재고 목록(상태/유효기간).
    @GetMapping("/{id}/codes")
    public ApiResponse<List<GiftInventoryResponse>> findCodes(@PathVariable Long id) {
        return ApiResponse.success(productService.findCodes(id));
    }

    // 코드 재고 적재. 적재된 코드 수 반환.
    @PostMapping("/{id}/codes")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Integer> addCodes(@PathVariable Long id, @Valid @RequestBody CodeLoadRequest request) {
        return ApiResponse.success(productService.addCodes(id, request));
    }

    // 기프티콘 수정(유효기간만).
    @PatchMapping("/{id}/codes/{codeId}")
    public ApiResponse<Void> updateCode(@PathVariable Long id, @PathVariable Long codeId,
                                        @RequestBody GiftUpdateRequest request) {
        productService.updateCode(id, codeId, request);
        return ApiResponse.success(null);
    }
}
