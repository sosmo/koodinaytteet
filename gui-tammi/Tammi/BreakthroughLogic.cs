using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tammi {

	public class BreakthroughLogic : IGameLogic {

		// The core functionality of the class.
		GameLogicCore<BreakthroughPawn> core;

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


		public BreakthroughLogic(int size)
			: this() {
			Initiate();
		}

		public BreakthroughLogic() {
			core = new GameLogicCore<BreakthroughPawn>();

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
		}

		public IMoveResults MovePawn(int rowFrom, int columnFrom, int rowTo, int columnTo) {
			BreakthroughPawn pawn = core.PawnAt(rowFrom, columnFrom);
			BreakthroughPawn target = core.PawnAt(rowTo, columnTo);
			BreakthroughPawn killed = null;
			BoardPosition from = new BoardPosition(rowFrom, columnFrom);
			BoardPosition to = new BoardPosition(rowTo, columnTo);
			bool gameOver = false;
			Player winner = pawn.Player;

			if (!ValidateMove(rowFrom, columnFrom, rowTo, columnTo)) {
				return new BreakthroughMoveResults(false, killed, from, to, gameOver, winner);
			}

			if (target != null) {
				core.RemovePawn(target.Row, target.Column);
				killed = target;
			}

			// If the pawn reaches the end of the board the game is over.
			if (rowTo == 0 || rowTo == core.Size-1) {
				gameOver = true;
			}

			core.MovePawn(rowFrom, columnFrom, rowTo, columnTo);

			// Swap the turn.
			if (core.Turn == Player.white) {
				core.Turn = Player.black;
			}
			else {
				core.Turn = Player.white;
			}

			return new BreakthroughMoveResults(true, killed, from, to, gameOver, winner);
		}

		public bool ValidateMove(int rowFrom, int columnFrom, int rowTo, int columnTo) {
			// Stop immediately if the target position is not on the board.
			if (rowTo < 0 || columnTo < 0 || rowTo >= core.Size || columnTo >= core.Size) {
				return false;
			}

			BreakthroughPawn pawn = core.PawnAt(rowFrom, columnFrom);
			BreakthroughPawn target = core.PawnAt(rowTo, columnTo);

			// Illegal moves (in addition to those over board):
			// no pawn on the square
			if ((pawn == null)
					// it's not the player's turn
					|| (pawn.Player != core.Turn)
					// moving over one square horizontally
					|| (Math.Abs(columnTo - columnFrom) > 1)
					// white moving over one square upwards
					|| (pawn.Player == Player.white && !(rowTo - rowFrom == -1))
					// black moving over one square downwards
					|| (pawn.Player == Player.black && !(rowTo - rowFrom == 1))
					// trying to cap a pawn that's directly in front
					|| (target != null && columnFrom - columnTo == 0)
					// trying to move on a friendly pawn
					|| (target != null && target.Player == pawn.Player)) {
				return false;
			}
			return true;
		}

		public IList<BoardPosition> ValidMoves(int rowFrom, int columnFrom) {
			IPawn pawn = core.PawnAt(rowFrom, columnFrom);
			int rowTo = rowFrom;
			if (pawn.Player == Player.white) {
				rowTo -= 1;
			}
			else {
				rowTo += 1;
			}
			List<BoardPosition> moves = new List<BoardPosition>(3);

			for (int i = columnFrom-1; i <= columnFrom+1; i++) {
				if (ValidateMove(rowFrom, columnFrom, rowTo, i)) {
					moves.Add(new BoardPosition(rowTo, i));
				}
			}

			return moves;
		}

		public IList<IPawn> MovablePawns(Player player) {
			List<IPawn> movable = new List<IPawn>();

			foreach (BreakthroughPawn p in core.Pawns[player]) {
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
			for (int i = 0; i < core.Size; i++) {
				for (int j = 0; j < core.Size; j++) {
					if (i > 1 && i < core.Size - 2) {
						continue;
					}
					BreakthroughPawn pawn = new BreakthroughPawn();
					if (i < 2) {
						pawn.Player = Player.black;
					}
					else {
						pawn.Player = Player.white;
					}

					core.AddPawn(pawn, i, j);
				}
			}
		}

	}

}
