package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViestiTykkaysRepository extends JpaRepository<ViestiTykkays, Long> {
    
    public List<ViestiTykkays> findByViesti(Viesti viesti);
    
    public ViestiTykkays findByViestiAndTykkaajausername(Viesti viesti, String tykkaajausername);
    
    boolean existsByViestiAndTykkaajausername(Viesti viesti, String tykkaajausername);
    
    
 
}
