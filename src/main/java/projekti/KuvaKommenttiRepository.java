package projekti;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KuvaKommenttiRepository extends JpaRepository<KuvaKommentti, Long> {
    
    public List<KuvaKommentti> findByKuva(Kuva kuva, Pageable pageable);
    
    public List<KuvaKommentti> findByKuva(Kuva kuva);
 
}
