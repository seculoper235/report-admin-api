package com.example.reportfrontapi.domain.product.controller.dto;

import java.time.LocalDate;

/**
 * 운영자 기프티콘 수정. 유효기간만 변경 가능(나머지 필드는 수정 불가).
 */
public record GiftUpdateCreateRequest(
        LocalDate validUntil
) {
}
