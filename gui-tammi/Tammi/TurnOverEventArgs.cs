using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tammi {

	public class TurnOverEventArgs : EventArgs {

		public Player Turn { get; set; }

		public TurnOverEventArgs(Player turn) {
			Turn = turn;
		}

	}

}
