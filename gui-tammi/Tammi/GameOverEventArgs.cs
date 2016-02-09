using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tammi {

	public class GameOverEventArgs : EventArgs {

		public Player Winner { get; set; }

		public GameOverEventArgs(Player winner) {
			Winner = winner;
		}

	}

}
