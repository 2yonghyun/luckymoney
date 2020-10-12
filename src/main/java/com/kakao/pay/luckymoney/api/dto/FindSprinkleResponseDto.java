package com.kakao.pay.luckymoney.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class FindSprinkleResponseDto {

    private LocalDateTime sprinkledTime;
    private int amount;
    private int pickedAmount;
    private List<FindPickupResponseDto> pickedPickupList;
}
