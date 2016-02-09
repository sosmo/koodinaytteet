using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tammi {

	public class CheckersLogic : IGameLogic {

		// The core functionality of the class.
		GameLogicCore<CheckersPawn> core;

		// When a multi-round move is in effect, this is set to the pawn that originally initiated it.
		CheckersPawn unfinishedPawn = null;

		public int Size {
			get { return core.Size; }
			set { core.Size = value; }
		}

		public Player Turn {
			get { return core.Turn; }
			set { core.Turn = value; }
		}

		public IList<BoardMove> MovesHistory {
			get { return core.MovesHistory; }
		}


		public CheckersLogic(int size)
			: this() {
			Initiate();
		}

		public CheckersLogic() {
			core = new GameLogicCore<CheckersPawn>();

			List<Player> players = new List<Player>(2);
			players.Add(Player.white);
			players.Add(Player.black);
			core.Players = players;
		}


		public void Initiate() {
			if (core.Size < 1) {
				throw new InvalidOperationException("Size needs to be set before initiating the game.");
			}

			core.Initiate();

			SetupBoard();

			unfinishedPawn = null;
		}

		public IMoveResults MovePawn(int rowFrom, int columnFrom, int rowTo, int columnTo) {
			CheckersPawn pawn = core.PawnAt(rowFrom, columnFrom);
			CheckersPawn target = core.PawnAt(rowTo, columnTo);

			CheckersPawn killed = GetPawnInBetween(rowFrom, columnFrom, rowTo, columnTo);

			BoardPosition from = new BoardPosition(rowFrom, columnFrom);
			BoardPosition to = new BoardPosition(rowTo, columnTo);
			bool gameOver = false;
			bool turnOver = true;
			Player winner = pawn.Player;
			bool becameKing = false;

			if (!ValidateMove(rowFrom, columnFrom, rowTo, columnTo)) {
				return new CheckersMoveResults(false, turnOver, killed, from, to, gameOver, winner, becameKing);
			}

			core.MovePawn(rowFrom, columnFrom, rowTo, columnTo);

			// If the pawn reaches the end of the board it becomes a king pawn.
			if ((pawn.Player == Player.white && rowTo == 0) || (pawn.Player == Player.black && rowTo == core.Size-1)) {
				pawn.PawnType = PawnType.king;
				becameKing = true;
			}

			if (killed != null) {
				core.RemovePawn(killed.Row, killed.Column);

				// Don't end the player's turn if it's possible for the current pawn capture more.
				if (!becameKing && CanPawnCap(rowTo, columnTo)) {
					turnOver = false;
				}

				// Mark the game as finished if the last enemy pawn was captured.
				if (core.Pawns[killed.Player].Count < 1) {
					gameOver = true;
				}
			}

			if (turnOver) {
				unfinishedPawn = null;

				// Swap the turn.
				if (core.Turn == Player.white) {
					core.Turn = Player.black;
				}
				else {
					core.Turn = Player.white;
				}

				// If the opponent can't move when his turn comes the game is over.
				if (MovablePawns(core.Turn).Count < 1) {
					winner = pawn.Player;
					gameOver = true;
				}
			}
			else {
				unfinishedPawn = pawn;
			}

			return new CheckersMoveResults(true, turnOver, killed, from, to, gameOver, winner, becameKing);
		}

		/// <summary>
		/// True, if the pawn at the coordinates can capture something.
		/// </summary>
		bool CanPawnCap(int row, int col) {
			CheckersPawn pawn = core.PawnAt(row, col);
			for (int i = -2; i <= 2; i += 4) {
				for (int j = -2; j <= 2; j += 4) {
					if (ValidateMove(row, col, row+i, col+j, false)) {
						return true;
					}
				}
			}
			return false;
		}

		/// <summary>
		/// True, if some of the player's pawns can capture something.
		/// </summary>
		bool CanPlayerCap(Player player) {
			foreach (CheckersPawn p in core.Pawns[player]) {
				if (CanPawnCap(p.Row, p.Column)) {
					return true;
				}
			}
			return false;
		}

		/// <summary>
		/// Gets the pawn that's one row and column from the start position and on the diagonal line from start to end position. Null for no pawn or erroneous arguments.
		/// </summary>
		CheckersPawn GetPawnInBetween(int rowFrom, int columnFrom, int rowTo, int columnTo) {
			int dx = rowTo - rowFrom;
			int dy = columnTo - columnFrom;
			int dxLen = Math.Abs(dx);
			int dyLen = Math.Abs(dy);
			CheckersPawn between = null;
			if (dxLen == dyLen && dxLen > 1) {
				int overRow = rowFrom + Math.Sign(dx);
				int overCol = columnFrom + Math.Sign(dy);
				if (overRow >= 0 && overRow < core.Size && overCol >= 0 && overCol < core.Size) {
					between = core.PawnAt(overRow, overCol);
				}
			}
			return between;
		}

		/// <summary>
		/// Mostly the same as ValidateMove of IGameLogic
		/// </summary>
		/// <param name="checkCaptureChance">Also check that the move captures a pawn if the player can capture any pawns.</param>
		bool ValidateMove(int rowFrom, int columnFrom, int rowTo, int columnTo, bool checkCaptureChance) {
			// Stop immediately if the target position is not on the board.
			if (rowTo < 0 || columnTo < 0 || rowTo >= core.Size || columnTo >= core.Size) {
				return false;
			}

			CheckersPawn pawn = core.PawnAt(rowFrom, columnFrom);
			CheckersPawn target = core.PawnAt(rowTo, columnTo);

			// Get the pawn we're capturing (if any).
			int dx = rowTo - rowFrom;
			int dy = columnTo - columnFrom;
			int dxLen = Math.Abs(dx);
			int dyLen = Math.Abs(dy);
			CheckersPawn over = GetPawnInBetween(rowFrom, columnFrom, rowTo, columnTo);

			// Illegal moves (in addition to those over board):
			// no pawn on the square
			if ((pawn == null)
					// it's not the player's turn
					|| (pawn.Player != core.Turn)
					// trying to move on an occupied square
					|| (target != null)
					// not moving diagonally
					|| (dxLen != dyLen)
					// trying to move over 2 rows/cols
					|| (dxLen > 2)
					// any non-king pawn moving in the "wrong" direction
					|| (pawn.PawnType != PawnType.king
							&& ((pawn.Player == Player.white && rowTo - rowFrom > -1)
									|| (pawn.Player == Player.black && rowTo - rowFrom < 1)))
					// trying to move two diagonal rows without capturing
					|| (over == null && dxLen > 1)
					// not capturing when there's a pawn that has the option
					|| (over == null && checkCaptureChance && CanPlayerCap(pawn.Player))
					// trying to cap a friendly pawn
					|| (over != null && over.Player == pawn.Player)
					// trying to move another pawn when there's one on a capturing streak
					|| (unfinishedPawn != null && pawn != unfinishedPawn)) {
				return false;
			}
			return true;
		}

		public bool ValidateMove(int rowFrom, int columnFrom, int rowTo, int columnTo) {
			return ValidateMove(rowFrom, columnFrom, rowTo, columnTo, true);
		}

		public IList<BoardPosition> ValidMoves(int rowFrom, int columnFrom) {
			List<BoardPosition> moves = new List<BoardPosition>(3);

			for (int i = rowFrom-2; i <= rowFrom+2; i++) {
				for (int j = columnFrom-2; j <= columnFrom+2; j++) {
					if (ValidateMove(rowFrom, columnFrom, i, j)) {
						moves.Add(new BoardPosition(i, j));
					}
				}
			}

			return moves;
		}

		public IList<IPawn> MovablePawns(Player player) {
			List<IPawn> movable = new List<IPawn>();

			foreach (CheckersPawn p in core.Pawns[player]) {
				if (ValidMoves(p.Row, p.Column).Count > 0) {
					movable.Add(p);
				}
			}

			return movable;
		}

		public int PawnCount(Player player) {
			return core.PawnCount(player);
		}

		public IPawn[,] PawnLayout() {
			return core.PawnLayout();
		}

		public IPawn PawnAt(int row, int column) {
			return core.PawnAt(row, column);
		}

		/// <summary>
		/// Set the pawns to their start positions on the core's internal board.
		/// </summary>
		void SetupBoard() {
			int midEnd = core.Size / 2;
			int midStart = midEnd - 1;
			for (int i = 0; i < core.Size; i++) {
				if (i >= midStart && i <= midEnd) {
					continue;
				}
				for (int j = 0; j < core.Size; j += 2) {
					int col = j;
					if (i % 2 == 0) {
						col++;
					}

					CheckersPawn pawn = new CheckersPawn();
					if (i > midEnd) {
						pawn.Player = Player.white;
					}
					else {
						pawn.Player = Player.black;
					}

					core.AddPawn(pawn, i, col);
				}
			}
		}

	}

}
