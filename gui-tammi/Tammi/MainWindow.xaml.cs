using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using Microsoft.Win32;
using System.IO;

namespace Tammi {

	/// <summary>
	/// Interaction logic for MainWindow.xaml
	/// </summary>
	public partial class MainWindow : Window {

		BoardGame game;
		// Allowed board sizes.
		List<int> boardSizes;

		// white
		string player1 = "White";
		// black
		string player2 = "Black";

		GameType currentGameType = GameType.checkers;

		Random random = new Random();


		public MainWindow() {
			InitializeComponent();

			DataContext = this;
		}

		/// <summary>
		/// Show a name inquiry dialog and perform necessary initialization before starting. The starting game type is selected based on "currentGameType".
		/// </summary>
		private void Window_Loaded(object sender, RoutedEventArgs e) {
			boardSizes = new List<int>(2);
			boardSizes.Add(8);
			boardSizes.Add(16);

			_board.Size = boardSizes[0];

			StartDialog dialog = new StartDialog();
			dialog.ShowDialog();

			if (dialog.PlayerNames[0].Trim() != "") {
				player1 = dialog.PlayerNames[0];
			}
			if (dialog.PlayerNames[1].Trim() != "") {
				player2 = dialog.PlayerNames[1];
			}

			game = new BoardGame(_board);
			IGameLogic gameLogic;
			switch (currentGameType) {
				case GameType.breakthrough:
					gameLogic = new BreakthroughLogic();
					break;
				default:
					gameLogic = new CheckersLogic();
					break;
			}
			game.Logic = gameLogic;

			game.GameOver += new EventHandler(GameOverHandler);
			game.TurnOver += new EventHandler(TurnChangedHandler);

			SetupGameStyles();

			DrawGameStarter();

			game.Start();
		}


		/// <summary>
		/// Set the starting player by random and update the game status message.
		/// </summary>
		void DrawGameStarter() {
			if (random.NextDouble() < 0.5) {
				game.StartingPlayer = Player.white;
				GameStatus = player1 + " starts!";
			}
			else {
				game.StartingPlayer = Player.black;
				GameStatus = player2 + " starts!";
			}
		}

		/// <summary>
		/// Initialize the game's and board's styles from the named XAML resources.
		/// </summary>
		void SetupGameStyles() {
			game.HighlightedPawnStyle = (Style)FindResource("_selectedPawn");
			game.HighlightedSquareStyle = (Style)FindResource("_highlightedSquare");

			Dictionary<Player, Style> playerStyles = new Dictionary<Player, Style>(2);
			playerStyles.Add(Player.white, (Style)FindResource("_whitePawn"));
			playerStyles.Add(Player.black, (Style)FindResource("_blackPawn"));
			game.PlayerStyles = playerStyles;

			Dictionary<PawnType, Style> pawnStyles = new Dictionary<PawnType, Style>(2);
			pawnStyles.Add(PawnType.normal, (Style)FindResource("_normalPawn"));
			pawnStyles.Add(PawnType.king, (Style)FindResource("_kingPawn"));
			game.PawnStyles = pawnStyles;
		}


		// Game flow handling

		/// <summary>
		/// Update the game status message and show a dialog asking for next actions.
		/// </summary>
		/// <param name="sender">not used</param>
		/// <param name="e">type: GameOverEventArgs</param>
		public void GameOverHandler(Object sender, EventArgs e) {
			GameOverEventArgs args = (GameOverEventArgs)e;
			StringBuilder msg = new StringBuilder(Tammi.Properties.Resources.GameOverMsg + " ");
			Player winner = args.Winner;
			// Set the congratulation message according to the player.
			if (winner == Player.white) {
				msg.Append(player1);
			}
			else {
				msg.Append(player2);
			}
			msg.Append("!");

			GameStatus = msg.ToString();

			// Play a winning sound.
			Uri audio = new Uri(@"Resources\game_over.wav", UriKind.Relative);
			MediaPlayer audioPlayer = new MediaPlayer();
			audioPlayer.Open(audio);
			audioPlayer.Play();

			GameOverDialog dialog = new GameOverDialog();
			dialog.ShowDialog(msg.ToString());

			switch (dialog.SelectedOption) {
				case GameStartOptions.newGame:
					DrawGameStarter();
					game.Start();
					break;
				case GameStartOptions.quit:
					Close();
					break;
				default:
					break;
			}
		}

		/// <summary>
		/// Update the game status message to indicate the player whose turn it is.
		/// </summary>
		/// <param name="sender">not used</param>
		/// <param name="e">type: TurnChangedEventArgs</param>
		public void TurnChangedHandler(Object sender, EventArgs e) {
			TurnOverEventArgs args = (TurnOverEventArgs)e;
			StringBuilder sb = new StringBuilder("Turn: ");
			if (args.Turn == Player.white) {
				sb.Append(player1);
			}
			else {
				sb.Append(player2);
			}
			GameStatus = sb.ToString();
		}


