using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tammi {

	/// <summary>
	/// An abstract template for pawns.
	/// </summary>
	public abstract class APawn : IPawn {

		public virtual Player Player { get; set; }
		public virtual int Row { get; set; }
		public virtual int Column { get; set; }
		public virtual PawnType PawnType { get; set; }

	}

}
