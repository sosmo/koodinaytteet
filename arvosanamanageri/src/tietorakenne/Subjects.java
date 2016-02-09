//package tietorakenne;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.Arrays;
//import java.util.Comparator;
//
//import sekalaiset.SailoException;
//
///**
 //* Luokka Subject-alkioiden hallintaan.
 //* 
 //* @author Sampo Osmonen
 //*/
//public class Subjects {
//
	//private String tiedosto = "oletus";
	//private boolean muutettu = false;
	//private int lkm = 0;
	//private int maxAlkiot = 35;
	//private Subject[] alkiot = new Subject[this.maxAlkiot];
//
	///**
	 //* @return alkioiden lukumäärä
	 //*/
	//public int getLkm() {
		//return this.lkm;
	//}
//
	///**
	 //* Lisää aineen alkiot-taulukkoon. Kasvattaa taulukkoa kasvatus-muuttujan verran aina, kun se tulee täyteen.
	 //*
	 //* @param aine lisättävän aineen viite
	 //* @example
	 //* <pre name="test">
	 //*  Subjects aineet = new Subjects();
	 //*  Subject o1 = new Subject(), o2 = new Subject();
	 //*  aineet.add(o1);
	 //*  aineet.add(o2);
	 //*  aineet.getLkm() === 2;
	 //*  aineet.get(1) === o2;
	 //*  for (int i = 0; i <= 35; i++) {
	 //*   aineet.add(o1);
	 //*  }
	 //*  aineet.get(36) === o1;
	 //* </pre>
	 //*/
	//public void add(Subject aine) {
		//int kasvatus = 50;
		//if (this.lkm >= this.maxAlkiot) {
			//this.maxAlkiot += kasvatus;
			//this.alkiot = Arrays.copyOf(this.alkiot, this.maxAlkiot);
		//}
		//this.alkiot[this.lkm] = aine;
		//this.lkm += 1;
	//}
//
	///**
	 //* Palauttaa halutun aineen viitteen.
	 //*
	 //* @param indeksi missä indeksissä olevan aineen viite halutaan
	 //* @return viite aineeseen, joka on taulukossa paikalla indeksi
	 //*/
	//public Subject get(int indeksi) {
		//if (indeksi < 0 || indeksi >= this.lkm) {
			//throw new IndexOutOfBoundsException("Laiton indeksi: " + indeksi);
		//}
		//return this.alkiot[indeksi];
	//}
//
	///**
	 //* Palauttaa Aineen, jonka nimi vastaa hakua.
	 //* 
	 //* @param aine hakujono nimelle
	 //* @return 1. aine, jonka nimi vastaa hakua
	 //*/
	//public Subject aineNimella(String aine) {
		//for (int i = 0; i < this.lkm; i++) {
			//if (this.alkiot[i].getAine().equals(aine)) {
				//return this.alkiot[i];
			//}
		//}
		//return null;
	//}
//
	///**
	 //* Palauttaa Aineen, jonka id vastaa hakua.
	 //* 
	 //* @param id hakujono id:lle
	 //* @return 1. aine, jonka id vastaa hakua
	 //*/
	//public Subject aineIdlla(int id) {
		//for (int i = 0; i < this.lkm; i++) {
			//if (this.alkiot[i].getId() == id) {
				//return this.alkiot[i];
			//}
		//}
		//return null;
	//}
