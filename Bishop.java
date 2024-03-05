import javax.swing.*;

class Bishop extends Piece {
	public Bishop(String initialPos, boolean color, ChessBoard board) {
		super(initialPos, color, board);
		setIcon(new ImageIcon(color ? "icons/white-bishop.png" : "icons/black-bishop.png"));
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

		// Valid diagonol move
		if (Math.abs(startCol - endCol) == Math.abs(startRow - endRow)) {
			// Left to Right
			if (startCol < endCol) {
				for (int i = 0; i < Math.abs(endCol - startCol) - 1; i++) {
					// Up to down
					if (startRow < endRow) {
						if (!getBoard().isEmpty((char) (endCol - 1 - i) + "" + (endRow - 1 - i)))
							return false;
					}
					// Down to up
					else {
						if (!getBoard().isEmpty((char) (endCol - 1 - i) + "" + (endRow + 1 + i)))
							return false;
					}
				}
			}
			// Right to Left
			else {
				for (int i = 0; i < Math.abs(endCol - startCol) - 1; i++) {
					if (startRow < endRow) {
						if (!getBoard().isEmpty((char) (endCol + 1 + i) + "" + (endRow - 1 - i)))
							return false;
					} else {
						if (!getBoard().isEmpty((char) (endCol + 1 + i) + "" + (endRow + 1 + i)))
							return false;
					}
				}
			}
			return true;
		}
		return false;
	}
}
