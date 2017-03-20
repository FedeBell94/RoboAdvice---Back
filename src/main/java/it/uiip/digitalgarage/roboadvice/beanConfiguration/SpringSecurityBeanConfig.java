package it.uiip.digitalgarage.roboadvice.beanConfiguration;

import it.uiip.digitalgarage.roboadvice.businesslogic.security.AuthEntryPoint;
import it.uiip.digitalgarage.roboadvice.businesslogic.security.AuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
public class SpringSecurityBeanConfig {

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return new AuthEntryPoint();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        return new AuthProvider();
    }
}
