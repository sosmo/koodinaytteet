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
	/// A dialog showing the user different actions after a game.
	/// </summary>
	public partial class GameOverDialog : Window {

		/// <summary>
		/// Get the choice the user made about the next action.
		/// </summary>
		public GameStartOptions SelectedOption { get; private set; }


		public GameOverDialog() {
			InitializeComponent();

			SelectedOption = GameStartOptions.cancel;
		}


		public void Show(string message) {
			ActuallyShow(message);

			base.Show();
		}

		public void ShowDialog(string message) {
			ActuallyShow(message);

			base.ShowDialog();
		}

		void ActuallyShow(string message) {
			_message.Content = message;
		}

		private void _newGame_Click(object sender, RoutedEventArgs e) {
			SelectedOption = GameStartOptions.newGame;
			Close();
		}

		private void _quit_Click(object sender, RoutedEventArgs e) {
			SelectedOption = GameStartOptions.quit;
			Close();
		}

	}
}
