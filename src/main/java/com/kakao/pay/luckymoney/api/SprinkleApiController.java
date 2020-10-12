package com.kakao.pay.luckymoney.api;

import com.kakao.pay.luckymoney.api.dto.CreateSprinkleRequestDto;
import com.kakao.pay.luckymoney.api.dto.CreateSprinkleResponseDto;
import com.kakao.pay.luckymoney.api.dto.FindPickupResponseDto;
import com.kakao.pay.luckymoney.api.dto.FindSprinkleResponseDto;
import com.kakao.pay.luckymoney.api.dto.UpdatePickupResponseDto;
import com.kakao.pay.luckymoney.constant.HttpHeaderKey;
import com.kakao.pay.luckymoney.domain.Pickup;
import com.kakao.pay.luckymoney.domain.Sprinkle;
import com.kakao.pay.luckymoney.service.SprinkleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/luckymoney")
@RequiredArgsConstructor
public class SprinkleApiController {

    private final SprinkleService sprinkleService;

    @GetMapping("/v1/health")
    public String healthCheck() {
        return "OK";
    }

    @GetMapping("/v1/sprinkles/{token}")
    public ResponseEntity<FindSprinkleResponseDto> inquire(@RequestHeader(HttpHeaderKey.USER_ID) Long userId,
                                                             @PathVariable("token") String token) {
        log.debug("request[GET][/v1/{token}]: userId={}, token={}", userId, token);

        Sprinkle foundSprinkle = sprinkleService.searchSprinkleByTokenWithPickupInDays(token, userId);

        FindSprinkleResponseDto responseBody = createFindSprinkleResponseDto(foundSprinkle);
        log.debug("response[GET][/v1/{token}]: body={}", responseBody);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping("/v1/sprinkles")
    public ResponseEntity<CreateSprinkleResponseDto> sprinkle(@RequestHeader(HttpHeaderKey.ROOM_ID) String roomId,
                                                              @RequestHeader(HttpHeaderKey.USER_ID) Long userId,
                                                              @RequestBody @Valid CreateSprinkleRequestDto requestBody) {
        log.debug("request[POST][/v1/sprinkles]: roomId={}, userId={}, body={}", roomId, userId, requestBody);


        String token = sprinkleService.sprinkleAndCreatePickups(roomId, userId, requestBody.getTotalAmount(), requestBody.getDivideNumber());

        CreateSprinkleResponseDto responseBody = new CreateSprinkleResponseDto(token);
        log.debug("request[POST][/v1/sprinkles]: body={}", responseBody);
        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }

    @PutMapping("/v1/sprinkles/pickups/{token}")
    public ResponseEntity<UpdatePickupResponseDto> pickup(@RequestHeader(HttpHeaderKey.ROOM_ID) String roomId,
                                                          @RequestHeader(HttpHeaderKey.USER_ID) Long userId,
                                                          @PathVariable("token") String token) {
        log.debug("request[PUT][/v1/sprinkles/pickups/{token}]: roomId={}, userId={}, token={}", roomId, userId, token);

        int amount = sprinkleService.pickup(roomId, userId, token);

        UpdatePickupResponseDto responseBody = new UpdatePickupResponseDto(amount);
        log.debug("request[PUT][/v1/sprinkles/pickups/{token}]: body={}", responseBody);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    private FindSprinkleResponseDto createFindSprinkleResponseDto(Sprinkle foundSprinkle) {
        List<Pickup> foundSprinklePickups = foundSprinkle.getPickups();

        return new FindSprinkleResponseDto(foundSprinkle.getSprinkledTime()
                , foundSprinkle.getAmount()
                , foundSprinklePickups.stream().filter(Pickup::isPicked).mapToInt(Pickup::getAmount).sum()
                , foundSprinklePickups.stream()
                .filter(Pickup::isPicked)
                .map(p -> new FindPickupResponseDto(p.getUserId(), p.getAmount()))
                .collect(Collectors.toList())
        );
    }
}
