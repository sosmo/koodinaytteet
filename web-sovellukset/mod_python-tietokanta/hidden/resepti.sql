DROP TABLE Liittyy
;
DROP TABLE Ohje
;
DROP TABLE resepti
;
DROP TABLE Yksikko
;
DROP TABLE Aine
;
DROP TABLE Ruokalaji
;

CREATE TABLE Ruokalaji (
Nimi VARCHAR(100) NOT NULL,
Kuvaus VARCHAR(250) DEFAULT '-',
RuokalajiID INTEGER PRIMARY KEY AUTOINCREMENT,
CONSTRAINT Ruokalaji_Nimi_UQ   
        UNIQUE (Nimi)
)
;

CREATE TABLE Aine (
Nimi VARCHAR(60) NOT NULL,
Kuvaus VARCHAR(250) DEFAULT '-',
AineID INTEGER PRIMARY KEY AUTOINCREMENT,

CONSTRAINT Aine_Nimi_UQ   
        UNIQUE (Nimi)
)
;



CREATE TABLE Yksikko (
Nimi VARCHAR(20) NOT NULL,
Lyhenne CHAR(3) NOT NULL,

CONSTRAINT Yksikko_PK 
	PRIMARY KEY (Lyhenne)
)
;


CREATE TABLE Resepti (
Nimi VARCHAR(100) NOT NULL,
Kuvaus VARCHAR(250) DEFAULT '-',
Henkilomaara INTEGER DEFAULT 2,
ReseptiID INTEGER PRIMARY KEY AUTOINCREMENT,
RuokalajiID INTEGER NOT NULL,

CONSTRAINT Resepti_RuokalajiID 
	FOREIGN KEY (RuokalajiID)
	REFERENCES Ruokalaji (RuokalajiID)
)
;

CREATE TABLE Ohje (
Vaihenro INTEGER NOT NULL,
ReseptiID INTEGER NOT NULL,
Ohjeteksti VARCHAR(255) NOT NULL,
CONSTRAINT Ohje_PK 
	PRIMARY KEY (Vaihenro, ReseptiID),
CONSTRAINT Ohje_ReseptiID 
	FOREIGN KEY (ReseptiID)
	REFERENCES Resepti (ReseptiID)
)
;



CREATE TABLE Liittyy (
Maara NUMERIC(5,2) DEFAULT 1,
Resepti_ReseptiID INTEGER NOT NULL,
Aine_AineID INTEGER NOT NULL,
Yksikko_Lyhenne CHAR(3) NOT NULL,

CONSTRAINT Liittyy_PK
	PRIMARY KEY (Resepti_ReseptiID,Aine_AineID,Yksikko_Lyhenne),
CONSTRAINT Liittyy_ReseptiID 
	FOREIGN KEY (Resepti_ReseptiID)
	REFERENCES Resepti (ReseptiID),
CONSTRAINT Liittyy_AineID 
	FOREIGN KEY (Aine_AineID)
	REFERENCES Aine (AineID),
CONSTRAINT Liittyy_Lyhenne 
	FOREIGN KEY (Yksikko_Lyhenne)
	REFERENCES Yksikko (Lyhenne)
)
;

-- Koska SQLite ei toteuta viite-eheyksiä toteutetaan ne itse
-- triggereiden avulla
CREATE TRIGGER fki_resepti_ruokalaji_id
BEFORE INSERT ON resepti
FOR EACH ROW BEGIN 
  SELECT CASE
     WHEN ((SELECT ruokalajiid FROM ruokalaji WHERE ruokalajiid = NEW.ruokalajiId) IS NULL)
     THEN RAISE(ABORT, 'insert on table "resepti" violates foreign key constraint "fk_resepti_ruokalaji_id"')
  END;
END;

CREATE TRIGGER fku_resepti_ruokalaji_id
BEFORE UPDATE ON resepti
FOR EACH ROW BEGIN 
   SELECT CASE
     WHEN ((SELECT ruokalajiid FROM ruokalaji WHERE ruokalajiid = NEW.ruokalajiId) IS NULL)
     THEN RAISE(ABORT, 'update on table "resepti" violates foreign key constraint "fk_resepti_ruokalaji_id"')
  END;
