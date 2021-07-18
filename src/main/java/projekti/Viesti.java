
package projekti;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Viesti extends AbstractPersistable<Long> {
    
    //Tämä olio tallentaa sovellukseen lähetetyt viestit eli "viserrykset".
    
    private String teksti;
    
    @ManyToOne
    private Kayttaja kayttaja;
    
    private String julkaisuaika;
    
    //Lista kaikkia viestin kommentteja varten.
    @OneToMany(mappedBy = "viesti")
    private List<ViestiKommentti> kommentit = new ArrayList<>();
    
    //Lista viestin tykkäyksiä varten.
    @OneToMany(mappedBy = "viesti")
    private List<ViestiTykkays> tykkaykset = new ArrayList<>();
    
    
}
