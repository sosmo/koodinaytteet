using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace Tammi {
	/// <summary>
	/// Interaction logic for StartDialog.xaml
	/// </summary>
	public partial class StartDialog : Window {

		/// <summary>
		/// Get the list of player names given in the dialog. 2 players. Empty strings if no input.
		/// </summary>
		List<string> _playerNames;
		public List<string> PlayerNames {
			get { return _playerNames; }
			private set { _playerNames = value; }
		}


		public StartDialog() {
			InitializeComponent();

			DataContext = this;

			PlayerNames = new List<string>(2);
			PlayerNames.Add("");
			PlayerNames.Add("");
		}


		private void Button_Click(object sender, RoutedEventArgs e) {
			PlayerNames[0] = _player1.Text;
			PlayerNames[1] = _player2.Text;
			Close();
		}

	}
}
