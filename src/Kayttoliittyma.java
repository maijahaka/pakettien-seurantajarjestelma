
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Kayttoliittyma {
    
    private Scanner lukija;
    private Tietokanta tietokanta;
    private Tehokkuustesti tehokkustesti;
    
    public Kayttoliittyma(Scanner lukija, Tietokanta tietokanta) throws SQLException {
        this.lukija = lukija;
        this.tietokanta = tietokanta;
        this.tehokkustesti = new Tehokkuustesti();
    }
    
    public void kaynnista() throws SQLException, ParseException {
        tulostaToiminnot();
        suoritaToiminto();
    }
    
    public void tulostaToiminnot() {
        System.out.println("Käytettävissä olevat toiminnot: \n"
                + "0: Sulje ohjelma\n"
                + "1: Luo taulut tyhjään tietokantaan\n"
                + "2: Lisää uusi paikka\n"
                + "3: Lisää uusi asiakas\n"
                + "4: Lisää uusi paketti\n"
                + "5: Lisää uusi tapahtuma\n"
                + "6: Hae kaikki paketin tapahtumat\n"
                + "7: Hae kaikki asiakkaan paketit ja niihin liittyvien tapahtumien määrä\n"
                + "8: Hae paikan tapahtumien määrä tiettynä päivänä\n"
                + "9: Suorita tietokannan tehokkuustesti"
                );
    }
    
    public void suoritaToiminto() throws SQLException, ParseException {
        
        String paikanNimi = "";
        String asiakkaanNimi = "";
        String seurantakoodi = "";
        
        while (true) {
            System.out.print("Valitse toiminto (0-9): ");
            String toiminto = lukija.nextLine();

            if (toiminto.equals("0")) {
                break;
            }
            
            switch (toiminto) {
                
                case "1":
                    tietokanta.luoTaulut();
                    break;
                case "2":
                    paikanNimi = kysyPaikka();
                    tietokanta.lisaaPaikka(paikanNimi);
                    break;
                case "3":
                    asiakkaanNimi = kysyAsiakas();
                    tietokanta.lisaaAsiakas(asiakkaanNimi);
                    break;
                case "4":
                    seurantakoodi = kysySeurantakoodi();
                    asiakkaanNimi = kysyAsiakas();
                    tietokanta.lisaaPaketti(seurantakoodi, asiakkaanNimi);
                    break;
                case "5":
                    seurantakoodi = kysySeurantakoodi();
                    paikanNimi = kysyPaikka();
                    String tapahtumanKuvaus = kysyKuvaus();
                    tietokanta.lisaaTapahtuma(seurantakoodi, paikanNimi, tapahtumanKuvaus);
                    break;
                case "6":
                    seurantakoodi = kysySeurantakoodi();
                    tietokanta.haePaketinTapahtumat(seurantakoodi);
                    break;
                case "7":
                    asiakkaanNimi = kysyAsiakas();
                    tietokanta.haeAsiakkaanPaketit(asiakkaanNimi);
                    break;
                case "8":
                    paikanNimi = kysyPaikka();
                    String paivamaara = kysyPaivamaara();
                    if (paivamaara.equals("virheellinen päivämäärä")) {
                        System.out.println("VIRHE: Tapahtumien haku ei onnistunut.");
                    } else {
                        tietokanta.haePaikanTapahtumienMaara(paikanNimi, paivamaara);
                    }
                    break;
                case "9":
                    tehokkustesti.ajaTesti();
                    break;
                default:
                    System.out.println("VIRHE: Toimintoa ei ole määritelty. Yritä uudestaan.");
                    break;
            }
        }
    }
        
    public String kysyPaikka() {
        System.out.print("Anna paikan nimi: ");
        String paikanNimi = lukija.nextLine();
        return paikanNimi;    
    }
    
    public String kysyAsiakas() {
        System.out.print("Anna asiakkaan nimi: ");
        String asiakkaanNimi = lukija.nextLine();
        return asiakkaanNimi;
    }
    
    public String kysySeurantakoodi() {
        System.out.print("Anna paketin seurantakoodi: ");
        String seurantakoodi = lukija.nextLine();
        return seurantakoodi;
    }
    
    public String kysyKuvaus() {
        System.out.print("Anna tapahtuman kuvaus: ");
        String tapahtumanKuvaus = lukija.nextLine();
        return tapahtumanKuvaus;
    }
    
    public String kysyPaivamaara() throws ParseException {
        try {
            System.out.print("Anna päivämäärä muodossa pp.kk.vvvv: ");
            String syote = lukija.nextLine();
            //Varmistetaan, että syöte on oikeassa muodossa ja poistetaan mahdolliset nollat päivämäärän ja kuukauden edestä.
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");
            LocalDate pvm = LocalDate.parse(syote, formatter);
            return formatter.format(pvm);
        } catch (Exception e) {
            System.out.println("VIRHE: Päivämäärän luku ei onnistunut. Varmista, että syötit päivämäärän muodossa pp.kk.vvvv.");
            return "virheellinen päivämäärä";
        }
    }
    
}
