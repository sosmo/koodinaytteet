using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

/// @author Sampo Osmonen
/// <summary>
/// Game of Life konsolissa.
/// </summary>
public class GameOfLife {

	private int[,] populaatio;
	private int jaksonaika = 500;
	// käydäänkö populaation muutokset läpi kerralla vai yksi kerrallaan vasemmasta yläkulmasta alkaen
	private bool vaiheittain = false;

	/// <summary>
	/// Alustaa pelin aloituspopulaation halutulla taulukolla olioita.
	/// </summary>
	/// <param name="populaatio"></param>
	public void alusta(int[,] populaatio) {
		this.populaatio = populaatio;
	}

	/// <summary>
	/// Alustaa pelin aloituspopulaation halutun kokoisella satunnaisella taulukolla olioita ja tyhjiä paikkoja.
	/// </summary>
	/// <param name="y">Taulukon rivimäärä.</param>
	/// <param name="x">Taulukon sarakemäärä.</param>
	public void alusta(int y, int x) {
		this.populaatio = new int[y, x];
		Random random = new Random();
		for (int i = 0; i < this.populaatio.GetLength(0); ++i) {
			for (int j = 0; j < this.populaatio.GetLength(1); ++j) {
				this.populaatio[i, j] = random.Next(0, 2);
			}
		}
	}

	/// <summary>
	/// Alustaa halutun taulukon satunnaisella aloituspopulaatiolla.
	/// </summary>
	/// <param name="y">Taulukon rivimäärä.</param>
	/// <param name="x">Taulukon sarakemäärä.</param>
	public static int[,] alustaTaulukko(int[,] alustettavaPopulaatio) {
		int[,] populaatio = (int[,])alustettavaPopulaatio.Clone();
		Random random = new Random();
		for (int i = 0; i < populaatio.GetLength(0); ++i) {
			for (int j = 0; j < populaatio.GetLength(1); ++j) {
				populaatio[i, j] = random.Next(0, 2);
			}
		}
		return populaatio;
	}

	/// <summary>
	/// Aloittaa pelin ja tulostaa uuden generaation aina jaksonajan välein.
	/// </summary>
	public void pelaa() {
		while (true) {
			tulostaPopulaatio();
			this.populaatio = seuraavaGeneraatio(this.populaatio, this.vaiheittain);
			System.Threading.Thread.Sleep(jaksonaika);
		}
	}

	/// <summary>
	/// Pyyhkii näytön ja tulostaa populaation (elossa olevat oliot +-merkkeinä).
	/// </summary>
	public void tulostaPopulaatio() {
		Console.Clear();
		for (int i = 0; i < this.populaatio.GetLength(0); ++i) {
			for (int j = 0; j < this.populaatio.GetLength(1); ++j) {
				if (this.populaatio[i, j] == 1) {
					Console.Write("+");
				}
				else {
					Console.Write(" ");
				}
			}
			Console.WriteLine();
		}
		Console.WriteLine();
	}

	/// <summary>
	/// Määritä seuraava generaatio annetun generaation perusteella.
	/// </summary>
	/// <param name="nykyGeneraatio">Populaatio, josta seuraava generaatio määritetään.</param>
	/// <returns>Uusi generaatio.</returns>
	/// <example>
	/// <pre name="test">
	///  int[,] populaatio;
	///  int[,] uusiPopulaatio;
	///  populaatio = new int[,] { {1, 0, 1, 1}, {0, 1, 1, 0}, {1, 0, 0, 0}, {1, 0, 0, 1} };
	///  uusiPopulaatio = GameOfLife.seuraavaGeneraatio(populaatio, false);
	///  GameOfLife.taulukkoJonoksi(uusiPopulaatio) === "0 0 1 1 |1 0 1 1 |1 0 1 0 |0 0 0 0 |";
	///  uusiPopulaatio = GameOfLife.seuraavaGeneraatio(uusiPopulaatio, false);
	///  GameOfLife.taulukkoJonoksi(uusiPopulaatio) === "0 1 1 1 |0 0 0 0 |0 0 1 1 |0 0 0 0 |";
	///  uusiPopulaatio = GameOfLife.seuraavaGeneraatio(uusiPopulaatio, false);
	///  GameOfLife.taulukkoJonoksi(uusiPopulaatio) === "0 0 1 0 |0 1 0 0 |0 0 0 0 |0 0 0 0 |";
	///
	///  populaatio = new int[,] { {1, 0, 1, 1}, {0, 1, 1, 0}, {1, 0, 0, 0}, {1, 0, 0, 1} };
	///  uusiPopulaatio = GameOfLife.seuraavaGeneraatio(populaatio, true);
	///  GameOfLife.taulukkoJonoksi(uusiPopulaatio) === "0 1 0 0 |1 0 0 0 |1 1 0 0 |1 1 1 0 |";
	///  uusiPopulaatio = GameOfLife.seuraavaGeneraatio(uusiPopulaatio, true);
	///  GameOfLife.taulukkoJonoksi(uusiPopulaatio) === "0 0 0 0 |1 1 0 0 |0 0 1 0 |0 1 1 0 |";
	///  uusiPopulaatio = GameOfLife.seuraavaGeneraatio(uusiPopulaatio, true);
	///  GameOfLife.taulukkoJonoksi(uusiPopulaatio) === "0 0 0 0 |0 0 0 0 |0 1 1 0 |0 1 1 0 |";
	/// </pre>
	/// </example>
	public static int[,] seuraavaGeneraatio(int[,] nykyGeneraatio, bool vaiheittain) {
		int[,] uusiGeneraatio = (int[,])nykyGeneraatio.Clone();
		int[,] vertailuGeneraatio = nykyGeneraatio;
		if (vaiheittain) {
			vertailuGeneraatio = uusiGeneraatio;
		}
		int[] naapurit;
		for (int i = 0; i < nykyGeneraatio.GetLength(0); ++i) {
			for (int j = 0; j < nykyGeneraatio.GetLength(1); ++j) {
				naapurit = laskeNaapurit(vertailuGeneraatio, i, j, 2, 1);
				if (vertailuGeneraatio[i, j] == 1) {
					if (naapurit[1] < 2 || naapurit[1] > 3) {
						uusiGeneraatio[i, j] = 0;
					}
				}
				else {
					if (naapurit[1] == 3) {
						uusiGeneraatio[i, j] = 1;
					}
				}
			}
		}
		return uusiGeneraatio;
	}

