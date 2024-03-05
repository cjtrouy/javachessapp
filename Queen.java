import javax.swing.*;

class Queen extends Piece {
	public Queen(String initialPos, boolean color, ChessBoard board) {
		super(initialPos, color, board);
		setIcon(new ImageIcon(color ? "icons/white-queen.png" : "icons/black-queen.png"));
	}

	public boolean isValidMove(JPanel startSquare, JPanel endSquare, boolean checkFlag) {
		Rook tempRook = new Rook(getCurrPos(), isWhite(), getBoard());
		Bishop tempBishop = new Bishop(getCurrPos(), isWhite(), getBoard());

		if (tempRook.isValidMove(startSquare, endSquare, checkFlag)
				|| tempBishop.isValidMove(startSquare, endSquare, checkFlag))
			return true;
		return false;
	}
}