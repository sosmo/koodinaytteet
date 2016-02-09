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
using System.Windows.Forms;

namespace Tammi {

	/// <summary>
	/// A dialog showing the user different options for a board game.
	/// </summary>
	public partial class SettingsDialog : Window {

		// Event handlers for available GUI interactions.
		public event EventHandler BoardSizeChanged = (s, e) => { };
		public event EventHandler GameTypeChanged = (s, e) => { };
		public event EventHandler WhitePawnColorChanged = (s, e) => { };
		public event EventHandler BlackPawnColorChanged = (s, e) => { };
		public event EventHandler WhiteSquareColorChanged = (s, e) => { };
		public event EventHandler BlackSquareColorChanged = (s, e) => { };

		/// <summary>
		/// Sets the initial selected board size.
		/// </summary>
		int _selectedBoardSize;
		public int SelectedBoardSize {
			get { return _selectedBoardSize; }
			set {
				_selectedBoardSize = value;
				_boardSize.SelectedItem = _selectedBoardSize;
			}
		}

		/// <summary>
		/// Sets the initial selected game type.
		/// </summary>
		GameType _selectedGameType;
		public GameType SelectedGameType {
			get { return _selectedGameType; }
			set {
				_selectedGameType = value;
				GameTypeInfo info = GameTypes.Find((item) => { return item.gameType == _selectedGameType; });
				_gameType.SelectedItem = info;
			}
		}


		public SettingsDialog() {
			InitializeComponent();

			DataContext = this;
		}


		// Handlers attached to GUI elements. These fire their corresponding public events.

		private void GameTypeItem_Selected(object sender, RoutedEventArgs e) {
			var s = (ComboBoxItem)sender;
			var gameTypeInfo = (GameTypeInfo)s.Content;
			var gameType = gameTypeInfo.gameType;
			GameTypeChanged(this, new GameTypeEventArgs(gameType));
		}

		private void BoardSizeItem_Selected(object sender, RoutedEventArgs e) {
			var s = (ComboBoxItem)sender;
			int size = (int)s.Content;
			BoardSizeChanged(sender, new IntegerEventArgs(size));
		}

		/// <summary>
		/// Show a color picker and return the selected color.
		/// </summary>
		/// <returns>The selected color, null if no selection.</returns>
		Color? ShowColorPicker() {
			ColorDialog dialog = new ColorDialog();
			if (dialog.ShowDialog() != System.Windows.Forms.DialogResult.OK) {
				return null;
			}
			System.Drawing.Color temp = dialog.Color;
			return Color.FromArgb(temp.A, temp.R, temp.G, temp.B);
		}

		private void _whitePawnColor_Click(object sender, RoutedEventArgs e) {
			Color? newColor = ShowColorPicker();
			if (newColor == null) {
				return;
			}
			WhitePawnColorChanged(this, new ColorEventArgs(newColor.Value));
		}

		private void _blackPawnColor_Click(object sender, RoutedEventArgs e) {
			Color? newColor = ShowColorPicker();
			if (newColor == null) {
				return;
			}
			BlackPawnColorChanged(this, new ColorEventArgs(newColor.Value));
		}

		private void _whiteSquareColor_Click(object sender, RoutedEventArgs e) {
			Color? newColor = ShowColorPicker();
			if (newColor == null) {
				return;
			}
			WhiteSquareColorChanged(this, new ColorEventArgs(newColor.Value));
		}

		private void _blackSquareColor_Click(object sender, RoutedEventArgs e) {
			Color? newColor = ShowColorPicker();
			if (newColor == null) {
				return;
			}
			BlackSquareColorChanged(this, new ColorEventArgs(newColor.Value));
		}

		private void _close_Click(object sender, RoutedEventArgs e) {
			Close();
		}


		/// <summary>
		/// The list of available sizes for the board. Forms the contents of the size picker.
		/// </summary>
		public static DependencyProperty SizesProperty = DependencyProperty.Register(
			"Sizes",
			typeof(List<int>),
			typeof(SettingsDialog),
			new FrameworkPropertyMetadata(
				new List<int>(),
				FrameworkPropertyMetadataOptions.AffectsRender));

		public List<int> Sizes {
			get { return (List<int>)GetValue(SizesProperty); }
			set {
				SetValue(SizesProperty, value);
				SelectedBoardSize = Sizes[0];
			}
		}

		/// <summary>
		/// The list of available game types. Forms the contents of the game type picker.
		/// </summary>
		public static DependencyProperty GameTypesProperty = DependencyProperty.Register(
			"GameTypes",
			typeof(List<GameTypeInfo>),
			typeof(SettingsDialog),
			new FrameworkPropertyMetadata(
				new List<GameTypeInfo>(),
				FrameworkPropertyMetadataOptions.AffectsRender));

		public List<GameTypeInfo> GameTypes {
			get { return (List<GameTypeInfo>)GetValue(GameTypesProperty); }
			set {
				SetValue(GameTypesProperty, value);
				SelectedGameType = GameTypes[0].gameType;
			}
		}

	}
}
