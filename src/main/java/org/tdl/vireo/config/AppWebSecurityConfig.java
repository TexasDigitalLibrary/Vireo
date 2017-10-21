package org.tdl.vireo.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.tdl.vireo.auth.service.VireoUserDetailsService;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

import edu.tamu.weaver.auth.config.WeaverWebSecurityConfiguration;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AppWebSecurityConfig extends WeaverWebSecurityConfiguration<User, UserRepo, VireoUserDetailsService> {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .sessionManagement()
                .sessionCreationPolicy(STATELESS)
            .and()
                .authorizeRequests()
                    .expressionHandler(webExpressionHandler())
                    .antMatchers("/**/*")
                        .permitAll()
            .and()
                .headers()
                    .frameOptions()
                    .disable()
            .and()
                .csrf()
                    .disable()
            .addFilter(tokenAuthorizationFilter());
        // @formatter:on
    }

    @Override
    protected String buildRoleHierarchy() {
        StringBuilder roleHeirarchy = new StringBuilder();
        Role[] roles = Role.values();
        for (int i = 0; i < roles.length; i++) {
            roleHeirarchy.append(roles[i].name());
            if (i < roles.length - 1) {
                roleHeirarchy.append(" > ");
            }
        }
        return roleHeirarchy.toString();
    }

}
