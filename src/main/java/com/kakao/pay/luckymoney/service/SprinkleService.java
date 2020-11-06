package com.kakao.pay.luckymoney.service;

import com.kakao.pay.luckymoney.constant.ErrorCode;
import com.kakao.pay.luckymoney.domain.Pickup;
import com.kakao.pay.luckymoney.domain.Sprinkle;
import com.kakao.pay.luckymoney.exception.ApiException;
import com.kakao.pay.luckymoney.repository.PickupRepository;
import com.kakao.pay.luckymoney.repository.SprinkleRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SprinkleService {

    private final SprinkleRepository sprinkleRepository;
    private final PickupRepository pickupRepository;

    public Sprinkle searchSprinkleByTokenWithPickupInDays(String token, Long userId) {
        Sprinkle foundSprinkle;

        try {
            foundSprinkle = sprinkleRepository.findSprinkleByTokenAndSprinkledTimeGreaterThan(token, 7);
        } catch (NoResultException e) {
            throw new ApiException(ErrorCode.E0101); // BadRequest 400
        }

        if (!foundSprinkle.getUserId().equals(userId)) {
            throw new ApiException(ErrorCode.E0301);  // Forbidden 403
        }

        return foundSprinkle;
    }

    @Transactional
    public String sprinkleAndCreatePickups(String roomId, Long userId, int totalAmount, int divideNumber) {
        if (totalAmount < divideNumber) throw new ApiException(ErrorCode.E0107); // BadRequest 400

        Sprinkle createdSprinkle = new Sprinkle(roomId, userId, totalAmount, divideNumber);

        try {
            sprinkleRepository.save(createdSprinkle);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.E0001); // InternalServiceError 500
        }

        createPickups(createdSprinkle);

        return createdSprinkle.getToken();
    }

    public void createPickups(Sprinkle sprinkle) {
        int leftover = sprinkle.getAmount();
        int divideNumber = sprinkle.getDivideNumber();

        for (int i = divideNumber; i > 0; i--) {
            int randomMoney = generateRandomMoneyWithDivideNumber(leftover, i);
            pickupRepository.save(new Pickup(sprinkle, randomMoney));
            leftover -= randomMoney;
        }
    }

    @Transactional
    public int pickup(String roomId, Long userId, String token) {
        Sprinkle targetSprinkle;

        try {
            targetSprinkle = sprinkleRepository.findByToken(token);
        } catch (NoResultException e) {
            throw new ApiException(ErrorCode.E0102); // BadRequest 400
        }

        validateBeforePickup(roomId, userId, targetSprinkle);

        List<Pickup> notPickedUpList = targetSprinkle.getPickups().stream()
                .filter(Pickup::isNotPicked)
                .collect(Collectors.toList());

        Pickup pickup = notPickedUpList.get(0);
        pickup.updateUserIdAndPickedTime(userId);
        return pickup.getAmount();
    }

    private void validateBeforePickup(String roomId, Long userId, Sprinkle sprinkle) {
        if (!StringUtils.equals(roomId, sprinkle.getRoomId())) {
            throw new ApiException(ErrorCode.E0302); // Forbidden 403
        }

        if (userId.equals(sprinkle.getUserId())) {
            throw new ApiException(ErrorCode.E0103); // BadRequest 400
        }

        if (sprinkle.isExpired(10)) {
            throw new ApiException(ErrorCode.E0104); // BadRequest 400
        }

        if (sprinkle.getPickups().stream().noneMatch(Pickup::isNotPicked)) {
            throw new ApiException(ErrorCode.E0105); // BadRequest 400
        }

        if (sprinkle.getPickups().stream()
                .filter(Pickup::isPicked)
                .anyMatch(p -> p.getUserId().equals(userId))) {
            throw new ApiException(ErrorCode.E0106); // BadRequest 400
        }
    }

    private int generateRandomMoneyWithDivideNumber(int leftover, int remainDivideNumber) {
        if (remainDivideNumber < 1 || leftover < remainDivideNumber) return 0;
        else if (remainDivideNumber == 1) return leftover;
        else return (int)(Math.random() * (leftover - (remainDivideNumber - 1))) + 1;
    }
}
