
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
public class Blokkaus extends AbstractPersistable<Long> {
    
    //Tällä oliolla lisätään käyttäjälle estetty käyttäjä, joka tunnistetaan nimellä ja profiilimerkkijonolla.
    
    private String blokatunnimi;    
    private String blokatunprofiilimerkkijono;
    
    @ManyToOne
    private Kayttaja kayttaja;
    
    
}
