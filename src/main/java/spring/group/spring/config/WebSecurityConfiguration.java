package spring.group.spring.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// TODO User: don't forget to add the necessary annotations
@Configuration
public class WebSecurityConfiguration {

    // TODO User: finish WebSecurityConfiguration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(c -> c.disable())
                .authorizeRequests(ar -> ar
                        .anyRequest().permitAll())
                .headers(h -> h.permissionsPolicy(permissionsPolicyConfig -> {}));
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
