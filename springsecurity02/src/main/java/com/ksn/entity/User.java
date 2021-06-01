package com.ksn.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/5/26 9:25
 */
//@Data
@TableName("t_user")
public class User implements UserDetails {

    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;
    @TableField("username")
    private String username;
    @TableField("password")
    private String password;
    @TableField("accountNonExpired")
    private boolean accountNonExpired;
    @TableField("accountNonLocked")
    private boolean accountNonLocked;
    @TableField("credentialsNonExpired")
    private boolean credentialsNonExpired;
    @TableField("enabled")
    private boolean enabled;
    @TableField(exist = false)
    private List<Role> roles;

    /**
     * 如果配置security限制在线人数，就一定要实现equals和hashCode方法，不然不会生效
     * 使用@Data是lombok已经实现了
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.getUsername());
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    /**
     * 这个地方是登录的时候给登录用户设置角色的，有没有权限访问全靠这个地方的角色有没有设置进去
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> list = new ArrayList<>();
        if (getRoles() != null && getRoles().size() > 0) {
            for (Role role : getRoles()) {
                list.add(new SimpleGrantedAuthority(role.getName()));
            }
        }
        return list;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
