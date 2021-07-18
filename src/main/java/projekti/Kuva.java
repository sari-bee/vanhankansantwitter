package projekti;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.AbstractPersistable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Kuva extends AbstractPersistable<Long> {
    
    //Tähän olioon talletetaan sovellukseen ladattujen kuvien tiedot.
    
    private String kuvaus;
    
    //Lokaalisti ja Herokua varten on erilliset kuva-annotaatiot.
    //Jos kuvatoiminnallisuudessa on ongelmia, kokeile vaihtaa aktiiviseksi toiset annotaatiot.
     
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition="BLOB")
    //@Type(type = "org.hibernate.type.BinaryType")
    //@Basic(fetch = FetchType.EAGER)
    private byte[] content;
    
    @ManyToOne
    private Kayttaja kayttaja;
    
    private Integer numero;
    
    //Lista kaikkia kuvan kommentteja varten.
    @OneToMany(mappedBy = "kuva", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<KuvaKommentti> kommentit = new ArrayList<>();
    
    //Lista kuvan tykkäyksiä varten.
    @OneToMany(mappedBy = "kuva", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<KuvaTykkays> tykkaykset = new ArrayList<>();
    
    
}
