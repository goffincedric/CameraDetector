package be.kdg.processor.security;

import be.kdg.processor.user.dom.Role;
import be.kdg.processor.user.dom.User;
import be.kdg.processor.user.exceptions.UserException;
import be.kdg.processor.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class that sets the default security for all web and rest controllers
 *
 * @author C&eacute;dric Goffin
 * @see WebSecurityConfigurerAdapter
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${spring.h2.console.path}")
    private String h2ConsolePath;

    /**
     * Enables BCryptPasswordEncoder to be used by spring as a bean.
     *
     * @return a BCryptPasswordEncoder object
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new DefaultUrlAuthenticationSuccessHandler();
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
                .antMatchers("/", "/home", "/error", "/api/**", "/css/**", "/img/**", "/js/**").permitAll()
                .antMatchers("/admin").hasAnyAuthority("WEBADMIN", "DBADMIN")
                .antMatchers(h2ConsolePath + "/**").hasAuthority("DBADMIN")
                .antMatchers("/**").hasAuthority("WEBADMIN")
                .and()
                .formLogin().loginPage("/login").permitAll().successHandler(successHandler())
                .and()
                .logout().permitAll();

        // H2 Console security config
        http.csrf().ignoringAntMatchers(h2ConsolePath + "/**");
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

        userService.save(new User("user", "user", new ArrayList<>()));
        userService.save(new User("dbadmin", "dbadmin", List.of(new Role("DBADMIN"))));
        userService.save(new User("webadmin", "webadmin", List.of(new Role("WEBADMIN"))));
        userService.save(new User("admin", "admin", List.of(new Role("DBADMIN"), new Role("WEBADMIN"))));
    }
}
