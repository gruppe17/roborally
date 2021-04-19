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
package dk.dtu.compute.se.pisd.roborally.dal.repository;

import dk.dtu.compute.se.pisd.roborally.dal.GameInDB;
import dk.dtu.compute.se.pisd.roborally.dal.IRepository;
import dk.dtu.compute.se.pisd.roborally.fileaccess.BoardLoader;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.enums.Command;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import dk.dtu.compute.se.pisd.roborally.model.enums.Phase;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
class Repository implements IRepository {
	private final PreparedStatements preparedStatements;
	Random random = new Random();

	private Connector connector;

	Repository(Connector connector) {
		this.connector = connector;
		preparedStatements = new PreparedStatements(connector);
	}

	/**
	 * <p>Creates a game in the database and returns a boolean indicating
	 * whether the creation was successful.</p>
	 *
	 * @param game the game to be created in the database
	 * @return true if the game was successfully created
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	@Override
	public boolean createGameInDB(@NotNull Game game, @NotNull String name) {
		if (game.getGameId() != null) {
			System.err.println("Game cannot be created in DB, since it has a game id already!");
			return false;
			//Todo: maybe it should just update it instead?
		}

		Connection connection = connector.getConnection();
		try {
			connection.setAutoCommit(false);
			PreparedStatement ps = preparedStatements.getInsertGameStatementRGK();
			// TODO: the name should eventually set by the user
			//       for the game and should be then used
			//       game.getName();
			ps.setString(1, name); // instead of name
			ps.setString(2, game.getBoard().getBoardName());
			ps.setNull(3, Types.TINYINT); // game.getPlayerNumber(game.getCurrentPlayer())); is inserted after players!
			ps.setInt(4, game.getPhase().ordinal());
			ps.setInt(5, game.getStep());

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

			ps = preparedStatements.getSelectGameStatementU();
			ps.setInt(1, game.getGameId());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rs.updateInt(DatabaseConstants.GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
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
	 *
	 * @param game the game to be updated in the database, may not be null
	 * @return
	 */
	@Override
	public boolean updateGameInDB(@NotNull Game game) {
		assert game.getGameId() != null;

		Connection connection = connector.getConnection();
		try {
			connection.setAutoCommit(false);

			PreparedStatement ps = preparedStatements.getSelectGameStatementU();
			ps.setInt(1, game.getGameId());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rs.updateInt(DatabaseConstants.GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
				rs.updateInt(DatabaseConstants.GAME_PHASE, game.getPhase().ordinal());
				rs.updateInt(DatabaseConstants.GAME_STEP, game.getStep());
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
			PreparedStatement ps = preparedStatements.getSelectGameStatementU();
			ps.setInt(1, id);

			ResultSet rs = ps.executeQuery();
			int playerNo = -1;
			if (rs.next()) {
				String boardName = rs.getString(DatabaseConstants.GAME_BOARD_NAME);
				game = new Game(BoardLoader.loadBoard(boardName));
				if (game == null) {
					return null;
				}
				playerNo = rs.getInt(DatabaseConstants.GAME_CURRENTPLAYER);
				// TODO currently we do not set the games name (needs to be added)
				game.setPhase(Phase.values()[rs.getInt(DatabaseConstants.GAME_PHASE)]);
				game.setStep(rs.getInt(DatabaseConstants.GAME_STEP));

			} else {
				// TODO error handling
				return null;
			}
			rs.close();

			game.setGameId(id);
			loadPlayersFromDB(game);
			loadActivationQueueFromDatabase(game);
			//todo: load store

			if (playerNo >= 0 && playerNo < game.getNumPlayers()) {
				game.setCurrentPlayer(game.getPlayer(playerNo));
			} else {
				// TODO  error handling
				return null;
			}

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
			PreparedStatement ps = preparedStatements.getSelectGameIdsStatement();
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(DatabaseConstants.GAME_GAMEID);
				String name = rs.getString(DatabaseConstants.GAME_NAME);
				result.add(new GameInDB(id, name));
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
		PreparedStatement ps = preparedStatements.getSelectPlayersStatementU();
		ps.setInt(1, game.getGameId());

		ResultSet rs = ps.executeQuery();
		for (int i = 0; i < game.getNumPlayers(); i++) {
			Player player = game.getPlayer(i);
			rs.moveToInsertRow();
			rs.updateInt(DatabaseConstants.PLAYER_GAMEID, game.getGameId());
			rs.updateInt(DatabaseConstants.PLAYER_PLAYERID, i);
			rs.updateString(DatabaseConstants.PLAYER_NAME, player.getName());
			rs.updateString(DatabaseConstants.PLAYER_COLOUR, player.getColor());
			rs.updateInt(DatabaseConstants.PLAYER_POSITION_X, player.getSpace().x);
			rs.updateInt(DatabaseConstants.PLAYER_POSITION_Y, player.getSpace().y);
			rs.updateInt(DatabaseConstants.PLAYER_HEADING, player.getDirection().ordinal());
			rs.updateInt(DatabaseConstants.PLAYER_ENERGY_CUBES, player.getEnergyCubes());
			rs.insertRow();
		}

		rs.close();
	}

	private void loadPlayersFromDB(Game game) throws SQLException {
		PreparedStatement ps = preparedStatements.getSelectPlayersASCStatement();
		ps.setInt(1, game.getGameId());

		ResultSet rs = ps.executeQuery();
		int i = 0;
		while (rs.next()) {
			int playerId = rs.getInt(DatabaseConstants.PLAYER_PLAYERID);
			if (i++ == playerId) {
				// TODO this should be more defensive
				String name = rs.getString(DatabaseConstants.PLAYER_NAME);
				String colour = rs.getString(DatabaseConstants.PLAYER_COLOUR);
				Player player = new Player(game, colour, name);
				game.addPlayer(player);

				int x = rs.getInt(DatabaseConstants.PLAYER_POSITION_X);
				int y = rs.getInt(DatabaseConstants.PLAYER_POSITION_Y);
				player.setSpace(game.getBoard().getSpace(x, y));
				int heading = rs.getInt(DatabaseConstants.PLAYER_HEADING);
				player.setDirection(Heading.values()[heading]);
				player.setEnergyCubes(rs.getInt(DatabaseConstants.PLAYER_ENERGY_CUBES));

				// TODO  should also load players program and hand here
			} else {
				// TODO error handling
				System.err.println("Game in DB does not have a player with id " + i + "!");
			}
		}
		rs.close();

		loadPlayerCardsFromDB(game.getPlayers());
	}

	private void updatePlayersInDB(Game game) throws SQLException {
		PreparedStatement ps = preparedStatements.getSelectPlayersStatementU();
		ps.setInt(1, game.getGameId());

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int playerId = rs.getInt(DatabaseConstants.PLAYER_PLAYERID);
			// TODO should be more defensive
			Player player = game.getPlayer(playerId);
			// rs.updateString(PLAYER_NAME, player.getName()); // not needed: player's names does not change
			rs.updateInt(DatabaseConstants.PLAYER_POSITION_X, player.getSpace().x);
			rs.updateInt(DatabaseConstants.PLAYER_POSITION_Y, player.getSpace().y);
			rs.updateInt(DatabaseConstants.PLAYER_HEADING, player.getDirection().ordinal());
			rs.updateInt(DatabaseConstants.PLAYER_ENERGY_CUBES, player.getEnergyCubes());
			// TODO error handling
			// TODO take care of case when number of players changes, etc
			rs.updateRow();
		}
		rs.close();

		// TODO error handling/consistency check: check whether all players were updated
	}

	/**
	 * <p>Creates all of the specified players' cards in the database</p>
	 *
	 * @param players a list of players whose cards are to be created in the database
	 * @throws SQLException
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void createPlayerCardsInDB(Player... players) throws SQLException {
		for (Player player : players) {
			createPlayerProgramInDatabase(player);
			createPlayerHandInDatabase(player);
			createPlayerDeckInDatabase(player);
			createPlayerDiscardInDatabase(player);
			//createPlayerUpgradeCardsInDatabase(player)
		}
	}

	/**
	 * <p>Creates the specified player' hand in the database.</p>
	 *
	 * @param player the player whose hand is to be created in the database
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void createPlayerHandInDatabase(Player player) throws SQLException {
		int gameID = player.game.getGameId(), playerID = player.game.getPlayerNumber(player);
		createCardsInDB(gameID, playerID, DatabaseConstants.CARD_TYPE_HAND, player.getHand());
	}

	/**
	 * <p>Creates the specified player' program in the database.</p>
	 *
	 * @param player the player whose program is to be created in the database
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void createPlayerProgramInDatabase(Player player) throws SQLException {
		int gameID = player.game.getGameId(), playerID = player.game.getPlayerNumber(player);
		createCardsInDB(gameID, playerID, DatabaseConstants.CARD_TYPE_PROGRAM, player.getProgram());
	}

	/**
	 * <p>Creates the specified player's discard pile in the database.</p>
	 * @param player the player whose discard pile is to be created in the database
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 * @throws SQLException
	 */
	private void createPlayerDiscardInDatabase(Player player) throws SQLException {
		int gameID = player.game.getGameId(), playerID = player.game.getPlayerNumber(player);
		createCardsInDB(gameID, playerID, DatabaseConstants.CARD_TYPE_DISCARD, player.getDeck().toArray(new CommandCard[0]));
	}

	/**
	 * <p>Creates the specified player's deck in the database.</p>
	 * @param player the player whose deck is to be created in the database
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 * @throws SQLException
	 */
	private void createPlayerDeckInDatabase(Player player) throws SQLException {
		int gameID = player.game.getGameId(), playerID = player.game.getPlayerNumber(player);
		createCardsInDB(gameID, playerID, DatabaseConstants.CARD_TYPE_DECK, player.getDeck().toArray(new CommandCard[0]));
	}

	/**
	 * <p>Create a list of {@link CommandCard}s in the database
	 * belonging to the {@link CommandCardField} a specified player's
	 * specified deck. I.e. their program, hand, discard pile,
	 * program deck, and so on</p>
	 *
	 * @param gameID   the ID of the game that the player belongs to
	 * @param playerID the ID of the player whom the deck belongs to
	 * @param cardType the type of the deck
	 * @param cCFields the deck that is to be created in the database
	 * @throws SQLException
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void createCardsInDB(int gameID, int playerID, int cardType, CommandCardField[] cCFields) throws SQLException {
		CommandCard[] commandCards = new CommandCard[cCFields.length];
		for (int i = 0; i < commandCards.length; i++) {
			commandCards[i] = cCFields[i].getCard();
		}
		createCardsInDB(gameID, playerID, cardType, commandCards);
	}

	/**
	 * <p>Create a list of {@link CommandCard}s in the database
	 * belonging to a specified player's specified deck. I.e. their
	 * program, hand, discard pile, program deck, and so on</p>
	 *
	 * @param gameID   the ID of the game that the player belongs to
	 * @param playerID the ID of the player whom the deck belongs to
	 * @param cardType the type of the deck
	 * @param cCards the deck that is to be created in the database
	 * @throws SQLException
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void createCardsInDB(int gameID, int playerID, int cardType, @NotNull CommandCard[] cCards) throws SQLException {
		PreparedStatement preparedStatement = preparedStatements.getSelectCardStatementUpdatable();
		preparedStatement.setInt(1, gameID);
		preparedStatement.setInt(2, playerID);
		ResultSet resultSetCards = preparedStatement.executeQuery();

		for (int i = 0; i < cCards.length; i++) {
			if (cCards[i] == null) continue;
			long cardID;
			while (true) {
				cardID = random.nextLong();
				try {
					resultSetCards.moveToInsertRow();
					resultSetCards.updateLong(DatabaseConstants.CARD_CARDID, cardID);
					resultSetCards.updateInt(DatabaseConstants.CARD_GAMEID, gameID);
					resultSetCards.updateInt(DatabaseConstants.CARD_PLAYERID, playerID);
					resultSetCards.updateInt(DatabaseConstants.CARD_TYPE, cardType);
					resultSetCards.updateInt(DatabaseConstants.CARD_POSITION, i);
					resultSetCards.insertRow();
					break;
				} catch (SQLException e) {
					if (e.getErrorCode() != 1062) throw e; //1062 == duplicate entry
				}
			}
			createCardCommandsInDatabase(cardID, cCards[i]);
		}
	}

	/**
	 * <p>Saves the {@link Command}s of a {@link CommandCard} in the database.</p>
	 *
	 * @param cardID    the primary key of the card in the database
	 * @param cCard     the card whose commands are to be saved in the database
	 * @throws SQLException
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void createCardCommandsInDatabase(long cardID, CommandCard cCard) throws SQLException {
		PreparedStatement ps = preparedStatements.getInsertCardCommandStatement();
		ps.setLong(1, cardID);
/*
		List<Command> commands = cCard.command.getOptions();
		if (commands.isEmpty()) commands = Collections.singletonList(cCard.command);
		for (Command command : commands) {
			ps.setInt(2, command.ordinal());
			ps.execute();
		}
 */
		ps.setInt(2, cCard.command.ordinal());
		ps.execute();
	}

