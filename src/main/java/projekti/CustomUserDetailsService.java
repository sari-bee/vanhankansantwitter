
package projekti;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Kayttaja kayttaja = kayttajaRepository.findByUsername(username);
        if (kayttaja == null) {
            throw new UsernameNotFoundException("Käyttäjää ei löydy!");
        }
        
        return new org.springframework.security.core.userdetails.User(
            kayttaja.getUsername(),
            kayttaja.getPassword(),
            true,
            true,
            true,
            true,
            Arrays.asList(new SimpleGrantedAuthority("USER")));
    }
    
}
