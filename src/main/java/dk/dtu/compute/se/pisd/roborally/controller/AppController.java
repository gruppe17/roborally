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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.RoboRally;

import dk.dtu.compute.se.pisd.roborally.dal.GameInDB;
import dk.dtu.compute.se.pisd.roborally.dal.repository.RepositoryAccess;
import dk.dtu.compute.se.pisd.roborally.fileaccess.BoardLoader;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.model.board.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.model.enums.Command;
import dk.dtu.compute.se.pisd.roborally.model.enums.Phase;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class AppController implements Observer {

	final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
	final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

	final private RoboRally roboRally;

	private GameController gameController;

	public AppController(@NotNull RoboRally roboRally) {
		this.roboRally = roboRally;
	}

	public void newGame() {
		ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
		dialog.setTitle("New game");
		dialog.setHeaderText("Select number of players");
		Optional<Integer> result = dialog.showAndWait();

		if (!result.isPresent()) return;

		// The UI should not allow this, but in case this happens anyway.
		// give the user the option to save the game or abort this operation!
		if (gameController != null && !stopGame()) return;

		Game game = new Game(getBoardFromUsers());
		//BoardLoader.saveBoard(game.getBoard(), "test");
		gameController = new GameController(game, this);

		initPlayers(game, result.get());
		chooseRobots(game);
		gameController.startProgrammingPhase();
		roboRally.createGameView(gameController);
	}

	/**
	 * <p>Returns a board chosen by the players.</p>
	 * @return a board chosen by the players
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private Board getBoardFromUsers(){
		//Todo: implement this method
		return BoardLoader.loadBoard(null);
	}

	/**
	 * <p>Initialises the specified number of players in
	 * the specified game.</p>
	 * @param game the game whose players are to be initialised
	 * @param numberOfPlayers the number of players to initialise
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void initPlayers(Game game, int numberOfPlayers) {
		//Todo: this method should maybe be in game
		for (int i = 0; i < numberOfPlayers; i++) {
			Player player = new Player(game, PLAYER_COLORS.get(i), "Player " + (i + 1));
			game.addPlayer(player);
			//Cards are added to the discard pile so that when the player
			// attempts to draw a card,the cards will be *shuffled* into the deck.
			player.playerController.addCardsToDiscardPile(getPlayerDeck());
			player.setSpace(game.getBoard().getSpace(i % game.getBoard().width, i));
		}
	}

	/**
	 * <p>Makes each player in the specified game who does
	 * not already have a robot choose one and set the
	 * corresponding {@link Player}'s robot accordingly.</p>
	 * @param game the game whose player's are to choose their robots
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void chooseRobots(Game game){
		//todo: get players' choices
		//todo: assign each player their choice
	}

	/**
	 * ...
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private LinkedList<CommandCard> getPlayerDeck(){
		//Todo: Read from file
		CommandCard[] commandCards = {
				new CommandCard(Command.LEFT), new CommandCard(Command.LEFT),
				new CommandCard(Command.RIGHT), new CommandCard(Command.RIGHT),
				new CommandCard(Command.OPTION_LEFT_RIGHT), new CommandCard(Command.OPTION_LEFT_RIGHT),
				new CommandCard(Command.UTURN),
				new CommandCard(Command.FORWARD), new CommandCard(Command.FORWARD), new CommandCard(Command.FORWARD),
				new CommandCard(Command.FORWARD), new CommandCard(Command.FORWARD),
				new CommandCard(Command.FAST_FORWARD), new CommandCard(Command.FAST_FORWARD), new CommandCard(Command.FAST_FORWARD),
				new CommandCard(Command.MOVE3),
				new CommandCard(Command.BACKUP),
				new CommandCard(Command.REPEAT), new CommandCard(Command.REPEAT),
				new CommandCard(Command.ENERGISE)
		};
		LinkedList<CommandCard> deck = new LinkedList<>();
		deck.addAll(Arrays.asList(commandCards));

		return deck;
	}

	/**
	 * <p>Saves the game of {@link #gameController}.</p>
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	public void saveGame() {
		if (gameController == null) return;

		// todo: If possible maybe ask player whether to overwrite the old save

		if (gameController.game.getGameId() != null) {
			RepositoryAccess.getRepository().updateGameInDB(gameController.game);
			return;
		}

		String name = getSaveName();
		if (name == null) return;

		RepositoryAccess.getRepository().createGameInDB(gameController.game, name);
	}

	/**
	 * <p>Asks the user to name the savefile and
	 * returns their non-blank answer or null if
	 * they cancel the operation.</p>
	 *
	 * @return a non-blank string or null
	 * @see String#isBlank()
	 */
	@Nullable
	private String getSaveName() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Save game");
		dialog.setHeaderText("Name:");
		//Todo: Game should remember name. If the chosen name is the
		// same as the name of the game, i.e. if a game would be stored
		// with the same name and ID, then overwrite the save (after
		// asking permission from the player), otherwise create a new
		// save (with a new gameID).
		//dialog.getEditor().setText();
		Optional<String> name;
		while (true){
			name = dialog.showAndWait();
			if (!name.isPresent()) return null;
			if (name.get().isBlank()) {
				//todo: inform the user of their wrongdoing!
				continue;
			}
			break;
		}
		return name.get();
	}

	/**
	 * Asks the users to chose a game from the database and loads it
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	public void loadGame() {
		if (gameController != null) return;
		List<GameInDB> gameInDBList = RepositoryAccess.getRepository().getGames();
		if (gameInDBList == null || gameInDBList.isEmpty()) return;
		//MenuItem[] games = new MenuItem[gameInDBs.length];
		String[] names = new String[gameInDBList.size()];
		int i = 0;
		for (GameInDB gameInDB: gameInDBList) {
			names[i] = (i + 1) + ". " + gameInDB.name;
			i++;
		}

		ChoiceDialog<String> dialog = new ChoiceDialog<>(names[0], names);
		dialog.setTitle("Load game");
		dialog.setHeaderText("Choose a game:");
		Optional<String> game = dialog.showAndWait();
		if (!game.isPresent()) return;
		String choice = game.get();
		choice = choice.substring(0, choice.indexOf('.'));

		gameController = new GameController(RepositoryAccess.getRepository().loadGameFromDB(gameInDBList.get(Integer.parseInt(choice)-1).id), this);
		gameController.updateCardFieldVisibility();
		roboRally.createGameView(gameController);
	}

	/**
	 * Stop playing the current game, giving the user the option to save
	 * the game or to cancel stopping the game. The method returns true
	 * if the game was successfully stopped (with or without saving the
	 * game); returns false, if the current game was not stopped. In case
	 * there is no current game, false is returned.
	 *
	 * @return true if the current game was stopped, false otherwise
	 */
	public boolean stopGame() {
		if (gameController == null) return false;

		// here we save the game (without asking the user).

		if(gameController.game.getPhase() != Phase.GAME_FINISHED) saveGame();

		gameController = null;

		roboRally.createGameView(null);

		return true;
	}

	public void exit() {
		if (gameController != null) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Exit RoboRally?");
			alert.setContentText("Are you sure you want to exit RoboRally?");
			Optional<ButtonType> result = alert.showAndWait();

			if (!result.isPresent() || result.get() != ButtonType.OK) {
				return; // return without exiting the application
			}
		}

		// If the user did not cancel, the RoboRally application will exit
		// after the option to save the game
		if (gameController == null || stopGame()) {
			Platform.exit();
		}
	}

	public boolean isGameRunning() {
		return gameController != null;
	}


	@Override
	public void update(Subject subject) {
		// XXX do nothing for now
	}

}
