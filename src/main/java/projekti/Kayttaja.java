
package projekti;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Kayttaja extends AbstractPersistable<Long> {
    
    //Rekisteröitymisen yhteydessä käyttäjä syöttää nimensä, käyttäjänimen, salasanan ja haluamansa merkkijonon,
    //jonka perusteella käyttäjä löydetään sovelluksesta. Listat alustetaan tyhjäksi ja profiilikuvan numeroksi 0.
    
    private String username;
    private String password;
    private String nimi;
    private String profiilimerkkijono;
    
    //Lista kaikkia käyttäjän lähettämiä viestejä varten.
    @OneToMany(mappedBy = "kayttaja")
    private List<Viesti> viestit = new ArrayList<>();
    
    //Lista kaikkia käyttäjän lisäämiä kuvia varten.
    @OneToMany(mappedBy = "kayttaja")
    private List<Kuva> kuvat = new ArrayList<>();
    
    //Lista kaikkia käyttäjän seuraamia käyttäjiä varten.
    @ManyToMany
    private List<Kayttaja> seurattavat = new ArrayList<>();
    
    //Lista niitä käyttäjiä varten, jotka seuraavat ko. käyttäjää.
    @ManyToMany(mappedBy = "seurattavat")
    private List<Kayttaja> seuraajat = new ArrayList<>();
    
    //Lista yksittäisten seuraamistapahtumien rekisteröimiseksi. Tätä tarvitaan, jotta
    //seuraamisen alkamisajankohta saadaan kirjattua.
    @OneToMany(mappedBy = "kayttaja")
    private List<Seuraus> seuraukset = new ArrayList<>();
    
    //Lista käyttäjän tekemiä muiden käyttäjien estoja varten.
    @OneToMany(mappedBy = "kayttaja")
    private List<Blokkaus> blokkaukset = new ArrayList<>();
    
    //Käyttäjän valitseman profiilikuvan numero käyttäjän kuva-albumissa.
    private Integer profiilikuvannumero;
    
    
    
}
