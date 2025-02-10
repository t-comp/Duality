package com.coms309.duality.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "reactions")
public class Reaction {
    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "activity_id", nullable = false)
    private long activityId;

    @Column(name = "emoji", nullable = false)
    private String emoji;

    @Column(name = "activity_creator_id", nullable = false)
    private String activityCreatorId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public Reaction() {
        this.createdAt = LocalDateTime.now();
    }

    public Reaction(long userId, long activityId, String emoji, String activityCreatorId) {
        this.userId = userId;
        this.activityId = activityId;
        this.emoji = emoji;
        this.activityCreatorId = activityCreatorId;
        this.createdAt = LocalDateTime.now();
    }

    //    int emojiSmilingHeart = 0x1F60D;
//    System.out.println(Character.toString(emojiSmilingHeart));
//  😀 — Grinning Face — 0x1F600 (U+1F600)
//  🤣 — Rofl— 0x1F923 (U+1F923)
//  🙂 — Slightly smile — 0x1F642 (U+1F642)
//  😍 — Smiling face with heart — 0x1F60D (U+1F60D)
//  😎 — Smiling face with sunglass — 0x1F60E (U+1F60E)


}
