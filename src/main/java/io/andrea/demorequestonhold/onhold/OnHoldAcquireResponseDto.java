package io.andrea.demorequestonhold.onhold;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OnHoldAcquireResponseDto {
    private LocalDateTime acquiredOn;
    private LocalDateTime unlockedOn;
    private String identifier;
}
