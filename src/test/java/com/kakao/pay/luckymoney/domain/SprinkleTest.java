package com.kakao.pay.luckymoney.domain;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class SprinkleTest {

    @Test
    public void createToken_토큰생성확인() throws Exception {
        //given
        Sprinkle sprinkle = new Sprinkle();

        //when
        String token1 = ReflectionTestUtils.invokeMethod(sprinkle, "createToken");
        String token2 = ReflectionTestUtils.invokeMethod(sprinkle, "createToken");

        //then
        assertEquals("토큰의 길이는 3이어야 합니다.", 3, token1.length(), token2.length());
        assertNotEquals("토큰은 랜덤해야 합니다.", token1, token2);
    }

    @Test
    public void isExpired_유효기간확인() throws Exception {
        //given
        Sprinkle sprinkle = new Sprinkle("testRoomId", 1L, 1000, 3);

        //when
        boolean before = sprinkle.isExpired(-1);
        boolean after = sprinkle.isExpired(10);

        //then
        assertTrue("유효기간이 지나면 true를 리턴해야 합니다.", before);
        assertFalse("유효기간이 지나지 않으면 false를 리턴해야 합니다.", after);
    }
}