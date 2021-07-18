
package projekti;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.transaction.Transactional;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class KuvaController {
    
    //Tässä kontrollerissa hoidetaan käyttäjän kuva-albumiin liittyvät toiminnot.
    
    @Autowired
    private KayttajaRepository kayttajaRepository;
    
    @Autowired
    private KuvaRepository kuvaRepository;
    
    @Autowired
    private KuvaKommenttiRepository kuvaKommenttiRepository;
    
    @Autowired
    private KuvaTykkaysRepository kuvaTykkaysRepository;
    
    @Autowired
    private SeurausRepository seurausRepository;
    
    //Kun mennään käyttäjän kuvasivulle, palautetaan ensimmäinen kuva
    @GetMapping("/kayttajat/{profiilimerkkijono}/kuvat")
    public String view(@PathVariable String profiilimerkkijono) {
        
        return "redirect:/kayttajat/" + profiilimerkkijono + "/kuvat/1";
    }
    
    //Palautetaan osoitteessa mainitun numeroinen kuva. Sivulla on myös mahdollisuus
    //siirtyä edelliseen tai seuraavaan kuvaan, lisätä uusi kuva (jos kuvia on alle 10)
    //tai vaihtaa nykyisen kuvan tilalle uusi kuva. Sekä lisätä nykyinen kuva profiilikuvaksi.
    @GetMapping("/kayttajat/{profiilimerkkijono}/kuvat/{numero}")
    public String viewOne(Model model, @PathVariable String profiilimerkkijono, @PathVariable Integer numero) {
        
        Integer imageCount = kuvaRepository.findByKayttaja(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono)).size();
        model.addAttribute("kayttaja", kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("omistaja", kayttajaRepository.findByUsername(auth.getName()));
        model.addAttribute("sallittukommentoija", seurausRepository.existsByKayttajaAndSeuraajaprofiilimerkkijono(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), kayttajaRepository.findByUsername(auth.getName()).getProfiilimerkkijono()));
        model.addAttribute("kuvamaara", imageCount);
        Integer maksimiMaara = 10;
        model.addAttribute("maksimimaara", maksimiMaara);
        if (imageCount == 0) {
            return "kuvat";
        }
        if (numero > 0 && numero <= imageCount) {
            Kuva kuva = kuvaRepository.findByKayttajaAndNumero(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), numero);
            model.addAttribute("kuvaus", kuva.getKuvaus());
            model.addAttribute("valinta", kuvaTykkaysRepository.existsByKuvaAndTykkaajausername(kuva, auth.getName()));
            Pageable pageable = PageRequest.of(0, 10, Sort.by("julkaisuaika").descending());
            model.addAttribute("kommentit", kuvaKommenttiRepository.findByKuva(kuva, pageable));
            model.addAttribute("tykkaykset", kuvaTykkaysRepository.findByKuva(kuva).size());
            model.addAttribute("profiilimerkkijono", profiilimerkkijono);
            model.addAttribute("current", numero);
        }
 
        if (numero < imageCount && numero > 0) {
            model.addAttribute("profiilimerkkijono", profiilimerkkijono);
            model.addAttribute("next", numero + 1);
        }
 
        if (numero > 1) {
            model.addAttribute("profiilimerkkijono", profiilimerkkijono);
            model.addAttribute("previous", numero - 1);
        }
 
        return "kuvat";
    }
    
    //Haetaan halutun kuvan sisältö.
    @GetMapping(path = "/kayttajat/{profiilimerkkijono}/kuvat/{numero}/content", produces = "image/*")
    @ResponseBody
    public byte[] getContent(@PathVariable String profiilimerkkijono, @PathVariable Integer numero) {
        return kuvaRepository.findByKayttajaAndNumero(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), numero).getContent();
    }
    
    //Toiminnallisuus, jonka avulla käyttäjä voi lisätä uuden kuvan. Tarkistetaan, että kuvia ei ole
    //käyttäjän kuva-albumissa vielä kymmentä, jolloin kuvan lisääminen ei ole mahdollista.
    //Tällöin uuden kuvan saa lisättyä vaihtamalla sen jonkin vanhan kuvan tilalle.
    @PostMapping("/kayttajat/{profiilimerkkijono}/kuvat/lisaakuva")
    public String julkaiseKuva(Model model, @PathVariable String profiilimerkkijono, @RequestParam String kuvaus, @RequestParam("file") MultipartFile file) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer numero = kuvaRepository.findByKayttaja(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono)).size() + 1;
        if (numero > 10) {
            model.addAttribute("info", "Voit lisätä enintään 10 kuvaa. Lisää uusi kuva jonkun aiemman kuvan päälle.");
            model.addAttribute("linkki", "/kayttajat/" + profiilimerkkijono + "/kuvat");
            return "info";
        }
        Kuva kuva = new Kuva();
        kuva.setKuvaus(kuvaus);
        kuva.setContent(file.getBytes());
        kuva.setKayttaja(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono));
        kuva.setNumero(numero);
        ArrayList<KuvaKommentti> kommentit = new ArrayList<>();
        ArrayList<KuvaTykkays> tykkaykset = new ArrayList<>();
        kuva.setKommentit(kommentit);
        kuva.setTykkaykset(tykkaykset);
        kuvaRepository.save(kuva);
        
        return "redirect:/kayttajat/" + profiilimerkkijono + "/kuvat/" + numero;
    }
    
    //Toiminnallisuus, jolla voi kuva-albumiin vaihtaa kuvan jonkin aiemmin lisätyn
    //kuvan tilalle.
    @Transactional
    @PostMapping("/kayttajat/{profiilimerkkijono}/kuvat/{numero}/vaihdakuva")
    public String vaihdaKuva(Model model, @PathVariable String profiilimerkkijono, @PathVariable Integer numero, @RequestParam String kuvaus, @RequestParam("file") MultipartFile file) throws IOException {
        if ((kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono).getProfiilikuvannumero().equals(numero))) {
            kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono).setProfiilikuvannumero(0);
        }
        kuvaRepository.findByKayttajaAndNumero(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), numero).setKuvaus(kuvaus);
        kuvaRepository.findByKayttajaAndNumero(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), numero).setContent(file.getBytes());
        kuvaRepository.findByKayttajaAndNumero(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), numero).getKommentit().clear();
        kuvaKommenttiRepository.findByKuva(kuvaRepository.findByKayttajaAndNumero(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), numero)).clear();
        kuvaRepository.findByKayttajaAndNumero(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), numero).getTykkaykset().clear();
        kuvaTykkaysRepository.findByKuva(kuvaRepository.findByKayttajaAndNumero(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), numero)).clear();
        return "redirect:/kayttajat/" + profiilimerkkijono + "/kuvat/" + numero;
    }
    
    //Aiemmin lisätyn kuvan poistaminen. Ellei ko. kohdalle lisätä uutta kuvaa,
    //näkyy kuva-albumissa tieto, että kuva on poistettu.
    @Transactional
    @PostMapping("/kayttajat/{profiilimerkkijono}/kuvat/{numero}/poistakuva")
    public String poistaKuva(Model model, @PathVariable String profiilimerkkijono, @PathVariable Integer numero) {
        if ((kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono).getProfiilikuvannumero().equals(numero))) {
            kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono).setProfiilikuvannumero(0);
        }
        kuvaRepository.findByKayttajaAndNumero(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), numero).setContent(null);
        kuvaRepository.findByKayttajaAndNumero(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), numero).setKuvaus("Kuva on poistettu");
        kuvaRepository.findByKayttajaAndNumero(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), numero).getKommentit().clear();
        kuvaKommenttiRepository.findByKuva(kuvaRepository.findByKayttajaAndNumero(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), numero)).clear();
        kuvaRepository.findByKayttajaAndNumero(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), numero).getTykkaykset().clear();
        kuvaTykkaysRepository.findByKuva(kuvaRepository.findByKayttajaAndNumero(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), numero)).clear();
        return "redirect:/kayttajat/" + profiilimerkkijono + "/kuvat/" + numero;
    }
    
    //Asetetaan tietty kuva-albumin kuva profiilikuvaksi. Jos profiilikuvaa ei
    //ole asetettu, käyttäjän profiilissa näkyy oletusprofiilikuva.
    @Transactional
    @PostMapping("/kayttajat/{profiilimerkkijono}/kuvat/{numero}/profiilikuvaksi")
    public String asetaProfiilikuva(Model model, @PathVariable String profiilimerkkijono, @PathVariable Integer numero) {
        kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono).setProfiilikuvannumero(numero);
        return "redirect:/kayttajat/" + profiilimerkkijono;
    }

    //Kuvan kommentointi.
    @PostMapping("/kayttajat/{profiilimerkkijono}/kuvat/{numero}/kommentoi")
    public String kommentoi(@PathVariable String profiilimerkkijono, @PathVariable Integer numero, @RequestParam String kommentti) {
        LocalDateTime aika = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String julkaisuaika = aika.format(formatter);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String kommentoijannimi = kayttajaRepository.findByUsername(auth.getName()).getNimi();
        String kommentoijanprofiilimerkkijono = kayttajaRepository.findByUsername(auth.getName()).getProfiilimerkkijono();
        kuvaKommenttiRepository.save(new KuvaKommentti(kommentti, kuvaRepository.findByKayttajaAndNumero(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), numero), julkaisuaika, kommentoijannimi, kommentoijanprofiilimerkkijono));
        return "redirect:/kayttajat/" + profiilimerkkijono + "/kuvat/" + numero;
    }
    
    //Kuvasta tykkääminen.
    @PostMapping("/kayttajat/{profiilimerkkijono}/kuvat/{numero}/tykkaa")
    public String tykkaa(@PathVariable String profiilimerkkijono, @PathVariable Integer numero) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Kuva kuva = kuvaRepository.findByKayttajaAndNumero(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), numero);
        kuvaTykkaysRepository.save(new KuvaTykkays(username, kuva));
        return "redirect:/kayttajat/" + profiilimerkkijono + "/kuvat/" + numero;
    }
    
    //Kuvan tykkäyksen poisto.
    @PostMapping("/kayttajat/{profiilimerkkijono}/kuvat/{numero}/poistatykkays")
    public String poistatykkays(@PathVariable String profiilimerkkijono, @PathVariable Integer numero) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Kuva kuva = kuvaRepository.findByKayttajaAndNumero(kayttajaRepository.findByProfiilimerkkijono(profiilimerkkijono), numero);
        Long tykkaysid = kuvaTykkaysRepository.findByKuvaAndTykkaajausername(kuva, username).getId();
        kuvaTykkaysRepository.deleteById(tykkaysid);
        return "redirect:/kayttajat/" + profiilimerkkijono + "/kuvat/" + numero;
    }
    
}

