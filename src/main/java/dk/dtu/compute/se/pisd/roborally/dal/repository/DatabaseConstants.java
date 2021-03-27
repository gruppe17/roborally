package dk.dtu.compute.se.pisd.roborally.dal.repository;

/**
 * <p>Collection of database constants used by the repository.</p>
 *
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
class DatabaseConstants {
	/*Tables*/
	static final String TABLE_GAME = "Game";
	static final String TABLE_PLAYER = "Player";
	static final String TABLE_ACTIVATION_QUEUE = "ActivationQueue";
	static final String TABLE_CARD = "Card";
	static final String TABLE_CARD_COMMAND = "CardCommand";

	/*Game*/
	static final String GAME_GAMEID = "gameID";
	static final String GAME_NAME = "name";
	static final String GAME_BOARD_NAME = "boardName";
	static final String GAME_CURRENTPLAYER = "currentPlayer";
	static final String GAME_PHASE = "phase";
	static final String GAME_STEP = "step";

	/*Player*/
	static final String PLAYER_PLAYERID = "playerID";
	static final String PLAYER_NAME = "name";
	static final String PLAYER_COLOUR = "colour";
	static final String PLAYER_GAMEID = GAME_GAMEID;
	static final String PLAYER_POSITION_X = "positionX";
	static final String PLAYER_POSITION_Y = "positionY";
	static final String PLAYER_HEADING = "heading";
	static final String PLAYER_ENERGY_CUBES = "energyCubes";

	/*Activation queue*/
	static final String ACTIVATION_QUEUE_GAMEID = GAME_GAMEID;
	static final String ACTIVATION_QUEUE_PLAYERID = PLAYER_PLAYERID;
	static final String ACTIVATION_QUEUE_PRIORITY = "priority";

	/*Card*/
	static final String CARD_CARDID = "cardID";
	static final String CARD_GAMEID = PLAYER_GAMEID;
	static final String CARD_PLAYERID = PLAYER_PLAYERID;
	static final String CARD_TYPE = "type";
	static final String CARD_POSITION = "position";

	/*Card command*/
	static final String CARD_COMMAND_CARD_ID = CARD_CARDID;
	static final String CARD_COMMAND = "command";

	//Should be enum?
	static final int CARD_TYPE_PROGRAM = 0;
	static final int CARD_TYPE_HAND = 1;
	static final int CARD_TYPE_DECK = 2;
	static final int CARD_TYPE_DISCARD = 3;
	static final int CARD_TYPE_UPGRADE = 4;

	/*SQL statements*/
	/**
	 * <p>The SQL command for inserting the commands
	 * associated with a specific card ín the database.</p>
	 */
	static final String SQL_INSERT_CARD_COMMAND_STATEMENT = "INSERT INTO " + TABLE_CARD_COMMAND + "(" + CARD_COMMAND_CARD_ID + ", " + CARD_COMMAND + ") VALUES(?, ?)";

	/**
	 * <p>The SQL command for selecting the cards
	 * associated with a specific player ín the database.</p>
	 */
	static final String SQL_SELECT_CARD_STATEMENT = "SELECT * FROM Card WHERE gameID = ? AND playerID = ? ORDER BY type, position ASC";

	/**
	 * <p>The SQL command for selecting the commands
	 * associated with a specific card ín the database.</p>
	 */
	static final String SQL_SELECT_CARD_COMMAND_STATEMENT = "SELECT * FROM CardCommand WHERE cardID = ?";

	/**
	 * <p>The SQL command for selecting the activation queue
	 * associated with a specific game ín the database.</p>
	 */
	static final String SQL_SELECT_ACTIVATION_QUEUE = "SELECT * FROM ActivationQueue WHERE gameID = ? ORDER BY priority ASC";
	static final String SQL_INSERT_GAME = "INSERT INTO Game(name, boardName, currentPlayer, phase, step) VALUES (?, ?, ?, ?, ?)";
	static final String SQL_SELECT_GAME = "SELECT * FROM Game WHERE gameID = ?";
	static final String SQL_SELECT_PLAYERS = "SELECT * FROM Player WHERE gameID = ?";
	static final String SQL_SELECT_PLAYERS_ASC = "SELECT * FROM Player WHERE gameID = ? ORDER BY playerID ASC";
	static final String SQL_SELECT_GAMES = "SELECT gameID, name FROM Game";
}