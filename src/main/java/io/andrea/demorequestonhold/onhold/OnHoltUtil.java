package io.andrea.demorequestonhold.onhold;

import io.andrea.demorequestonhold.request.RequestOnHoldRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;

public class OnHoltUtil {

    private OnHoltUtil() {
    }

    public static void configureOnTimeoutAction(
            DeferredResult<ResponseEntity<OnHoldAcquireResponseDto>> deferredResult,
            String identifier,
            RequestOnHoldRepository semaphoreRepository,
            Map<String, ?> localDeferredResults) {
        deferredResult.onTimeout(() -> {
            semaphoreRepository.deleteById(identifier);
            deferredResult.setResult(timeoutResult());
            localDeferredResults.remove(identifier);
        });
    }

    private static ResponseEntity<OnHoldAcquireResponseDto> timeoutResult() {
        OnHoldAcquireResponseDto res = new OnHoldAcquireResponseDto();
        res.setIdentifier("TIMED-OUT");
        return ResponseEntity.ok(res);
    }
}
