package projekti;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViestiKommenttiRepository extends JpaRepository<ViestiKommentti, Long> {
    
    public List<ViestiKommentti> findByViesti(Viesti viesti, Pageable pageable);
 
}
