package com.bxb.sunduk_pay.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contributers")
@Data

public class Contributer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String contributerId;

    @OneToOne
    @JoinColumn(name = "user_contributer_id")
    private User userContributer;

    @ManyToOne
    @JoinColumn(name = "global_pot_id")
    private GlobalPot globalPot;

    private String name;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    private byte[] profileImage;

    private Double amountContributed;

    private Boolean isAnonymous;
    private Boolean isUser;

    /** Automatically captured timestamp of record creation. */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** Automatically updated timestamp of the last modification. */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