	/**
	 * <p>Deletes the cards of the specified players in the database</p>
	 *
	 * @param players a list of players whose cards are to be deleted from the database
	 * @throws SQLException
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void deletePlayerCardsInDB(Player... players) throws SQLException {
		PreparedStatement preparedStatement = preparedStatements.getSelectCardStatementUpdatable();
		for (Player player : players) {
			preparedStatement.setInt(1, player.game.getGameId());
			preparedStatement.setInt(2, player.game.getPlayerNumber(player));
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				resultSet.deleteRow();
			}
		}
	}

	/**
	 * <p>Updates the specified players' cards in the database.</p>
	 *
	 * @param players a list of players whose cards are to be updated in the database
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void updatePlayerCardsInDB(Player... players) throws SQLException {
		deletePlayerCardsInDB(players);
		createPlayerCardsInDB(players);
	}


	/**
	 * <p>Reads cards from database and assigns them to the right
	 * positions in all decks of all specified players.</p>
	 *
	 * @param players the players whose cards are to be read from the database
	 * @throws SQLException
	 * @author Tobias Nyholm Maneschijn, s205422@student.dtu.dk
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void loadPlayerCardsFromDB(Player... players) throws SQLException {
		PreparedStatement ps = preparedStatements.getSelectCardStatementUpdatable();

		for (Player player : players) {
			ps.setInt(1, player.game.getGameId());
			ps.setInt(2, player.game.getPlayerNumber(player));
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				long cardId = rs.getLong(DatabaseConstants.CARD_CARDID);
				int type = rs.getInt(DatabaseConstants.CARD_TYPE);
				int position = rs.getInt(DatabaseConstants.CARD_POSITION);

				Command command = loadPlayerCardCommandFromDB(cardId);
				CommandCard card = new CommandCard(command);
				switch (type) {
					case DatabaseConstants.CARD_TYPE_PROGRAM:
						player.getProgramField(position).setCard(card);
						break;
					case DatabaseConstants.CARD_TYPE_HAND:
						player.getHandField(position).setCard(card);
						break;
					case DatabaseConstants.CARD_TYPE_DECK:
						player.playerController.addCardToDeck(card);
						break;
					case DatabaseConstants.CARD_TYPE_DISCARD:
						player.playerController.addCardToDiscardPile(card);
						break;
					case DatabaseConstants.CARD_TYPE_UPGRADE:
						break;
				}

			}

		}
	}

	/**
	 * <p> Reads from database a command from assigned to the specific cardId </p>
	 * @param cardID
	 * @return
	 * @throws SQLException
	 * @author Tobias Nyholm Maneschijn, s205422@student.dtu.dk
	 */
	private Command loadPlayerCardCommandFromDB(long cardID) throws SQLException {
		PreparedStatement ps = preparedStatements.getSelectCardCommandStatement();

		ps.setLong(1, cardID);
		ResultSet rs = ps.executeQuery();

		// Get first row
		rs.next();
		int commandId = rs.getInt(DatabaseConstants.CARD_COMMAND);

		return Command.values()[commandId];


	}