END;

-- estetään poistot jos viite-eheys rikkoontuu
CREATE TRIGGER fkd_resepti_ruokalaji_id
BEFORE DELETE ON ruokalaji
FOR EACH ROW BEGIN 
  SELECT CASE
     WHEN ((SELECT ruokalajiid FROM resepti WHERE ruokalajiid = OLD.ruokalajiId) IS NOT NULL)
     THEN RAISE(ABORT, 'delete on table "ruokalaji" violates foreign key constraint "fk_resepti_ruokalaji_id"')
  END;
END;


-- ohje

-- Koska SQLite ei toteuta viite-eheyksiä toteutetaan ne itse
-- triggereiden avulla
CREATE TRIGGER fki_ohje_resepti_id
BEFORE INSERT ON ohje
FOR EACH ROW BEGIN 
  SELECT CASE
     WHEN ((SELECT reseptiid FROM resepti WHERE reseptiid = NEW.reseptiId) IS NULL)
     THEN RAISE(ABORT, 'insert on table "ohje" violates foreign key constraint "fki_ohje_resepti_id"')
  END;
END;

CREATE TRIGGER fku_ohje_resepti_id
BEFORE UPDATE ON ohje
FOR EACH ROW BEGIN 
   SELECT CASE
     WHEN ((SELECT reseptiid FROM resepti WHERE reseptiid = NEW.reseptiId) IS NULL)
     THEN RAISE(ABORT, 'update on table "ohje" violates foreign key constraint "fku_ohje_resepti_id"')
  END;
END;

-- estetään poistot jos viite-eheys rikkoontuu
CREATE TRIGGER fkd_resepti_ohje_id
BEFORE DELETE ON resepti
FOR EACH ROW BEGIN 
  SELECT CASE
     WHEN ((SELECT reseptiid FROM ohje WHERE reseptiid = OLD.reseptiId) IS NOT NULL)
     THEN RAISE(ABORT, 'delete on table "resepti" violates foreign key constraint "fkd_resepti_ohje_id"')
  END;
END;



INSERT INTO ruokalaji VALUES ('Alkuruoka','-',1);
INSERT INTO ruokalaji VALUES ('Pääruoka','-',2);
INSERT INTO ruokalaji VALUES ('Lisäkeruoka','-',3);
INSERT INTO ruokalaji VALUES ('Väli - tai iltapala','-',4);
INSERT INTO ruokalaji VALUES ('Jälkiruoka','-',5);
INSERT INTO ruokalaji VALUES ('Suolainen leivonnainen','-',6);
INSERT INTO ruokalaji VALUES ('Makea leivonnainen','-',7);

