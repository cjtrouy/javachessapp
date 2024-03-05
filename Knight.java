import javax.swing.*;

class Knight extends Piece {
	// private boolean hasMoved;
	public Knight(String initialPos, boolean color, ChessBoard board) {
		super(initialPos, color, board);
		setIcon(new ImageIcon(color ? "icons/white-knight.png" : "icons/black-knight.png"));
	}

	public boolean isValidMove(JPanel startSquare, JPanel endSquare, boolean checkFlag) {

		char startCol = startSquare.getName().charAt(0);
		int startRow = Character.getNumericValue(startSquare.getName().charAt(1));

		char endCol = endSquare.getName().charAt(0);
		int endRow = Character.getNumericValue(endSquare.getName().charAt(1));

		if (!checkFlag) {
			if (!super.isValidMove(startSquare, endSquare, checkFlag))
				return false;
		}

		if (sameColor(endSquare.getName()))
			return false;

		// Above original position
		if (endRow > startRow) {
			// Left side
			if (endCol < startCol) {
				if (endCol == startCol - 2 && endRow == startRow + 1)
					return true;
				if (endCol == startCol - 1 && endRow == startRow + 2)
					return true;
			}
			// right side
			else {
				if (endCol == startCol + 2 && endRow == startRow + 1)
					return true;
				if (endCol == startCol + 1 && endRow == startRow + 2)
					return true;
			}
		}
		// Below original position
		else {
			if (endCol < startCol) {
				if (endCol == startCol - 2 && endRow == startRow - 1)
					return true;
				if (endCol == startCol - 1 && endRow == startRow - 2)
					return true;
			} else {
				if (endCol == startCol + 2 && endRow == startRow - 1)
					return true;
				if (endCol == startCol + 1 && endRow == startRow - 2)
					return true;
			}
		}

		return false;
	}
}