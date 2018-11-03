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
 * @author C&eacute;dric Goffin
 * @see WebSecurityConfigurerAdapter
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Enables BCryptPasswordEncoder to be used by spring as a bean.
     *
     * @return a BCryptPasswordEncoder object
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Method that configures the security (user/role access) for all urls.
     *
     * @param http built-in HttpSecurity class
     * @throws Exception when a problem occurrred while configuring the http object
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/home", "/api/**", "/css/**", "/js/**").permitAll()
                .antMatchers("/**").hasAuthority("WEBADMIN")
                .antMatchers("/h2-console/**").hasAuthority("DBADMIN")
                .and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/admin").permitAll()
                .and()
                .logout().permitAll();
        http.csrf().disable();
        http.headers().frameOptions().disable();
    }

    /**
     * Sets UserService as custom UserDetailService and initializes the database with a default 'admin' user.
     *
     * @param auth        built-in Authentication manager
     * @param userService service for the user package
     * @throws Exception     when UserService does not get accepted as custom UserDetailsService
     * @throws UserException when default 'admin' user cannot be created
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, UserService userService) throws Exception, UserException {
        auth.userDetailsService(userService);

        userService.save(new User("admin", "admin", List.of(new Role("WEBADMIN"), new Role("DBADMIN"))));
    }
}
