package com.fc.sns.model;

import com.fc.sns.enums.AlarmType;
import com.fc.sns.model.entity.Alarm;
import com.fc.sns.model.json.AlarmArgs;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;

@Getter
@Slf4j
public class AlarmDto {
    private final Long id;
//    private final UserDto user;
    private final AlarmType type;
    private final AlarmArgs args;
    private final Timestamp registeredAt;
    private final Timestamp updatedAt;
    private final Timestamp deletedAt;

    protected AlarmDto() {
        this.id = null;
        this.type = null;
        this.args = null;
        this.registeredAt = null;
        this.updatedAt = null;
        this.deletedAt = null;
    }

    @Builder
    public AlarmDto(Long id, AlarmType type, AlarmArgs args, Timestamp registeredAt, Timestamp updatedAt, Timestamp deletedAt) {
        this.id = id;
        this.type = type;
        this.args = args;
        this.registeredAt = registeredAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static AlarmDto fromAlarm(Alarm alarm) {
        return AlarmDto.builder()
                .id(alarm.getId())
                .type(alarm.getType())
                .args(alarm.getArgs())
                .registeredAt(alarm.getRegisteredAt())
                .updatedAt(alarm.getUpdatedAt())
                .deletedAt(alarm.getDeletedAt())
                .build();
    }
}