//
	///**
	 //* Poistaa aineen, jonka id vastaa hakua.
	 //* 
	 //* @param id haku, jota vastaava 1. aine poistetaan
	 //* @return true, jos poistettiin jotain, muuten false
	 //*/
	//public boolean remove(int id) {
		//int kohdalla = 0;
		//while (kohdalla < this.lkm && this.alkiot[kohdalla].getId() != id) {
			//kohdalla++;
		//}
		//// jos ei löydy
		//if (kohdalla > this.lkm - 1) {
			//return false;
		//}
		//int paikka = kohdalla + 1;
		//while (paikka < this.lkm) {
			//// iffiä ei tässä tarvittaisi koska hakua vastaavia id:itä voi olla vain 1, joten kaikkia saa siirtää
			//if (this.alkiot[paikka].getId() != id) {
				//this.alkiot[kohdalla++] = this.alkiot[paikka];
			//}
			//paikka++;
		//}
		//this.lkm = kohdalla;
		//return true;
	//}
	//
	///**
	 //* Lajittelee alkiot.
	 //*/
	//public void lajittele() {
		//Arrays.sort(this.alkiot, new Comparator<Subject>() {
			//@Override
			//public int compare(Subject eka, Subject toka) {
				//if (eka == null) {
					//return 1;
				//}
				//if (toka == null) {
					//return -1;
				//}
				//return eka.compareTo(toka);
			//}
		//});
	//}
//
	//
	///**
	 //* Lukee halutun kansion.
	 //* 
	 //* @param kansio luettavan kansion nimi
	 //* @throws SailoException jos lukeminen ei onnistu
	 //*/
	//public void lue(String kansio) throws SailoException {
		//if (kansio.equals("")) {
			//kansio = this.tiedosto;  // ihan sama
		//}
		//try (BufferedReader br = new BufferedReader(new FileReader(kansio + File.separator + "aineet.dat"))) {  //TODO
			//this.tiedosto = kansio;
			//String line;
			//while ((line = br.readLine()) != null) {
				//if (line.equals("") || line.charAt(0) == '#') {
					//continue;
				//}
				//Subject uusi = new Subject();
				//uusi.parse(line);
				//add(uusi);
			//}
		//}
		//catch (FileNotFoundException e) {
			//throw new SailoException("Tiedosto ei aukea! " + e.getMessage());
		//}
		//catch (IOException e) {
			//throw new SailoException("Virhe! " + e.getMessage());
		//}
	//}
	//
	///**
	 //* Tallentaa aineet tiedostoon.  
	 //*
	 //* @param yla kansion, johon tiedosto tallennetaan, nimi
	 //* @throws SailoException jos talletus epäonnistuu
	 //*/
	//public void tallenna(String yla) throws SailoException {
		////if (!muutettu) return;
		//
		//String kohde = yla + File.separator + "aineet.dat";
		//File tiedosto = new File(kohde);
	//
		//File nykyBak = new File(yla + File.separator + "aineet.bak");
		//File uusiBak = new File(kohde);
		//nykyBak.delete();
		//uusiBak.renameTo(nykyBak);
		//try {
			//uusiBak.createNewFile();
		//} catch (IOException e) {
			//throw new SailoException("Varmuuskopioin luonti ei onnistunut!");
		//}
	//
		//try (PrintWriter pw = new PrintWriter(new FileWriter(tiedosto))) {
			//for (int i = 0; i < this.lkm; i++) { //TODO: Iteraattori
				//Subject aine= this.alkiot[i];
				//pw.println(aine.toString());
			//}
		//} catch ( FileNotFoundException ex ) {
			//throw new SailoException("Tiedosto " + tiedosto.getName() + " ei aukea");
		//} catch ( IOException ex ) {
			//throw new SailoException("Tiedoston " + tiedosto.getName() + " kirjoittamisessa ongelmia");
		//}
	//
		//this.muutettu = false;
	//}
//
//}










package tietorakenne;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sekalaiset.StorageException;

/**
 * Luokka Subject-alkioiden hallintaan.
 * 
 * @author Sampo Osmonen
 */
public class Subjects extends AUnits<Subject> {

	public Subjects() {
		super(Subject.class, "aineet");
	}
	
	
	/**
	 * Lajittelee alkiot.
	 */
	public static List<Subject> sortByNameAscending(List<Subject> items) {
		Collections.sort(items);
		return items;
	}

}
