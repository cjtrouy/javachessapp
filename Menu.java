import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Vector;

class Menu {
	private JFrame startMenu;
	private JPanel controlPanel;

	private JFrame optionsMenuFrame;
	private JPanel optionsMenuPanel;

	private JFrame colorMenuFrame;
	private JPanel colorMenuPanel;

	private JFrame iconMenuFrame;
	private JPanel iconMenuPanel;

	private JFrame playOnlineFrame;
	private JPanel playOnlinePanel;

	private JFrame loginMenuFrame;
	private JPanel loginMenuPanel;
	private JPanel onlineUsersPanel;
	private Vector<JPanel> userPanels = new Vector<JPanel>();

	private JFrame newUserFrame;
	private JPanel newUserPanel;

	private JFrame waitMenuFrame;
	private JPanel waitMenuPanel;

	private int colorSelection;
	private int iconSelection;

	private Menu menu;

	// client-server connector
	public Client connector;

	public ChessBoard board;

	int WIDTH, HEIGHT;

	public Menu() {
		WIDTH = 400;
		HEIGHT = 400;
		colorSelection = 1;
		iconSelection = 1;
		menu = this;
		createMenu();
	}

	public void createMenu() {
		JButton startGame = new JButton("Start");
		JButton playOnlineButton = new JButton("Play Online!");
		JButton optionsButton = new JButton("Options");
		JButton quit = new JButton("Quit");

		JLabel header = new JLabel("Conner's Chess Extravaganza");
		header.setHorizontalAlignment(SwingConstants.CENTER);

		startMenu = new JFrame("Start Menu");
		startMenu.setSize(WIDTH, HEIGHT);
		startMenu.setLayout(new GridLayout(2, 1));
		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());
		startMenu.add(header);
		startMenu.add(controlPanel);

