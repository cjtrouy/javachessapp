import javax.swing.*;

class King extends Piece {
	private String whiteKingIcon;
	private Rook castlingRook;
	private boolean castling;

	public King(String initialPos, boolean color, ChessBoard board) {
		super(initialPos, color, board);
		switch (board.getIconSelector()) {
			case 1:
				whiteKingIcon = "icons/white-king.png";
				break;
			case 2:
				whiteKingIcon = "icons/white-king2.png";
				break;
		}
		setIcon(new ImageIcon(color ? whiteKingIcon : "icons/black-king.png"));
	}

	// Function to determine if a move is valid
	public boolean isValidMove(JPanel startSquare, JPanel endSquare, boolean checkFlag) {
		castling = false;
		char startCol = startSquare.getName().charAt(0);
		int startRow = Character.getNumericValue(startSquare.getName().charAt(1));

		char endCol = endSquare.getName().charAt(0);
		int endRow = Character.getNumericValue(endSquare.getName().charAt(1));

		JPanel squareToCheck;

		if (!checkFlag) {
			if (!super.isValidMove(startSquare, endSquare, checkFlag))
				return false;
		}
		if (sameColor(endSquare.getName()))
			return false;

		// Castling (row clicked is start row)
		if (endSquare.getName().charAt(1) == getStartPos().charAt(1)) {
			// Piece hasn't moved
			if (!ifMoved()) {
				// Right castle
				if (endCol == startCol + 2) {
					// Check to see if piece(s) is/are present between king and rook
					for (int i = 1; i < 3; i++) {
						// If a piece exists
						if (!getBoard().isEmpty((char) (startCol + i) + "" + startRow))
							return false;
					}

					// Check if Rook is present and hasn't moved
					squareToCheck = getBoard().squareMap.get((char) (endCol + 1) + "" + startRow);
					// Piece is present on square
					if (squareToCheck.getComponentCount() > 0) {
						// Piece is rook
						if (squareToCheck.getComponent(0) instanceof Rook) {
							castlingRook = (Rook) squareToCheck.getComponent(0);
							// Rook hasn't moved
							if (!castlingRook.ifMoved()) {
								castling = true;
								castlingRook.setRightRook(true);
								return true;
							}
						}
					}
					return false;
				}
				// Left castle
				if (endCol == startCol - 2) {
					// Check if pieces are present between the rook and king
					for (int i = 1; i < 4; i++) {
						// If a piece exists
						if (!getBoard().isEmpty((char) (startCol - i) + "" + startRow))
							return false;
					}
					// Check that the Rook is present/hasn't moved
					squareToCheck = getBoard().squareMap.get((char) (endCol - 2) + "" + startRow);
					// Piece is present on square
					if (squareToCheck.getComponentCount() > 0) {
						if (squareToCheck.getComponent(0) instanceof Rook) {
							castlingRook = (Rook) squareToCheck.getComponent(0);
							if (!castlingRook.ifMoved()) {
								castling = true;
								castlingRook.setRightRook(false);
								return true;
							}
						}
					}
					return false;
				}
			}

		}
		// All other movement
		if (endRow > startRow + 1 || endRow < startRow - 1 || endCol > startCol + 1 || endCol < startCol - 1)
			return false;

		return true;
	}

	public boolean isCastling() {
		return castling;
	}

	public Rook getRook() {
		return castlingRook;
	}

	public void castled() {
		castling = false;
	}
}