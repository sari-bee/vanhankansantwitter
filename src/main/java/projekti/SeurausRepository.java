package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeurausRepository extends JpaRepository<Seuraus, Long> {
    
    public List<Seuraus> findByKayttaja(Kayttaja kayttaja);
    
    public Seuraus findByKayttajaAndSeuraajaprofiilimerkkijono(Kayttaja kayttaja, String seuraajaprofiilimerkkijono);
    
    boolean existsByKayttajaAndSeuraajaprofiilimerkkijono(Kayttaja kayttaja, String seuraajaprofiilimerkkijono);
    
}
