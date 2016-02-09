using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Media;

namespace Tammi {

	/// <summary>
	/// Static methods related to Styles.
	/// </summary>
	class Styles {

		/// <summary>
		/// Get a new style by combining two existing ones.
		/// </summary>
		/// <param name="baseStyle">The style whose setters get overridden if necessary.</param>
		/// <param name="additions">The style whose setters override baseStyle's.</param>
		/// <returns>The combination of the given styles.</returns>
		public static Style CombineStyles(Style baseStyle, Style additions) {
			Style newStyle = new Style(baseStyle.TargetType);

			foreach (Setter setter in baseStyle.Setters) {
				// Avoid double setters for the same property.
				if (additions.Setters.Contains(setter, new SetterComparer())) {
					continue;
				}
				newStyle.Setters.Add(setter);
			}
			// TODO: should probably check double triggers too
			foreach (Trigger trigger in baseStyle.Triggers) {
				newStyle.Triggers.Add(trigger);
			}

			if (baseStyle.BasedOn != null) {
				newStyle = CombineStyles(newStyle, baseStyle.BasedOn);
			}

			if (additions == null) {
				return newStyle;
			}

			foreach (Setter setter in additions.Setters) {
				newStyle.Setters.Add(setter);
			}
			foreach (Trigger trigger in additions.Triggers) {
				newStyle.Triggers.Add(trigger);
			}

			return newStyle;
		}

	}

	class SetterComparer : IEqualityComparer<SetterBase> {

		public bool Equals(SetterBase x, SetterBase y) {
			// TODO: seems to work, but not nice
			Setter a = (Setter)x;
			Setter b = (Setter)y;
			return a.Property.Name == b.Property.Name;
		}

		public int GetHashCode(SetterBase obj) {
			throw new NotImplementedException();
		}

	}

}
