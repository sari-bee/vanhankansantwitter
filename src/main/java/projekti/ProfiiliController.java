
package projekti;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfiiliController {
    
    //Tässä kontrollerissa hoidetaan käyttäjän "seinään" liittyvät asiat muilta
    //osin, paitsi kuvien osalta, joille on oma kontrollerinsa.
    
    @Autowired
    private KayttajaRepository kayttajaRepository;
    
    @Autowired
    private ViestiRepository viestiRepository;
    
    @Autowired
    private ViestiKommenttiRepository viestiKommenttiRepository;
    
    @Autowired
    private ViestiTykkaysRepository viestiTykkaysRepository;
    
    @Autowired
    private SeurausRepository seurausRepository;
    
    //Haetaan yksittäisen käyttäjän "seinä". Sivulla näkyvät viestit ("viserrykset")
    //käyttäjältä itseltään ja hänen seuraamiltaan käyttäjiltä.
    @GetMapping("/kayttajat/{profiilimerkkijono}")
    public String kayttaja(Model model, @PathVariable String profiilimerkkijono) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("omistaja", kayttajaRepository.findByUsername(auth.getName()));
        model.addAttribute("kayttaja", kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono));
        List <Kayttaja> kayttajat = new ArrayList<>();
        kayttajat.addAll(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono).getSeurattavat());
        kayttajat.add(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono));
        Pageable pageable = PageRequest.of(0, 25, Sort.by("julkaisuaika").descending());
        model.addAttribute("viestit", viestiRepository.findByKayttajaIn(kayttajat, pageable));
        return "profiili";
    } 
    
    //Käyttäjä voi julkaista uuden viestin ("viserryksen").
    @PostMapping("/julkaise")
    public String julkaise(@RequestParam String teksti) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String profiilimerkkijono = kayttajaRepository.findByUsername(auth.getName()).getProfiilimerkkijono();
        LocalDateTime aika = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String julkaisuaika = aika.format(formatter);
        viestiRepository.save(new Viesti(teksti, kayttajaRepository.findByUsername(auth.getName()), julkaisuaika, new ArrayList<>(), new ArrayList<>()));
        return "redirect:/kayttajat/" + profiilimerkkijono;
    }
    
    //Hakee yksittäisen viestin ja siihen liittyvät kommentit (max 10) ja tykkäykset.
    //Antaa mahdollisuuden kommentoida viestiä ja tykätä viestistä.
    @GetMapping("/kayttajat/{profiilimerkkijono}/viestit/{id}")
    public String viesti(Model model, @PathVariable String profiilimerkkijono, @PathVariable Long id) {
        Viesti viesti = viestiRepository.findByKayttajaAndId(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("valinta", viestiTykkaysRepository.existsByViestiAndTykkaajausername(viesti, auth.getName()));
        model.addAttribute("viesti", viesti);
        model.addAttribute("kayttaja", kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono));      
        model.addAttribute("omistaja", kayttajaRepository.findByUsername(auth.getName()));
        model.addAttribute("sallittukommentoija", seurausRepository.existsByKayttajaAndSeuraajaprofiilimerkkijono(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), kayttajaRepository.findByUsername(auth.getName()).getProfiilimerkkijono()));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("julkaisuaika").descending());
        model.addAttribute("kommentit", viestiKommenttiRepository.findByViesti(viesti, pageable));
        return "viesti";
    }
    
    //Viestin kommentointi. Sallittua vain jos seuraa ko. käyttäjää.
    @PostMapping("/kayttajat/{profiilimerkkijono}/viestit/{id}/kommentoi")
    public String kommentoi(@PathVariable String profiilimerkkijono, @PathVariable Long id, @RequestParam String kommentti) {
        LocalDateTime aika = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String julkaisuaika = aika.format(formatter);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String kommentoijannimi = kayttajaRepository.findByUsername(auth.getName()).getNimi();
        String kommentoijanprofiilimerkkijono = kayttajaRepository.findByUsername(auth.getName()).getProfiilimerkkijono();
        viestiKommenttiRepository.save(new ViestiKommentti(kommentti, viestiRepository.findByKayttajaAndId(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), id), julkaisuaika, kommentoijannimi, kommentoijanprofiilimerkkijono));
        return "redirect:/kayttajat/" + profiilimerkkijono + "/viestit/" + id;
    }
    
    //Viestistä tykkääminen.
    @PostMapping("/kayttajat/{profiilimerkkijono}/viestit/{id}/tykkaa")
    public String tykkaa(@PathVariable Long id, @PathVariable String profiilimerkkijono, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Viesti viesti = viestiRepository.findByKayttajaAndId(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), id);
        viestiTykkaysRepository.save(new ViestiTykkays(username, viesti));
        return "redirect:/kayttajat/" + profiilimerkkijono + "/viestit/" + id;
    }
    
    //Viestin tykkäyksen poisto.
    @PostMapping("/kayttajat/{profiilimerkkijono}/viestit/{id}/poistatykkays")
    public String poistatykkays(@PathVariable Long id, @PathVariable String profiilimerkkijono) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Viesti viesti = viestiRepository.findByKayttajaAndId(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), id);
        Long tykkaysid = viestiTykkaysRepository.findByViestiAndTykkaajausername(viesti, username).getId();
        viestiTykkaysRepository.deleteById(tykkaysid);
        return "redirect:/kayttajat/" + profiilimerkkijono + "/viestit/" + id;
    }
}