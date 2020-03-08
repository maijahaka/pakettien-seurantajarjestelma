# pakettien-seurantajarjestelma
Järjestelmä pakettien tapahtumien seuraamiseen. Tietokantojen perusteet -kurssin harjoitustyö.

Toteutus sisältää Java-komentoriviohjelman, jonka kautta käyttäjä pystyy luomaan SQL-tietokannan, lisäämään sinne tietoa ja suorittamaan erilaisia hakuja.

## Toiminnot

Ohjelma sisältää seuraavat toiminnot:

1.	Sovelluksen tarvitsemien taulujen luominen tyhjään tietokantaan. Taulujen tulee olla olemassa, jotta muita toimintoja voidaan käyttää.
1.	Uuden paikan lisääminen tietokantaan, kun käyttäjä antaa paikan nimen.
1.	Uuden asiakkaan lisääminen tietokantaan, kun käyttäjä antaa asiakkaan nimen.
1.	Uuden paketin lisääminen tietokantaan, kun käyttäjä antaa paketin seurantakoodin ja asiakkaan nimen.
1.	Uuden tapahtuman lisääminen tietokantaan, kun käyttäjä antaa paketin seurantakoodin, tapahtuman paikan ja tapahtuman kuvauksen.
1.	Paketin kaikkien tapahtumien hakeminen seurantakoodin perusteella.
1.	Asiakkaan kaikkien pakettien ja niihin liittyvien tapahtumien määrän hakeminen.
1.	Tiettyyn paikkaan ja päivämäärään liittyvien tapahtumien määrän hakeminen.
1.	Tietokannan tehokkuustestin suorittaminen. 

Kun sovellus käynnistetään, ohjelma antaa listan mahdollisista toiminnoista. Käyttäjä valitsee suoritettavan toiminnon syöttämällä sen numeron. Sovellus suljetaan syöttämällä "0".

Halutun toiminnon kysely on toteutettu ohjelman käyttöliittymässä. Käyttäjän syötteen perusteella ohjelma suorittaa toimintoon liittyvän metodin Tietokanta-luokasta, joka sisältää ohjelman tarvitsemat tietokannan käyttöön liittyvät metodit.

Toimintojen tarkempi toteutus on selostettu alla:

### 1. Taulujen luonti

Tämä toiminto luo sovelluksen tarvitsemat taulut (Paikat, Asiakkaat, Käyttäjät ja Tapahtumat) tyhjään tietokantaan. Tämä toiminto tulee olla suoritettuna ennen muiden toimintojen käyttämistä ja sen voi suorittaa yhteen tietokantaan vain kerran. Sovellus antaa virheviestin, jos taulut ovat jo olemassa ja tätä toimintoa yritetään suorittaa.

### 2. Paketin lisäys tietokantaan

Tämä toiminto pyytää ensin käyttäjältä paikan nimen syötteenä Käyttöliittymä-luokan metodissa. Tämän jälkeen syöte parametrisoidaan ja lisätään tietokannan Paikat-tauluun Tietokanta-luokan metodissa. Jokaisella paketilla tulee olla eri nimi. Ohjelma antaa virheilmoituksen, jos käyttäjä yrittää lisätä paikkaa jo tietokannassa olevalla paikan nimellä. Virheilmoitus annetaan myös, jos ensimmäistä toimintoa ei ole suoritettu eli tietokannan tauluja ei ole luotu.

### 3. Asiakkaan lisäys tietokantaan

Asiakkaan lisäys toimii vastaavalla tavalla kuin paikan lisäys. Nyt käyttäjältä kysytään syötteenä asiakkaan nimi ja se lisätään tietokannan Asiakkaat-tauluun. Myös asiakkaiden nimien tulee olla uniikkeja.

### 4. Paketin lisäys tietokantaan

Käyttäjältä kysytään paketin seurantakoodi ja sen asiakkaan nimi, johon paketti liittyy. Asiakkaan tulee olla olemassa tietokannassa. Molemmat syötteet parametrisoidaan. Tietokanta-luokan metodi hakee Asiakkaat-taulusta asiakkaan id:n syötetyn nimen perusteella, ja seurantakoodi ja asiakas-id lisätään tietokannan Paketit-tauluun. Ohjelma antaa virheviestin, jos samalla seurantakoodilla on jo olemassa paketti, jos annetulla nimellä ei löydy asiakasta tai jos tietokannan tauluja ei ole luotu.

### 5. Tapahtuman lisäys tietokantaan

Käyttäjältä kysytään paketin seurantakoodi, tapahtumaan liittyvä paikka ja tapahtuman kuvaus. Paketin ja paikan tulee olla olemassa tietokannassa. Syötteet parametrisoidaan, ja Tietokanta-luokan metodit hakevat annettuja seurantakoodia ja paikkaa vastaavat paketti-id:n ja paikka-id:n tietokannan Paketit- ja Paikat-tauluista. Tämän jälkeen tietokannan Tapahtumat-tauluun lisätään paketti-id, paikka-id ja tapahtuman kuvaus sekä lisäyshetki, joka on Tietokanta-luokan metodilla sopivaan String-muotoon muutettu järjestelmän senhetkinen aika. Ohjelma antaa virheilmoituksen, jos annettua pakettia tai paikkaa ei löydy tai jos tietokannan tauluja ei ole luotu.

### 6. Paketin tapahtumien haku

Käyttäjä antaa syötteenä paketin seurantakoodin. Tietokanta-luokan metodi parametrisoi syötteen ja hakee seurantakoodia vastaavan id:n Paketit-taulukosta. Tämän jälkeen ohjelma tulostaa Tapahtumat-taulukosta kaikkia tätä paketti-id:tä vastaavien tapahtumien tiedot. Ohjelma antaa virheilmoituksen, jos annetulla seurantakoodilla ei löydy pakettia tai jos tietokannan tauluja ei ole luotu.

