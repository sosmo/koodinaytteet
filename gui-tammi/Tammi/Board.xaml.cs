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
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace Tammi {

	/// <summary>
	/// A standard chess-style board game board. The board's coordinates are zero-based and start at the top left square.
	/// </summary>
	public partial class Board : UserControl {

		Pawn[,] pawns;
		Shape[,] squares;

		/// <summary>
		/// Get an array representing the locations of pawns.
		/// </summary>
		public Pawn[,] PawnLayout {
			get {
				int n = pawns.Length;
				Pawn[,] copy = new Pawn[n, n];
				Array.Copy(pawns, copy, n);
				return copy;
			}
		}


		public Board() {
			InitializeComponent();

			DataContext = this;

			Size = 2;
			InvalidateSize();
		}

		private void UserControl_Loaded(object sender, RoutedEventArgs e) {
			InvalidateLayout();
		}


		/// <summary>
		/// Set the board's pawn layout directly from an array representing the locations of pawns.
		/// </summary>
		/// <param name="pawns">An array representing the locations of pawns, nulls where there is no pawn.</param>
		public void SetupPawns(Pawn[,] pawns) {
			int n = Size;
			if (!(pawns.GetLength(0) == n && pawns.GetLength(1) == n)) {
				throw new ArrayTypeMismatchException("The array containing the pawns must be [n,n] in size, where n is the board's current length/height.");
			}
			this.pawns = pawns;

			InvalidateLayout();
		}

		/// <summary>
		/// Update the styles of all the squares in case something has been changed.
		/// </summary>
		public void UpdateSquareStyles() {
			Brush[] squareFills = { WhiteFill, BlackFill };

			for (int i = 0; i < Size; i++) {
				for (int j = 0; j < Size; j++) {
					Shape square = squares[i, j];
					// The square must be manually given a new style.
					square.Style = new Style();

					square.Fill = squareFills[(i+j) % 2];
				}
			}
		}

		// Not really needed.
		public void AddPawn(Pawn pawn, int row, int column) {
			throw new NotImplementedException();
		}

		/// <summary>
		/// Remove the pawn at the specified row and column.
		/// </summary>
		/// <param name="row">Count starts from 0 at the upper left square.</param>
		/// <param name="column">Count starts from 0 at the upper left square.</param>
		public void RemovePawn(int row, int column) {
			Pawn pawn = pawns[row, column];
			pawns[row, column] = null;

			var grid = (Grid)FindName("_grid");
			grid.Children.Remove(pawn);
		}

		/// <summary>
		/// Get the pawn at the specified row and column.
		/// </summary>
		/// <param name="row">Count starts from 0 at the upper left square.</param>
		/// <param name="column">Count starts from 0 at the upper left square.</param>
		/// <returns>The pawn at the coordinates, or null.</returns>
		public Pawn GetPawn(int row, int column) {
			return pawns[row, column];
		}

		/// <summary>
		/// Move a pawn from a square to another.
		/// </summary>
		/// <param name="rowFrom">Count starts from 0 at the upper left square.</param>
		/// <param name="columnFrom">Count starts from 0 at the upper left square.</param>
		/// <param name="rowTo">Count starts from 0 at the upper left square.</param>
		/// <param name="columnTo">Count starts from 0 at the upper left square.</param>
		public void MovePawn(int rowFrom, int columnFrom, int rowTo, int columnTo) {
			Pawn pawn = pawns[rowFrom, columnFrom];
			pawns[rowFrom, columnFrom] = null;
			pawns[rowTo, columnTo] = pawn;

			Grid.SetRow(pawn, rowTo);
			Grid.SetColumn(pawn, columnTo);

			Board.SetRow(pawn, rowTo);
			Board.SetColumn(pawn, columnTo);
		}

		// Not going to be implemented yet.
		public void MovePawn(Pawn pawn, int rowTo, int columnTo) {
			throw new NotImplementedException();
			//MovePawn(Board.GetRow(pawn), Board.GetColumn(pawn), rowTo, columnTo);
		}

		/// <summary>
		/// Highlight a square with the specified style.
		/// </summary>
		/// <param name="row">Count starts from 0 at the upper left square.</param>
		/// <param name="column">Count starts from 0 at the upper left square.</param>
		/// <param name="style">Style whose setters are applied on the square.</param>
		public void HighlightSquare(int row, int column, Style style) {
			if (style == null) {
				return;
			}
			Shape square = squares[row, column];
			square.Style = Styles.CombineStyles(square.Style, style);
		}

		/// <summary>
		/// Set a square to its basic style.
		/// </summary>
		/// <param name="row">Count starts from 0 at the upper left square.</param>
		/// <param name="column">Count starts from 0 at the upper left square.</param>
		public void ResetSquareStyle(int row, int column) {
			Shape square = squares[row, column];
			Brush[] squareFills = { WhiteFill, BlackFill };

			// The square must be manually given a new style.
			square.Style = new Style();

			square.Fill = squareFills[(row+column) % 2];
		}

		/// <summary>
		/// Update the backing arrays when size changes.
		/// </summary>
		void InvalidateSize() {
			pawns = new Pawn[Size, Size];
			squares = new Shape[Size, Size];
		}

		/// <summary>
		/// Update the visual layout (size, square styles, pawns) when size changes.
		/// </summary>
		void InvalidateLayout() {
			var grid = (Grid)FindName("_grid");

			int n = Size;

			grid.Children.Clear();
			grid.RowDefinitions.Clear();
			grid.ColumnDefinitions.Clear();

			for (int i = 0; i < n; i++) {
				RowDefinition rowDef = new RowDefinition();
				grid.RowDefinitions.Add(rowDef);
				ColumnDefinition colDef = new ColumnDefinition();
				grid.ColumnDefinitions.Add(colDef);
			}

			Brush[] squareFills = { WhiteFill, BlackFill };

			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					Rectangle square = new Rectangle();
					// The square must be manually given a new style.
					square.Style = new Style();

					square.Fill = squareFills[(i+j) % 2];

					Panel.SetZIndex(square, 0);

					Grid.SetRow(square, i);
					Grid.SetColumn(square, j);
					grid.Children.Add(square);

					Board.SetRow(square, i);
					Board.SetColumn(square, j);

					squares[i, j] = square;

					square.PreviewMouseUp += RaiseSquareClickEvent;

					Pawn pawn = pawns[i, j];
					if (pawn == null) {
						continue;
					}

					Panel.SetZIndex(pawn, 1);

					Grid.SetRow(pawn, i);
					Grid.SetColumn(pawn, j);
					grid.Children.Add(pawn);

					Board.SetRow(pawn, i);
					Board.SetColumn(pawn, j);
				}
			}
		}


		/// <summary>
		/// Number of rows/columns on the board.
		/// </summary>
		/// <value>Min 1.</value>
		public static DependencyProperty SizeProperty = DependencyProperty.Register(
			"Size",
			typeof(int),
			typeof(Board),
			new FrameworkPropertyMetadata(
				2,
				FrameworkPropertyMetadataOptions.AffectsRender,
				OnSizeChanged));

		public int Size {
			get { return (int)GetValue(SizeProperty); }
			set { SetValue(SizeProperty, value); }
		}

		private static void OnSizeChanged(DependencyObject sender, DependencyPropertyChangedEventArgs e) {
			var board = (Board)sender;
			board.InvalidateSize();
			board.InvalidateLayout();
		}

		/// <summary>
		/// Fill for white squares.
		/// </summary>
		public static DependencyProperty WhiteFillProperty = DependencyProperty.Register(
			"WhiteFill",
			typeof(Brush),
			typeof(Board),
			new FrameworkPropertyMetadata(
				new SolidColorBrush(Colors.White),
				FrameworkPropertyMetadataOptions.AffectsRender,
				OnSquareStyleChanged));

		public Brush WhiteFill {
			get { return (Brush)GetValue(WhiteFillProperty); }
			set { SetValue(WhiteFillProperty, value); }
		}

		/// <summary>
		/// Fill for black squares.
		/// </summary>
		public static DependencyProperty BlackFillProperty = DependencyProperty.Register(
			"BlackFill",
			typeof(Brush),
			typeof(Board),
			new FrameworkPropertyMetadata(
				new SolidColorBrush(Colors.Black),
				FrameworkPropertyMetadataOptions.AffectsRender,
				OnSquareStyleChanged));

		public Brush BlackFill {
			get { return (Brush)GetValue(BlackFillProperty); }
			set { SetValue(BlackFillProperty, value); }
		}

		private static void OnSquareStyleChanged(DependencyObject sender, DependencyPropertyChangedEventArgs e) {
			var board = (Board)sender;
			board.InvalidateLayout();
		}


		/// <summary>
		/// Gets executed, when a square is clicked.
		/// </summary>
		public static readonly RoutedEvent SquareClickEvent = EventManager.RegisterRoutedEvent(
			"SquareClick",
			RoutingStrategy.Bubble,
			typeof(SquareClickEventArgs),
			typeof(Board));

		public event RoutedEventHandler SquareClick {
			add { AddHandler(SquareClickEvent, value); }
			remove { RemoveHandler(SquareClickEvent, value); }
		}

		void RaiseSquareClickEvent(Object sender, RoutedEventArgs e) {
			var square = (Shape)sender;
			int row = Board.GetRow(square);
			int column = Board.GetColumn(square);
			SquareClickEventArgs args = new SquareClickEventArgs(SquareClickEvent, sender, row, column);
			RaiseEvent(args);
		}



		/// <summary>
		/// Attach a row on a board to an element.
		/// </summary>
		/// <value>Min 0, max board size.</value>
		public static readonly DependencyProperty RowProperty =
			DependencyProperty.RegisterAttached(
				"Row",
				typeof(int),
				typeof(Board),
				new UIPropertyMetadata(0));

		public static int GetRow(DependencyObject obj) {
			return (int)obj.GetValue(RowProperty);
		}

		public static void SetRow(DependencyObject obj, int value) {
			obj.SetValue(RowProperty, value);
		}

		/// <summary>
		/// Attach a column on a board to an element.
		/// </summary>
		/// <value>Min 0, max board size.</value>
		public static readonly DependencyProperty ColumnProperty =
			DependencyProperty.RegisterAttached(
				"Column",
				typeof(int),
				typeof(Board),
				new UIPropertyMetadata(0));

		public static int GetColumn(DependencyObject obj) {
			return (int)obj.GetValue(ColumnProperty);
		}

		public static void SetColumn(DependencyObject obj, int value) {
			obj.SetValue(ColumnProperty, value);
		}

	}



	/// <summary>
	/// Custom RoutedEventArgs containing the row and column of a game board's square that was clicked.
	/// </summary>
	public class SquareClickEventArgs : RoutedEventArgs {

		public int Row { get; set; }
		public int Column { get; set; }

		public SquareClickEventArgs(RoutedEvent routedEvent, Object source, int row, int column)
			: base(routedEvent, source) {
			Row = row;
			Column = column;
		}

	}

}
