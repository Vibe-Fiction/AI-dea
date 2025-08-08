package com.spring.aidea.vibefiction.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "votes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "proposal_id"}))
public class Votes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long voteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false)
    private Proposals proposal;

    @Column(name = "voted_at", nullable = false, updatable = false)
    private LocalDateTime votedAt;

    @PrePersist
    protected void onCreate() {
        this.votedAt = LocalDateTime.now();


    }
}
