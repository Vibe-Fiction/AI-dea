package com.spring.aidea.vibefiction.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "collaborators")
public class Collaborators {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collaborate_id")
    private Long collaborateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id", nullable = false)
    private Novels novel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    // --- Enum ---
    public enum Role { OWNER, COLLABORATOR }



}