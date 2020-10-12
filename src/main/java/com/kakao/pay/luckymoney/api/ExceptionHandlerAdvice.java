package com.kakao.pay.luckymoney.api;

import com.kakao.pay.luckymoney.api.dto.ApiExceptionResponseDto;
import com.kakao.pay.luckymoney.constant.ErrorCode;
import com.kakao.pay.luckymoney.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiExceptionResponseDto> handle(ApiException e) {
        log.error(e.getMessage(), e);

        ApiExceptionResponseDto responseBody;

        switch (e.getErrorCode()) {
            case E0001:
                responseBody = new ApiExceptionResponseDto(e.getErrorCode().toString(), e.getMessage());
                return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
            case E0101:
            case E0102:
            case E0103:
            case E0104:
            case E0105:
            case E0106:
            case E0107:
                responseBody = new ApiExceptionResponseDto(e.getErrorCode().toString(), e.getMessage());
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            case E0301:
            case E0302:
                responseBody = new ApiExceptionResponseDto(e.getErrorCode().toString(), e.getMessage());
                return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
            default:
                responseBody = new ApiExceptionResponseDto(ErrorCode.E0000.toString(), "예상치 못한 오류가 발생했습니다.");
                return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
