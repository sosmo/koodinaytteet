using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Media;

namespace Tammi {

	public class ColorEventArgs : EventArgs {

		public Color Color { get; private set; }


		public ColorEventArgs(Color color) {
			Color = color;
		}

	}


	public class IntegerEventArgs : EventArgs {

		public int Integer { get; private set; }


		public IntegerEventArgs(int integer) {
			Integer = integer;
		}

	}


	public class GameTypeEventArgs : EventArgs {

		public GameType GameType { get; private set; }


		public GameTypeEventArgs(GameType type) {
			GameType = type;
		}

	}

}
