using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tammi {

	/// <summary>
	/// A basic frame for a board game logic class.
	/// </summary>
	/// <typeparam name="T">The type of pawn to operate with.</typeparam>
	class GameLogicCore<T> where T : IPawn {

		// The backing array is abstracted away. Directly manipulating the array is not desired since each operation requires extra checks/logic.
		T[,] board = null;

		public IList<Player> Players { get; set; }

		public Player Turn { get; set; }

		// TODO: use an immutable interface
		public IDictionary<Player, List<T>> Pawns { get; private set; }

		int _size = 0;
		public int Size {
			get { return _size; }
			set { _size = value; }
		}

		List<BoardMove> _movesHistory;
		public IList<BoardMove> MovesHistory {
			get {
				BoardMove[] array = new BoardMove[_movesHistory.Count];
				_movesHistory.CopyTo(array);
				List<BoardMove> list = new List<BoardMove>(array);
				return list;
			}
		}

		// TODO
		//Stack<T[,]> undoStates;
		//Stack<T[,]> redoStates;


		public void Initiate() {
			if (Size < 1) {
				throw new InvalidOperationException("Size needs to be set before initiating.");
			}
			if (Players == null || Players.Count < 1) {
				throw new InvalidOperationException("Players needs to be set before initiating.");
			}

			board = new T[Size, Size];

			Pawns = new Dictionary<Player, List<T>>();
			foreach (Player player in Players) {
				Pawns[player] = new List<T>();
				Pawns[player] = new List<T>();
			}

			_movesHistory = new List<BoardMove>();

			// TODO
			//undoStates = new Stack<T[,]>();
			//redoStates = new Stack<T[,]>();
		}

		public int PawnCount(Player player) {
			return Pawns[player].Count;
		}

		public IPawn[,] PawnLayout() {
			IPawn[,] copy = new IPawn[Size, Size];
			Array.Copy(board, copy, board.Length);
			return copy;
		}

		/// <summary>
		/// Get the pawn at the specified coordinates. The board's coordinates are zero-based and start at the top left square.
		/// </summary>
		public T PawnAt(int row, int column) {
			return board[row, column];
		}

		/// <summary>
		/// Move a pawn from the specified coordinates to new ones. The board's coordinates are zero-based and start at the top left square.
		/// </summary>
		public void MovePawn(int rowFrom, int columnFrom, int rowTo, int columnTo) {
			T pawn = board[rowFrom, columnFrom];

			board[rowTo, columnTo] = pawn;
			board[rowFrom, columnFrom] = default(T);

			pawn.Row = rowTo;
			pawn.Column = columnTo;

			_movesHistory.Add(new BoardMove(
				new BoardPosition(rowFrom, columnFrom),
				new BoardPosition(rowTo, columnTo), Turn));
		}

		/// <summary>
		/// Remove the pawn at the specified coordinates. The board's coordinates are zero-based and start at the top left square.
		/// </summary>
		public void RemovePawn(int row, int column) {
			T pawn = board[row, column];

			board[row, column] = default(T);

			Pawns[pawn.Player].Remove(pawn);
		}

		/// <summary>
		/// Add a pawn at the specified coordinates. The board's coordinates are zero-based and start at the top left square.
		/// </summary>
		public void AddPawn(T pawn, int row, int column) {
			board[row, column] = pawn;

			pawn.Row = row;
			pawn.Column = column;

			Pawns[pawn.Player].Add(pawn);
		}

		// TODO
		//public void SetUndoPoint() {
		//    redoStates.Clear();

		//    T[,] temp = new T[Size, Size];
		//    Array.Copy(board, temp, board.Length);
		//    undoStates.Push(temp);
		//}

		//public void Undo() {
		//    T[,] temp = new T[Size, Size];
		//    Array.Copy(board, temp, board.Length);
		//    redoStates.Push(temp);

		//    T[,] prevState = undoStates.Pop();
		//    board = prevState;
		//}

		//public void Redo() {
		//    T[,] temp = new T[Size, Size];
		//    Array.Copy(board, temp, board.Length);
		//    undoStates.Push(temp);

		//    T[,] nextState = redoStates.Pop();
		//    board = nextState;
		//}

	}

}
