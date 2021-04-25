package io.andrea.demorequestonhold.api;

import io.andrea.demorequestonhold.onhold.OnHoldAcquireRequestDto;
import io.andrea.demorequestonhold.onhold.OnHoldAcquireResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@RequestMapping("/api/onhold")
public interface OnHoldController {

    @PostMapping("/aquire")
    DeferredResult<ResponseEntity<OnHoldAcquireResponseDto>> aquire(@RequestBody OnHoldAcquireRequestDto onHoldaquireRequestDto) throws InterruptedException;

    @GetMapping("/unlock/{identifier}")
    OnHoldAcquireResponseDto unlock(@PathVariable("identifier") String identifier);
}
