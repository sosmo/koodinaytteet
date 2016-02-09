using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tammi {

	/// <summary>
	/// Contains info related to a move on a board.
	/// </summary>
	public class BoardMove {

		public BoardPosition From { get; private set; }
		public BoardPosition To { get; private set; }
		public Player Player { get; private set; }


		public BoardMove(BoardPosition from, BoardPosition to, Player player) {
			From = from;
			To = to;
			Player = player;
		}

	}

}
