package org.ff4j.springboot.conf;


import org.ff4j.web.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer.UserDetailsBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class FF4jSecurityConfig extends WebSecurityConfigurerAdapter {

    private final ApiConfig apiConfig;


    @Autowired
    public FF4jSecurityConfig(ApiConfig apiConfig) {
        this.apiConfig = apiConfig;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        UserDetailsManagerConfigurer config = auth.inMemoryAuthentication();

        int count = apiConfig.getUsers().keySet().size();
        int idx = 0;
        for (String currentUser : apiConfig.getUsers().keySet()) {
            UserDetailsBuilder udb = config.withUser(currentUser)
                    .password(apiConfig.getUsers().get(currentUser))
                    .roles(apiConfig.getPermissions().get(currentUser).toArray(new String[0]));
            // There is another user to use
            if (idx++ < count) {
                config = udb.and();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (apiConfig.isAuthenticate()) {
            // ENFORCE AUTHENTICATION
            http.httpBasic().
                    // DISABLE CSRF
                            and().csrf().disable().
                    authorizeRequests()
                    .antMatchers("/ff4j-web-console/**").permitAll()
                    .antMatchers("/api/**").permitAll()
                    .antMatchers("/").permitAll()
                    .anyRequest().authenticated();
        }
    }
}
