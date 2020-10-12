package com.kakao.pay.luckymoney.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateSprinkleRequestDto {

    private int totalAmount;
    private int divideNumber;
}
