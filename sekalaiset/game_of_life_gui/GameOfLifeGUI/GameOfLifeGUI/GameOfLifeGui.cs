using System;
using System.Collections.Generic;
using Jypeli;
using Jypeli.Assets;
using Jypeli.Controls;
using Jypeli.Effects;
using Jypeli.Widgets;

public class GameOfLifeGui : PhysicsGame {
	private const int NY = 60;
	private int[,] sukupolvi;
	private int[,] seuraavaSukupolvi;
	private GameObject[,] oliot;
	private Timer timer = new Timer();
	private Color[] varit = { Color.Black, Color.White };

	public override void Begin() {
		Level.Background.Color = Color.Black;

		double dy = Screen.Height / NY; // Lasketaan ruudun korkeus pikseleinä
		int nx = (int)(Screen.Width / dy);  // ja montako mahtuu X-suuntaan
		int ny = NY;

		sukupolvi = new int[ny, nx];     //  Luodaan taulukot
		seuraavaSukupolvi = new int[ny, nx];
		oliot = new GameObject[ny, nx];
		seuraavaSukupolvi = sukupolvi; // jos tämä on mukana, käyttäytyy eri tavalla

		LuoOliot(this, oliot);

		Camera.ZoomToAllObjects();

		timer.Interval = 0.1; // timeri antamaan tapahtuma 0.1 sek välein

		timer.Timeout += LaskeJaPiirraSeuraavaSukupolvi;
		ArvoSukupolvi();  // jos halutaan käynnistää automaattisesti
	}

	private void ArvoSukupolvi() {
		this.sukupolvi = GameOfLife.alustaTaulukko(this.sukupolvi);
		this.seuraavaSukupolvi = sukupolvi;
		timer.Start();
	}

	private static void LuoOliot(PhysicsGame game, GameObject[,] oliot) {
		double koko = Screen.Height / oliot.GetLength(0);
		double x = 0.0;
		double y = 0.0;
		GameObject olio;
		for (int i = 0; i < oliot.GetLength(0); ++i) {
			for (int j = 0; j < oliot.GetLength(1); ++j) {
				olio = new GameObject(koko, koko, Shape.Rectangle);
				oliot[i, j] = olio;
				olio.X = x;
				olio.Y = y;
				x += koko;
				game.Add(olio);
			}
			x = 0.0;
			y -= koko;
		}
	}

	private void LaskeJaPiirraSeuraavaSukupolvi() {
		this.seuraavaSukupolvi = GameOfLife.seuraavaGeneraatio(this.seuraavaSukupolvi, false);
		for (int i = 0; i < this.seuraavaSukupolvi.GetLength(0); ++i) {
			for (int j = 0; j < this.seuraavaSukupolvi.GetLength(1); ++j) {
				this.oliot[i, j].Color = this.varit[this.seuraavaSukupolvi[i, j]];
			}
		}
	}
}
