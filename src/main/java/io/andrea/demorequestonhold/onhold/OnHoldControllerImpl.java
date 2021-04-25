package io.andrea.demorequestonhold.onhold;

import io.andrea.demorequestonhold.api.OnHoldController;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@AllArgsConstructor
public class OnHoldControllerImpl implements OnHoldController {

    private final OnHoldService service;

    @Override
    public DeferredResult<ResponseEntity<OnHoldAcquireResponseDto>> aquire(OnHoldAcquireRequestDto onHoldaquireRequestDto) {
        return service.aquire(onHoldaquireRequestDto);
    }

    @Override
    public OnHoldAcquireResponseDto unlock(String identifier) {
        service.unlock(identifier);
        return null;
    }
}
