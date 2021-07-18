
package projekti;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class ViestiKommentti extends AbstractPersistable<Long> {
    
    //Tämä olio tallentaa viestien kommentit
    
    private String teksti;
    
    @ManyToOne
    private Viesti viesti;
    
    private String julkaisuaika;
    
    private String kommentoijannimi;
    private String kommentoijanprofiilimerkkijono;
    
    
}
