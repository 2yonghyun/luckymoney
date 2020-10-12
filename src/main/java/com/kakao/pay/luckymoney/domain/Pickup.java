package com.kakao.pay.luckymoney.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pickup {

    @Id @GeneratedValue
    @Column(name = "pickup_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sprinkle_id")
    private Sprinkle sprinkle;

    @NotNull
    @Min(value = 1)
    private int amount;

    private Long userId;

    private LocalDateTime pickedTime;

    public Pickup(Sprinkle sprinkle, int amount) {
        updateSprinkle(sprinkle);
        this.amount = amount;
    }

    protected void updateSprinkle(Sprinkle sprinkle) {
        this.sprinkle = sprinkle;
        sprinkle.getPickups().add(this);
    }

    public void updateUserIdAndPickedTime(Long userId) {
        this.userId = userId;
        this.pickedTime = LocalDateTime.now();
    }

    public boolean isPicked() {
        return Objects.nonNull(userId);
    }

    public boolean isNotPicked() {
        return !isPicked();
    }
}
