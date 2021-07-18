package projekti;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlokkausRepository extends JpaRepository<Blokkaus, Long> {
    
    public Blokkaus findByKayttajaAndBlokatunprofiilimerkkijono(Kayttaja kayttaja, String blokatunprofiilimerkkijono);
    
    boolean existsByKayttajaAndBlokatunprofiilimerkkijono(Kayttaja kayttaja, String blokatunprofiilimerkkijono);
    
}
