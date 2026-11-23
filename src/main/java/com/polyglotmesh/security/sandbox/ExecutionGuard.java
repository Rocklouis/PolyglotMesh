package com.polyglotmesh.security.sandbox;

import org.graalvm.polyglot.Context;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class ExecutionGuard {

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public ScheduledFuture<?> scheduleCancellation(
            Context context, Duration timeout) {

        if (context == null || timeout == null
                || timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("Invalid context or timeout");
        }

        return scheduler.schedule(() -> {
            try {
                context.close(true);
            } catch (RuntimeException e) {
                // Context may already be closed.
            }
        }, timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }
}