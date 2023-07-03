package com.fc.sns.fixture;

import com.fc.sns.enums.AlarmType;
import com.fc.sns.model.entity.Alarm;
import com.fc.sns.model.entity.User;
import com.fc.sns.model.json.AlarmArgs;

public class AlarmFixture {

    public static Alarm get(Long id, User user, AlarmType type, AlarmArgs args) {
        return Alarm.builder()
                .id(id)
                .user(user)
                .args(args)
                .type(type)
                .build();
    }
}
