package com.example.reportfrontapi.domain.product.controller;

import com.example.reportfrontapi.common.response.ApiResponse;
import com.example.reportfrontapi.domain.product.application.ProductService;
import com.example.reportfrontapi.domain.product.application.dto.CodeLoadRequest;
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

    // 코드 재고 적재. 적재된 코드 수 반환.
    @PostMapping("/{id}/codes")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Integer> addCodes(@PathVariable Long id, @Valid @RequestBody CodeLoadRequest request) {
        return ApiResponse.success(productService.addCodes(id, request));
    }
}
