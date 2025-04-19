package com.example.MemberService.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(unique = true)
    public String loginId;

    public String userName;

    public UserEntity(String loginId, String userName) {
        this.loginId = loginId;
        this.userName = userName;
    }
}
