
package projekti;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class KayttajaController {
    
    //Tässä controllerissa hoidetaan kirjautumiseen ja rekisteröitymiseen liittyvät
    //toiminnot, käyttäjien selailuun ja hakuun liittyvät toiminnot, käyttäjien
    //seuraaminen ja esto ja muut vastaavat käyttäjätoiminnallisuudet.
    
    @Autowired
    private KayttajaRepository kayttajaRepository;
    
    @Autowired
    private SeurausRepository seurausRepository;
    
    @Autowired
    private BlokkausRepository blokkausRepository;
    
    //Kaikkien käyttäjien listaus aakkosjärjestyksessä.
    @GetMapping("/kayttajat")
    public String kayttajat(Model model) {
        model.addAttribute("kayttajat", kayttajaRepository.findAll(Sort.by("nimi").ascending()));
        return "kayttajat";
    }
    
    //Käyttäjiä voi hakea nimen tai sen osan perusteella. Hakutulokset tulevat
    //käytännössä kirjautuneen käyttäjän omalle etusivulle, minkä vuoksi näkymään
    //lisätään myös tähän liittyviä asioita.
    @PostMapping("/haekayttajaa")
    public String haekayttajaa(Model model, @RequestParam String nimi) {
        model.addAttribute("haetutkayttajat", kayttajaRepository.findByNimiContainingIgnoreCase(nimi));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("kayttaja", kayttajaRepository.findByUsername(auth.getName()));
        model.addAttribute("seurattavat", kayttajaRepository.findByUsername(auth.getName()).getSeurattavat());
        model.addAttribute("seuraajat", seurausRepository.findByKayttaja(kayttajaRepository.findByUsername(auth.getName())));
        return "etusivu";
    }
    
    //Kirjautuessa käyttäjä ohjataan kirjautumissivulle ja siitä kirjautumisen
    //jälkeen käyttäjän omalle etusivulle.
    @GetMapping("/kirjaudu")
    public String kirjaudu() {
        return "redirect:/etusivu";
    }
    
    //Etusivu on käyttäjän henkilökohtainen näkymä, josta hän pääsee linkin kautta
    //profiiliinsa ("seinälleen"), voi lisätä viestejä ("viserryksiä"), voi tarkastella
    //seuraajiaan ja seurattaviaan ja voi hakea käyttäjiä.
    @GetMapping("/etusivu")
    public String etusivu(Model model) {
        String nimi = "Ei määritelty";
        model.addAttribute("haetutkayttajat", kayttajaRepository.findByNimiContainingIgnoreCase(nimi));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("kayttaja", kayttajaRepository.findByUsername(auth.getName()));
        model.addAttribute("seurattavat", kayttajaRepository.findByUsername(auth.getName()).getSeurattavat());
        model.addAttribute("seuraajat", seurausRepository.findByKayttaja(kayttajaRepository.findByUsername(auth.getName())));
        return "etusivu";
    } 
    
    //Rekisteröityminen tapahtuu lomakkeella, johon lisätään käyttäjän nimi,
    //käyttäjänimi, salasana ja profiilimerkkijono jolla käyttäjän seinä löytyy
    //sovelluksesta, Käyttäjänimen ja profiilimerkkijonon tulee olla uniikkeja.
    //Rekisteröitymisen jälkeen käyttäjä ohjataan kirjautumissivulle.
    @PostMapping("/rekisteroidy")
    public String rekisteroidy(
            @RequestParam String nimi,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String profiilimerkkijono,
            Model model) {
        
        if (kayttajaRepository.existsKayttajaByUsername(username)) {
            model.addAttribute("info", "Käyttäjänimi on jo käytössä!");
            model.addAttribute("linkki", "/");
            return "info";
        } 
        if (kayttajaRepository.existsKayttajaByProfiilimerkkijono(profiilimerkkijono)) {
            model.addAttribute("info", "Osoite on jo käytössä!");
            model.addAttribute("linkki", "/");
            return "info";
        } 
        String salasana = passwordEnkooderi().encode(password);
        kayttajaRepository.save(new Kayttaja(username, salasana, nimi, profiilimerkkijono, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0));
        return "redirect:/kirjaudu";
    }
    
    @Bean
    public PasswordEncoder passwordEnkooderi() {
        return new BCryptPasswordEncoder();
    }
    
    //Käyttäjä voi ottaa tietyn toisen käyttäjän seurantaan. Käyttäjä ei voi seurata
    //itseään, käyttäjää jota hän jo seuraa tai käyttäjää joka on estänyt hänet.
    @Transactional
    @PostMapping("kayttajat/{profiilimerkkijono}/seuraa")
    public String seuraa(Model model, @PathVariable String profiilimerkkijono) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String seuraajanusername = auth.getName();
        if (seurausRepository.existsByKayttajaAndSeuraajaprofiilimerkkijono(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), kayttajaRepository.findByUsername(seuraajanusername).getProfiilimerkkijono())) {
            model.addAttribute("info", "Seuraat jo tätä käyttäjää!");
            model.addAttribute("linkki", "/kayttajat/" + profiilimerkkijono);
            return "info";
        }
        if (blokkausRepository.existsByKayttajaAndBlokatunprofiilimerkkijono(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), kayttajaRepository.findByUsername(seuraajanusername).getProfiilimerkkijono())) {
            model.addAttribute("info", "Tämä käyttäjä on estänyt sinut :(");
            model.addAttribute("linkki", "/kayttajat/" + profiilimerkkijono);
            return "info";
        }
        kayttajaRepository.findByUsername(seuraajanusername).getSeurattavat().add(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono));
        LocalDateTime aika2 = LocalDateTime.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String seurausaika = aika2.format(formatter2);
        seurausRepository.save(new Seuraus(kayttajaRepository.findByUsername(seuraajanusername).getNimi(), kayttajaRepository.findByUsername(seuraajanusername).getProfiilimerkkijono(), kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), seurausaika));
        model.addAttribute("info", "Seuraat nyt tätä käyttäjää!");
        model.addAttribute("linkki", "/kayttajat/" + profiilimerkkijono);
        return "info";
    }
    
    //Tällä toiminnallisuudella seuraajan voi poistaa ja estää häntä aloittamasta
    //seuraamista uudelleen.
    @Transactional
    @PostMapping("/poistaseuraaja/{profiilimerkkijono}")
    public String poistaseuraus(@PathVariable String profiilimerkkijono) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Kayttaja seurattu = kayttajaRepository.findByUsername(auth.getName());
        Kayttaja seuraaja = kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono);
        kayttajaRepository.findByUsername(auth.getName()).getSeuraajat().remove(seuraaja);
        kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono).getSeurattavat().remove(seurattu);
        blokkausRepository.save(new Blokkaus(seuraaja.getNimi(), profiilimerkkijono, seurattu));
        Long id = seurausRepository.findByKayttajaAndSeuraajaprofiilimerkkijono(seurattu, profiilimerkkijono).getId();
        seurausRepository.deleteById(id);
        return "redirect:/etusivu";
    }
    
}
