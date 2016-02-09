using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tammi {

	/// <summary>
	/// Contains coordinates on a game board. Row and column coordinates start from 0 at the upper left square.
	/// </summary>
	public class BoardPosition {

		public readonly int row;
		public readonly int column;


		/// <param name="row">Count starts from 0 at the upper left square.</param>
		/// <param name="column">Count starts from 0 at the upper left square.</param>
		public BoardPosition(int row, int column) {
			this.row = row;
			this.column = column;
		}

	}

}