### 7. Asiakkaan pakettien haku

Toiminto toimii vastaavalla tavalla kuin paketin toimintojen haku, mutta nyt käyttäjältä pyydetään syötteena asiakkaan nimi, jota vastaava id etsitään Asiakkaat-taulusta. Tämän jälkeen ohjelma tulostaa kaikkien asiakkaan pakettien seurantakoodit ja kuhunkin pakettiin liittyvien tapahtumien määrän. Ohjelma antaa virheilmoituksen, jos annetulla nimellä ei löydy asiakasta tai jos tietokannan tauluja ei ole luotu.

### 8. Paikan tapahtumien määrä tiettynä päivänä

Asiakkaalta pyydetään syötteenä paikan nimi ja päivämäärä. Ohjelma varmistaa, että päivämäärä on syötetty toivotussa muodossa ja poistaa mahdolliset nollat päivämäärän ja kuukauden numeron edestä, sillä päivämäärät on syötetty Tapahtumat-tauluun ilman nollia. Ohjelma parametrisoi paikan nimen ja hakee sitä vastaavan id:n Paikat-taulukosta. Tämän jälkeen ohjelma tulostaa tätä paikka-id:tä vastaavien tapahtumien määrän annettuna päivämääränä.

### 9. Tehokkuustesti

Suoritetaan tietokannan tehokkuustesti. Tehokkuustestissä ohjelma suorittaa seuraavat toiminnot:

1.	Tietokantaan lisätään tuhat paikkaa nimillä P1, P2, P3, jne.
1.	Tietokantaan lisätään tuhat asiakasta nimillä A1, A2, A3, jne.
1.	Tietokantaan lisätään tuhat pakettia, joista jokaiseen liitetään asiakas asiakas-id:tä käyttäen.
1.	Tietokantaan lisätään miljoona tapahtumaa, joista jokaiseen liitetään paketti paketti-id:tä käyttäen.
1.	Suoritetaan tuhat kyselyä, joista jokaisessa haetaan jonkin asiakkaan pakettien määrä asiakas-id:tä käyttäen.
1.	Suoritetaan tuhat kyselyä, joista jokaisessa haetaan jonkin paketin tapahtumien määrä.

Ohjelma tulostaa eri vaiheisiin kuluneen ajan.

## Tietokantakaavio

Ohjelman luoman tietokannan tietokantakaavio:

![Tietokantakaavio](tietokantakaavio.png)

Tietokannassa on neljä taulua: Paikat, Asiakkaat, Paketit ja Tapahtumat.

Paikat-taulu sisältää paikkojen nimiä, joiden tulee olla uniikkeja. Vastaavasti Asiakkaat-taulu sisältää uniikkeja asiakkaiden nimiä. Paketit-taulu sisältää uniikkeja pakettien seurantakoodeja, minkä lisäksi jokaiseen pakettiin on liitetty asiakas viittauksella Asiakkaat-tauluun. Yhdellä asiakkaalla voi olla useampia paketteja.

Tapahtumat-taulu koostuu paketteihin liittyvistä tapahtumista, joista jokainen liittyy tiettyyn pakettiin ja paikkaan. Lisäksi taulu sisältää sarakkeet tapahtumien kuvaukselle ja lisäysajankohdalle. Yhteen pakettiin voi liittyä useampia tapahtumia, ja useampi tapahtuma voi yhdistyä samaan paikkaan.

## SQL-skeema

Ohjelman luoman tietokannan SQL-skeema:

```sql
CREATE TABLE Paikat (id INTEGER PRIMARY KEY, nimi TEXT UNIQUE);	
CREATE TABLE Asiakkaat (id INTEGER PRIMARY KEY, nimi TEXT UNIQUE);
CREATE TABLE Paketit (id INTEGER PRIMARY KEY, seurantakoodi TEXT UNIQUE, asiakas_id INTEGER REFERENCES Asiakkaat);
CREATE TABLE Tapahtumat (id INTEGER PRIMARY KEY, paketti_id INTEGER REFERENCES Paketit, paikka_id INTEGER REFERENCES Paikat, kuvaus TEXT, lisayshetki DATETIME);
```

## Indeksit

Tietokannan hakuja voi nopeuttaa lisäämällä indeksit Paketit-taulun asiakas-id -sarakkeeseen ja Tapahtumat-taulun paketti-id-sarakkeeseen.

Indeksit lisätään lisäämällä seuraavat komennot Tietokanta-luokan luoTaulut()-metodiin:

```java
s.execute("CREATE INDEX idx_asiakas ON Paketit (asiakas_id)");
s.execute("CREATE INDEX idx_paketti ON Tapahtumat (paketti_id)");
```

Tällöin luodun tietokannan SQL-skeema näyttää seuraavalta:

```sql
CREATE TABLE Paikat (id INTEGER PRIMARY KEY, nimi TEXT UNIQUE);	
CREATE TABLE Asiakkaat (id INTEGER PRIMARY KEY, nimi TEXT UNIQUE);
CREATE TABLE Paketit (id INTEGER PRIMARY KEY, seurantakoodi TEXT UNIQUE, asiakas_id INTEGER REFERENCES Asiakkaat);
CREATE INDEX idx_asiakas ON Paketit (asiakas_id);
CREATE TABLE Tapahtumat (id INTEGER PRIMARY KEY, paketti_id INTEGER REFERENCES Paketit, paikka_id INTEGER REFERENCES Paikat, kuvaus TEXT, lisayshetki DATETIME);
CREATE INDEX idx_paketti ON Tapahtumat (paketti_id);
```



