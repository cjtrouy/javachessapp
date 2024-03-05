import javax.swing.*;

class Pawn extends Piece {
	public Pawn(String initialPos, boolean color, ChessBoard board) {
		super(initialPos, color, board);
		setImageIcon();
	}

	private void setImageIcon() {
		if (isWhite()) {
			setIcon(new ImageIcon("icons/white-pawn.png"));
		} else {
			setIcon(new ImageIcon("icons/black-pawn.png"));
		}
	}

	public boolean isValidMove(JPanel startSquare, JPanel endSquare, boolean checkFlag) {
		char startCol = startSquare.getName().charAt(0);
		int startRow = Character.getNumericValue(startSquare.getName().charAt(1));

		char endCol = endSquare.getName().charAt(0);
		int endRow = Character.getNumericValue(endSquare.getName().charAt(1));

		String newLocation = "";

		if (!checkFlag) {
			if (!super.isValidMove(startSquare, endSquare, checkFlag))
				return false;
		}

		// White pieces
		if (isWhite()) {
			// can't move into square occupied by same color
			if (!getBoard().isEmpty(endSquare.getName())) {

			}
			// Can't move back
			if (endRow < startRow)
				return false;

			// Taking a piece
			if (endCol == startCol + 1 || endCol == startCol - 1) {
				if (endRow == startRow + 1) {
					if (!getBoard().isEmpty(endSquare.getName())) {
						if (sameColor(endSquare.getName()))
							return false;
						return true;
					}
				}
			}

			// Can't move side to side
			if (startCol != endCol)
				return false;

			// can't move into occupied space
			if (!getBoard().isEmpty(endSquare.getName()))
				return false;

			// Pawn boosting
			if (getStartPos().equals(getCurrPos())) {
				if (endRow == startRow + 2) {
					newLocation = String.valueOf(startCol) + (startRow + 1);
					if (getBoard().isEmpty(newLocation))
						return true;
				}
			}

			// Can't move more than one space
			if (endRow - 1 != startRow)
				return false;
		}

		// Black Pieces
		else {
			// Can't move back
			if (endRow > startRow)
				return false;

			// Taking a piece
			if (endCol == startCol + 1 || endCol == startCol - 1) {
				if (endRow == startRow - 1) {
					if (!getBoard().isEmpty(endSquare.getName())) {
						if (sameColor(endSquare.getName()))
							return false;
						return true;
					}
				}
			}

			// Can't move side to side
			if (startCol != endCol)
				return false;

			// can't move into occupied space
			if (!getBoard().isEmpty(endSquare.getName()))
				return false;

			// Pawn boosting
			if (getStartPos().equals(getCurrPos())) {
				if (endRow == startRow - 2) {
					newLocation = String.valueOf(startCol) + (startRow - 1);
					if (getBoard().isEmpty(newLocation))
						return true;
				}
			}

			// can't move more than once space
			if (endRow + 1 != startRow)
				return false;
		}

		return true;
	}
}
