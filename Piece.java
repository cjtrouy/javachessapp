import java.awt.MediaTracker;

import javax.swing.*;

class Piece extends JLabel {
	private String startPos;
	private String currPos;
	private boolean white;
	private ChessBoard boardRef;
	private boolean hasMoved;

	public Piece() {
		startPos = "a1";
		currPos = startPos;
		white = true;
		hasMoved = false;
	}

	public Piece(String initialPos, boolean color, ChessBoard board) {
		startPos = initialPos;
		currPos = initialPos;
		white = color;
		boardRef = board;
		hasMoved = false;
	}

	public boolean isWhite() {
		return white;
	}

	public boolean isValidMove(JPanel start, JPanel end, boolean checkFlag) {
		if (this.isWhite() != boardRef.isWhite())
			return false;
		return true;
	}

	public String getCurrPos() {
		return currPos;
	}

	public String getStartPos() {
		return startPos;
	}

	public void setCurrPos(String newPos) {
		currPos = newPos;
	}

	public ChessBoard getBoard() {
		return boardRef;
	}

	// Returns true if there's a piece of the same color on the square marked by
	// location.
	public boolean sameColor(String location) {
		if (boardRef.isEmpty(location))
			return false;
		for (Piece piece : boardRef.getPieceVector(isWhite())) {
			if (piece.getCurrPos().equals(location)) {
				return true;
			}
		}
		return false;
	}

	// Placeholder
	public boolean isCheck() {
		return false;
	}

	public void checkIcon(Icon pieceIcon) {
		if (pieceIcon != null && pieceIcon instanceof ImageIcon) {
			ImageIcon imageIcon = (ImageIcon) pieceIcon;
			if (imageIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
				return;
			}
		}
		System.err.println("Error setting icon");
	}

	public void moved() {
		hasMoved = true;
	}

	public boolean ifMoved() {
		return hasMoved;
	}

}