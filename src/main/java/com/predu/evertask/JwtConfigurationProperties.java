package com.predu.evertask;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource("classpath:jwt.properties")
public class JwtConfigurationProperties {

    @Value( "${jwt.jwtSecret}" )
    private String jwtSecret;

    @Value( "${jwt.jwtIssuer}" )
    private String jwtIssuer;
}