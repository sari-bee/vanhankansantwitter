package projekti;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViestiRepository extends JpaRepository<Viesti, Long> {
    
    public List<Viesti> findByKayttaja(Kayttaja kayttaja);
    
    public List<Viesti> findByKayttajaIn(List<Kayttaja> kayttajat, Pageable pageable);
    
    public Viesti findByKayttajaAndId(Kayttaja kayttaja, Long id);
    
}
