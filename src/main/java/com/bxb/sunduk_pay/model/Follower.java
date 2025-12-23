package com.bxb.sunduk_pay.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "followers")
@Data
@Builder
public class Follower {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String followerId;

    @OneToOne
    @JoinColumn(name = "user_follower_id", nullable = false)
    private User followerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "global_pot_id")
    @ToString.Exclude
    private GlobalPot globalPot;

    /** Automatically captured timestamp of record creation. */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** Automatically updated timestamp of the last modification. */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
