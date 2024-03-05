import javax.swing.*;

class Rook extends Piece {
	private boolean rightRook;

	public Rook(String initialPos, boolean color, ChessBoard board) {
		super(initialPos, color, board);
		setImageIcon();
	}

	private void setImageIcon() {
		if (isWhite()) {
			setIcon(new ImageIcon("icons/white-rook.png"));
		} else {
			setIcon(new ImageIcon("icons/black-rook.png"));
		}
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

		// vertical movement
		if (startCol == endCol) {
			for (int i = 0; i < Math.abs(startRow - endRow) - 1; i++) {
				// Checks if there is a piece inbetween start and end square (non-inclusive)
				if (endRow > startRow) {
					if (!getBoard().isEmpty(startCol + "" + (endRow - 1 - i)))
						return false;
				}
				if (startRow > endRow) {
					if (!getBoard().isEmpty(startCol + "" + (startRow - 1 - i)))
						return false;
				}
			}
			return true;
		}
		// Horizontal movement
		else if (startRow == endRow) {
			for (int i = 0; i < Math.abs(startCol - endCol) - 1; i++) {
				// Left to Right
				if (endCol > startCol) {
					if (!getBoard().isEmpty((char) (endCol - 1 - i) + "" + startRow))
						return false;
				}

				// Right to Left
				if (startCol > endCol) {
					if (!getBoard().isEmpty((char) (startCol - 1 - i) + "" + startRow))
						return false;
				}
			}
			return true;
		}
		return false;
	}

	public void setRightRook(boolean set) {
		rightRook = set;
	}

	public boolean isRightRook() {
		return rightRook;
	}

}