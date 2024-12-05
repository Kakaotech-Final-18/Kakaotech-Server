package com.be.parrotalk.talk.service;

import com.be.parrotalk.login.UserRepository;
import com.be.parrotalk.login.domain.User;
import com.be.parrotalk.login.util.ProviderType;
import com.be.parrotalk.talk.domain.Talks;
import com.be.parrotalk.talk.dto.CreateTalkRequest;
import com.be.parrotalk.talk.dto.ReceiverInfoResponse;
import com.be.parrotalk.talk.dto.UpdateReceiverRequest;
import com.be.parrotalk.talk.repository.TalkRepository;
import com.be.parrotalk.talk.util.RoomStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TalkService {

    private final UserRepository userRepository;
    private final TalkRepository talkRepository;

    public Long createTalk(String userId, CreateTalkRequest createTalkRequest) {
        // Sender(User) 조회
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // Talks 엔티티 생성 및 저장
        Talks newTalk = Talks.builder()
                .roomName(createTalkRequest.getRoomName())
                .sender(user)
                .createdAt(LocalDateTime.now())
                .closedAt(null) // 초기값 null
                .status(RoomStatus.INACTIVE)
                .build();

        talkRepository.save(newTalk);
        return newTalk.getTalkId();
    }

    public ReceiverInfoResponse updateReceiver(UpdateReceiverRequest updateReceiverRequest) {
        Talks talk = talkRepository.findById(updateReceiverRequest.getTalkId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Talk입니다."));

        User receiver = userRepository.findByEmail(updateReceiverRequest.getReceiverEmail())
                .orElseGet(() -> {
                    // 새로운 User 객체 생성 및 저장
                    User newUser = User.builder()
                            .nickname("익명") // 고유 닉네임 생성
                            .email(updateReceiverRequest.getReceiverEmail())
                            .provider(ProviderType.NONE) // 기본 Provider 설정
                            .profileImage("default") // 기본 프로필 이미지 URL
                            .build();
                    return userRepository.save(newUser); // 저장 후 반환
                });

        talk.setReceiver(receiver);
        talkRepository.save(talk);
        return new ReceiverInfoResponse(
                talk.getReceiver().getNickname(),
                talk.getReceiver().getProfileImage()
        );
    }

}
