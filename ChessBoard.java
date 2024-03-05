// Create a map of square names and squares (map<String><JPanel>) where the name of the square (i.e., a4) is the key
// and the square it corresponds to is the object.
// This would just help with efficiency when it comes to searching

import java.awt.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.HashMap;

class ChessBoard {

	private JFrame chessBoardFrame;
	private JPanel chessBoardPanel;

	private JPanel squareClicked = null;
	private JPanel previousSquare = null;
	private Color[] colors; // TODO: use a value grabbed from Menu.java to set the colors.
	private int WIDTH, HEIGHT;

	private Vector<Piece> blackPieces = new Vector<>(16);
	private Vector<Piece> whitePieces = new Vector<>(16);

	private King whiteKing;
	private King blackKing;

	private int iconSelector;
	private boolean white;
	private boolean whiteTurn = true;
	private Menu menuPointer;

	// Sounds
	private String movementSound = "soundFX/movement-sound.wav";
	private String captureSound = "soundFX/capture-sound.wav";
	private String checkSound = "soundFX/check-sound.wav";
	private String startGameSound = "soundFX/startgame-sound.wav";
	private String castlingSound = "soundFX/castling-sound.wav";

	public HashMap<String, JPanel> squareMap = new HashMap<>();

	public ChessBoard(int colorSelector, int iconSelection, int playerColor, Menu menu) {
		setColor(colorSelector);
		iconSelector = iconSelection;
		WIDTH = 800;
		HEIGHT = 800;
		if (playerColor == 1)
			white = true;
		else
			white = false;
		menuPointer = menu;
	}

	public boolean isWhite() {
		return white;
	}

	private void setColor(int colorSelector) {
		colors = new Color[2];

		switch (colorSelector) {
			case 1:
				colors[0] = new Color(240, 217, 181);
				colors[1] = new Color(181, 136, 99);
				break;

			case 2:
				colors[0] = new Color(238, 238, 210);
				colors[1] = new Color(118, 150, 86);
				break;
		}

	}

	public void createChessBoard() {

		chessBoardFrame = new JFrame("Chess Board");
		chessBoardFrame.setLayout(new GridLayout(1, 1));
		chessBoardFrame.setSize(WIDTH, HEIGHT);
		chessBoardPanel = new JPanel();
		chessBoardPanel.setLayout(new GridLayout(8, 8));

		String[] columns = { "a", "b", "c", "d", "e", "f", "g", "h" };
		boolean whiteSquare = true;

		if (white) {
			for (int row = 0; row < 8; row++) {
				for (int col = 0; col < 8; col++) {
					JPanel square = new JPanel();
					square.setName(columns[col] + (8 - row));
					squareMap.put(square.getName(), square);
					square.setBackground(whiteSquare ? colors[0] : colors[1]);
					// square.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
					whiteSquare = !whiteSquare;

					if (row == 0 || row == 7)
						setPiece(square);

					if (row == 1 || row == 6)
						setPawn(row, square);

					chessBoardPanel.add(square);
				}
				whiteSquare = !whiteSquare;
			}
		}
		// Black orientation
		else {
			for (int row = 7; row >= 0; row--) {
				for (int col = 7; col >= 0; col--) {
					JPanel square = new JPanel();
					square.setName(columns[col] + (8 - row));
					squareMap.put(square.getName(), square);
					square.setBackground(whiteSquare ? colors[0] : colors[1]);
					// square.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
					whiteSquare = !whiteSquare;

					if (row == 0 || row == 7)
						setPiece(square);

					if (row == 1 || row == 6)
						setPawn(row, square);

					chessBoardPanel.add(square);
				}
				whiteSquare = !whiteSquare;
			}
		}
		chessBoardFrame.add(chessBoardPanel);
		chessBoardFrame.setVisible(true);
		playSound(startGameSound);

		createActionListener();

	}

	private void setPiece(JPanel square) {
		Piece currPiece;
		// Sets the color
		boolean color = true;
		if (square.getName().charAt(1) == '8')
			color = false;

		switch (square.getName().charAt(0)) {
			// Rooks
			case 'a':
			case 'h':
				square.add(new Rook(square.getName(), color, this));
				break;
			// Knights
			case 'b':
			case 'g':
				square.add(new Knight(square.getName(), color, this));
				break;
			// Bishops
			case 'c':
			case 'f':
				square.add(new Bishop(square.getName(), color, this));
				break;
			// Queens
			case 'd':
				square.add(new Queen(square.getName(), color, this));
				break;
			// Kings
			case 'e':
				square.add(new King(square.getName(), color, this));
				if (color) {
					whiteKing = (King) square.getComponent(0);
				} else {
					blackKing = (King) square.getComponent(0);
				}
				break;
		}
		currPiece = (Piece) square.getComponent(0);
		if (color)
			whitePieces.add(currPiece);
		else
			blackPieces.add(currPiece);

	}

