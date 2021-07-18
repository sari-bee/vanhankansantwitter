package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KayttajaRepository extends JpaRepository<Kayttaja, Long> {
    
    Kayttaja findByUsername(String username);
    
    List<Kayttaja> findByNimiContainingIgnoreCase(String nimi);
    
    boolean existsKayttajaByUsername(String username);
    
    boolean existsKayttajaByProfiilimerkkijono(String profiilimerkkijono);
    
    Kayttaja findByProfiilimerkkijono(String profiilimerkkijono);
    
}
