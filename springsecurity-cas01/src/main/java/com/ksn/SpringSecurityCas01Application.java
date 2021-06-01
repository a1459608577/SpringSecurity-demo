package com.ksn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * @author admin
 */
@ConfigurationPropertiesScan
@SpringBootApplication
public class SpringSecurityCas01Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityCas01Application.class, args);
    }

}
