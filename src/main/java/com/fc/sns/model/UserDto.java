package com.fc.sns.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fc.sns.enums.UserRole;
import com.fc.sns.model.entity.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private UserRole role;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    @Builder
    public UserDto(Long id, String username, String password, UserRole role, Timestamp registeredAt, Timestamp updatedAt, Timestamp deletedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.registeredAt = registeredAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUserName())
                .password(user.getPassword())
                .role(user.getRole())
                .registeredAt(user.getRegisteredAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.toString()));
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return this.deletedAt == null;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return this.deletedAt == null;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return this.deletedAt == null;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return this.deletedAt == null;
    }
}
