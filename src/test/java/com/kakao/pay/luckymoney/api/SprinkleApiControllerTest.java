package com.kakao.pay.luckymoney.api;

import com.kakao.pay.luckymoney.api.dto.CreateSprinkleRequestDto;
import com.kakao.pay.luckymoney.api.dto.FindPickupResponseDto;
import com.kakao.pay.luckymoney.api.dto.FindSprinkleResponseDto;
import com.kakao.pay.luckymoney.domain.Pickup;
import com.kakao.pay.luckymoney.domain.Sprinkle;
import com.kakao.pay.luckymoney.service.SprinkleService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SprinkleApiControllerTest {

    @Autowired
    SprinkleApiController sprinkleApiController;

    @Autowired
    TestRestTemplate testRestTemplate;

    @MockBean
    SprinkleService mockSprinkleService;

    @Test
    public void healthCheck_건강체크() throws Exception {
        //given

        //when
        String result = testRestTemplate.getForObject("/luckymoney/v1/health", String.class);

        //then
        Assert.assertEquals("건강체크는 OK를 반환합니다.", "OK", result);
    }

    @Test
    public void inquire_조회확인() throws Exception {
        //given
        Long userId = 123L;
        String roomId = "testRoomId";
        String token = "abc";
        Sprinkle sprinkle = Mockito.mock(Sprinkle.class);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-USER-ID", userId.toString());

        //when
        when(mockSprinkleService.searchSprinkleByTokenWithPickupInDays(token, userId)).thenReturn(sprinkle);
        ResponseEntity<FindSprinkleResponseDto> result = testRestTemplate.exchange(
                "/luckymoney/v1/sprinkles/" + token, HttpMethod.GET, new HttpEntity<>(headers), FindSprinkleResponseDto.class);

        //then
        Assert.assertEquals("정상 조회에서는 200응답을 받아야 합니다.", HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void sprinkle_뿌리기확인() throws Exception {
        //given
        Long userId = 123L;
        String roomId = "testRoomId";
        int totalAmount = 1000;
        int divideNumber = 2;
        String token = "abc";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("X-USER-ID", userId.toString());
        headers.add("X-ROOM-ID", roomId);

        CreateSprinkleRequestDto requestDto = new CreateSprinkleRequestDto(totalAmount, divideNumber);

        HttpEntity<CreateSprinkleRequestDto> request = new HttpEntity<>(requestDto, headers);

        //when
        when(mockSprinkleService.sprinkleAndCreatePickups(roomId, userId, totalAmount, divideNumber)).thenReturn(token);
        ResponseEntity<CreateSprinkleRequestDto> result = testRestTemplate.postForEntity(
                "/luckymoney/v1/sprinkles/", request, CreateSprinkleRequestDto.class);

        //then
        Assert.assertEquals("정상 뿌리기에서는 201응답을 받아야 합니다.", HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    public void pickup_줍기확인() throws Exception {
        //given
        Long userId = 123L;
        String roomId = "testRoomId";
        String token = "abc";
        int amount = 200;

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-USER-ID", userId.toString());
        headers.add("X-ROOM-ID", roomId);

        //when
        when(mockSprinkleService.pickup(roomId, userId, token)).thenReturn(amount);
        ResponseEntity<String> result = testRestTemplate.exchange(
                "/luckymoney/v1/sprinkles/pickups/" + token, HttpMethod.PUT, new HttpEntity<>(headers), String.class);

        //then
        Assert.assertEquals("정상 줍기에서는 200응답을 받아야 합니다.", HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void createFindSprinkleResponseDto_조회응답객체생성확인() throws Exception {
        //given
        String roomId = "testRoomId";
        Long sprinkleUserId = 1000L;
        int totalAmount = 1000;
        Long pickupUserId = 123L;
        int pickAmount1 = 300;
        Sprinkle sprinkle = new Sprinkle(roomId, sprinkleUserId, totalAmount, 2);
        Pickup pickup1 = new Pickup(sprinkle, pickAmount1);
        new Pickup(sprinkle, totalAmount - pickAmount1);
        pickup1.updateUserIdAndPickedTime(pickupUserId);

        //when
        FindSprinkleResponseDto result = ReflectionTestUtils.invokeMethod(sprinkleApiController, "createFindSprinkleResponseDto", sprinkle);

        //then
        List<FindPickupResponseDto> expectedPickedPickupList = new ArrayList<>();
        expectedPickedPickupList.add(new FindPickupResponseDto(pickupUserId, pickAmount1));
        FindSprinkleResponseDto expected = new FindSprinkleResponseDto(sprinkle.getSprinkledTime(), sprinkle.getAmount(), pickAmount1, expectedPickedPickupList);

        assertEquals("생성된 결과 기대치와 같아야 합니다.", expected, result);
    }
}