
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
public class Seuraus extends AbstractPersistable<Long> {
    
    //Tällä oliolla talletetaan tieto siitä, kuka ja koska on alkanut seurata
    //tiettyä käyttäjää.
    
    private String seuraajannimi;
    private String seuraajaprofiilimerkkijono;
    
    @ManyToOne
    private Kayttaja kayttaja;
    
    private String seurausaika;
    
    
}
