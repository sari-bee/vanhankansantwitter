
package projekti;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KuvaTykkays extends AbstractPersistable<Long> {
    
    //Tämä olio tallentaa kuvien tykkäykset
    
    private String tykkaajausername;
    
    @ManyToOne
    private Kuva kuva;
}
