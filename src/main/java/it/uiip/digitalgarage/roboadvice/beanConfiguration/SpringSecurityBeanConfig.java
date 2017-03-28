package it.uiip.digitalgarage.roboadvice.beanConfiguration;

import it.uiip.digitalgarage.roboadvice.businesslogic.security.AuthEntryPoint;
import it.uiip.digitalgarage.roboadvice.businesslogic.security.AuthProvider;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Configuration of the SpringSecurity entities.
 */
@Configuration
@EnableJpaRepositories("it.uiip.digitalgarage.roboadvice.persistence.repository")
public class SpringSecurityBeanConfig {

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthEntryPoint();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserRepository userRepository) {
        return new AuthProvider(userRepository);
    }

}
