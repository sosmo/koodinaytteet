using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tammi {

	/// <summary>
	/// Ties information to a board game type.
	/// </summary>
	public class GameTypeInfo {

		public readonly GameType gameType;
		public readonly string gameName;


		public GameTypeInfo(GameType gameType, string gameName) {
			this.gameType = gameType;
			this.gameName = gameName;
		}


		public override string ToString() {
			return gameName;
		}

	}

}