		controlPanel.add(startGame);
		controlPanel.add(playOnlineButton);
		controlPanel.add(optionsButton);
		controlPanel.add(quit);

		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		startGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				board = new ChessBoard(colorSelection, iconSelection, 1, menu);
				board.createChessBoard();
			}
		});

		optionsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createOptionsMenu();
			}
		});

		playOnlineButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connector = new Client(menu);
				connector.startConnection();
				createOnlinePlayMenu();
			}
		});
	}

	public void start() {
		startMenu.setVisible(true);
	}

	private void createOptionsMenu() {
		startMenu.setVisible(false);

		JButton changeColorButton = new JButton("Change Color");
		JButton changeIconButton = new JButton("Change Icons");
		JButton backButton = new JButton("Back");

		JLabel header = new JLabel("Options Menu");
		header.setHorizontalAlignment(SwingConstants.CENTER);

		optionsMenuFrame = new JFrame("Options");
		optionsMenuFrame.setSize(WIDTH, HEIGHT);
		optionsMenuFrame.setLayout(new GridLayout(2, 1));

		optionsMenuPanel = new JPanel();
		optionsMenuPanel.setLayout(new FlowLayout());

		optionsMenuFrame.add(header);
		optionsMenuFrame.add(optionsMenuPanel);

		optionsMenuPanel.add(changeColorButton);
		optionsMenuPanel.add(changeIconButton);
		optionsMenuPanel.add(backButton);

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startMenu.setVisible(true);
				optionsMenuFrame.dispose();
			}
		});

		changeColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createColorMenu();
			}
		});

		changeIconButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createIconMenu();
			}
		});
		optionsMenuFrame.setVisible(true);

	}

	private void createColorMenu() {
		optionsMenuFrame.setVisible(false);

		colorMenuFrame = new JFrame("Color Selection");
		colorMenuPanel = new JPanel();
		JButton defaultColor = new JButton("Default");
		JButton chess_comColor = new JButton("Chess.com");
		JButton okButton = new JButton("OK");
		JLabel header = new JLabel("Select a color scheme!");
		header.setHorizontalAlignment(SwingConstants.CENTER);

		colorMenuFrame.setLayout(new GridLayout(2, 1));
		colorMenuFrame.setSize(WIDTH, HEIGHT);
		colorMenuPanel.setLayout(new FlowLayout());

		colorMenuPanel.add(defaultColor);
		colorMenuPanel.add(chess_comColor);
		colorMenuPanel.add(okButton);
		colorMenuFrame.add(header);
		colorMenuFrame.add(colorMenuPanel);

		defaultColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				colorSelection = 1;
			}
		});

		chess_comColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				colorSelection = 2;
			}
		});

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				optionsMenuFrame.setVisible(true);
				colorMenuFrame.dispose();
			}
		});

		colorMenuFrame.setVisible(true);

	}

	// Lets user choose between different icons
	private void createIconMenu() {
		optionsMenuFrame.setVisible(false);

		iconMenuFrame = new JFrame("Icon Selection");
		iconMenuPanel = new JPanel();
		JLabel header = new JLabel("Select an icon setting");

		JButton defaultButton = new JButton("Default Icons");
		JButton workIconsButton = new JButton("Work Icons");
		JButton okButton = new JButton("OK");

		iconMenuFrame.setLayout(new GridLayout(2, 1));
		iconMenuFrame.setSize(WIDTH, HEIGHT);
		iconMenuPanel.setLayout(new FlowLayout());
		header.setHorizontalAlignment(SwingConstants.CENTER);

		iconMenuFrame.add(header);
		iconMenuFrame.add(iconMenuPanel);
		iconMenuPanel.add(defaultButton);
		iconMenuPanel.add(workIconsButton);
		iconMenuPanel.add(okButton);

		defaultButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				iconSelection = 1;
			}
		});

		workIconsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				iconSelection = 2;
			}
		});

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				optionsMenuFrame.setVisible(true);
				iconMenuFrame.dispose();
			}
		});
		iconMenuFrame.setVisible(true);
	}

	public void createLoginMenu(String userName) {
		playOnlineFrame.dispose();

		// Declarations
		JLabel header;
		JButton viewButton;
		JButton logoutButton;
		JScrollPane scrollPane;

		// Initialization
		header = new JLabel("Welcome " + userName + "!");
		loginMenuFrame = new JFrame("Welcome Page");
		loginMenuPanel = new JPanel();
		onlineUsersPanel = new JPanel();
		viewButton = new JButton("View Online Users");
		logoutButton = new JButton("Logout");
		scrollPane = new JScrollPane(onlineUsersPanel);

		// Member variables
		header.setHorizontalAlignment(SwingConstants.CENTER);
		loginMenuFrame.setSize(WIDTH, HEIGHT);
		loginMenuFrame.setLayout(new GridLayout(3, 1));
		loginMenuPanel.setLayout(new FlowLayout());
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		// Add variables
		loginMenuFrame.add(header);
		loginMenuFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		loginMenuFrame.add(loginMenuPanel);
		loginMenuPanel.add(logoutButton);
		loginMenuPanel.add(viewButton);

		loginMenuFrame.setVisible(true);

		// Button methods
		logoutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connector.logout();
			}
		});

		viewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connector.viewUsers();
			}
		});
	}

	public void displayUsers(Vector<String> users) {
		// ITerate over vector and add to onlineUsersPanel
		onlineUsersPanel.setLayout(new BoxLayout(onlineUsersPanel, BoxLayout.Y_AXIS));
		onlineUsersPanel.removeAll();
		userPanels.clear();
		for (int i = 0; i < users.size(); i++) {
			userPanels.add(new JPanel(new FlowLayout()));
			JLabel username = new JLabel(users.get(i));
			JButton inviteButton = new JButton("invite");

			userPanels.get(i).add(username);
			userPanels.get(i).add(inviteButton);

			onlineUsersPanel.add(userPanels.get(i));

			inviteButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					createWaitMenu(username.getText());
					connector.inviteUser(username.getText());
				}
			});

		}
		loginMenuFrame.setVisible(false);
		loginMenuFrame.setVisible(true);
	}

	private void createOnlinePlayMenu() {
		// Declarations
		TextField userName;
		JPasswordField userPassword;
		JLabel header;
		JLabel userNameLabel;
		JLabel userPasswordLabel;
		JPanel userNamePanel;
		JPanel userPasswordPanel;
		JPanel buttonsPanel;
		JButton okButton;
		JButton backButton;
		JButton signUpButton;

		// Initializators
		playOnlineFrame = new JFrame("Online Play");
		playOnlinePanel = new JPanel();
		header = new JLabel("Online Play Menu");
		userNameLabel = new JLabel("User Name: ");
		userName = new TextField(10);
		userPassword = new JPasswordField(10);
		userPasswordLabel = new JLabel("Password");
		userNamePanel = new JPanel();
		userPasswordPanel = new JPanel();
		buttonsPanel = new JPanel();
		okButton = new JButton("OK");
		backButton = new JButton("Back");
		signUpButton = new JButton("New Users");

		// Member variables
		playOnlineFrame.setSize(WIDTH, HEIGHT);
		playOnlineFrame.setLayout(new GridLayout(2, 1));
		playOnlinePanel.setLayout(new GridLayout(3, 1));
		userNamePanel.setLayout(new FlowLayout());
		userPasswordPanel.setLayout(new FlowLayout());
		buttonsPanel.setLayout(new FlowLayout());
		header.setHorizontalAlignment(SwingConstants.CENTER);
		// userPassword.setEchoChar('*');

		// Add variables
		playOnlineFrame.add(header);
		playOnlineFrame.add(playOnlinePanel);
		userNamePanel.add(userNameLabel);
		userNamePanel.add(userName);
		userPasswordPanel.add(userPasswordLabel);
		userPasswordPanel.add(userPassword);
		buttonsPanel.add(backButton);
		buttonsPanel.add(okButton);
		buttonsPanel.add(signUpButton);
		playOnlinePanel.add(userNamePanel);
		playOnlinePanel.add(userPasswordPanel);
		playOnlinePanel.add(buttonsPanel);

		// Display Menu
		startMenu.setVisible(false);
		playOnlineFrame.setVisible(true);

		// Button Functions
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String passwordString = new String(userPassword.getPassword());
				sendUserInfo(userName.getText(), passwordString);
			}
		});

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startMenu.setVisible(true);
				playOnlineFrame.dispose();

			}
		});

		signUpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newUserMenu();
			}
		});
	}

	private void sendUserInfo(String userName, String userPassword) {
		if (userName.equals("") || userPassword.equals("")) {
			flagError(0);
			return;
		}

		connector.sendUserInfo(userName, userPassword);
	}

	private void newUserMenu() {
		playOnlineFrame.setVisible(false);

		// Declartions
		JPanel newUserNamePanel;
		JPanel newPasswordPanel;
		JPanel confirmPasswordPanel;
		JPanel buttonPanel;
		JLabel header;
		JLabel userNameLabel;
		JLabel passwordLabel;
		JLabel confirmPasswordLabel;
		JTextField userNameInput;
		JPasswordField passwordInput;
		JPasswordField confirmPasswordField;
		JButton backButton;
		JButton okButton;

		// Initializiation
		// FRAMES
		newUserFrame = new JFrame("User Registration");
		// PANELS
		newUserPanel = new JPanel();
		newUserNamePanel = new JPanel();
		newPasswordPanel = new JPanel();
		confirmPasswordPanel = new JPanel();
		buttonPanel = new JPanel();
		// LABELS
		userNameLabel = new JLabel("Username: ");
		passwordLabel = new JLabel("New Password: ");
		confirmPasswordLabel = new JLabel("Confirm Password: ");
		header = new JLabel("User Registration");
		// TEXTFIELDS
		userNameInput = new JTextField(10);
		passwordInput = new JPasswordField(10);
		confirmPasswordField = new JPasswordField(10);
		// BUTTONS
		backButton = new JButton("Back");
		okButton = new JButton("OK");

		// Member variabels
		newUserFrame.setSize(WIDTH, HEIGHT);
		newUserFrame.setLayout(new GridLayout(2, 1));
		header.setHorizontalAlignment(SwingConstants.CENTER);
		newUserPanel.setLayout(new GridLayout(4, 1));
		newUserNamePanel.setLayout(new FlowLayout());
		newPasswordPanel.setLayout(new FlowLayout());
		buttonPanel.setLayout(new FlowLayout());
		confirmPasswordPanel.setLayout(new FlowLayout());

		// Add variables
		newUserFrame.add(header);
		newUserFrame.add(newUserPanel);
		newUserPanel.add(newUserNamePanel);
		newUserPanel.add(newPasswordPanel);
		newUserPanel.add(confirmPasswordPanel);
		newUserPanel.add(buttonPanel);

		newUserNamePanel.add(userNameLabel);
		newUserNamePanel.add(userNameInput);

		newPasswordPanel.add(passwordLabel);
		newPasswordPanel.add(passwordInput);

		confirmPasswordPanel.add(confirmPasswordLabel);
		confirmPasswordPanel.add(confirmPasswordField);

		buttonPanel.add(backButton);
		buttonPanel.add(okButton);

		// Set visible
		newUserFrame.setVisible(true);

		// Button functionality
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createNewUser(userNameInput.getText(), new String(passwordInput.getPassword()),
						new String(confirmPasswordField.getPassword()));
			}
		});

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playOnlineFrame.setVisible(true);
				newUserFrame.dispose();
			}
		});

	}

	// Later use regex to confirm length + special characters
	private void createNewUser(String userName, String password, String confirmPassword) {
		// Fields unpopulated
		if (userName.equals("") || password.equals("")) {
			flagError(0);
			return;
		}
		// Password must equal confirm passwords
		if (!password.equals(confirmPassword)) {
			flagError(3);
			return;
		}

		connector.sendNewUser(userName, password);
	}

	public void flagError(int errorCode) {
		String errorText = "ERROR: ";
		switch (errorCode) {
			case 0:
				errorText += "All fields must be populated.";
				break;
			case 1:
				errorText += "Unknown opcode in client class.";
				break;
			case 2:
				errorText += "Incorrect username or password.";
				break;
			case 3:
				errorText += "Passwords do not match.";
				break;
			case 4:
				errorText += "Unable to create new user. Try again later.";
				break;
			case 5:
				errorText += "No online users.";
				break;
			default:
				errorText += "An unknown error has occured.";
				break;
		}

		JLabel errorLabel = new JLabel(errorText);
		JFrame errorFrame = new JFrame("Error");
		JPanel errorPanel = new JPanel();
		JButton okButton = new JButton("OK");

		errorFrame.setSize(WIDTH - 100, HEIGHT / 2);
		errorFrame.setLayout(new GridLayout(2, 1));
		errorPanel.setLayout(new FlowLayout());
		errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		errorFrame.add(errorLabel);
		errorFrame.add(errorPanel);
		errorPanel.add(okButton);

		errorFrame.setVisible(true);

		// Methods
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				errorFrame.dispose();

			}
		});

	}

	public void createSuccessMessage(int message) {
		String successText = "SUCCESS! ";
		switch (message) {
			case 0:
				successText += "User account creation successful.";
				break;
			case 1:
				successText += "You have successfully logged out.";
				break;
			default:
				break;
		}

		JFrame successFrame = new JFrame("Success");
		JLabel successLabel = new JLabel(successText);
		JPanel successPanel = new JPanel();
		JButton continueButton = new JButton("Continue");

		// Member variables
		successFrame.setSize(WIDTH - 100, HEIGHT / 2);
		successFrame.setLayout(new GridLayout(2, 1));
		successPanel.setLayout(new FlowLayout());
		successLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// Add
		successFrame.add(successLabel);
		successFrame.add(successPanel);
		successPanel.add(continueButton);

		// Set visible
		successFrame.setVisible(true);

		switch (message) {
			case 0:
				continueButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						playOnlineFrame.setVisible(true);
						newUserFrame.dispose();
						successFrame.dispose();
					}
				});
				break;
			case 1:
				continueButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						startMenu.setVisible(true);
						loginMenuFrame.dispose();
						successFrame.dispose();
					}
				});
				break;
		}
	}

	public void invitationMessage(String opponentName) {
		JFrame messageFrame;
		JPanel buttonPanel;
		JButton yesButton;
		JButton noButton;
		JLabel header;
		JLabel question;

		messageFrame = new JFrame("Invitation");
		header = new JLabel("You received an invitation from: " + opponentName);
		header.setHorizontalAlignment(SwingConstants.CENTER);
		question = new JLabel("Would you like to accept?");
		question.setHorizontalAlignment(SwingConstants.CENTER);
		yesButton = new JButton("YES");
		noButton = new JButton("NO");
		buttonPanel = new JPanel(new FlowLayout());

		messageFrame.setSize(WIDTH - 100, HEIGHT / 2);
		messageFrame.setLayout(new GridLayout(3, 1));
		messageFrame.add(header);
		messageFrame.add(question);
		buttonPanel.add(yesButton);
		buttonPanel.add(noButton);
		messageFrame.add(buttonPanel);

		messageFrame.setVisible(true);

		yesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connector.sendInviteResponse(true);
				messageFrame.dispose();
			}
		});
		noButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connector.sendInviteResponse(false);
				messageFrame.dispose();
			}
		});

	}

	private ImageIcon resizeIcon(ImageIcon icon) {
		int width = 55, height = 55;
		Image image = icon.getImage();

		BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = resizedImage.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g2d.drawImage(image, 0, 0, width, height, null);
		g2d.dispose();

		return new ImageIcon(resizedImage);
	}

	public void pawnPromotion(JPanel promotionSquare, String prevSquare) {
		Piece promotingPawn = (Piece) promotionSquare.getComponent(0);
		String fileExtension = promotingPawn.isWhite() ? "icons/white-" : "icons/black-";
		JPanel promotionPanel = new JPanel();
		promotionPanel.setLayout(new BoxLayout(promotionPanel, BoxLayout.Y_AXIS));
		promotionSquare.setLayout(null);

		// Add the buttons and icons
		JButton queenButton = new JButton(resizeIcon(new ImageIcon(fileExtension + "queen.png")));
		JButton rookButton = new JButton(resizeIcon(new ImageIcon(fileExtension + "rook.png")));
		JButton bishopButton = new JButton(resizeIcon(new ImageIcon(fileExtension + "bishop.png")));
		JButton knightButton = new JButton(resizeIcon(new ImageIcon(fileExtension + "knight.png")));

		promotionPanel.add(queenButton);
		promotionPanel.add(rookButton);
		promotionPanel.add(bishopButton);
		promotionPanel.add(knightButton);

		promotionSquare.remove(promotingPawn);

		JScrollPane scrollPane = new JScrollPane(promotionPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(0, 0, promotionSquare.getWidth(), promotionSquare.getHeight());
		promotionSquare.add(scrollPane);
		promotionSquare.repaint();

		// Button methods
		queenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handlePiece(1, promotingPawn, promotionSquare, prevSquare);
			}
		});

		rookButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handlePiece(2, promotingPawn, promotionSquare, prevSquare);
			}
		});

		knightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handlePiece(3, promotingPawn, promotionSquare, prevSquare);
			}
		});

		bishopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handlePiece(4, promotingPawn, promotionSquare, prevSquare);
			}
		});

	}

	private void handlePiece(int pieceNum, Piece promotingPawn, JPanel promotionSquare, String prevSquare) {
		Piece promotingPiece;
		promotionSquare.removeAll();
		switch (pieceNum) {
			// Queen
			case 1:
				promotingPiece = new Queen(promotingPawn.getStartPos(), promotingPawn.isWhite(), board);
				break;
			// Rook
			case 2:
				promotingPiece = new Rook(promotingPawn.getStartPos(), promotingPawn.isWhite(), board);
				break;
			// Knight
			case 3:
				promotingPiece = new Knight(promotingPawn.getStartPos(), promotingPawn.isWhite(), board);
				break;
			// Bishop
			case 4:
				promotingPiece = new Bishop(promotingPawn.getStartPos(), promotingPawn.isWhite(), board);
				break;
			default:
				promotingPiece = new Piece();
				break;
		}
		promotingPiece.setCurrPos(promotingPawn.getCurrPos());

		// Remove pawn from vector and add new piece
		board.getPieceVector(promotingPawn.isWhite()).remove(promotingPawn);
		board.getPieceVector(promotingPiece.isWhite()).add(promotingPiece);

		connector.sendPawnPromo(prevSquare + "" + promotionSquare.getName(), pieceNum);

		// Add new piece and repaint
		promotionSquare.setLayout(new FlowLayout());
		promotionSquare.add(promotingPiece);
		promotionSquare.revalidate();
		promotionSquare.repaint();
		
		
	}

	public void startGame(int userColor) {
		board = new ChessBoard(colorSelection, iconSelection, userColor, menu);
		board.createChessBoard();
	}

	// Menu that displays after a user invites another user.
	private void createWaitMenu(String username) {
		loginMenuFrame.setVisible(false);

		JButton cancelButton;
		JLabel header;

		waitMenuFrame = new JFrame("Wait Menu");
		waitMenuPanel = new JPanel(new FlowLayout());
		cancelButton = new JButton("Cancel");
		header = new JLabel("Waiting on " + username);

		waitMenuFrame.setSize(WIDTH - 100, HEIGHT / 2);
		header.setHorizontalAlignment(SwingConstants.CENTER);
		waitMenuFrame.setLayout(new GridLayout(2, 1));

		waitMenuFrame.add(header);
		waitMenuFrame.add(waitMenuPanel);
		waitMenuPanel.add(cancelButton);

		waitMenuFrame.setVisible(true);
	}
}