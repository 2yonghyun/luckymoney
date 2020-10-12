package com.kakao.pay.luckymoney.service;

import com.kakao.pay.luckymoney.constant.ErrorCode;
import com.kakao.pay.luckymoney.domain.Pickup;
import com.kakao.pay.luckymoney.domain.Sprinkle;
import com.kakao.pay.luckymoney.exception.ApiException;
import com.kakao.pay.luckymoney.repository.PickupRepository;
import com.kakao.pay.luckymoney.repository.SprinkleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class SprinkleServiceTest {

    @Autowired SprinkleService sprinkleService;
    @Autowired SprinkleRepository sprinkleRepository;
    @Autowired PickupRepository pickupRepository;

    @Test
    public void searchSprinkleByTokenWithPickupInDays_토큰으로몇일안뿌리기찾기() throws Exception {
        //given
        String roomId = "testRoomId";
        Long userId = 1000L;
        Sprinkle sprinkle = new Sprinkle(roomId, userId, 1000, 3);
        sprinkleRepository.save(sprinkle);
        pickupRepository.save(new Pickup(sprinkle, 200));
        pickupRepository.save(new Pickup(sprinkle, 300));
        pickupRepository.save(new Pickup(sprinkle, 500));

        String token = sprinkle.getToken();

        //when
        Sprinkle foundSprinkle = sprinkleService.searchSprinkleByTokenWithPickupInDays(token, userId);

        //then
        assertEquals("토큰을 통해 찾은 뿌리기객체는 같은 토큰값을 가진 객체와 같아야 합니다.", sprinkle, foundSprinkle);
    }

    @Test
    public void searchSprinkleByTokenWithPickupInDays_토큰으로몇일안뿌리기찾기_올바르지않은토큰() throws Exception {
        //given
        String roomId = "testRoomId";
        Long userId = 1000L;
        Sprinkle sprinkle = new Sprinkle(roomId, userId, 1000, 3);
        sprinkleRepository.save(sprinkle);
        pickupRepository.save(new Pickup(sprinkle, 200));
        pickupRepository.save(new Pickup(sprinkle, 300));
        pickupRepository.save(new Pickup(sprinkle, 500));
        String token = sprinkle.getToken();
        
        //when
        token = token.replace(token.charAt(0), token.charAt(1)).replace(token.charAt(2), token.charAt(0));

        //then
        try {
            sprinkleService.searchSprinkleByTokenWithPickupInDays(token, userId);
            fail("예외가 발생해야만 합니다.");
        } catch (ApiException e) {
            assertEquals("에러코드가 일치해야만 합니다.", ErrorCode.E0101, e.getErrorCode());
        }
    }

    @Test
    public void searchSprinkleByTokenWithPickupInDays_토큰으로몇일안뿌리기찾기_조회기간경과() throws Exception {
        //given
        String roomId = "testRoomId";
        Long userId = 1000L;
        Sprinkle sprinkle = new Sprinkle(roomId, userId, 1000, 3);
        sprinkleRepository.save(sprinkle);
        pickupRepository.save(new Pickup(sprinkle, 200));
        pickupRepository.save(new Pickup(sprinkle, 300));
        pickupRepository.save(new Pickup(sprinkle, 500));

        String token = sprinkle.getToken();

        //when
        ReflectionTestUtils.setField(sprinkle, "sprinkledTime", LocalDateTime.now().minusDays(7));

        //then
        try {
            sprinkleService.searchSprinkleByTokenWithPickupInDays(token, userId);
            fail("예외가 발생해야만 합니다.");
        } catch (ApiException e) {
            assertEquals("에러코드가 일치해야만 합니다.", ErrorCode.E0101, e.getErrorCode());
        }
    }

    @Test
    public void searchSprinkleByTokenWithPickupInDays_토큰으로몇일안뿌리기찾기_본인확인() throws Exception {
        //given
        String roomId = "testRoomId";
        Long userId = 1000L;
        Sprinkle sprinkle = new Sprinkle(roomId, userId, 1000, 3);
        sprinkleRepository.save(sprinkle);
        pickupRepository.save(new Pickup(sprinkle, 200));
        pickupRepository.save(new Pickup(sprinkle, 300));
        pickupRepository.save(new Pickup(sprinkle, 500));

        String token = sprinkle.getToken();

        //when
        userId = 9L;

        //then
        try {
            sprinkleService.searchSprinkleByTokenWithPickupInDays(token, userId);
            fail("예외가 발생해야만 합니다.");
        } catch (ApiException e) {
            assertEquals("에러코드가 일치해야만 합니다.", ErrorCode.E0301, e.getErrorCode());
        }
    }

    @Test
    public void sprinkleAndCreatePickups_뿌리기와줍기을저장후토큰을반환() throws Exception {
        //given
        String roomId = "roomId";
        Long userId = 1000L;
        int totalAmount = 1000;
        int divideNumber = 3;

        //when
        String token = sprinkleService.sprinkleAndCreatePickups(roomId, userId, totalAmount, divideNumber);

        //then
        Sprinkle savedSprinkle = sprinkleRepository.findByToken(token);
        List<Pickup> pickups = savedSprinkle.getPickups();

        assertEquals("반환된 토큰은 3자리여야 합니다.", 3, token.length());
        assertEquals("입력한 파라미터와 저장된 값은 같아야 합니다.", roomId, savedSprinkle.getRoomId());
        assertEquals("입력한 파라미터와 저장된 값은 같아야 합니다.", userId, savedSprinkle.getUserId());
        assertEquals("입력한 파라미터와 저장된 값은 같아야 합니다.", totalAmount, savedSprinkle.getAmount());
        assertEquals("입력한 파라미터와 저장된 값은 같아야 합니다.", divideNumber, savedSprinkle.getDivideNumber());
        assertEquals("나누려는 수 만큼 줍기객체도 생성되어야 합니다.", divideNumber, pickups.size());
    }

    @Test
    public void sprinkleAndCreatePickups_뿌리기와줍기을생성후저장_뿌기기금액부족체크() throws Exception {
        //given
        String roomId = "roomId";
        Long userId = 1000L;

        //when
        int totalAmount = 1;
        int divideNumber = 3;

        //then
        try {
            sprinkleService.sprinkleAndCreatePickups(roomId, userId, totalAmount, divideNumber);
            fail("예외가 발생해야만 합니다.");
        } catch (ApiException e) {
            assertEquals("에러코드가 일치해야만 합니다.", ErrorCode.E0107, e.getErrorCode());
        }
    }

    @Test
    public void createPickups_랜덤값으로줍기을생성후저장() throws Exception {
        //given
        String roomId = "testRoomId";
        Long userId = 1000L;
        int sprinkleAmount = 1000;
        int divideNumber = 2;
        Sprinkle sprinkle = new Sprinkle(roomId, userId, sprinkleAmount, divideNumber);

        //when
        sprinkleService.createPickups(sprinkle);
        
        //then
        List<Pickup> pickups = sprinkle.getPickups();
        Pickup pickup1 = pickups.get(0);
        Pickup pickup2 = pickups.get(1);

        assertEquals("생성된 줍기객체의 수는 나누려는 수는 같아야 합니다.", divideNumber, pickups.size());
        assertNotEquals("생성된 줍기객체의 금액은 랜덤해야 합니다.", pickup1.getAmount(), pickup2.getAmount());
        assertEquals("생성된 줍기객체의 금액의 합은 뿌리기의 금액과 같아야 합니다."
                , sprinkleAmount, pickups.stream().mapToInt(Pickup::getAmount).sum());
    }
    
    @Test
    public void pickup_대상줍기갱신후금액반환() throws Exception {
        //given
        String roomId = "testRoomId";
        Long sprinkleUserId = 1000L;
        Long pickupUserId = 9L;
        Sprinkle sprinkle = new Sprinkle(roomId, sprinkleUserId, 1000, 3);
        sprinkleRepository.save(sprinkle);
        new Pickup(sprinkle, 200);
        new Pickup(sprinkle, 300);
        new Pickup(sprinkle, 500);
        String token = sprinkle.getToken();

        //when
        int pickedAmount1 = sprinkleService.pickup(roomId, pickupUserId, token);

        //then
        Pickup pickup = sprinkle.getPickups().get(0);

        assertEquals("줍기를 시도한 유저아이디가 갱신되어야 합니다.", pickupUserId, pickup.getUserId());
        assertNotNull("줍기를 시도한 시간이 갱신되어야 합니다.", pickup.getPickedTime());
        assertEquals("줍기를 시도한 액수와 줍기객체의 액수가 같아야 합니다.", pickup.getAmount(), pickedAmount1);
    }

    @Test
    public void pickup_대상뿌리기검색후줍기_존재하지않는대상() throws Exception {
        //given
        String roomId = "testRoomId";
        Long sprinkleUserId = 1000L;
        Long pickupUserId = 9L;
        Sprinkle sprinkle = new Sprinkle(roomId, sprinkleUserId, 1000, 3);
        sprinkleRepository.save(sprinkle);
        new Pickup(sprinkle, 200);
        new Pickup(sprinkle, 300);
        new Pickup(sprinkle, 500);
        String token = sprinkle.getToken();

        //when
        token = token.replace(token.charAt(0), token.charAt(1)).replace(token.charAt(2), token.charAt(0));

        //then
        try {
            ReflectionTestUtils.invokeMethod(sprinkleService, "pickup", roomId, pickupUserId, token);
            fail("예외가 발생해야만 합니다.");
        } catch (ApiException e) {
            assertEquals("에러코드가 일치해야만 합니다.", ErrorCode.E0102, e.getErrorCode());
        }
    }

    @Test
    public void validateBeforePickup_줍기전조건확인() throws Exception {
        //given
        String roomId = "testRoomId";
        Long sprinkleUserId = 1000L;
        Long pickupUserId = 9L;
        Sprinkle sprinkle = new Sprinkle(roomId, sprinkleUserId, 1000, 3);
        sprinkleRepository.save(sprinkle);
        new Pickup(sprinkle, 200);
        new Pickup(sprinkle, 300);
        new Pickup(sprinkle, 500);

        //when
        ReflectionTestUtils.invokeMethod(sprinkleService, "validateBeforePickup", roomId, pickupUserId, sprinkle);

        //then
    }

    @Test
    public void validateBeforePickup_줍기전조건확인_동일방체크() throws Exception {
        //given
        String roomId = "testRoomId";
        Long sprinkleUserId = 1000L;
        Long pickupUserId = 9L;
        Sprinkle sprinkle = new Sprinkle(roomId, sprinkleUserId, 1000, 3);
        sprinkleRepository.save(sprinkle);
        new Pickup(sprinkle, 200);
        new Pickup(sprinkle, 300);
        new Pickup(sprinkle, 500);

        //when
        roomId = "testDifferentRoomId";

        //then
        try {
            ReflectionTestUtils.invokeMethod(sprinkleService, "validateBeforePickup", roomId, pickupUserId, sprinkle);
            fail("예외가 발생해야만 합니다.");
        } catch (ApiException e) {
            assertEquals("에러코드가 일치해야만 합니다.", ErrorCode.E0302, e.getErrorCode());
        }
    }

    @Test
    public void validateBeforePickup_줍기전조건확인_자기자신체크() throws Exception {
        //given
        String roomId = "testRoomId";
        Long sprinkleUserId = 1000L;
        Sprinkle sprinkle = new Sprinkle(roomId, sprinkleUserId, 1000, 3);
        sprinkleRepository.save(sprinkle);
        new Pickup(sprinkle, 200);
        new Pickup(sprinkle, 300);
        new Pickup(sprinkle, 500);

        //when
        Long pickupUserId = 1000L;

        //then
        try {
            ReflectionTestUtils.invokeMethod(sprinkleService, "validateBeforePickup", roomId, pickupUserId, sprinkle);
            fail("예외가 발생해야만 합니다.");
        } catch (ApiException e) {
            assertEquals("에러코드가 일치해야만 합니다.", ErrorCode.E0103, e.getErrorCode());
        }
    }

    @Test
    public void validateBeforePickup_줍기전조건확인_유효기간체크() throws Exception {
        //given
        String roomId = "testRoomId";
        Long sprinkleUserId = 1000L;
        Long pickupUserId = 9L;
        Sprinkle sprinkle = new Sprinkle(roomId, sprinkleUserId, 1000, 3);
        sprinkleRepository.save(sprinkle);
        new Pickup(sprinkle, 200);
        new Pickup(sprinkle, 300);
        new Pickup(sprinkle, 500);

        //when
        ReflectionTestUtils.setField(sprinkle, "sprinkledTime", LocalDateTime.now().minusMinutes(10));

        //then
        try {
            ReflectionTestUtils.invokeMethod(sprinkleService, "validateBeforePickup", roomId, pickupUserId, sprinkle);
            fail("예외가 발생해야만 합니다.");
        } catch (ApiException e) {
            assertEquals("에러코드가 일치해야만 합니다.", ErrorCode.E0104, e.getErrorCode());
        }
    }

    @Test
    public void validateBeforePickup_줍기전조건확인_남은줍기체크() throws Exception {
        //given
        String roomId = "testRoomId";
        Long sprinkleUserId = 1000L;
        Long pickupUserId = 9L;
        Sprinkle sprinkle = new Sprinkle(roomId, sprinkleUserId, 1000, 3);
        sprinkleRepository.save(sprinkle);
        new Pickup(sprinkle, 200);
        new Pickup(sprinkle, 300);
        new Pickup(sprinkle, 500);

        //when
        ReflectionTestUtils.setField(sprinkle.getPickups().get(0), "userId", 1L);
        ReflectionTestUtils.setField(sprinkle.getPickups().get(1), "userId", 2L);
        ReflectionTestUtils.setField(sprinkle.getPickups().get(2), "userId", 3L);

        //then
        try {
            ReflectionTestUtils.invokeMethod(sprinkleService, "validateBeforePickup", roomId, pickupUserId, sprinkle);
            fail("예외가 발생해야만 합니다.");
        } catch (ApiException e) {
            assertEquals("에러코드가 일치해야만 합니다.", ErrorCode.E0105, e.getErrorCode());
        }
    }

    @Test
    public void validateBeforePickup_줍기전조건확인_두번줍기금지체크() throws Exception {
        //given
        String roomId = "testRoomId";
        Long sprinkleUserId = 1000L;
        Long pickupUserId = 9L;
        Sprinkle sprinkle = new Sprinkle(roomId, sprinkleUserId, 1000, 3);
        sprinkleRepository.save(sprinkle);
        new Pickup(sprinkle, 200);
        new Pickup(sprinkle, 300);
        new Pickup(sprinkle, 500);

        //when
        ReflectionTestUtils.setField(sprinkle.getPickups().get(0), "userId", pickupUserId);

        //then
        try {
            ReflectionTestUtils.invokeMethod(sprinkleService, "validateBeforePickup", roomId, pickupUserId, sprinkle);
            fail("예외가 발생해야만 합니다.");
        } catch (ApiException e) {
            assertEquals("에러코드가 일치해야만 합니다.", ErrorCode.E0106, e.getErrorCode());
        }
    }

    @Test
    public void generateRandomMoneyWithDivideNumber_랜덤으로돈나누기확인() throws Exception {
        //given
        int leftover = 1000;
        int divideNumber = 3;

        //when
        int money1 = (int) Optional.ofNullable(
                ReflectionTestUtils.invokeMethod(sprinkleService, "generateRandomMoneyWithDivideNumber", leftover, divideNumber))
                .orElseThrow();

        leftover -= money1;
        divideNumber--;

        int money2 = (int) Optional.ofNullable(
                ReflectionTestUtils.invokeMethod(sprinkleService, "generateRandomMoneyWithDivideNumber", leftover, divideNumber))
                .orElseThrow();
        leftover -= money2;
        divideNumber--;

        int money3 = (int) Optional.ofNullable(
                ReflectionTestUtils.invokeMethod(sprinkleService, "generateRandomMoneyWithDivideNumber", leftover, divideNumber))
                .orElseThrow();

        int money4 = (int) Optional.ofNullable(
                ReflectionTestUtils.invokeMethod(sprinkleService, "generateRandomMoneyWithDivideNumber", 1000, -1))
                .orElseThrow();

        int money5 = (int) Optional.ofNullable(
                ReflectionTestUtils.invokeMethod(sprinkleService, "generateRandomMoneyWithDivideNumber", 2, 5))
                .orElseThrow();

        //then
        assertNotEquals("생성된 금액은 랜덤해야 합니다.", money1, money2);
        assertEquals("나눠가지는 수가 마지막일 경우 잔액의 전부를 분배 받아야 합니다.", leftover, money3);
        assertEquals("나눠가지는 수가 1보다 낮을 경우 0을 반환합니다.", 0, money4);
        assertEquals("나눠가지는 수가 잔액이 낮을 경우 0을 반환합니다.", 0, money5);
    }
}