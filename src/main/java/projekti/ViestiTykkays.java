
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
public class ViestiTykkays extends AbstractPersistable<Long> {
    
    //Tämä olio tallentaa viestien tykkäykset
    
    private String tykkaajausername;
    
    @ManyToOne
    private Viesti viesti;
}