INSERT INTO aine VALUES ('Valkosipulinkynsi','-',4);
INSERT INTO aine VALUES ('Ruuanvalmistusjugurtti','-',5);
INSERT INTO aine VALUES ('Mangochutney','-',6);
INSERT INTO aine VALUES ('Vehnäjauho','-',11);
INSERT INTO aine VALUES ('Sokeri','-',10);
INSERT INTO aine VALUES ('Kananmuna','-',9);
INSERT INTO aine VALUES ('Suola','-',7);
INSERT INTO aine VALUES ('Banaani','-',1);
INSERT INTO aine VALUES ('Curry','-',2);
INSERT INTO aine VALUES ('Sipuli','-',3);
INSERT INTO aine VALUES ('Perunajauho','-',12);
INSERT INTO aine VALUES ('Leivinjauhe','-',13);
INSERT INTO aine VALUES ('Kahvi','-',14);
INSERT INTO aine VALUES ('Mantelilikööri','-',15);
INSERT INTO aine VALUES ('Maustamaton tuorejuusto','-',16);
INSERT INTO aine VALUES ('Ranskankerma','-',17);
INSERT INTO aine VALUES ('Kuohukerma','-',18);
INSERT INTO aine VALUES ('Keltuainen','-',19);
INSERT INTO aine VALUES ('Valkuainen','-',20);
INSERT INTO aine VALUES ('Liivatelehti','-',21);
INSERT INTO aine VALUES ('Tomusokeri','-',22);
INSERT INTO aine VALUES ('Jauheliha','-',23);
INSERT INTO aine VALUES ('Purjo','-',24);
INSERT INTO aine VALUES ('Öljy','-',8);
INSERT INTO aine VALUES ('Lehtiselleri','-',25);
INSERT INTO aine VALUES ('Tomaattimurska','-',26);
INSERT INTO aine VALUES ('Lihaliemikuutio','-',27);
INSERT INTO aine VALUES ('Cayennepippuri','-',28);
INSERT INTO aine VALUES ('Valkoiset pavut tomaattikastikkeessa','-',29);
INSERT INTO aine VALUES ('Ananasmurska','-',30);
INSERT INTO aine VALUES ('Sooda','-',31);
INSERT INTO aine VALUES ('Voi','-',32);
INSERT INTO aine VALUES ('Porkkanaraaste','-',33);
INSERT INTO aine VALUES ('Maustamaton koskenlaskijajuusto','-',34);
INSERT INTO aine VALUES ('Vaniljasokeri','-',35);
INSERT INTO aine VALUES ('Digestivekeksi','-',36);
INSERT INTO aine VALUES ('Rommi','-',37);
INSERT INTO aine VALUES ('Kaurahiutale','-',38);
INSERT INTO aine VALUES ('Siirappi','-',39);
INSERT INTO aine VALUES ('Margariini','-',40);

INSERT INTO yksikko VALUES ('Kappale','kpl');
INSERT INTO yksikko VALUES ('Desilitra','dl ');
INSERT INTO yksikko VALUES ('Litra','l  ');
INSERT INTO yksikko VALUES ('Gramma','g  ');
INSERT INTO yksikko VALUES ('Kilogramma','kg ');
INSERT INTO yksikko VALUES ('Teelusikka','tl ');
INSERT INTO yksikko VALUES ('Ruokalusikka','rkl');

INSERT INTO resepti VALUES ('Ananas Con Carne','Tulinen',4,1,2);
INSERT INTO resepti VALUES ('Tiramisu','Hieno herkku',6,2,7);
INSERT INTO resepti VALUES ('Banaanicurry','Mainio kasvisruoka',2,3,2);
INSERT INTO resepti VALUES ('Porkkanapiirakka','Suuri herkku',8,4,7);
INSERT INTO resepti VALUES ('Rommikakku','Hyytelökakku, ei tarvi paistaa',6,5,7);
INSERT INTO resepti VALUES ('Kauralastut','Helppo ja nopea',4,6,7);

