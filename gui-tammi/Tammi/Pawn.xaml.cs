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
	/// Interaction logic for Pawn.xaml
	/// </summary>
	public partial class Pawn : UserControl {

		public Pawn() {
			InitializeComponent();
		}

		private void _shape_Loaded(object sender, RoutedEventArgs e) {
			Shape shape = (Shape)FindName("_shape");
			shape.PreviewMouseUp += RaiseClickEvent;
		}


		/// <summary>
		/// The owner of the pawn.
		/// </summary>
		public static readonly DependencyProperty PlayerProperty = DependencyProperty.Register(
			"Player",
			typeof(Player),
			typeof(Pawn),
			new FrameworkPropertyMetadata(
				Player.white,
				FrameworkPropertyMetadataOptions.AffectsRender));

		public Player Player {
			get { return (Player)GetValue(PlayerProperty); }
			set { SetValue(PlayerProperty, value); }
		}

		/// <summary>
		/// The type of the pawn.
		/// </summary>
		public static readonly DependencyProperty PawnTypeProperty = DependencyProperty.Register(
			"PawnType",
			typeof(PawnType),
			typeof(Pawn),
			new FrameworkPropertyMetadata(
				PawnType.normal,
				FrameworkPropertyMetadataOptions.AffectsRender));

		public PawnType PawnType {
			get { return (PawnType)GetValue(PawnTypeProperty); }
			set { SetValue(PawnTypeProperty, value); }
		}


		// A bunch of visual properties.

		public static readonly DependencyProperty FillProperty = DependencyProperty.Register(
			"Fill",
			typeof(Brush),
			typeof(Pawn),
			new FrameworkPropertyMetadata(
				new SolidColorBrush(Colors.White),
				FrameworkPropertyMetadataOptions.AffectsRender,
				OnFillChanged));

		public Brush Fill {
			get { return (Brush)GetValue(FillProperty); }
			set { SetValue(FillProperty, value); }
		}

		private static void OnFillChanged(DependencyObject sender, DependencyPropertyChangedEventArgs e) {
			var pawn = (Pawn)sender;
			var shape = (Shape)pawn.FindName("_shape");
			shape.Fill = (Brush)e.NewValue;
		}

		public static readonly DependencyProperty StrokeProperty = DependencyProperty.Register(
			"Stroke",
			typeof(SolidColorBrush),
			typeof(Pawn),
			new FrameworkPropertyMetadata(
				new SolidColorBrush(Colors.Black),
				FrameworkPropertyMetadataOptions.AffectsRender,
				OnStrokeChanged));

		public SolidColorBrush Stroke {
			get { return (SolidColorBrush)GetValue(StrokeProperty); }
			set { SetValue(StrokeProperty, value); }
		}

		private static void OnStrokeChanged(DependencyObject sender, DependencyPropertyChangedEventArgs e) {
			var pawn = (Pawn)sender;
			var shape = (Shape)pawn.FindName("_shape");
			shape.Stroke = (SolidColorBrush)e.NewValue;
		}

		public static readonly DependencyProperty StrokeThicknessProperty = DependencyProperty.Register(
			"StrokeThickness",
			typeof(double),
			typeof(Pawn),
			new FrameworkPropertyMetadata(
				1.0,
				FrameworkPropertyMetadataOptions.AffectsRender,
				OnStrokeThicknessChanged));

		public double StrokeThickness {
			get { return (double)GetValue(StrokeThicknessProperty); }
			set { SetValue(StrokeThicknessProperty, value); }
		}

		private static void OnStrokeThicknessChanged(DependencyObject sender, DependencyPropertyChangedEventArgs e) {
			var pawn = (Pawn)sender;
			var shape = (Shape)pawn.FindName("_shape");
			shape.StrokeThickness = (double)e.NewValue;
		}


		/// <summary>
		/// Gets fired when a pawn is clicked.
		/// </summary>
		public static readonly RoutedEvent ClickEvent = EventManager.RegisterRoutedEvent(
			"Click",
			RoutingStrategy.Bubble,
			typeof(RoutedEventArgs),
			typeof(Pawn));

		public event RoutedEventHandler Click {
			add { AddHandler(ClickEvent, value); }
			remove { RemoveHandler(ClickEvent, value); }
		}

		void RaiseClickEvent(Object sender, RoutedEventArgs e) {
			RoutedEventArgs args = new RoutedEventArgs(ClickEvent, sender);
			RaiseEvent(args);
		}

	}

}
