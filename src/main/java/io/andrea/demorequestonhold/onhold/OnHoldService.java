package io.andrea.demorequestonhold.onhold;

import io.andrea.demorequestonhold.request.RequestOnHold;
import io.andrea.demorequestonhold.request.RequestOnHoldRepository;
import io.andrea.demorequestonhold.request.RequestOnHoldStatus;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableScheduling
@AllArgsConstructor
public class OnHoldService {

    /**
     * Refer to https://www.baeldung.com/spring-deferred-result
     */

    private static final long DEFERRED_TIME_OUT_MILLIS = 2500;
    private static final long POLLING_DELAY_MILLIS = 30;
    private static final long REMOVE_ORPHAN_EACH_MILLIS = 10000;
    private static final ConcurrentHashMap<String, DeferredResult<ResponseEntity<OnHoldAcquireResponseDto>>> deferredResults = new ConcurrentHashMap<>();

    private final RequestOnHoldRepository requestOnHoldRepository;

    public DeferredResult<ResponseEntity<OnHoldAcquireResponseDto>> aquire(OnHoldAcquireRequestDto onHoldaquireRequestDto) {
        DeferredResult<ResponseEntity<OnHoldAcquireResponseDto>> deferredResult = new DeferredResult<>(DEFERRED_TIME_OUT_MILLIS);
        deferredResults.put(onHoldaquireRequestDto.getIdentifier(), deferredResult);

        RequestOnHold requestOnHold = new RequestOnHold();
        requestOnHold.setIdentifier(onHoldaquireRequestDto.getIdentifier());
        requestOnHold.setExpireOn(new Timestamp(System.currentTimeMillis() + DEFERRED_TIME_OUT_MILLIS));
        requestOnHoldRepository.save(requestOnHold);

        OnHoltUtil.configureOnTimeoutAction(
                deferredResult,
                onHoldaquireRequestDto.getIdentifier(),
                requestOnHoldRepository,
                deferredResults);
        return deferredResult;
    }

    public boolean unlock(String identifier) {
        Optional<RequestOnHold> r = requestOnHoldRepository.findById(identifier);
        if (r.isPresent()) {
            return unlock(r.get());
        }
        return false;
    }

    public boolean unlock(RequestOnHold request) {
        if (deferredResults.containsKey(request.getIdentifier())) {
            DeferredResult<ResponseEntity<OnHoldAcquireResponseDto>> deferredResult = deferredResults.remove(request.getIdentifier());
            deferredResult.setResult(null);
            requestOnHoldRepository.delete(request);
            return true;
        } else if (request.getStatus() != RequestOnHoldStatus.RECEIVED) {
            request.setStatus(RequestOnHoldStatus.RECEIVED);
            requestOnHoldRepository.save(request);
            return false;
        }
        return false;
    }

    @Scheduled(fixedDelay = POLLING_DELAY_MILLIS)
    private void checkForRequestReceived() {
        requestOnHoldRepository.getReceivedRequest().forEach(this::unlock);
    }

    @Scheduled(fixedDelay = REMOVE_ORPHAN_EACH_MILLIS)
    private void removeOrphanRequests() {
        Timestamp t = new Timestamp(System.currentTimeMillis() - REMOVE_ORPHAN_EACH_MILLIS);
        requestOnHoldRepository.deleteSignalsExpiredBefore(t);
    }
}
