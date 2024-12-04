package com.be.parrotalk.login.service;

import com.be.parrotalk.login.UserRepository;
import com.be.parrotalk.login.domain.User;
import com.be.parrotalk.login.dto.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserInfoDTO getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID로 사용자를 찾을 수 없습니다: " + userId));

        return UserInfoDTO.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();
    }
}