		// UI functions

		/// <summary>
		/// Convert a list of moves into a formatted string.
		/// </summary>
		/// <param name="moves">List of moves.</param>
		/// <param name="format">Desired format per line; {0}, {1} for starting row/col, {2}, {3} for end row/col.</param>
		/// <returns>The coordinates of "moves" in order and formatted according to "format".</returns>
		string FormatMoves(IList<BoardMove> moves, string format) {
			string nl = Environment.NewLine;

			StringBuilder sb = new StringBuilder();

			foreach (BoardMove move in moves) {
				ChessPosition fromPos = AbstractBoard.ToChessPosition(move.From, game.Size);
				ChessPosition toPos = AbstractBoard.ToChessPosition(move.To, game.Size);
				sb.Append(String.Format(format, fromPos.row, fromPos.column, toPos.row, toPos.column));
				sb.Append(nl);
			}

			return sb.ToString();
		}

		/// <summary>
		/// Show a file dialog and write the performed moves into a file line by line.
		/// </summary>
		/// <param name="sender">not used</param>
		/// <param name="e">not used</param>
		private void Save_Click(object sender, RoutedEventArgs e) {
			SaveFileDialog dialog = new SaveFileDialog();

			if (dialog.ShowDialog() != true) {
				return;
			}
			string file = dialog.FileName;

			using (FileStream fs = new FileStream(file, FileMode.OpenOrCreate, FileAccess.Write))
			using (StreamWriter sw = new StreamWriter(fs)) {
				int boardSize = game.Size;
				IList<BoardMove> moves = game.Logic.MovesHistory;

				string nl = Environment.NewLine;

				// Set the header info.
				StringBuilder sb = new StringBuilder();
				String headerFormat = "{0} -vs- {1}";
				sb.Append(String.Format(headerFormat, player1, player2) + nl);
				String boardInfoFormat = "{0} x {0}";
				sb.Append(String.Format(boardInfoFormat, game.Size) + nl);
				sb.Append(nl + "Moves:" + nl);

				String lineFormat = "{0}{1} -> {2}{3}";
				sb.Append(FormatMoves(moves, lineFormat));

				sw.Write(sb.ToString());
			}
		}

		/// <summary>
		/// Show a print dialog and print an image of the board followed by the performed moves line by line.
		/// </summary>
		/// <param name="sender">not used</param>
		/// <param name="e">not used</param>
		private void Print_Click(object sender, RoutedEventArgs e) {
			IList<BoardMove> moves = game.Logic.MovesHistory;

			PrintDialog dialog = new PrintDialog();

			if (dialog.ShowDialog() != true) {
				return;
			}

			string nl = Environment.NewLine;

			FlowDocument document = new FlowDocument();

			StringBuilder sb;
			Paragraph p;

			// Set the header info.
			sb = new StringBuilder();
			String headerFormat = "{0} -vs- {1}";
			sb.Append(String.Format(headerFormat, player1, player2) + nl);
			String boardInfoFormat = "{0} x {0}";
			sb.Append(String.Format(boardInfoFormat, game.Size) + nl);

			p = new Paragraph(new Run(sb.ToString()));
			document.Blocks.Add(p);

			sb.Clear();


			// Put an image of the board on the document.
			Image img = FrameworkElementToImage(_board, 96, 96);

			p = new Paragraph(new InlineUIContainer(img));
			document.Blocks.Add(p);


			sb.Append(nl + "Moves:" + nl);
			String lineFormat = "{0}{1} -> {2}{3}";
			sb.Append(FormatMoves(moves, lineFormat));

			p = new Paragraph(new Run(sb.ToString()));
			document.Blocks.Add(p);

			dialog.PrintDocument(((IDocumentPaginatorSource)document).DocumentPaginator, "Moves");
		}

		private void About_Click(object sender, RoutedEventArgs e) {
			About dialog = new About();
			dialog.ShowDialog();
		}

		private void Help_Click(object sender, RoutedEventArgs e) {
			Uri helpUri = new Uri(@"Resources\help.html", UriKind.Relative);
			System.Diagnostics.Process.Start(System.IO.Path.GetFullPath(helpUri.ToString()));
		}

		private void NewGame_Click(object sender, RoutedEventArgs e) {
			DrawGameStarter();
			game.Start();
		}


