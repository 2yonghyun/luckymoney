package com.kakao.pay.luckymoney.repository;

import com.kakao.pay.luckymoney.domain.Pickup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class PickupRepository {

    private final EntityManager em;

    public void save(Pickup pickup) {
        em.persist(pickup);
    }
}