INSERT INTO liittyy VALUES (400.00,1,23,'g  ');
INSERT INTO liittyy VALUES (4.00,1,3,'kpl');
INSERT INTO liittyy VALUES (1.00,1,24,'kpl');
INSERT INTO liittyy VALUES (2.00,1,25,'kpl');
INSERT INTO liittyy VALUES (400.00,1,26,'g  ');
INSERT INTO liittyy VALUES (1.00,1,27,'kpl');
INSERT INTO liittyy VALUES (3.00,1,4,'kpl');
INSERT INTO liittyy VALUES (2.00,1,28,'tl ');
INSERT INTO liittyy VALUES (1.00,1,7,'tl ');
INSERT INTO liittyy VALUES (5.00,1,8,'rkl');
INSERT INTO liittyy VALUES (400.00,1,29,'g  ');
INSERT INTO liittyy VALUES (450.00,1,30,'g  ');
INSERT INTO liittyy VALUES (4.00,2,9,'kpl');
INSERT INTO liittyy VALUES (1.50,2,10,'dl ');
INSERT INTO liittyy VALUES (1.00,2,11,'dl ');
INSERT INTO liittyy VALUES (0.75,2,12,'dl ');
INSERT INTO liittyy VALUES (1.00,2,13,'tl ');
INSERT INTO liittyy VALUES (300.00,2,16,'g  ');
INSERT INTO liittyy VALUES (1.00,2,17,'dl ');
INSERT INTO liittyy VALUES (2.00,2,18,'dl ');
INSERT INTO liittyy VALUES (3.00,2,19,'kpl');
INSERT INTO liittyy VALUES (3.00,2,20,'kpl');
INSERT INTO liittyy VALUES (6.00,2,15,'rkl');
INSERT INTO liittyy VALUES (4.00,2,21,'kpl');
INSERT INTO liittyy VALUES (2.00,2,22,'dl ');
INSERT INTO liittyy VALUES (2.00,2,14,'dl ');
INSERT INTO liittyy VALUES (2.00,3,1,'kpl');
INSERT INTO liittyy VALUES (2.00,3,2,'rkl');
INSERT INTO liittyy VALUES (1.00,3,3,'kpl');
INSERT INTO liittyy VALUES (3.00,3,4,'kpl');
INSERT INTO liittyy VALUES (2.00,3,5,'dl ');
INSERT INTO liittyy VALUES (3.00,3,6,'rkl');
INSERT INTO liittyy VALUES (1.00,3,7,'tl ');
INSERT INTO liittyy VALUES (1.00,3,8,'rkl');
INSERT INTO liittyy VALUES (4.00,4,9,'kpl');
INSERT INTO liittyy VALUES (4.00,4,11,'dl ');
INSERT INTO liittyy VALUES (3.00,4,10,'dl ');
INSERT INTO liittyy VALUES (3.00,4,31,'tl ');
INSERT INTO liittyy VALUES (2.00,4,13,'tl ');
INSERT INTO liittyy VALUES (6.00,4,33,'dl ');
INSERT INTO liittyy VALUES (200.00,4,34,'g  ');
INSERT INTO liittyy VALUES (3.00,4,22,'dl ');
INSERT INTO liittyy VALUES (250.00,4,32,'g  ');
INSERT INTO liittyy VALUES (3.00,4,35,'tl ');
INSERT INTO liittyy VALUES (200.00,5,36,'g  ');
INSERT INTO liittyy VALUES (1.00,5,10,'rkl');
INSERT INTO liittyy VALUES (4.00,5,19,'kpl');
INSERT INTO liittyy VALUES (1.50,5,10,'dl ');
INSERT INTO liittyy VALUES (4.00,5,18,'dl ');
INSERT INTO liittyy VALUES (3.00,5,37,'rkl');
INSERT INTO liittyy VALUES (5.00,5,21,'kpl');
INSERT INTO liittyy VALUES (100.00,5,40,'g  ');
INSERT INTO liittyy VALUES (50.00,6,40,'g  ');
INSERT INTO liittyy VALUES (2.50,6,38,'dl ');
INSERT INTO liittyy VALUES (2.00,6,11,'rkl');
INSERT INTO liittyy VALUES (1.00,6,13,'tl ');
INSERT INTO liittyy VALUES (1.00,6,10,'dl ');
INSERT INTO liittyy VALUES (1.00,6,35,'tl ');
INSERT INTO liittyy VALUES (1.00,6,9,'kpl');

