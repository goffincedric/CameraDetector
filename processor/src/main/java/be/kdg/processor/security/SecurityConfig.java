package be.kdg.processor.security;

import be.kdg.processor.user.dom.Role;
import be.kdg.processor.user.dom.User;
import be.kdg.processor.user.exceptions.UserException;
import be.kdg.processor.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

/**
 * Configuration class that sets the default security for all web and rest controllers
 *
 * @author Cédric Goffin
 * @see WebSecurityConfigurerAdapter
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/home", "/api/**", "/css/**", "/js/**").permitAll()
                .antMatchers("/h2-console/**").hasAuthority("dbadmin")
                .antMatchers("/**").hasAuthority("webadmin")
                .and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/admin").permitAll()
                .and()
                .logout().permitAll();
        http.csrf().disable();
        http.headers().frameOptions().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, UserService userService) throws Exception, UserException {
        auth.userDetailsService(userService);

        userService.createUser(new User("admin", "admin", List.of(new Role("webadmin"), new Role("dbadmin"))));
    }
}
