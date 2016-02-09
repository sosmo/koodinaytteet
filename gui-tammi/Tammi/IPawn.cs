using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tammi {

	/// <summary>
	/// An abstract representation of a chess-style board game pawn.
	/// </summary>
	public interface IPawn {

		/// <summary>
		/// Count starts from 0 at the upper left square.
		/// </summary>
		int Row { get; set; }

		/// <summary>
		/// Count starts from 0 at the upper left square.
		/// </summary>
		int Column { get; set; }

		/// <summary>
		/// The player who owns the pawn.
		/// </summary>
		Player Player { get; set; }

		/// <summary>
		/// The type of the pawn.
		/// </summary>
		PawnType PawnType { get; set; }

	}

}
