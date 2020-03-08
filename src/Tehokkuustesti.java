
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Tehokkuustesti {
    
    private DateTimeFormatter formatter;
    
    public Tehokkuustesti() throws SQLException {
        this.formatter = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm");
    }
    
    public void ajaTesti() throws SQLException {
        try {
            Connection dbTesti = DriverManager.getConnection("jdbc:sqlite:harjoitustyo.db");
            Statement sTesti = dbTesti.createStatement();
            String paikanNimi = "";
            String asiakkaanNimi = "";
            String pvmJaAika = "";
            sTesti.execute("BEGIN TRANSACTION");
    //        1. Tietokantaan lisätään tuhat paikkaa nimillä P1, P2, P3, jne.
            long aika11 = System.nanoTime();
            PreparedStatement p1 = dbTesti.prepareStatement("INSERT INTO Paikat (nimi) VALUES ('P' || ?)");
            for (int i = 1; i <= 1000; i++) {
                p1.setInt(1, i); 
                p1.executeUpdate();
            }
            long aika12 = System.nanoTime();
            System.out.println("Vaiheeseen 1 kului aikaa " + (aika12 - aika11)/1e9 + " sekuntia");
    //        2. Tietokantaan lisätään tuhat asiakasta nimillä A1, A2, A3, jne.
            long aika21 = System.nanoTime();
            PreparedStatement p2 = dbTesti.prepareStatement("INSERT INTO Asiakkaat (nimi) VALUES ('A' || ?)");
            for (int i = 1; i <= 1000; i++) {
                p2.setInt(1, i); 
                p2.executeUpdate();
            }
            long aika22 = System.nanoTime();
            System.out.println("Vaiheeseen 2 kului aikaa " + (aika22 - aika21)/1e9 + " sekuntia");
    //        3. Tietokantaan lisätään tuhat pakettia, jokaiselle jokin asiakas.
            long aika31 = System.nanoTime();
            PreparedStatement p3 = dbTesti.prepareStatement("INSERT INTO Paketit (seurantakoodi, asiakas_id) VALUES (?, ?)");
            for (int i = 1; i <= 1000; i++) {
                p3.setInt(1, i);
                p3.setInt(2, i);
                p3.executeUpdate();
            }
            long aika32 = System.nanoTime();
            System.out.println("Vaiheeseen 3 kului aikaa " + (aika32 - aika31)/1e9 + " sekuntia");
    //        4. Tietokantaan lisätään miljoona tapahtumaa, jokaiselle jokin paketti.
            long aika41 = System.nanoTime();
            PreparedStatement p4 = dbTesti.prepareStatement("INSERT INTO Tapahtumat (paketti_id, paikka_id, kuvaus, lisayshetki) "
                            + "VALUES (?, ?, 'tapahtuman kuvaus', ?)");
            for (int i = 1; i <= 1000; i++) {
                for (int j = 1; j <= 1000; j++) {
                    p4.setInt(1, i);
                    p4.setInt(2, j);
                    p4.setString(3, formatter.format(LocalDateTime.now()));
                    p4.executeUpdate();
                }
            }
            long aika42 = System.nanoTime();
            System.out.println("Vaiheeseen 4 kului aikaa " + (aika42 - aika41)/1e9 + " sekuntia");
            sTesti.execute("COMMIT");
    //        5. Suoritetaan tuhat kyselyä, joista jokaisessa haetaan jonkin asiakkaan pakettien määrä.
            long aika51 = System.nanoTime();
            PreparedStatement p5 = dbTesti.prepareStatement("SELECT COUNT(*) FROM Paketit WHERE asiakas_id = ?");
            for (int i = 1; i <= 1000; i++) {
                p5.setInt(1,i);
                p5.executeQuery();
            }
            long aika52 = System.nanoTime();
            System.out.println("Vaiheeseen 5 kului aikaa " + (aika52 - aika51)/1e9 + " sekuntia");
    //        6. Suoritetaan tuhat kyselyä, joista jokaisessa haetaan jonkin paketin tapahtumien määrä.
            long aika61 = System.nanoTime();
            PreparedStatement p6 = dbTesti.prepareStatement("SELECT COUNT(*) FROM Tapahtumat WHERE paketti_id = ?");
            for (int i = 1; i <= 1000; i++) {
                p6.setInt(1,i);
                p6.executeQuery();
            }
            long aika62 = System.nanoTime();
            System.out.println("Vaiheeseen 6 kului aikaa " + (aika62 - aika61)/1e9 + " sekuntia");
            dbTesti.close();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                System.out.println("VIRHE: Testin suoritus ei onnistunut. Varmista, että tietokannan taulut on luotu.");
            } else if (e.getErrorCode() == 19) {
                System.out.println("VIRHE: Testin suoritus ei onnistunut. Varmista, ettei tietokannassa ole paikkoja nimillä P1...1000, "
                        + "asiakkaita nimillä A1...1000 tai paketteja seurantakoodeilla 1...1000. \n"
                        + "Tehokkuustestin voi suorittaa tietokannalle vain kerran. "
                        + "Tarvittaessa yritä uudestaan tyhjälle tietokannalle.");
            } else {
                System.out.println("VIRHE: Testin suoritus ei onnistunut.");
            }
        }
    }
}

