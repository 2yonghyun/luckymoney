package com.kakao.pay.luckymoney.repository;

import com.kakao.pay.luckymoney.domain.Sprinkle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class SprinkleRepository {

    private final EntityManager em;

    public void save(Sprinkle sprinkle) {
        em.persist(sprinkle);
    }

    public Sprinkle findByToken(String token) throws NoResultException {
        return em.createQuery("select s from Sprinkle s where s.token = :token", Sprinkle.class)
                .setParameter("token", token)
                .getSingleResult();
    }

    public Sprinkle findSprinkleByTokenAndSprinkledTimeGreaterThan(String token, int days)  throws NoResultException {
        return em.createQuery(
                "select s from Sprinkle s" +
                        " join fetch s.pickups" +
                        " where s.token = :token and s.sprinkledTime > :beforeDays", Sprinkle.class)
                .setParameter("token", token)
                .setParameter("beforeDays", LocalDateTime.now().minusDays(days))
                .getSingleResult();
    }
}