	private void setPawn(int row, JPanel square) {
		Piece currPiece;

		// Creates the pawns
		boolean color = true;
		if (row == 1)
			color = false;

		square.add(new Pawn(square.getName(), color, this));

		currPiece = (Piece) square.getComponent(0);
		if (color)
			whitePieces.add(currPiece);
		else
			blackPieces.add(currPiece);
	}

	public boolean isEmpty(String location) {
		for (int i = 0; i < whitePieces.size(); i++) {
			if (whitePieces.get(i).getCurrPos().equals(location))
				return false;
		}
		for (int i = 0; i < blackPieces.size(); i++) {
			if (blackPieces.get(i).getCurrPos().equals(location))
				return false;
		}
		return true;
	}

	public void createActionListener() {
		chessBoardPanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Piece pieceToMove;
				JPanel rookCastlingSquare;
				King kingPiece = null;
				boolean deletePiece = false;
				Piece pieceToRemove = new Piece();
				boolean castling = false;
				boolean isPromoting = false;

				int x = e.getX();
				int y = e.getY();

				// Set the square clicked to previous square
				if (squareClicked != null) {
					previousSquare = squareClicked;
				}
				// get the JPanel square in the form of component object
				Component componentClicked = chessBoardPanel.getComponentAt(x, y);

				// Cast the component to a JPanel
				squareClicked = (JPanel) componentClicked;

				// Set the border of the previous square to null.
				if (previousSquare != null)
					previousSquare.setBorder(null);

				// Set the border of the clicked square to yellow
				squareClicked.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 4));

				// Ensure that it's your turn:
				if ((whiteTurn && white) || (!whiteTurn && !white)) {
					// Check if the move is valid:
					if (previousSquare != null) {
						// The previously selected square has a piece on, now check if ValidMove
						if (previousSquare.getComponentCount() > 0) {
							pieceToMove = (Piece) previousSquare.getComponent(0);

							// Now that we have the piece, check if valid move
							if (pieceToMove.isValidMove(previousSquare, squareClicked, false)) {
								// Move the piece
								// If there's already a piece there, "take" it
								if (squareClicked.getComponentCount() > 0) {
									pieceToRemove = (Piece) squareClicked.getComponent(0);
									if (pieceToRemove.isWhite())
										whitePieces.remove(pieceToRemove);
									else
										blackPieces.remove(pieceToRemove);
									deletePiece = true;
								}
								pieceToMove.setCurrPos(squareClicked.getName());

								// Moving this piece result in check, redo everything and break.
								if (pieceToMove.isWhite()) {
									if (isCheck(whiteKing, squareMap.get(whiteKing.getCurrPos()))) {
										playSound(checkSound);
										pieceToMove.setCurrPos(previousSquare.getName());
										if (deletePiece) {
											blackPieces.add(pieceToRemove);
											deletePiece = false;
										}
										return;
									}
								} else {
									if (isCheck(blackKing, squareMap.get(blackKing.getCurrPos()))) {
										playSound(checkSound);
										pieceToMove.setCurrPos(previousSquare.getName());
										if (deletePiece) {
											whitePieces.add(pieceToRemove);
											deletePiece = false;
										}
										return;
									}
								}

								// If King castling
								if (pieceToMove instanceof King) {
									kingPiece = (King) pieceToMove;
									if (kingPiece.isCastling()) {
										// Castling King (right) side
										String originalRookPos = kingPiece.getRook().getCurrPos();
										if (kingPiece.getRook().isRightRook()) {
											rookCastlingSquare = squareMap
													.get((char) (squareClicked.getName().charAt(0) - 1) + ""
															+ squareClicked.getName().charAt(1));
											rookCastlingSquare.add(kingPiece.getRook());
											rookCastlingSquare.repaint();
											squareMap.get((char) (squareClicked.getName().charAt(0) + 1) + ""
													+ squareClicked.getName().charAt(1)).repaint();
											kingPiece.getRook().setCurrPos(rookCastlingSquare.getName());
										}
										// Castling Queen (left) side
										else {
											rookCastlingSquare = squareMap
													.get((char) (squareClicked.getName().charAt(0) + 1) + ""
															+ squareClicked.getName().charAt(1));
											rookCastlingSquare.add(kingPiece.getRook());
											rookCastlingSquare.repaint();
											squareMap.get(kingPiece.getRook().getCurrPos()).repaint();
											kingPiece.getRook().setCurrPos(rookCastlingSquare.getName());
										}
										// Sound Effects
										playSound(castlingSound);
										// Need to send the castled move when the king castles
										menuPointer.connector.sendMove(
												previousSquare.getName() + squareClicked.getName(),
												originalRookPos + rookCastlingSquare.getName());
										castling = true;
										kingPiece.castled();
									}
								}

								boolean isCheck = false;

								if (pieceToMove.isWhite()) {
									if (pieceToMove.isValidMove(squareClicked, squareMap.get(blackKing.getCurrPos()),
											false))
										isCheck = true;
								} else {
									if (pieceToMove.isValidMove(squareClicked, squareMap.get(whiteKing.getCurrPos()),
											false))
										isCheck = true;
								}
								if (isCheck)
									playSound(checkSound);

								if (deletePiece) {
									squareClicked.remove(pieceToRemove);
									if (!isCheck)
										playSound(captureSound);
								}
								squareClicked.add(pieceToMove);
								pieceToMove.moved();

								// Pawn Promotion
								if (pieceToMove instanceof Pawn) {
									// Pawn at ends of board.
									if (squareClicked.getName().charAt(1) == '1'
											|| squareClicked.getName().charAt(1) == '8') {
										System.out.println("Entering pawn promotion");
										menuPointer.pawnPromotion(squareClicked, previousSquare.getName());
										isPromoting = true;
									}
								}

								// Create a network flag that sends the move only if we're connected to server;
								whiteTurn = !whiteTurn;
								if (!castling && !isPromoting) {
									if (!deletePiece && !isCheck)
										playSound(movementSound);

									menuPointer.connector.sendMove(previousSquare.getName() + squareClicked.getName());
								}
								deletePiece = false;
								castling = false;
								isPromoting = false;
								squareClicked.setBorder(null);
								previousSquare = null;
								squareClicked = null;
							}
						}
					}
				}
			}
		});
	}

	public int getIconSelector() {
		return iconSelector;
	}

	// Check if check for the square the king is trying to move to
	// Need to create a copy of the pieces
	public boolean isCheck(King kingPiece, JPanel endSquare) {
		Piece attackingPiece;

		// If king is white, check black pieces
		if (kingPiece.isWhite()) {
			for (int i = 0; i < blackPieces.size(); i++) {
				attackingPiece = blackPieces.get(i);
				if (attackingPiece.isValidMove(squareMap.get(attackingPiece.getCurrPos()), endSquare, true)) {
					return true;
				}
			}
		} else {
			for (int i = 0; i < whitePieces.size(); i++) {
				attackingPiece = whitePieces.get(i);
				if (attackingPiece.isValidMove(squareMap.get(attackingPiece.getCurrPos()), endSquare, true)) {
					return true;
				}
			}
		}
		return false;
	}

	public Vector<Piece> getPieceVector(boolean color) {
		if (color)
			return whitePieces;
		return blackPieces;
	}

	public void opponentMove(String move, boolean castling, boolean promoting) {

		// For castling
		if (castling) {
			String kingStartSquareString = move.charAt(0) + "" + move.charAt(1);
			String kingEndSquareString = move.charAt(2) + "" + move.charAt(3);
			String rookStartSquareString = move.charAt(4) + "" + move.charAt(5);
			String rookEndSquareString = move.charAt(6) + "" + move.charAt(7);

			JPanel kingStartSquare = squareMap.get(kingStartSquareString);
			JPanel kingEndSquare = squareMap.get(kingEndSquareString);
			JPanel rookStartSquare = squareMap.get(rookStartSquareString);
			JPanel rookEndSquare = squareMap.get(rookEndSquareString);

			Piece kingToMove = (Piece) kingStartSquare.getComponent(0);
			Piece rookToMove = (Piece) rookStartSquare.getComponent(0);

			// set the positions.
			kingToMove.setCurrPos(kingEndSquareString);
			rookToMove.setCurrPos(rookEndSquareString);

			// Add the pieces to the respective squares.
			kingEndSquare.add(kingToMove);
			rookEndSquare.add(rookToMove);

			// Repaint the squares.
			kingStartSquare.repaint();
			kingEndSquare.repaint();
			rookStartSquare.repaint();
			rookEndSquare.repaint();

			// Sound FX
			playSound(castlingSound);

		}
		// Handles pawn promotion
		else if (promoting) {
			char promotionChar;
			String initialSquareString, endSquareString;
			Piece promotingPiece;
			JPanel promotingSquare, initialSquare;
			Piece pieceToPromote;

			// Set the Variables
			initialSquareString = move.charAt(1) + "" + move.charAt(2);
			endSquareString = move.charAt(3) + "" + move.charAt(4);
			promotingSquare = squareMap.get(endSquareString);
			initialSquare = squareMap.get(initialSquareString);
			pieceToPromote = (Piece) initialSquare.getComponent(0);
			promotionChar = move.charAt(0);

			// Create the new piece
			switch (promotionChar) {
				case '1':
					promotingPiece = new Queen(pieceToPromote.getStartPos(), pieceToPromote.isWhite(), this);
					break;
				case '2':
					promotingPiece = new Rook(pieceToPromote.getStartPos(), pieceToPromote.isWhite(), this);
					break;
				case '3':
					promotingPiece = new Knight(pieceToPromote.getStartPos(), pieceToPromote.isWhite(), this);
					break;
				case '4':
					promotingPiece = new Bishop(pieceToPromote.getStartPos(), pieceToPromote.isWhite(), this);
					break;
				default:
					promotingPiece = new Piece();
					break;
			}
			promotingPiece.setCurrPos(move.charAt(3) + "" + move.charAt(4));

			// Remove piece from vector and board
			initialSquare.remove(pieceToPromote);
			getPieceVector(promotingPiece.isWhite()).remove(pieceToPromote);
			getPieceVector(promotingPiece.isWhite()).add(promotingPiece);

			boolean isCheck = false;
			boolean pieceTaken = false;

			// Checks if player is in check so can play check sound
			if (promotingPiece.isWhite()) {
				if (promotingPiece.isValidMove(promotingSquare, squareMap.get(blackKing.getCurrPos()), false)) {
					isCheck = true;
				}
			} else {
				if (promotingPiece.isValidMove(promotingSquare, squareMap.get(whiteKing.getCurrPos()), false)) {
					isCheck = true;
				}
			}
			if (isCheck)
				playSound(checkSound);

			// Remove any pieces on the end square:
			if (promotingSquare.getComponentCount() > 0) {
				Piece pieceToRemove = (Piece) promotingSquare.getComponent(0);
				promotingSquare.remove(pieceToRemove);
				getPieceVector(pieceToRemove.isWhite()).remove(pieceToRemove);
				pieceTaken = true;
				if (!isCheck)
					playSound(captureSound);
			}
			if (!pieceTaken && !isCheck) {
				playSound(movementSound);
			}
			// add piece to board
			promotingSquare.add(promotingPiece);
			initialSquare.repaint();
			promotingSquare.revalidate();
			promotingSquare.repaint();

		}
		// Every other type of move
		else {
			String startSquareString = move.charAt(0) + "" + move.charAt(1);
			String endSquareString = move.charAt(2) + "" + move.charAt(3);

			JPanel startSquarePanel = squareMap.get(startSquareString);
			JPanel endSquarePanel = squareMap.get(endSquareString);

			Piece pieceToMove = (Piece) startSquarePanel.getComponent(0);
			Piece pieceToTake;

			// Update the moved piece in the pieces array
			pieceToMove.setCurrPos(endSquareString);

			boolean pieceTaken = false;
			boolean isCheck = false;

			if (pieceToMove.isWhite()) {
				if (pieceToMove.isValidMove(endSquarePanel, squareMap.get(blackKing.getCurrPos()), false))
					isCheck = true;
			} else {
				if (pieceToMove.isValidMove(endSquarePanel, squareMap.get(whiteKing.getCurrPos()), false))
					isCheck = true;
			}

			if (isCheck)
				playSound(checkSound);

			// Object occupies square.
			if (endSquarePanel.getComponentCount() > 0) {
				pieceToTake = (Piece) endSquarePanel.getComponent(0);
				if (white) {
					whitePieces.remove(pieceToTake);
				} else {
					blackPieces.remove(pieceToTake);
				}
				endSquarePanel.remove(pieceToTake);
				pieceTaken = true;
				if (!isCheck)
					playSound(captureSound);
			}
			if (!pieceTaken && !isCheck)
				playSound(movementSound);

			endSquarePanel.add(pieceToMove);
			startSquarePanel.repaint();
			endSquarePanel.repaint();
		}
		// Swap the turn
		whiteTurn = !whiteTurn;
	}

	// Method for sound FX
	public static void playSound(String filePath) {
		try {
			File soundFile = new File(filePath);
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
			clip.start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}
}
