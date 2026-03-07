package com.habbashx.ecommerce.dto.response;

import com.habbashx.ecommerce.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class UserInformationResponse {

    private Integer id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<Role> roles;

    private boolean isAccountLocked;
    private boolean isAccountEnabled;
    private boolean isAccountNonExpired;
}