		/// <summary>
		/// Show a settings dialog.
		/// </summary>
		/// <param name="sender">not used</param>
		/// <param name="e">not used</param>
		private void Settings_Click(object sender, RoutedEventArgs e) {
			SettingsDialog dialog = new SettingsDialog();

			dialog.Sizes = boardSizes;
			dialog.GameTypes = GetGamesInfo();

			// Set the handlers for dialog options.
			dialog.BoardSizeChanged += SizeChangedHandler;
			dialog.GameTypeChanged += GameTypeChangedHandler;
			dialog.WhitePawnColorChanged += WhitePawnColorChangedHandler;
			dialog.BlackPawnColorChanged += BlackPawnColorChangedHandler;
			dialog.WhiteSquareColorChanged += WhiteSquareColorChangedHandler;
			dialog.BlackSquareColorChanged += BlackSquareColorChangedHandler;

			dialog.SelectedBoardSize = game.Size;
			dialog.SelectedGameType = currentGameType;

			dialog.ShowDialog();
		}

		List<GameTypeInfo> GetGamesInfo() {
			List<GameTypeInfo> infos = new List<GameTypeInfo>();
			infos.Add(new GameTypeInfo(GameType.breakthrough, "Breakthrough"));
			infos.Add(new GameTypeInfo(GameType.checkers, "Checkers"));
			return infos;
		}

		// Handlers related to settings dialog options

		void SizeChangedHandler(Object sender, EventArgs e) {
			var args = (IntegerEventArgs)e;
			game.SetSize(args.Integer);
		}

		void GameTypeChangedHandler(Object sender, EventArgs e) {
			var args = (GameTypeEventArgs)e;
			GameType gameType = args.GameType;

			currentGameType = gameType;

			IGameLogic gameLogic;
			switch (gameType) {
				case GameType.breakthrough:
					gameLogic = new BreakthroughLogic();
					break;
				default:
					gameLogic = new CheckersLogic();
					break;
			}
			game.Logic = gameLogic;
			game.Start();
		}

		void ChangePawnColors(Player player, Color color) {
			// Only solid colors supported.
			Brush brush = new SolidColorBrush(color);
			var styles = game.PlayerStyles;
			Style current = styles[player];
			// Create a new style based on current pawn style augmented with the new Fill.
			Style newStyle = new Style();
			newStyle.Setters.Add(new Setter(Shape.FillProperty, brush));
			newStyle = Styles.CombineStyles(current, newStyle);
			styles[player] = newStyle;
			game.UpdatePawnStyles();
		}

		void WhitePawnColorChangedHandler(Object sender, EventArgs e) {
			var args = (ColorEventArgs)e;
			ChangePawnColors(Player.white, args.Color);
		}

		void BlackPawnColorChangedHandler(Object sender, EventArgs e) {
			var args = (ColorEventArgs)e;
			ChangePawnColors(Player.black, args.Color);
		}

		void WhiteSquareColorChangedHandler(Object sender, EventArgs e) {
			var args = (ColorEventArgs)e;
			Brush brush = new SolidColorBrush(args.Color);
			_board.WhiteFill = brush;
			_board.UpdateSquareStyles();
		}

		void BlackSquareColorChangedHandler(Object sender, EventArgs e) {
			var args = (ColorEventArgs)e;
			Brush brush = new SolidColorBrush(args.Color);
			_board.BlackFill = brush;
			_board.UpdateSquareStyles();
		}


		/// <summary>
		/// Convert an element into an image.
		/// </summary>
		/// <param name="element">Element to convert.</param>
		/// <param name="xDpi">Horizontal DPI of the result.</param>
		/// <param name="yDpi">Vertical DPI of the result.</param>
		/// <returns>An image of the given element.</returns>
		static Image FrameworkElementToImage(FrameworkElement element, int xDpi, int yDpi) {
			RenderTargetBitmap rtb = new RenderTargetBitmap((int)element.ActualWidth, (int)element.ActualHeight, xDpi, yDpi, PixelFormats.Pbgra32);
			rtb.Render(element);

			PngBitmapEncoder png = new PngBitmapEncoder();
			png.Frames.Add(BitmapFrame.Create(rtb));
			MemoryStream stream = new MemoryStream();
			png.Save(stream);

			BitmapImage bmp = new BitmapImage();
			bmp.BeginInit();
			bmp.StreamSource = stream;
			bmp.CacheOption = BitmapCacheOption.OnLoad;
			bmp.EndInit();
			bmp.Freeze();

			Image img = new Image();
			img.Source = bmp;

			return img;
		}


		// WPF properties

		/// <summary>
		/// A status message related to the game's state.
		/// </summary>
		public static readonly DependencyProperty GameStatusProperty = DependencyProperty.Register(
			"GameStatus",
			typeof(string),
			typeof(MainWindow),
			new FrameworkPropertyMetadata(
				"",
				FrameworkPropertyMetadataOptions.AffectsRender));

		public string GameStatus {
			get { return (string)GetValue(GameStatusProperty); }
			set { SetValue(GameStatusProperty, value); }
		}

	}

}
