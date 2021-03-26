/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.dal;

import dk.dtu.compute.se.pisd.roborally.fileaccess.BoardLoader;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.enums.Command;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import dk.dtu.compute.se.pisd.roborally.model.enums.Phase;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
class Repository implements IRepository {

	private static final String GAME_GAMEID = "gameID";

	private static final String GAME_NAME = "name";

	private static final String GAME_CURRENTPLAYER = "currentPlayer";

	private static final String GAME_PHASE = "phase";

	private static final String GAME_STEP = "step";

	private static final String PLAYER_PLAYERID = "playerID";

	private static final String PLAYER_NAME = "name";

	private static final String PLAYER_COLOUR = "colour";

	private static final String PLAYER_GAMEID = "gameID";

	private static final String PLAYER_POSITION_X = "positionX";

	private static final String PLAYER_POSITION_Y = "positionY";

	private static final String PLAYER_HEADING = "heading";

	private static final String ACTIVATION_QUEUE_GAMEID = "gameID";

	private static final String ACTIVATION_QUEUE_PLAYERID = "playerID";

	private static final String ACTIVATION_QUEUE_PRIORITY = "priority";

	private static final String CARD_TYPE = "type";

	private static final String CARD_POSITION = "position";



	//Should be enum?
	private static final int CARD_TYPE_PROGRAM = 0;
	private static final int CARD_TYPE_HAND = 1;
	private static final int CARD_TYPE_DECK = 2;
	private static final int CARD_TYPE_DISCARD = 3;
	private static final int CARD_TYPE_UPGRADE = 4;



	private Connector connector;

	Repository(Connector connector){
		this.connector = connector;
	}

	/**
	 * <p>Creates a game in the database and returns a boolean indicating
	 * whether the creation was successful.</p>
	 * @param game the game to be created in the database
	 * @return true if the game was successfully created
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	@Override
	public boolean createGameInDB(@NotNull Game game) {
		if (game.getGameId() != null) {
			System.err.println("Game cannot be created in DB, since it has a game id already!");
			return false;
			//Todo: maybe it should just update it instead?
		}

		Connection connection = connector.getConnection();
		try {
			connection.setAutoCommit(false);
			PreparedStatement ps = getInsertGameStatementRGK();
			// TODO: the name should eventually set by the user
			//       for the game and should be then used
			//       game.getName();
			ps.setString(1, "Date: " + new Date()); // instead of name
			ps.setNull(2, Types.TINYINT); // game.getPlayerNumber(game.getCurrentPlayer())); is inserted after players!
			ps.setInt(3, game.getPhase().ordinal());
			ps.setInt(4, game.getStep());

			// If you have a foreign key constraint for current players,
			// the check would need to be temporarily disabled, since
			// MySQL does not have a per transaction validation, but
			// validates on a per row basis.
			// Statement statement = connection.createStatement();
			// statement.execute("SET foreign_key_checks = 0");

			int affectedRows = ps.executeUpdate();
			ResultSet generatedKeys = ps.getGeneratedKeys();
			if (affectedRows == 1 && generatedKeys.next()) {
				game.setGameId(generatedKeys.getInt(1));
			}
			generatedKeys.close();

			// Enable foreign key constraint check again:
			// statement.execute("SET foreign_key_checks = 1");
			// statement.close();

			createPlayersInDB(game);
			createPlayerCardsInDB(game.getPlayers());

			// since player is a foreign key, activation queue can only be
			// created now, since MySQL does not have a per transaction validation,
			// but validates on a per row basis.
			createActivationQueueInDB(game);

			ps = getSelectGameStatementU();
			ps.setInt(1, game.getGameId());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rs.updateInt(GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
				rs.updateRow();
			} else {
				// TODO error handling
			}
			rs.close();

			connection.commit();
			connection.setAutoCommit(true);
			return true;
		} catch (SQLException e) {
			// TODO error handling
			e.printStackTrace();
			System.err.println("Some DB error");
			try {
				connection.rollback();
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				// TODO error handling
				e1.printStackTrace();
			}
		}
		return false;
	}



	/**
	 * <p>Updates a game in the database and returns a boolean indicating
	 * whether the operation was successful.</p>
	 * @param game the game to be updated in the database, may not be null
	 * @return
	 */
	@Override
	public boolean updateGameInDB(@NotNull Game game) {
		assert game.getGameId() != null;

		Connection connection = connector.getConnection();
		try {
			connection.setAutoCommit(false);

			PreparedStatement ps = getSelectGameStatementU();
			ps.setInt(1, game.getGameId());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rs.updateInt(GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
				rs.updateInt(GAME_PHASE, game.getPhase().ordinal());
				rs.updateInt(GAME_STEP, game.getStep());
				rs.updateRow();
			} else {
				// TODO error handling
			}
			rs.close();

			updatePlayersInDB(game);
			updateActivationQueueInDB(game);
			//TODO this method needs to be implemented
			updatePlayerCardsInDB(game.getPlayers());


            connection.commit();
            connection.setAutoCommit(true);
			return true;
		} catch (SQLException e) {
			// TODO error handling
			e.printStackTrace();
			System.err.println("Some DB error");

			try {
				connection.rollback();
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				// TODO error handling
				e1.printStackTrace();
			}
		}

		return false;
	}

