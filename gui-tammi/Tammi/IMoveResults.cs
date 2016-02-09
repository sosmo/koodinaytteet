using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tammi {

	/// <summary>
	/// General information about the results of a move in a chess-style board game.
	/// </summary>
	public interface IMoveResults {

		/// <summary>True, if the move was successful. If it wasn't the rest of the properties are undefined.</summary>
		bool Success { get; }
		/// <summary>True, if the move ended the current player's turn.</summary>
		bool TurnOver { get; }
		/// <summary>True, if the move endend the game.</summary>
		bool GameOver { get; }
		/// <summary>A list of positions representing the path the pawn moved. Positions are ordered, and the first item is the starting position, last is the end position.</summary>
		IList<BoardPosition> Path { get; }
		/// <summary>A list of pawns that were captured during the move.</summary>
		IList<IPawn> PawnsKilled { get; }
		/// <summary>The winning player, valid only if GameOver is true.</summary>
		Player Winner { get; }

	}

}
