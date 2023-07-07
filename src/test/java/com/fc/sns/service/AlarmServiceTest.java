package com.fc.sns.service;

import com.fc.sns.fixture.UserFixture;
import com.fc.sns.model.entity.User;
import com.fc.sns.repository.EmitterRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlarmServiceTest {

    @InjectMocks
    private AlarmService alarmService;

    @Mock
    private EmitterRepository emitterRepository;

    @Test
    void 알람구독() {
        //given
        User user = UserFixture.get("username", "p", 1L);
        SseEmitter emitter = mock(SseEmitter.class);

        //when
        when(emitterRepository.save(eq(user.getId()), any(SseEmitter.class))).thenReturn(emitter);
        Assertions.assertDoesNotThrow(() -> {
            SseEmitter sseEmitter = alarmService.connectAlarm(user.getId());
        });
        verify(emitterRepository, times(1)).save(eq(user.getId()), any(SseEmitter.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"completionCallback", "timeoutCallback"})
    void 알람을_구독후_완료시_콜백함수_호출(String fieldName) {
        //given
        User user = UserFixture.get("username", "p", 1L);

        //when
        Assertions.assertDoesNotThrow(() -> {
            SseEmitter sseEmitter = alarmService.connectAlarm(user.getId());
            reflectionCallbackExec(fieldName, sseEmitter);
        });

        //then
        verify(emitterRepository, times(1)).save(any(), any());
        verify(emitterRepository, times(1)).delete(any());
    }

    @Test
    void 알람전송() {
        //when
        SseEmitter emitter = mock(SseEmitter.class);
        when(emitterRepository.get(anyLong())).thenReturn(Optional.of(emitter));
        Assertions.assertDoesNotThrow(() -> {
            alarmService.send(1L, anyLong());
        });

        //then
        verify(emitterRepository, times(1)).get(anyLong());
    }

    private void reflectionCallbackExec(String fieldName, SseEmitter sseEmitter) throws NoSuchFieldException, IllegalAccessException {
        Field callbackObjField = sseEmitter.getClass()
                .getSuperclass()// ResponseBodyEmitter
                .getDeclaredField(fieldName);
        callbackObjField.setAccessible(true);// 접근제한자 해제

        Runnable callback = (Runnable) callbackObjField.get(sseEmitter);
        callback.run();
    }
}
