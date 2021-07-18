
package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class ProductionSecurityConfiguration extends WebSecurityConfigurerAdapter {
    
    //Luokka määrittelee sovelluksessa vaadittavat kirjautumistoiminnallisuudet.
    //Liikenne ohjataan https:ään. Kaikki muut pyynnöt vaativat kirjautumisen,
    //paitsi sivu, jolta pääsee kirjautumaan tai rekisteröitymään. Lisäksi
    //uloskirjautuminen on sallittua kaikille.
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.requiresChannel()
            .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
            .requiresSecure();
        http.authorizeRequests()
             .antMatchers("/").permitAll()
             .antMatchers("/rekisteroidy").permitAll()
             .anyRequest().authenticated();
        http.formLogin()
             .permitAll();
        http.logout()
             .permitAll()
             .logoutSuccessUrl("/");
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}