	@Override
	public Game loadGameFromDB(int id) {
		Game game;
		try {
			// TODO here, we could actually use a simpler statement
			//      which is not updatable, but reuse the one from
			//      above for the pupose
			PreparedStatement ps = getSelectGameStatementU();
			ps.setInt(1, id);

			ResultSet rs = ps.executeQuery();
			int playerNo = -1;
			if (rs.next()) {
				// TODO the width and height could eventually come from the database
				// int width = AppController.BOARD_WIDTH;
				// int height = AppController.BOARD_HEIGHT;
				// game = new Board(width,height);
				// TODO and we should also store the used game board in the database
				//      for now, we use the default game board
				game = new Game(BoardLoader.loadBoard("test3"));
				if (game == null) {
					return null;
				}
				playerNo = rs.getInt(GAME_CURRENTPLAYER);
				// TODO currently we do not set the games name (needs to be added)
				game.setPhase(Phase.values()[rs.getInt(GAME_PHASE)]);
				game.setStep(rs.getInt(GAME_STEP));

			} else {
				// TODO error handling
				return null;
			}
			rs.close();

			game.setGameId(id);
			loadPlayersFromDB(game);
			loadActivationQueueFromDatabase(game);

			if (playerNo >= 0 && playerNo < game.getNumPlayers()) {
				game.setCurrentPlayer(game.getPlayer(playerNo));
			} else {
				// TODO  error handling
				return null;
			}

			/* TOODO this method needs to be implemented first
			loadCardFieldsFromDB(game);
			*/

			return game;
		} catch (SQLException e) {
			// TODO error handling
			e.printStackTrace();
			System.err.println("Some DB error");
		}
		return null;
	}


