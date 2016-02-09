using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tammi {

	public class BreakthroughMoveResults : IMoveResults {

		public bool Success { get; private set; }
		public bool GameOver { get; private set; }
		public IList<BoardPosition> Path { get; private set; }
		public IList<IPawn> PawnsKilled { get; private set; }
		public Player Winner { get; private set; }

		public bool TurnOver {
			get {
				return true;
			}
		}

		public BreakthroughMoveResults(bool success, IPawn pawnKilled, BoardPosition start, BoardPosition end, bool gameOver, Player winner) {
			Success = success;
			GameOver = gameOver;
			Winner = winner;

			Path = new List<BoardPosition>(2);
			Path.Add(start);
			Path.Add(end);

			PawnsKilled = new List<IPawn>(1);
			if (pawnKilled != null) {
				PawnsKilled.Add(pawnKilled);
			}
		}

	}

}
