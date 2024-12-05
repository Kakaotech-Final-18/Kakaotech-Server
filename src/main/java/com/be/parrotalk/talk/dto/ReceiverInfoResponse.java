package com.be.parrotalk.talk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReceiverInfoResponse {
    private String nickname;
    private String profileImage;
}
