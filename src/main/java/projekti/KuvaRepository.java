package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KuvaRepository extends JpaRepository<Kuva, Long> {
    
    public List<Kuva> findByKayttaja(Kayttaja kayttaja);
    
    public Kuva findByKayttajaAndNumero(Kayttaja kayttaja, Integer numero);
    
}
