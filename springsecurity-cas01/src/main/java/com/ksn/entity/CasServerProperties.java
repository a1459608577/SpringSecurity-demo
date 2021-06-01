package com.ksn.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/5/29 18:14
 */
@Data
@ConfigurationProperties(prefix = "cas.server")
public class CasServerProperties {

    private String prefix;
    private String login;
    private String logout;
}
