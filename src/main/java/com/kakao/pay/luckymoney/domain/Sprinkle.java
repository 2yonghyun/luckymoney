package com.kakao.pay.luckymoney.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sprinkle {

    @OneToMany(mappedBy = "sprinkle")
    private final List<Pickup> pickups = new ArrayList<>();
    @Id @GeneratedValue
    @Column(name = "sprinkle_id")
    private Long id;
    @Column(name = "token", length = 3, nullable = false, unique = true)
    private String token;
    @NotEmpty
    private String roomId;
    @NotNull
    private Long userId;
    @NotNull
    @Min(value = 1)
    private int amount;
    @NotNull
    @Min(value = 1)
    private int divideNumber;
    @NotNull
    private LocalDateTime sprinkledTime;

    public Sprinkle(String roomId, Long userId, int totalAmount, int divideNumber) {
        this.token = createToken();
        this.roomId = roomId;
        this.userId = userId;
        this.amount = totalAmount;
        this.divideNumber = divideNumber;
        this.sprinkledTime = LocalDateTime.now();
    }

    private String createToken() {
        return RandomStringUtils.randomAlphanumeric(3);
    }

    public boolean isExpired(int minutes) {
        return sprinkledTime.isBefore(LocalDateTime.now().minusMinutes(minutes));
    }
}
