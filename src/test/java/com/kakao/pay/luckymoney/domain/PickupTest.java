package com.kakao.pay.luckymoney.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PickupTest {

    @Test
    public void isPicked_줍기완료확인() throws Exception {
        //given
        Sprinkle sprinkle = new Sprinkle("testRoomId", 1L, 1000, 3);
        Pickup pickup1 = new Pickup(sprinkle, 500);
        Pickup pickup2 = new Pickup(sprinkle, 200);

        //when
        pickup1.updateUserIdAndPickedTime(2L);
        boolean isPicked = pickup1.isPicked();
        boolean isNotPicked = pickup2.isPicked();

        //then
        assertTrue("Pickup의 userId가 null이 아닌 경우 true를 반환해야 합니다.", isPicked);
        assertFalse("Pickup의 userId가 null인 경우 false를 반환해야 합니다.", isNotPicked);
    }

    @Test
    public void isNotPicked_미달성줍기확인() throws Exception {
        //given
        Sprinkle sprinkle = new Sprinkle("testRoomId", 1L, 1000, 3);
        Pickup pickup1 = new Pickup(sprinkle, 500);
        Pickup pickup2 = new Pickup(sprinkle, 200);

        //when
        pickup1.updateUserIdAndPickedTime(2L);
        boolean isPicked = pickup1.isNotPicked();
        boolean isNotPicked = pickup2.isNotPicked();

        //then
        assertFalse("Pickup의 userId가 null이 아닌 경우 false를 반환해야 합니다.", isPicked);
        assertTrue("Pickup의 userId가 null인 경우 true를 반환해야 합니다.", isNotPicked);
    }
}