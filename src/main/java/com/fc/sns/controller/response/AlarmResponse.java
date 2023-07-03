package com.fc.sns.controller.response;

import com.fc.sns.enums.AlarmType;
import com.fc.sns.model.AlarmDto;
import com.fc.sns.model.json.AlarmArgs;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class AlarmResponse {
    private final Long id;
    private final AlarmType type;
    private final AlarmArgs args;
    private final String text;
    private final Timestamp registeredAt;
    private final Timestamp updatedAt;
    private final Timestamp deletedAt;

    protected AlarmResponse() {
        this.id = null;
        this.type = null;
        this.args = null;
        this.text = null;
        this.registeredAt = null;
        this.updatedAt = null;
        this.deletedAt = null;
    }

    @Builder
    public AlarmResponse(Long id, AlarmType type, AlarmArgs args, String text, Timestamp registeredAt, Timestamp updatedAt, Timestamp deletedAt) {
        this.id = id;
        this.type = type;
        this.args = args;
        this.text = text;
        this.registeredAt = registeredAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static AlarmResponse fromAlarmDto(AlarmDto alarmDto) {
        return AlarmResponse.builder()
                .id(alarmDto.getId())
                .type(alarmDto.getType())
                .args(alarmDto.getArgs())
                .text(alarmDto.getType().getAlarmText())
                .registeredAt(alarmDto.getRegisteredAt())
                .updatedAt(alarmDto.getUpdatedAt())
                .deletedAt(alarmDto.getDeletedAt())
                .build();
    }
}
