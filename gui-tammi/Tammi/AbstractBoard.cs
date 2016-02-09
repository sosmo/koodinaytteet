using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tammi {

	/// <summary>
	/// Contains static methods related to game boards.
	/// </summary>
	class AbstractBoard {

		const int firstCode = (int)'a';


		/// <summary>
		/// Convert a position to chess notation.
		/// </summary>
		/// <param name="position">The BoardPosition to convert.</param>
		/// <param name="boardSize">The size of the board related to the position.</param>
		/// <returns>The chess-style position corresponding to the parameters.</returns>
		public static ChessPosition ToChessPosition(BoardPosition position, int boardSize) {
			int row = boardSize - position.row;
			char col = (char)(firstCode + position.column);
			return new ChessPosition(row, col);
		}

	}

}
