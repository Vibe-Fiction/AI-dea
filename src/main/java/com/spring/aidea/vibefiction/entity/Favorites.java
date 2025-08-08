package com.spring.aidea.vibefiction.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorites",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "novel_id", "category"}))
public class Favorites {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long favoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id", nullable = false)
    private Novels novel;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private FavoriteCategory category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // --- Enum ---
    public enum FavoriteCategory {
        BOOKMARK,
        LIKE
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
