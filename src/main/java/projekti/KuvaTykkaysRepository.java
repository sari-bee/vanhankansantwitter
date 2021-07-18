package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KuvaTykkaysRepository extends JpaRepository<KuvaTykkays, Long> {
    
    public List<KuvaTykkays> findByKuva(Kuva kuva);
    
    public KuvaTykkays findByKuvaAndTykkaajausername(Kuva kuva, String tykkaajausername);
    
    boolean existsByKuvaAndTykkaajausername(Kuva kuva, String tykkaajausername);
    
    
 
}
