
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Scanner;

public class TikapeHarjoitustyo {

    public static void main(String[] args) throws SQLException, ParseException {
        
        Scanner lukija = new Scanner(System.in);
        Tietokanta tietokanta = new Tietokanta();
        
        Kayttoliittyma kayttoliittyma = new Kayttoliittyma(lukija, tietokanta);
        kayttoliittyma.kaynnista();
        
    }
    
}
