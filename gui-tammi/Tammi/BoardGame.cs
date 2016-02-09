using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Media.Animation;
using System.Windows.Media;

namespace Tammi {

	/// <summary>
	/// A class to handle the basics of a Checkers-style board game. Handles the interaction of a UI game board and pawns with a logic component.
	/// </summary>
	class BoardGame {

		/// <summary>
		/// The player to start the new game.
		/// </summary>
		public Player StartingPlayer { get; set; }
		/// <summary>
		/// Gets the number of columns/rows on the game board.
		/// </summary>
		public int Size { get; private set; }

		// Game element styles. The allowed properties to change are those that are available in the target classes.
		public Style HighlightedPawnStyle { get; set; }
		public Style HighlightedSquareStyle { get; set; }
		public Dictionary<Player, Style> PlayerStyles { get; set; }
		public Dictionary<PawnType, Style> PawnStyles { get; set; }

		/// <summary>
		/// The board for the game.
		/// </summary>
		Board _board = null;
		public Board Board {
			get {
				return _board;
			}
			set {
				_board = value;
				if (Logic != null && Logic.Size != _board.Size) {
					Logic.Size = _board.Size;
				}
				Board.SquareClick += HandleSquareClick;
			}
		}

		/// <summary>
		/// The logic component to play the game. This defines the game type.
		/// </summary>
		IGameLogic _logic = null;
		public IGameLogic Logic {
			get {
				return _logic;
			}
			set {
				_logic = value;
				if (_logic.Size < 1 && Board != null) {
					_logic.Size = Board.Size;
				}
			}
		}

		/// <summary>Fired when the game ends.</summary>
		public event EventHandler GameOver = (s, e) => { };
		/// <summary>Fired a turn ends.</summary>
		public event EventHandler TurnOver = (s, e) => { };

		// Current start and end positions of an unfinished move. If either is not set for the current move it will be null.
		BoardPosition startPos = null;
		BoardPosition targetPos = null;

		List<BoardPosition> highlightedSquares = new List<BoardPosition>();

		// Each player has a dictionary of styles corresponding to different pawn types. In effect, both Player and PawnType are needed to identify the style of a pawn.
		Dictionary<Player, Dictionary<PawnType, Style>> playerPawnStyles;


		/// <summary>
		/// Create a new game with the specified board.
		/// </summary>
		/// <param name="board">Board to set.</param>
		public BoardGame(Board board) {
			Board = board;

			Size = board.Size;
		}

		public BoardGame() { }


		/// <summary>
		/// Start a new game.
		/// </summary>
		/// <exception cref="InvalidOperationException">Thrown if either the board or the logic component is unset, or if their sizes don't match.</exception>
		public void Start() {
			if (Board == null) {
				throw new InvalidOperationException("Board needs to be set before starting.");
			}
			if (Logic == null) {
				throw new InvalidOperationException("Logic needs to be set before starting.");
			}
			if (Logic.Size != Board.Size) {
				throw new InvalidOperationException("The sizes of the board and the logic component don't match.");
			}

			Logic.Turn = StartingPlayer;
			Logic.Initiate();

			SetupPawnStyles();

			SetupPawns();

			UpdatePawnStyles();
		}

		/// <summary>
		/// Set a new board size for the game. Starts a new game with the new board size.
		/// </summary>
		/// <param name="size">The desired amount of squares per board row/column.</param>
		public void SetSize(int size) {
			Size = size;
			Board.Size = size;
			Logic.Size = size;
			Start();
		}

		/// <summary>
		/// Get the pawn layout from Logic and set Board accordingly.
		/// </summary>
		void SetupPawns() {
			int n = Board.Size;
			IPawn[,] squares = Logic.PawnLayout();
			Pawn[,] pawns = new Pawn[n, n];

			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					IPawn logicalPawn = squares[i, j];

					if (logicalPawn == null) {
						continue;
					}

					Pawn p = new Pawn();
					p.Player = logicalPawn.Player;
					p.PawnType = logicalPawn.PawnType;
					p.Style = GetBasicPawnStyle(p);

					p.Click += HandlePawnClick;

					pawns[i, j] = p;
				}
			}

