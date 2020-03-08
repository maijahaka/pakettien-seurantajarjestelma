
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Tietokanta {
    
    private Connection db;
    private Statement s;
    
    public Tietokanta() throws SQLException {
        this.db = DriverManager.getConnection("jdbc:sqlite:harjoitustyo.db");
        this.s = db.createStatement();
        //Viiteavainten ehtojen valvonta SQLitessa
        s.execute("PRAGMA foreign_keys = ON");
    }
    
    public void luoTaulut() throws SQLException {
        try {
            s.execute("BEGIN TRANSACTION");
            s.execute("CREATE TABLE Paikat (id INTEGER PRIMARY KEY, nimi TEXT UNIQUE)");
            s.execute("CREATE TABLE Asiakkaat (id INTEGER PRIMARY KEY, nimi TEXT UNIQUE)");
            s.execute("CREATE TABLE Paketit "
                    + "(id INTEGER PRIMARY KEY, seurantakoodi TEXT UNIQUE, "
                    + "asiakas_id INTEGER REFERENCES Asiakkaat)");
//            s.execute("CREATE INDEX idx_asiakas ON Paketit (asiakas_id)"); //indeksi
            s.execute("CREATE TABLE Tapahtumat (id INTEGER PRIMARY KEY, "
                    + "paketti_id INTEGER REFERENCES Paketit, "
                    + "paikka_id INTEGER REFERENCES Paikat, kuvaus TEXT, "
                    + "lisayshetki DATETIME)");
//            s.execute("CREATE INDEX idx_paketti ON Tapahtumat (paketti_id)"); //indeksi
            s.execute("COMMIT");
            System.out.println("Tietokanta luotu");
        } catch (SQLException e) {
            System.out.println("VIRHE: Taulujen luominen ei onnistunut. Varmista, että tietokanta on tyhjä.");
        }
    }
    
    public void lisaaPaikka(String paikanNimi) throws SQLException {
        try {
            PreparedStatement p = db.prepareStatement("INSERT INTO Paikat (nimi) VALUES (?)");
            p.setString(1,paikanNimi);
            p.execute();
            System.out.println("Paikka lisätty");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                //SQLException (ErrorCode: 1): SQL error or missing database (no such table: Paikat) 
                System.out.println("VIRHE: Paikan lisääminen tietokantaan ei onnistunut. Varmista, että taulu \"Paikat\" on luotu tietokantaan.");
            } else if (e.getErrorCode() == 19) {
                //SQLException (ErrorCode: 19): Abort due to constraint violation (UNIQUE constraint failed: Paikat.nimi)
                System.out.println("VIRHE: Paikka \"" + paikanNimi + "\" on jo olemassa.");
            } else {
                System.out.println("VIRHE: Paikan lisääminen tietokantaan ei onnistunut.");
            }      
        }
    }
    
    public void lisaaAsiakas(String asiakkaanNimi) throws SQLException {
        try {
            PreparedStatement p = db.prepareStatement("INSERT INTO Asiakkaat (nimi) VALUES (?)");
            p.setString(1,asiakkaanNimi);
            p.execute();
            System.out.println("Asiakas lisätty");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                //SQLException (ErrorCode: 1): SQL error or missing database (no such table: Asiakkaat) 
                System.out.println("VIRHE: Asiakkaan lisääminen tietokantaan ei onnistunut. Varmista, että taulu \"Asiakkaat\" on luotu tietokantaan.");
            } else if (e.getErrorCode() == 19) {
                //SQLException (ErrorCode: 19): Abort due to constraint violation (UNIQUE constraint failed: Asiakkaat.nimi)
                System.out.println("VIRHE: Asiakas \"" + asiakkaanNimi + "\" on jo olemassa.");
            } else {
                System.out.println("VIRHE: Asiakkaan lisääminen tietokantaan ei onnistunut.");
            }
        }
    }
    
    public void lisaaPaketti(String seurantakoodi, String asiakkaanNimi) throws SQLException {
        try {
            int asiakasId = haeAsiakasId(asiakkaanNimi);
            PreparedStatement p = db.prepareStatement("INSERT INTO Paketit (seurantakoodi, asiakas_id) VALUES (?, " + asiakasId + ")");
            p.setString(1,seurantakoodi);
            p.execute();
            System.out.println("Paketti lisätty");
        } catch (SQLException e) {
            String virheviestiSeurantakoodiOnJoOlemassa = "[SQLITE_CONSTRAINT]  Abort due to constraint violation (UNIQUE constraint failed: Paketit.seurantakoodi)";
            if (e.getErrorCode() == 1) {
                //SQLException (ErrorCode: 1): SQL error or missing database (no such table: Paketit)
                System.out.println("VIRHE: Paketin lisääminen tietokantaan ei onnistunut. Varmista, että taulu \"Paketit\" on luotu tietokantaan.");
            } else if (e.getMessage().equals(virheviestiSeurantakoodiOnJoOlemassa)) {
                //SQLException (ErrorCode: 19): Abort due to constraint violation (UNIQUE constraint failed: Paketit.seurantakoodi)
                System.out.println("VIRHE: Seurantakoodi \"" + seurantakoodi + "\" on jo olemassa.");
            } else {
                System.out.println("VIRHE: Paketin lisääminen tietokantaan ei onnistunut.");
            }
        }
    }
    
    public void lisaaTapahtuma(String seurantakoodi, String paikanNimi, String tapahtumanKuvaus) throws SQLException {
        try {
            int pakettiId = haePakettiId(seurantakoodi);
            int paikkaId = haePaikkaId(paikanNimi);
            String lisayshetki = haePaivamaaraJaAika();
            PreparedStatement p = db.prepareStatement("INSERT INTO Tapahtumat (paketti_id, paikka_id, kuvaus, lisayshetki) VALUES (" + pakettiId + ", " + paikkaId + ", ?, '" + lisayshetki + "')");
            p.setString(1,tapahtumanKuvaus);
            p.execute();
            System.out.println("Tapahtuma lisätty");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                //SQLException (ErrorCode: 1): SQL error or missing database (no such table: Tapahtumat) 
                System.out.println("VIRHE: Tapahtuman lisääminen ei onnistunut. Varmista, että taulu \"Tapahtumat\" on luotu tietokantaan.");
            } else {
                System.out.println("VIRHE: Tapahtuman lisääminen ei onnistunut.");
            }
        }
    }
    
    public void haePaketinTapahtumat(String seurantakoodi) throws SQLException {
        try {
            int pakettiId = haePakettiId(seurantakoodi);
            if (pakettiId == -1) {
                System.out.println("VIRHE: Tapahtumien haku ei onnistunut.");
            } else {
                ResultSet r = s.executeQuery("SELECT * FROM Tapahtumat WHERE paketti_id = " + pakettiId);
                boolean onTapahtumia = false;
                while (r.next()) {
                    String lisayshetki = r.getString("lisayshetki");
                    int paikkaId = r.getInt("paikka_id");
                    String paikanNimi = haePaikanNimi(paikkaId);
                    String tapahtumanKuvaus = r.getString("kuvaus");
                    System.out.println(lisayshetki + ", " + paikanNimi + ", "+ tapahtumanKuvaus);
                    onTapahtumia = true;
                }
                if (!onTapahtumia) {
                    System.out.println("Paketilla \"" + seurantakoodi + "\" ei ole tapahtumia.");
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                //SQLException (ErrorCode: 1): SQL error or missing database (no such table: Tapahtumat) 
                System.out.println("VIRHE: Tapahtumien haku ei onnistunut. Varmista, että taulu \"Tapahtumat\" on luotu tietokantaan.");
            } else {
                System.out.println("VIRHE: Tapahtumien haku ei onnistunut.");
            }
        }
    }

    public void haeAsiakkaanPaketit(String asiakkaanNimi) throws SQLException {
        try {
            Statement s1 = db.createStatement();
            int asiakasId = haeAsiakasId(asiakkaanNimi);
            if (asiakasId == -1) {
                System.out.println("VIRHE: Pakettien haku ei onnistunut.");
            } else {
                ResultSet r1 = s.executeQuery("SELECT * FROM Paketit WHERE asiakas_id = " + asiakasId);
                boolean onPaketteja = false;
                while (r1.next()) {
                    String seurantakoodi = r1.getString("seurantakoodi");
                    int pakettiId = r1.getInt("id");
                    ResultSet r2 = s1.executeQuery("SELECT COUNT(*) AS tapahtumien_maara FROM Tapahtumat WHERE paketti_id = " + pakettiId);
                    int tapahtumienMaara = r2.getInt("tapahtumien_maara");
                    System.out.print(seurantakoodi + ", " + tapahtumienMaara + " ");
                    if (tapahtumienMaara == 1) {
                        System.out.println("tapahtuma");
                    } else {
                        System.out.println("tapahtumaa");
                    }
                    onPaketteja = true;
                } 
                if (!onPaketteja) {
                    System.out.println("Asiakkaalla \"" + asiakkaanNimi + "\" ei ole paketteja.");
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                //SQLException (ErrorCode: 1): SQL error or missing database (no such table: Paketit) 
                System.out.println("VIRHE: Pakettien haku ei onnistunut. Varmista, että taulu \"Paketit\" on luotu tietokantaan.");
            } else {
                System.out.println("VIRHE: Pakettien haku ei onnistunut.");
            }
        }    
    } 
    
    public void haePaikanTapahtumienMaara(String paikanNimi, String paivamaara) throws SQLException {
        try {
            int paikkaId = haePaikkaId(paikanNimi);
            if (paikkaId == -1) {
                System.out.println("VIRHE: Tapahtumien haku ei onnistunut.");
            } else {
                String pvm = paivamaara;
                ResultSet r1 = s.executeQuery("SELECT COUNT(*) AS tapahtumien_maara FROM (SELECT * FROM Tapahtumat WHERE paikka_id = " + paikkaId + " AND lisayshetki LIKE '" + pvm + "%')");
                int tapahtumienMaara = r1.getInt("tapahtumien_maara");
                System.out.println("Tapahtumien määrä: " + tapahtumienMaara);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                //SQLException (ErrorCode: 1): SQL error or missing database (no such table: Tapahtumat) 
                System.out.println("VIRHE: Tapahtumien haku ei onnistunut. Varmista, että taulu \"Tapahtumat\" on luotu tietokantaan.");
            } else {
                System.out.println("VIRHE: Tapahtumien haku ei onnistunut.");
            }
        }
    }
    
    public int haeAsiakasId(String asiakkaanNimi) throws SQLException {
        try {
            PreparedStatement p = db.prepareStatement("SELECT id FROM Asiakkaat WHERE nimi=?");
            p.setString(1,asiakkaanNimi);
            ResultSet r = p.executeQuery();
            return r.getInt("id");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                //SQLException (ErrorCode: 1): SQL error or missing database (no such table: Asiakkaat) 
                System.out.println("VIRHE: Asiakasta ei löytynyt. Varmista, että taulu \"Asiakkaat\" on luotu tietokantaan.");
                return -1;
            } else {
                //Jos nimellä asiakkaanNimi ei löydy asiakasta, palautetaan arvo -1.
                System.out.println("VIRHE: Asiakasta \"" + asiakkaanNimi + "\" ei ole olemassa.");
                return -1;
            }
        }
    }
    
    public int haePakettiId(String seurantakoodi) throws SQLException {
        try {
            PreparedStatement p = db.prepareStatement("SELECT id FROM Paketit WHERE seurantakoodi=?");
            p.setString(1,seurantakoodi);
            ResultSet r = p.executeQuery();
            return r.getInt("id");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
            //SQLException (ErrorCode: 1): SQL error or missing database (no such table: Paketit) 
                System.out.println("VIRHE: Pakettia ei löytynyt. Varmista, että taulu \"Paketit\" on luotu tietokantaan.");
                return -1;
            } else {
                //Jos annetulla seurantakoodilla ei löydy pakettia, palautetaan arvo -1.
                System.out.println("VIRHE: Pakettia seurantakoodilla \"" + seurantakoodi + "\" ei ole olemassa.");
                return -1;
            }
        }
    }
        
    public int haePaikkaId(String paikanNimi) throws SQLException {
        try {
            PreparedStatement p = db.prepareStatement("SELECT id FROM Paikat WHERE nimi=?");
            p.setString(1,paikanNimi);
            ResultSet r = p.executeQuery();
            return r.getInt("id");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                //SQLException (ErrorCode: 1): SQL error or missing database (no such table: Paikat) 
                System.out.println("VIRHE: Paikkaa ei löytynyt. Varmista, että taulu \"Paikat\" on luotu tietokantaan.");
                return -2;
            } else {
                //Jos nimellä paikanNimi ei löydy paikkaa, palautetaan arvo -1.
                System.out.println("VIRHE: Paikkaa \"" + paikanNimi + "\" ei ole olemassa.");
                return -1;
            }
        }
    }
    
    public String haePaikanNimi(int paikkaId) throws SQLException {
        try {
            Statement s1 = db.createStatement();
            ResultSet r1 = s1.executeQuery("SELECT nimi FROM Paikat WHERE id = " + paikkaId);
            return r1.getString("nimi");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                //SQLException (ErrorCode: 1): SQL error or missing database (no such table: Paikat) 
                System.out.println("VIRHE: Paikkaa ei löytynyt. Varmista, että taulu \"Paikat\" on luotu tietokantaan.");
                return "paikkaa ei löydy";
            } else {
                System.out.println("VIRHE: Paikkaa ei ole olemassa.");
                return "paikkaa ei löydy";
            }
        }
    }
    
    public String haePaivamaaraJaAika() {
        LocalDateTime pvmJaAika = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm");
        return formatter.format(pvmJaAika);
    }
    
}
