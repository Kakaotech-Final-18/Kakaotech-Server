package com.be.parrotalk.login.dto;

import com.be.parrotalk.login.domain.ProviderType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class UserDTO {

    private Long id;
    private String nickname;
    private String email;
    private String profileImage;
    private ProviderType provider;
}