package com.fc.sns.model.json;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlarmArgs {
    private Long fromUserId;//알람을 발생시킨 사람
    private Long targetId;//알람을 받는 사람
}
//00가 새 코멘트를 작성했습니다. -> postId, commentId
//00외 2명이 새 코멘트를 작성했습니다. -> commentId, commentId
