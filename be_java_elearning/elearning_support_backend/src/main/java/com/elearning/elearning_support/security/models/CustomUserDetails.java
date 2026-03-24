package com.elearning.elearning_support.security.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.elearning.elearning_support.entities.department.Department;
import com.elearning.elearning_support.entities.permission.Permission;
import com.elearning.elearning_support.entities.role.Role;
import com.elearning.elearning_support.entities.users.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {

    private User user;

    private Set<String> roles;

    private Collection<? extends GrantedAuthority> authorities;

    private Set<Long> departmentIds;

    public CustomUserDetails(User user) {
        this.user = user;
        this.roles = user.getRoles().stream().map(Role::getCode).collect(Collectors.toSet());
        this.departmentIds = user.getDepartments().stream().map(Department::getId).collect(Collectors.toSet());
//        List<Permission> permissions = new ArrayList<>();
//        for (Role role : user.getRoles()) {
//            permissions.addAll(role.getPermissions());
//        }
        // auth by permission
//        this.authorities = lstPermission.stream().map(permission -> new SimpleGrantedAuthority(permission.getCode()))
//            .collect(Collectors.toList());
        // auth by role
        this.authorities = this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return this.user.getId();
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