	/**
	 * <p>Loads the activation queue from the database
	 * into the game. The players should be loaded first!</p>
	 *
	 * @param game the game whose activation queue is to be loaded
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void loadActivationQueueFromDatabase(Game game) throws SQLException {
		PreparedStatement preparedStatement = preparedStatements.getSelectActivationQueueStatementUpdatable();
		preparedStatement.setInt(1, game.getGameId());
		ResultSet resultSet = preparedStatement.executeQuery();

		PriorityQueue<Player> activationQueue = new PriorityQueue<>(Comparator.comparingInt(Player::getDistanceToPrioritySpace));
		while (resultSet.next()) {
			activationQueue.add(game.getPlayer(resultSet.getInt(DatabaseConstants.ACTIVATION_QUEUE_PLAYERID)));
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
	 *
	 * @param game the game which activation queue should be deleted
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void deleteActivationQueueInDB(Game game) throws SQLException {
		PreparedStatement preparedStatement = preparedStatements.getSelectActivationQueueStatementUpdatable();
		preparedStatement.setInt(1, game.getGameId());
		ResultSet resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			resultSet.deleteRow();
		}
		resultSet.close();
	}

	/**
	 * <p>Create the activation queue of a {@link Game} in the database.</p>
	 *
	 * @param game the game which activation queue should be created in the database
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void createActivationQueueInDB(Game game) throws SQLException {
		PreparedStatement preparedStatement = preparedStatements.getSelectActivationQueueStatementUpdatable();
		preparedStatement.setInt(1, game.getGameId());
		ResultSet resultSet = preparedStatement.executeQuery();
		Player[] activationQueue = game.getPlayerActivationQueue();
		for (int i = 0; i < activationQueue.length; i++) {
			resultSet.moveToInsertRow();
			resultSet.updateInt(DatabaseConstants.ACTIVATION_QUEUE_GAMEID, game.getGameId());
			resultSet.updateInt(DatabaseConstants.ACTIVATION_QUEUE_PLAYERID, game.getPlayerNumber(activationQueue[i]));
			resultSet.updateInt(DatabaseConstants.ACTIVATION_QUEUE_PRIORITY, i);
			resultSet.insertRow();
		}
		resultSet.close();
	}
}
