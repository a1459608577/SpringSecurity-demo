package com.ksn.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.*;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/6/1 15:13
 */
@Data
public class AuthClientDetails implements ClientDetails {

    /**
     * 主键
     */
    private String id;
    /**
     * 平台唯一标识
     */
    private String appId;
    /**
     * 平台私钥
     */
    private String privateKey;
    private List<String> scopes;
    /**
     * 自动审核操作域
     */
    private List<String> autoApproveScope;
    /**
     * 授权类型
     */
    private List<String> grantTypes;
    /**
     * 转向请求地址
     */
    private LinkedList<String> redirectUris;
    /**
     * 资源唯一标识
     */
    private List<String> resourceIds;
    private List<String> roles;
    /**
     * token附加信息
     */
    private Map<String, Object> additionalInfo;
    /**
     * 令牌验证间隔（有效时间，单位：秒）
     */
    private Integer tokenValidSeconds;
    /**
     * 令牌刷新时间（单位：秒）
     */
    private Integer tokenRefreshSeconds;

    @Override
    public String getClientId() {
        return appId;
    }

    @Override
    public Set<String> getResourceIds() {
        return null;
    }

    @Override
    public boolean isSecretRequired() {
        return false;
    }

    @Override
    public String getClientSecret() {
        return null;
    }

    @Override
    public boolean isScoped() {
        return false;
    }

    @Override
    public Set<String> getScope() {
        return null;
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return null;
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return null;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return null;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return null;
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return false;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return null;
    }
}
