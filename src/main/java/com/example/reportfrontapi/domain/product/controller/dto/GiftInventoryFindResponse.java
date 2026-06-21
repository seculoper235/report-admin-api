package com.example.reportfrontapi.domain.product.controller.dto;

import com.example.reportfrontapi.domain.gift.model.GiftInventory;
import com.example.reportfrontapi.domain.gift.model.GiftInventoryStatus;

import java.time.LocalDate;

/**
 * 운영자 기프티콘 재고 단건 응답. 목록에서는 상태/유효기간만 노출한다.
 * (code/pin 등 민감정보는 포함하지 않는다.)
 */
public record GiftInventoryFindResponse(
        Long giftInventoryId,
        GiftInventoryStatus status,
        LocalDate validUntil
) {
    public static GiftInventoryFindResponse from(GiftInventory inventory) {
        return new GiftInventoryFindResponse(
                inventory.getGiftInventoryId(),
                inventory.getStatus(),
                inventory.getValidUntil());
    }
}
