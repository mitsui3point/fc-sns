package com.fc.sns.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class EmitterRepository {
    private Map<String, SseEmitter> emitterMap = new HashMap<>();

    public SseEmitter save(Long userId, SseEmitter emitter) {
        String key = getKey(userId);
        emitterMap.put(key, emitter);
        log.info("Set sseEmitter: {}", userId);
        return emitterMap.get(key);
    }

    private static String getKey(Long userId) {
        return new StringBuffer("Emitter:UID:").append(userId).toString();
    }

    public void delete(Long userId) {
        emitterMap.remove(getKey(userId));
    }

    public Optional<SseEmitter> get(Long userId) {
        String key = getKey(userId);
        log.info("Get sseEmitter: {}", userId);
        return Optional.ofNullable(emitterMap.get(key));
    }
}