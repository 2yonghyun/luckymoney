package com.kakao.pay.luckymoney.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindPickupResponseDto {

    private long userId;
    private int amount;
}