INSERT INTO ohje VALUES (1,6,'Sekoita kaikki aineet sulaneeseen rasvaan.');
INSERT INTO ohje VALUES (2,6,'Nosta taikinasta pellille leivinpaperille pieniä nokareita kahdella lusikalla. Jätä reilut välit.');
INSERT INTO ohje VALUES (3,6,'Paista kauralastuja n. 5 minuuttia 200 asteessa. Irrota leivinpaperista hieman jäähtyneinä.');
INSERT INTO ohje VALUES (1,5,'Vatkaa keltuaiset ja sokeri vahdoksi.');
INSERT INTO ohje VALUES (2,5,'Lisää vatkattu kerma ja rommi vaahtoon');
INSERT INTO ohje VALUES (3,5,'Liota lehtiä kulmassa vedessä, liuota sitten kiehuvaan veteen, anna hieman jäähtyä ennen vaahtoon lisäämistä.');
INSERT INTO ohje VALUES (4,5,'Anna hyytyä jääkaapissa ennen tarjoamista n. 2 h. Mieluummin yön yli.');
INSERT INTO ohje VALUES (1,4,'Munat ja sokeri vaahdoksi.');
INSERT INTO ohje VALUES (2,4,'Jauhot sekaisin, lisataan vaahtoon.');
INSERT INTO ohje VALUES (3,4,'Sulatettua voita 175g ja raaste lisätään varovasti sekoittaen.');
INSERT INTO ohje VALUES (4,4,'Sulata loppu voi (ei ruskisteta) pilko juusto sekaan, miedolla lämmöllä kunnes juusto sulaa.');
INSERT INTO ohje VALUES (5,4,'Nosta levyltä, lisää sokerit. Levitä heti leivoslevyn päälle');
INSERT INTO ohje VALUES (1,3,'Silppua sipuli ja murskaa valkosipulinkynsi');
INSERT INTO ohje VALUES (2,3,'Kuumenna pannussa öljyä ja kuullota sipulisilppu');
INSERT INTO ohje VALUES (3,3,'Lisää curryjauhe, valkosipuli, mangochutney ja suola');
INSERT INTO ohje VALUES (4,3,'Kiehuta miedolla lämmöllä viisi minuuttia, lisää kerma ja kiehuta viela viisi minuuttia.');
INSERT INTO ohje VALUES (5,3,'Lisää ruuanvalmistusjugurtti. Lämmitä mutta älä keitä koska jugurtti juoksettuu helposti.');
INSERT INTO ohje VALUES (6,3,'Lohko banaanit viipaleiksi ja sekoita joukkoon.');
INSERT INTO ohje VALUES (7,3,'Tarjoaa persiljariisin kera.');
INSERT INTO ohje VALUES (2,2,'Kaada taikina leivinpaperilla vuorattuun uunipannuun. Paista 200 asteessa n.12min (silmät tarkkana :)');
INSERT INTO ohje VALUES (1,2,'Vatkaa munat ja sokeri vaahdoksi. Sekoita muut aineet keskenään ja seulo vaahtoon');
INSERT INTO ohje VALUES (3,2,'ripota leivinpaperille sokeria, kumoa levy sille ja poista leivinpaperi. Anna jäähtyä.');
INSERT INTO ohje VALUES (4,2,'Liota liivatelehdet kuumaan kahviin. Anna jäähtyä.');
INSERT INTO ohje VALUES (5,2,'Vatkaa keltuaiset ja sokeri vaahdoksi, lisää vaahtoon juusto, ranskankerma, mantelilikööri ja liivateseos. Vaahdota kerma. Vatkaa valkuaiset kovaksi vaahdoksi. Lisää molemmat varovasti seokseen.');
INSERT INTO ohje VALUES (6,2,'Leikkaa kakkulevy kahtia, kostuta levyt kahvi-likööriseoksella. Pane toinen puolikas sopivan kokoiseen vuokaan. Levitä puolet täytteestä levylle, nosta toinen levy ja levitä päällimmäiseksi loput täytteestä. Tasoita ja ripota pinnalle kaakaojauhetta.');
INSERT INTO ohje VALUES (7,2,'Anna jähmettyä jääkaapissa.');
INSERT INTO ohje VALUES (1,1,'Kuumenna öljy padassa ja ruskista lihaa ja sipulia kovalla lämmöllä noin 10-15 minuuttia koko ajan sekoittaen, kunnes neste on haihtunut.');
INSERT INTO ohje VALUES (2,1,'Mausta suolalla ja chilillä (kannattaa olla varovainen:) ja puserra pataan valkosipulia. Kaada nesteeksi tomaatit liemineen ja vähennä lämpöä. Sekoita pataa silloin tällöin. Kaada sekaan 1.5 dl vettä ja lisää lihaliemikuutio.');
INSERT INTO ohje VALUES (3,1,'nostele pataan paprika, purjo ja selleri. Kaada joukkoon ananasmurska ja pavut. Jos jätät ananasmurskan pois niin tuloksena on tavallinen chili con carne.');
INSERT INTO ohje VALUES (4,1,'Tarjoa keitetyn riisin kera.');



