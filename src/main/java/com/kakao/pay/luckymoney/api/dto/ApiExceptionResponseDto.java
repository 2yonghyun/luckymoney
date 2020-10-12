package com.kakao.pay.luckymoney.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiExceptionResponseDto {

    private String code;
    private String message;
}
