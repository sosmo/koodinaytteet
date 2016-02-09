using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tammi {

	/// <summary>
	/// A class to play chess-style board games internally and report its state outside.
	/// </summary>
	public interface IGameLogic {

		/// <summary>
		/// The size of the board used in the game.
		/// </summary>
		int Size { get; set; }

		/// <summary>
		/// Initiate a new game of the Size given earlier.
		/// </summary>
		/// <exception cref="InvalidOperationException">Thrown if Size hasn't been set.</exception>
		void Initiate();

		/// <summary>
		/// The player who's allowed to move next.
		/// </summary>
		Player Turn { get; set; }

		/// <summary>
		/// Get the layout of pawns on the board as an array.
		/// </summary>
		/// <returns>Row and column coordinates start from 0 at the upper left square.</returns>
		IPawn[,] PawnLayout();

		/// <summary>
		/// Move the pawn from coordinates to new coordinates.
		/// </summary>
		/// <param name="rowFrom">Count starts from 0 at the upper left square.</param>
		/// <param name="columnFrom">Count starts from 0 at the upper left square.</param>
		/// <param name="rowTo">Count starts from 0 at the upper left square.</param>
		/// <param name="columnTo">Count starts from 0 at the upper left square.</param>
		/// <returns>Results of the move.</returns>
		IMoveResults MovePawn(int rowFrom, int columnFrom, int rowTo, int columnTo);

		/// <summary>
		/// Get the pawn at the coordinates.
		/// </summary>
		/// <param name="row">Count starts from 0 at the upper left square.</param>
		/// <param name="column">Count starts from 0 at the upper left square.</param>
		/// <returns>The pawn at the specified row and column.</returns>
		IPawn PawnAt(int row, int column);

		/// <summary>
		/// Check if the move from the coordinates to the new ones is legal.
		/// </summary>
		/// <param name="rowFrom">Count starts from 0 at the upper left square.</param>
		/// <param name="columnFrom">Count starts from 0 at the upper left square.</param>
		/// <param name="rowTo">Count starts from 0 at the upper left square.</param>
		/// <param name="columnTo">Count starts from 0 at the upper left square.</param>
		/// <returns>True, if the move is legal</returns>
		bool ValidateMove(int rowFrom, int columnFrom, int rowTo, int columnTo);

		/// <summary>
		/// The amount of legal moves to be made, starting at the specified square.
		/// </summary>
		/// <param name="rowFrom">Start row.</param>
		/// <param name="columnFrom">Start column.</param>
		/// <returns>The amount of legal moves the pawn at the start row/column can make. Zero if there's no pawn.</returns>
		IList<BoardPosition> ValidMoves(int rowFrom, int columnFrom);

		/// <summary>
		/// Get the list of pawns a player can move legally.
		/// </summary>
		/// <param name="player">Player whose pawns to get.</param>
		/// <returns>A list of legally movable pawns for the player.</returns>
		IList<IPawn> MovablePawns(Player player);

		/// <summary>
		/// Get the number pawns a player has left.
		/// </summary>
		/// <param name="player">Player whose pawns to get.</param>
		/// <returns>The number of pawns the player has.</returns>
		int PawnCount(Player player);

		/// <summary>
		/// Get the list of moves that have been made on a board, in order.
		/// </summary>
		IList<BoardMove> MovesHistory { get; }

	}

}
