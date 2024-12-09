package com.be.parrotalk.talk.domain;

import com.be.parrotalk.login.domain.User;
import com.be.parrotalk.talk.util.RoomStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "talks")
public class Talks {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long talkId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;

    @Column(length = 255)
    private String roomName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id", nullable = false)
    private User sender;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "user_id")
    private User receiver;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime closedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.closedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.closedAt = LocalDateTime.now();
    }
}
