using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tammi {

	class CheckersMoveResults : IMoveResults {

		public bool Success { get; private set; }
		public bool TurnOver { get; private set; }
		public bool GameOver { get; private set; }
		public IList<BoardPosition> Path { get; private set; }
		public IList<IPawn> PawnsKilled { get; private set; }
		public Player Winner { get; private set; }

		/// <summary>
		/// True, if the moving pawn became a king pawn.
		/// </summary>
		public bool BecameKing { get; private set; }


		public CheckersMoveResults(bool success, bool turnOver, IPawn pawnKilled, BoardPosition start, BoardPosition end, bool gameOver, Player winner, bool becameKing) {
			Success = success;
			TurnOver = turnOver;
			GameOver = gameOver;
			Winner = winner;

			Path = new List<BoardPosition>(2);
			Path.Add(start);
			Path.Add(end);

			PawnsKilled = new List<IPawn>(1);
			if (pawnKilled != null) {
				PawnsKilled.Add(pawnKilled);
			}

			BecameKing = becameKing;
		}

	}

}
