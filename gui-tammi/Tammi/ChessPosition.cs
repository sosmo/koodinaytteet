using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tammi {

	/// <summary>
	/// A board position using the algebraic chess notation.
	/// </summary>
	class ChessPosition {

		public readonly int row;
		public readonly char column;


		/// <param name="row">Count starts at the left bottom square from 1.</param>
		/// <param name="column">Count starts at the left bottom square from a.</param>
		public ChessPosition(int row, char column) {
			this.row = row;
			this.column = column;
		}

	}

}
