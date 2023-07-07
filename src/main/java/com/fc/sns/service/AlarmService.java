package com.fc.sns.service;

import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmService {
    private final EmitterRepository emitterRepository;
    private static final long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private static final String ALARM_NAME = "alarm";

    public SseEmitter connectAlarm(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);// A specialization of ResponseBodyEmitter for sending
        emitterRepository.save(userId, emitter);
        //완료
        emitter.onCompletion(() -> emitterRepository.delete(userId));//delete
        //타임아웃시
        emitter.onTimeout(() -> emitterRepository.delete(userId));//delete
        //emitter send
        emitterSend(0L, emitter, "connect completed");

        return emitter;
    }

    public void send(Long alarmId, Long userId) {
        emitterRepository
                .get(userId)
                .ifPresentOrElse(
                        emitter -> emitterSend(alarmId, emitter, "new alarm"),
                        () -> log.info("No emitter found")
                );
    }

    private void emitterSend(Long alarmId, SseEmitter emitter, String data) {
        SseEmitter.SseEventBuilder sendData = SseEmitter.event()
                .id(String.valueOf(alarmId))//event id; 마지막 connect 식별하여 그 다음 event 를 이어나갈 수 있는 용도로 사용
                .name(ALARM_NAME)//eventSource.addEventListener("alarm", ... event name
                .data(data);//전송 데이터
        try {
            emitter.send(sendData);
        } catch (IOException e) {
            throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }
    }

}
