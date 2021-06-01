package com.ksn.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ksn
 * @version 1.0
 * @date 2021/5/29 18:17
 */
@Data
@ConfigurationProperties(prefix = "cas.client")
public class CasClientProperties {

    private String prefix;
    private String login;
    private String logoutRelative;
    private String logout;
}