	/// <summary>
	/// Kutsu seuraavaGeneraatio-aliohjelmaa niin, että se tulostaa populaation perusteella seuraavan generaation vaiheittain käsiteltynä.
	/// </summary>
	public void asetaSeuraavaGeneraatio() {
		this.populaatio = seuraavaGeneraatio(this.populaatio, false);
	}

	/// <summary>
	/// Kutsu seuraavaGeneraatio-aliohjelmaa niin, että se tulostaa populaation perusteella seuraavan generaation samanaikaisesti käsiteltynä.
	/// </summary>
	public void asetaSeuraavaGeneraatioVaiheittain() {
		this.populaatio = seuraavaGeneraatio(this.populaatio, true);
	}

	/// <summary>
	/// Laske populaation tietyn paikan ympärillä olevat "naapuri"tilat, ja palauta lista eri tilojen yksikköjen määristä.
	/// </summary>
	/// <param name="populaatio">Koko populaatio, josta tiloja lasketaan.</param>
	/// <param name="i">Kokonaispopulaation rivi-indeksi, josta naapurit lasketaan.</param>
	/// <param name="j">Kokonaispopulaation sarakeindeksi, josta naapurit lasketaan.</param>
	/// <param name="naapureita">Kokonaismäärä kaikista mahdollisista tiloista, joita naapureina voi olla.</param>
	/// <param name="sade">Kuinka monta naapuria yhteen suuntaan alkupisteestä lasketaan, "säde".</param>
	/// <returns>Lista eri naapurien määristä, lajiteltuna naapurien ominaisarvojen mukaan indekseihin.</returns>
	/// <example>
	/// <pre name="test">
	///  int[,] populaatio = new int[,] { {1, 1, 0, 1, 1}, {1, 1, 0, 1, 0}, {0, 1, 1, 0, 0}, {1, 0, 1, 1, 1} };
	///  GameOfLife.laskeNaapurit(populaatio, 2, 2, 2, 1)[0] === 3;
	///  GameOfLife.laskeNaapurit(populaatio, 2, 2, 2, 1)[1] === 5;
	///  GameOfLife.laskeNaapurit(populaatio, 0, 0, 2, 1)[0] === 0;
	///  GameOfLife.laskeNaapurit(populaatio, 0, 0, 2, 1)[1] === 3;
	/// </pre>
	/// </example>
	public static int[] laskeNaapurit(int[,] populaatio, int i, int j, int naapureita, int sade) {
		int alkurivi = i - sade;
		int alkusarake = j - sade;
		int aliruudukonHalkaisija = (2 * sade + 1);
		int aliruudukonLoppurivi = alkurivi + aliruudukonHalkaisija;
		int aliruudukonLoppusarake = alkusarake + aliruudukonHalkaisija;
		// Tähän eri tyyppisten naapurien määrät. Määrät asetellaan listaan järjestyksessä naapurien tyyppien numeroarvon mukaan, eli tyyppien numerointi pitää aloittaa nollasta.
		int[] naapurit = new int[naapureita];
		for (int k = alkurivi; k < aliruudukonLoppurivi; ++k) {
			for (int l = alkusarake; l < aliruudukonLoppusarake; ++l) {
				if (k >= 0 && k < populaatio.GetLength(0) && l >= 0 && l < populaatio.GetLength(1)) {
					naapurit[populaatio[k, l]] += 1;
				}
			}
		}
		// lähtöruutu pois
		--naapurit[populaatio[i, j]];
		return naapurit;
	}

	/// <summary>
	/// Muunna 2-ulotteinen taulukko jonoksi.
	/// </summary>
	/// <param name="matriisi"></param>
	/// <returns></returns>
	/// <example>
	/// <pre name="test">
	///  GameOfLife.taulukkoJonoksi(new int[,] { {0, 0, 1}, {0, 1, 1} }) === "0 0 1 |0 1 1 |";
	/// </pre>
	/// </example>
	public static string taulukkoJonoksi(int[,] matriisi) {
		StringBuilder yhdistetty = new StringBuilder("");
		for (int i = 0; i < matriisi.GetLength(0); ++i) {
			for (int j = 0; j < matriisi.GetLength(1); ++j) {
				yhdistetty.Append(matriisi[i, j] + " ");
			}
			yhdistetty.Append("|");
		}
		return yhdistetty.ToString();
	}

}
