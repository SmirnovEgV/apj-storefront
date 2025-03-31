package edu.byui.apj.storefront.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/user-profile.html").authenticated() // Requires authentication
                        .anyRequest().permitAll() // All other requests are permitted
                )
                .formLogin((form) -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/perform-login")
                        .failureUrl("/login.html?error=Invalid+Login")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/perform-logout") // Change logout URL
                        .logoutSuccessUrl("/index.html") // Redirect after logout
                        .invalidateHttpSession(true) // Invalidate session
                        .clearAuthentication(true) // Remove authentication info
                        .deleteCookies("JSESSIONID") )
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username("apj-user")
                        .password("password123")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }

}
