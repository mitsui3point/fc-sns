package com.fc.sns.model.entity;

import com.fc.sns.enums.UserRole;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "\"user\"")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@SQLDelete(sql = "UPDATE \"user\" set deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(value = EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    public User(Long id, String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
    }

    @Builder
    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @PrePersist
    public void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    public void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    //변경감지
    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateRole(UserRole role) {
        this.role = role;
    }
}
