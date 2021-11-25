package com.mareninss.blogapi.config;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
  USER(Set.of(Permission.USER)),
  MODERATOR(Set.of(Permission.MODERATE, Permission.USER));

  private final Set<Permission> permissions;

  Role(Set<Permission> permissions) {
    this.permissions = permissions;
  }

  public Set<Permission> getPermissions() {
    return permissions;
  }


  public Set<SimpleGrantedAuthority> getAuthorities() {
    return permissions.stream()
        .map(permission -> new SimpleGrantedAuthority(permission.getPermission())).collect(
            Collectors.toSet());
  }
}
