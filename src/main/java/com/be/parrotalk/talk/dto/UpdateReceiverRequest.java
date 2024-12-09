package com.be.parrotalk.talk.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateReceiverRequest {
    private Long talkId;       // Talk의 ID
    private String receiverEmail;   // 업데이트할 receiver의 ID
}