			Board.SetupPawns(pawns);
		}

		/// <summary>
		/// Update the styles of all pawns in case any of the pawn-specific styles has changed.
		/// </summary>
		public void UpdatePawnStyles() {
			SetupPawnStyles();
			foreach (Pawn p in Board.PawnLayout) {
				if (p != null) {
					// Get the new style for the pawn.
					Style style = GetBasicPawnStyle(p);
					p.Style = style;

					// For whatever reason the Fill property doesn't get changed when applying the style. All other properties work fine. Leave this out and the changed pawns will be white.
					foreach (Setter setter in style.Setters) {
						if (setter.Property.Name == "Fill") {
							p.Fill = (Brush)setter.Value;
						}
					}
				}
			}
		}

		/// <summary>
		/// Construct the dictionary where pawn styles are stored in relation to Player and PawnType.
		/// </summary>
		void SetupPawnStyles() {
			playerPawnStyles = new Dictionary<Player, Dictionary<PawnType, Style>>();

			foreach (Player player in PlayerStyles.Keys) {
				Style playerStyle = PlayerStyles[player];
				Dictionary<PawnType, Style> pawnsPerPlayer = new Dictionary<PawnType, Style>();
				playerPawnStyles.Add(player, pawnsPerPlayer);
				foreach (PawnType pawnType in PawnStyles.Keys) {
					Style pawnStyle = PawnStyles[pawnType];
					Style newStyle = Styles.CombineStyles(playerStyle, pawnStyle);
					pawnsPerPlayer.Add(pawnType, newStyle);
				}
			}
		}

		/// <summary>
		/// Get the normal style for a pawn.
		/// </summary>
		/// <param name="pawn">The pawn whose default style is wanted.</param>
		/// <returns>The default style of the pawn.</returns>
		Style GetBasicPawnStyle(Pawn pawn) {
			try {
				return playerPawnStyles[pawn.Player][pawn.PawnType];
			}
			catch (KeyNotFoundException e) {
				throw new KeyNotFoundException("No style supplied for the pawn of Player: " + pawn.Player + ", Type: " + pawn.PawnType + ".", e);
			}
		}

		/// <summary>
		/// Highlight the squares a pawn can move to.
		/// </summary>
		/// <param name="row">The row of the pawn whose moves are wanted. Count starts from 0 at the upper left square.</param>
		/// <param name="column">The column of the pawn whose moves are wanted. Count starts from 0 at the upper left square</param>
		void HighlightPossibleMoves(int row, int column) {
			IList<BoardPosition> moves = Logic.ValidMoves(row, column);
			foreach (BoardPosition move in moves) {
				Board.HighlightSquare(move.row, move.column, HighlightedSquareStyle);

				highlightedSquares.Add(move);
			}
		}

		void RemoveMoveHighlights() {
			foreach (BoardPosition bp in highlightedSquares) {
				Board.ResetSquareStyle(bp.row, bp.column);
			}
			highlightedSquares = new List<BoardPosition>(4);
		}

		/// <summary>
		/// Highlight the pawns the current player in turn can move.
		/// </summary>
		void HighlightMovablePawns() {
			IList<IPawn> pawns = Logic.MovablePawns(Logic.Turn);
			foreach (IPawn p in pawns) {
				Pawn highlight = Board.GetPawn(p.Row, p.Column);
				highlight.Style = Styles.CombineStyles(highlight.Style, HighlightedPawnStyle);
			}
		}

		void RemovePawnHighlights() {
			// Possible optimizations: Use a separate list for pawns in Board, have an instance variable signaling if a full clear is needed.
			foreach (Pawn p in Board.PawnLayout) {
				Style s;
				if (p != null && p.Style != (s = GetBasicPawnStyle(p))) {
					p.Style = s;
				}
			}
		}

		/// <summary>
		/// Using startPos and targetPos, try to move the pawn on the logic component's board. Handle the results and move the pawns on the UI if needed.
		/// </summary>
		void MakeMove() {
			Pawn selectedPawn = Board.GetPawn(startPos.row, startPos.column);

			IMoveResults results = Logic.MovePawn(startPos.row, startPos.column, targetPos.row, targetPos.column);

			if (!results.Success) {
				HighlightPossibleMoves(startPos.row, startPos.column);

				PostMoveCleanup(selectedPawn);
				return;
			}

			if (results.PawnsKilled.Count > 0) {
				foreach (IPawn p in results.PawnsKilled) {
					Board.RemovePawn(p.Row, p.Column);
				}
			}

			Board.MovePawn(startPos.row, startPos.column, targetPos.row, targetPos.column);

			// Checkers special attributes
			if (results is CheckersMoveResults) {
				var checkersResults = (CheckersMoveResults)results;

				if (checkersResults.BecameKing) {
					// Animate the pawn's size change.
					// Not neat, but better than implementing generic animations interface for this one event.

					ThicknessAnimation ta = new ThicknessAnimation();
					ta.From = selectedPawn.Margin;

					selectedPawn.PawnType = PawnType.king;
					// Get the style for the now king-type pawn.
					Style newStyle = GetBasicPawnStyle(selectedPawn);
					// Create a dummy pawn and apply the new style on it, because getting a specific property's value directly from a Style is awkward.
					Pawn dummyPawn = new Pawn();
					dummyPawn.Style = newStyle;
					Thickness endMargin = dummyPawn.Margin;

					ta.To = endMargin;
					ta.Duration = TimeSpan.FromMilliseconds(500);
					Storyboard sb = new Storyboard();
					sb.FillBehavior = FillBehavior.Stop;
					sb.Children.Add(ta);
					Storyboard.SetTarget(ta, selectedPawn);
					Storyboard.SetTargetProperty(ta, new PropertyPath("Margin"));
					sb.Begin();

					selectedPawn.Style = newStyle;
				}
			}

			if (results.GameOver) {
				PostMoveCleanup(selectedPawn);
				GameOver(this, new GameOverEventArgs(results.Winner));
				return;
			}

			if (!results.TurnOver) {
				var moves = results.Path;
				startPos = moves[moves.Count-1];
			}
			else {
				PostMoveCleanup(selectedPawn);
				TurnOver(this, new TurnOverEventArgs(Logic.Turn));
			}
		}

		// Maybe replace with goto
		void PostMoveCleanup(Pawn selectedPawn) {
			selectedPawn.Style = GetBasicPawnStyle(selectedPawn);

			startPos = null;
			targetPos = null;
		}

		void HandlePawnClick(Object sender, RoutedEventArgs e) {
			RemovePawnHighlights();

			RemoveMoveHighlights();

			Pawn pawn = (Pawn)sender;
			int row = Board.GetRow(pawn);
			int col = Board.GetColumn(pawn);

			// Previously selected pawn, if any.
			IPawn active = null;
			// If a starting position has been set, a selected pawn exists.
			if (startPos != null) {
				active = Logic.PawnAt(startPos.row, startPos.column);
			}

			IList<BoardPosition> possibleMoves = Logic.ValidMoves(row, col);

			// TODO: ugly
			// Highlight movable pawns if the pawn can't move or trying to select the wrong color pawn to move.
			if (pawn.Player == Logic.Turn && possibleMoves.Count == 0 || (pawn.Player != Logic.Turn && startPos == null)) {
				startPos = null;
				HighlightMovablePawns();
				return;
			}

			// We're selecting a pawn for the first time or changing the selected pawn.
			if (active == null || active.Player == pawn.Player) {
				startPos = new BoardPosition(row, col);

				pawn.Style = Styles.CombineStyles(pawn.Style, HighlightedPawnStyle);

				if (possibleMoves.Count == 1) {
					targetPos = new BoardPosition(possibleMoves[0].row, possibleMoves[0].column);

					MakeMove();
				}
			}
			else {
				targetPos = new BoardPosition(row, col);

				MakeMove();
			}
		}

		void HandleSquareClick(Object sender, RoutedEventArgs e) {
			Board b = (Board)sender;
			var args = (SquareClickEventArgs)e;
			int row = args.Row;
			int col = args.Column;

			// A pawn to move has yet not been selected.
			if (startPos == null) {
				// Return if there's no pawn in the square.
				if (Logic.PawnAt(row, col) == null) {
					return;
				}

				RemovePawnHighlights();

				RemoveMoveHighlights();

				IList<BoardPosition> possibleMoves = Logic.ValidMoves(row, col);

				if (possibleMoves.Count == 0) {
					HighlightMovablePawns();
					return;
				}

				startPos = new BoardPosition(row, col);

				Pawn p = Board.GetPawn(row, col);
				p.Style = Styles.CombineStyles(p.Style, HighlightedPawnStyle);

				if (possibleMoves.Count == 1) {
					targetPos = new BoardPosition(possibleMoves[0].row, possibleMoves[0].column);

					MakeMove();
				}
			}
			else {
				targetPos = new BoardPosition(row, col);

				MakeMove();
			}
		}

	}

}
