using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tammi {

	/// <summary>
	/// Players of a board game.
	/// </summary>
	public enum Player { white, black };

	/// <summary>
	/// Types a board game pawn may have.
	/// </summary>
	public enum PawnType { normal, king };

	/// <summary>
	/// Actions an user can perform when starting a new game.
	/// </summary>
	public enum GameStartOptions { quit, newGame, cancel };

	/// <summary>
	/// Different board games.
	/// </summary>
	public enum GameType { checkers, breakthrough };

}