	@Override
	public List<GameInDB> getGames() {
		// TODO when there many games in the DB, fetching all available games
		//      from the DB is a bit extreme; eventually there should a
		//      methods that can filter the returned games in order to
		//      reduce the number of the returned games.
		List<GameInDB> result = new ArrayList<>();
		try {
			PreparedStatement ps = getSelectGameIdsStatement();
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(GAME_GAMEID);
				String name = rs.getString(GAME_NAME);
				result.add(new GameInDB(id,name));
			}
			rs.close();
		} catch (SQLException e) {
			// TODO proper error handling
			e.printStackTrace();
		}
		return result;
	}

	private void createPlayersInDB(Game game) throws SQLException {
		// TODO code should be more defensive
		PreparedStatement ps = getSelectPlayersStatementU();
		ps.setInt(1, game.getGameId());

		ResultSet rs = ps.executeQuery();
		for (int i = 0; i < game.getNumPlayers(); i++) {
			Player player = game.getPlayer(i);
			rs.moveToInsertRow();
			rs.updateInt(PLAYER_GAMEID, game.getGameId());
			rs.updateInt(PLAYER_PLAYERID, i);
			rs.updateString(PLAYER_NAME, player.getName());
			rs.updateString(PLAYER_COLOUR, player.getColor());
			rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
			rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
			rs.updateInt(PLAYER_HEADING, player.getHeading().ordinal());
			rs.insertRow();
		}

		rs.close();
	}

	private void loadPlayersFromDB(Game game) throws SQLException {
		PreparedStatement ps = getSelectPlayersASCStatement();
		ps.setInt(1, game.getGameId());

		ResultSet rs = ps.executeQuery();
		int i = 0;
		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			if (i++ == playerId) {
				// TODO this should be more defensive
				String name = rs.getString(PLAYER_NAME);
				String colour = rs.getString(PLAYER_COLOUR);
				Player player = new Player(game, colour ,name);
				game.addPlayer(player);

				int x = rs.getInt(PLAYER_POSITION_X);
				int y = rs.getInt(PLAYER_POSITION_Y);
				player.setSpace(game.getBoard().getSpace(x,y));
				int heading = rs.getInt(PLAYER_HEADING);
				player.setHeading(Heading.values()[heading]);

				// TODO  should also load players program and hand here
			} else {
				// TODO error handling
				System.err.println("Game in DB does not have a player with id " + i +"!");
			}
		}
		rs.close();
	}

	private void updatePlayersInDB(Game game) throws SQLException {
		PreparedStatement ps = getSelectPlayersStatementU();
		ps.setInt(1, game.getGameId());

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			// TODO should be more defensive
			Player player = game.getPlayer(playerId);
			// rs.updateString(PLAYER_NAME, player.getName()); // not needed: player's names does not change
			rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
			rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
			rs.updateInt(PLAYER_HEADING, player.getHeading().ordinal());
			// TODO error handling
			// TODO take care of case when number of players changes, etc
			rs.updateRow();
		}
		rs.close();

		// TODO error handling/consistency check: check whether all players were updated
	}

	/**
	 * <p>Creates all of the specified players' cards in the database</p>
	 * @param players a list of players whose cards are to be created in the database
	 * @throws SQLException
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void createPlayerCardsInDB(Player... players) throws SQLException {
		for (Player player: players) {
			createPlayerProgramInDatabase(player);
			createPlayerHandInDatabase(player);
			//createPlayerDeckInDatabase(player);
			//createPlayerDiscardInDatabase(player); //?
			//createPlayerUpgradeCardsInDatabase(player)
		}
	}

	/**
	 * <p>Creates the specified player' hand in the database.</p>
	 * @param player the player whose hand is to be created in the database
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void createPlayerHandInDatabase(Player player) throws SQLException {
		int gameID = player.game.getGameId(), playerID = player.game.getPlayerNumber(player);
		createCardsInDB(gameID, playerID, CARD_TYPE_HAND, player.getHand());
	}

	/**
	 * <p>Creates the specified player' program in the database.</p>
	 * @param player the player whose program is to be created in the database
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void createPlayerProgramInDatabase(Player player) throws SQLException {
		int gameID = player.game.getGameId(), playerID = player.game.getPlayerNumber(player);
		createCardsInDB(gameID, playerID, CARD_TYPE_PROGRAM, player.getProgram());
	}

	/**
	 * <p>Create a list of {@link CommandCardField}s in the database
	 * belonging to a specified player's specified deck. I.e. their
	 * program, hand, discard pile, program deck, and so on</p>
	 * @param gameID the ID of the game that the player belongs to
	 * @param playerID the ID of the player whom the deck belongs to
	 * @param cardType the type of the deck
	 * @param cCFields the deck that is to be created in the database
	 * @throws SQLException
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void createCardsInDB(int gameID, int playerID, int cardType, CommandCardField[] cCFields) throws SQLException {
		PreparedStatement preparedStatement = getSelectCardStatementUpdatable();
		preparedStatement.setInt(1, gameID);
		preparedStatement.setInt(2, playerID);
		ResultSet resultSetCards = preparedStatement.executeQuery();

		for (int i = 0; i < cCFields.length; i++) {
			CommandCardField cCardField = cCFields[i];
			if (cCardField.getCard() == null) continue;

			resultSetCards.moveToInsertRow();
			resultSetCards.updateInt(PLAYER_GAMEID, gameID);
			resultSetCards.updateInt(PLAYER_PLAYERID, playerID);
			resultSetCards.updateInt(CARD_TYPE, cardType);
			resultSetCards.updateInt(CARD_POSITION, i);
			resultSetCards.updateRow();

			createCardCommandsInDatabase(gameID, playerID, cardType, i, cCardField);

		}


	}

	/**
	 * <p>Saves the {@link Command}s of a {@link CommandCard} in the database.</p>
	 * @param gameID part of primary key of cCardField in database
	 * @param playerID part of primary key of cCardField in database
	 * @param cardType part of primary key of cCardField in database
	 * @param position part of primary key of cCardField in database
	 * @param cCardField the card field of the card whose commands are to be saved in the database
	 * @throws SQLException
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void createCardCommandsInDatabase(int gameID, int playerID, int cardType, int position, CommandCardField cCardField) throws SQLException {
		PreparedStatement ps = getSelectCardCommandStatementUpdatable();
		ps.setInt(1, gameID);
		ps.setInt(2, playerID);
		ps.setInt(3, cardType);
		ps.setInt(4, position);
		ResultSet resultSetCommands = ps.executeQuery();
		List<Command> commands = cCardField.getCard().command.getOptions();
		if (commands.isEmpty()) commands.add(cCardField.getCard().command);
		for (Command command: commands) {
			resultSetCommands.moveToInsertRow();
			resultSetCommands.updateInt(PLAYER_GAMEID, gameID);
			resultSetCommands.updateInt(PLAYER_PLAYERID, playerID);
			resultSetCommands.updateInt(CARD_TYPE, cardType);
			resultSetCommands.updateInt(CARD_POSITION, position);
			resultSetCommands.updateInt(CARD_COMMAND, command.ordinal());
		}
	}


	
	

	/**
	 *
	 * @param players
	 */
	private void updatePlayerCardsInDB(Player... players) {

	}


	private PreparedStatement getSelectCardStatementUpdatable() {

	}

	private PreparedStatement getSelectCardCommandStatementUpdatable() {

	}

	/**
	 * <p>Loads the activation queue from the database
	 * into the game. The players should be loaded first!</p>
	 * @param game the game whose activation queue is to be loaded
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void loadActivationQueueFromDatabase(Game game) throws SQLException {
		PreparedStatement preparedStatement = getSelectActivationQueueStatementUpdatable();
		preparedStatement.setInt(1, game.getGameId());
		ResultSet resultSet = preparedStatement.executeQuery();

		PriorityQueue<Player> activationQueue = new PriorityQueue<>();
		while (resultSet.next()){
			activationQueue.add(game.getPlayer(resultSet.getInt(PLAYER_PLAYERID)));
		}
		game.setPlayerActivationQueue(activationQueue);
		resultSet.close();
	}

	/**
	 * <p>Update the activation queue of a {@link Game} stored in the database.</p>
	 * <p>This done by deleting the activation queue and then recreating it.
	 * This is done because it is not possible to simply update the priority
	 * of the players, as not necessarily all players were in the activation queue
	 * before, and some players in the activation queue when it was stored may
	 * not be in it any longer.</p>
	 *
	 * @param game the game which activation queue should be updated in the database
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void updateActivationQueueInDB(Game game) throws SQLException {
		deleteActivationQueueInDB(game);
		createActivationQueueInDB(game);
	}



	/**
	 * <p>Deletes the activation queue of a {@link Game} in the database.</p>
	 * @param game the game which activation queue should be deleted
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void deleteActivationQueueInDB(Game game) throws SQLException {
		PreparedStatement preparedStatement = getSelectActivationQueueStatementUpdatable();
		preparedStatement.setInt(1, game.getGameId());
		ResultSet resultSet = preparedStatement.executeQuery();
		while (resultSet.next()){
			resultSet.deleteRow();
		}
		resultSet.close();
	}

	/**
	 * <p>Create the activation queue of a {@link Game} in the database.</p>
	 * <p><b>NOTICE:</b> this destroys the playerActivationQueue of the game.
	 * If the game is to be continued, then it must be recreated. This should
	 * <b>NOT</b> be done be calling {@link Game#playerQueueForceRepopulate()}
	 * as this will not take into account any players who have already been
	 * activated, nor whether any players have been pushed around.</p>
	 * @param game the game which activation queue should be created in the database
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void createActivationQueueInDB(Game game) throws SQLException {
		PreparedStatement preparedStatement = getSelectActivationQueueStatementUpdatable();
		preparedStatement.setInt(1, game.getGameId());
		ResultSet resultSet = preparedStatement.executeQuery();
		//todo: this changes the Game. If the game is to be continued, then it must be reloaded.
		// Something should be done to deal with that.
		Player player;
		int i = 0;
		while ((player = game.nextPlayer()) != null){
			resultSet.moveToInsertRow();
			resultSet.updateInt(ACTIVATION_QUEUE_GAMEID, game.getGameId());
			resultSet.updateInt(ACTIVATION_QUEUE_PLAYERID, game.getPlayerNumber(player));
			resultSet.updateInt(ACTIVATION_QUEUE_PRIORITY, i);
			resultSet.insertRow();
			i++;
		}
		resultSet.close();
	}

	/**
	 * <p>The SQL command for selecting the activation queue
	 * associated with a specific game ín the database.</p>
	 */
	private static final String SQL_SELECT_ACTIVATION_QUEUE = "SELECT * FROM ActivationQueue WHERE gameID = ? ORDER BY priority ASC";

	/**
	 * <p>The prepared statement for getting and updating the
	 * activation queue associated with a given gameID. Setting
	 * parameter 1, will set the gameID.</p>
	 * @see #getSelectActivationQueueStatementUpdatable()
	 */
	private PreparedStatement selectActivationQueueStatement = null;

	/**
	 * <p>Initializes, if not already initialized, and returns the
	 * {@link #selectActivationQueueStatement}. Executing the
	 * statement will return an updatable {@link ResultSet}.</p>
	 * @return 	the prepared statement for getting and updating
	 * 			the player activation associated with a given gameID
	 *
	 * @author 	Rasmus Nylander, s205418@student.dtu.dk
	 * @see 	#SQL_SELECT_ACTIVATION_QUEUE
	 * @see 	#selectActivationQueueStatement
	 */
	private PreparedStatement getSelectActivationQueueStatementUpdatable() {
		if (selectActivationQueueStatement != null) return selectActivationQueueStatement;
		Connection connection = connector.getConnection();
		try {
			selectActivationQueueStatement = connection.prepareStatement(SQL_SELECT_ACTIVATION_QUEUE,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return selectActivationQueueStatement;
	}


	private static final String SQL_INSERT_GAME =
			"INSERT INTO Game(name, currentPlayer, phase, step) VALUES (?, ?, ?, ?)";

	private PreparedStatement insert_game_stmt = null;

	private PreparedStatement getInsertGameStatementRGK() {
		if (insert_game_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				insert_game_stmt = connection.prepareStatement(
						SQL_INSERT_GAME,
						Statement.RETURN_GENERATED_KEYS);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return insert_game_stmt;
	}

	private static final String SQL_SELECT_GAME =
			"SELECT * FROM Game WHERE gameID = ?";
	
	private PreparedStatement select_game_stmt = null;
	
	private PreparedStatement getSelectGameStatementU() {
		if (select_game_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_game_stmt = connection.prepareStatement(
						SQL_SELECT_GAME,
						ResultSet.TYPE_FORWARD_ONLY,
					    ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_game_stmt;
	}
		
	private static final String SQL_SELECT_PLAYERS =
			"SELECT * FROM Player WHERE gameID = ?";

	private PreparedStatement select_players_stmt = null;

	private PreparedStatement getSelectPlayersStatementU() {
		if (select_players_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_players_stmt = connection.prepareStatement(
						SQL_SELECT_PLAYERS,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_players_stmt;
	}

	private static final String SQL_SELECT_PLAYERS_ASC =
			"SELECT * FROM Player WHERE gameID = ? ORDER BY playerID ASC";
	
	private PreparedStatement select_players_asc_stmt = null;
	
	private PreparedStatement getSelectPlayersASCStatement() {
		if (select_players_asc_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				// This statement does not need to be updatable
				select_players_asc_stmt = connection.prepareStatement(
						SQL_SELECT_PLAYERS_ASC);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_players_asc_stmt;
	}
	
	private static final String SQL_SELECT_GAMES =
			"SELECT gameID, name FROM Game";
	
	private PreparedStatement select_games_stmt = null;
	
	private PreparedStatement getSelectGameIdsStatement() {
		if (select_games_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_games_stmt = connection.prepareStatement(
						SQL_SELECT_GAMES);
			} catch (SQLException e) {
				// TODO error handling
				e.printStackTrace();
			}
		}
		return select_games_stmt;
	}



}
