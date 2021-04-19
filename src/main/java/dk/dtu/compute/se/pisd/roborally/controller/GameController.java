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

import dk.dtu.compute.se.pisd.roborally.interfaces.IActivateable;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.model.board.Board;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElement.ActivationElement;
import dk.dtu.compute.se.pisd.roborally.model.enums.Command;
import dk.dtu.compute.se.pisd.roborally.model.enums.Phase;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * <p>The controller for a {@link Game}.</p>
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class GameController {

	final public Game game;



	final AppController appController;

	public GameController(@NotNull Game game, AppController appController) {
		this.game = game;
		this.appController = appController;
	}

	/**
	 * <p>Starts the programming phase.</p>
	 *
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 * @author Tobias Maneschijn, s205422@student.dtu.dk
	 * @author Oscar
	 */
	public void startProgrammingPhase() {
		game.setPhase(Phase.PROGRAMMING);
		game.setCurrentPlayer(game.getPlayer(0));
		game.setStep(0);

		for (Player player : game.getPlayers()) {
			if (player == null) continue;
			player.playerController.discardProgram();
			player.playerController.discardHand();
			player.playerController.fillHand();
		}

		setProgramFieldsVisibility(true);

		setHandFieldsVisibility(true);
	}

	/**
	 * <p>Finishes the programming phase and starts
	 * the activation phase.</p>
	 *
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 * @author Tobias Maneschijn, s205422@student.dtu.dk
	 * @author Oscar
	 */
	public void finishProgrammingPhase() {
		setProgramFieldsVisibility(false);
		makeProgramFieldVisible(0);
		game.setPhase(Phase.ACTIVATION);
		game.playerQueueForceRepopulate();
		game.setCurrentPlayer(game.nextPlayer());
		game.setStep(0);
	}

	/**
	 * <p>Makes the specified program field visible.</p>
	 *
	 * @param register the index of the program field to make visible
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void makeProgramFieldVisible(int register) {
		if (register < 0 || register >= Player.NO_REGISTERS) return;
		for (Player player : game.getPlayers()) {
			player.getProgramField(register).setVisible(true);
		}
	}

	/**
	 * <p>Makes all the program fields invisible.</p>
	 *
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 * @see #setProgramFieldsVisibility(boolean)
	 * @deprecated
	 */
	private void makeProgramFieldsInvisible() {
		setProgramFieldsVisibility(false);
	}

	/**
	 * <p>Sets the visibility of all the program fields.</p>
	 *
	 * @param visible true if the program fields should be visible
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void setProgramFieldsVisibility(boolean visible) {
		for (Player player : game.getPlayers()) {
			if (player == null) continue;
			for (CommandCardField cCField : player.getProgram()) {
				cCField.setVisible(visible);
			}
		}
	}

	/**
	 * <p>Sets the visibility of all the players' hands.</p>
	 *
	 * @param visible whether the hand fields should be visible
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void setHandFieldsVisibility(boolean visible) {
		for (Player player : game.getPlayers()) {
			if (player == null) continue;
			for (CommandCardField cCField : player.getHand()) {
				cCField.setVisible(visible);
			}
		}
	}

	/**
	 * <p>Runs the entirety of the players' programs.</p>
	 *
	 * @see #executeStep()
	 */
	public void executePrograms() {
		game.setStepMode(false);
		continuePrograms();
	}

	/**
	 * <p>Runs the next instruction of the next player's program.</p>
	 *
	 * @see #executePrograms()
	 */
	public void executeStep() {
		game.setStepMode(true);
		continuePrograms();
	}

	/**
	 * <p>Continues, or starts, the execution of the players' programs in
	 * accordance with {@link Game#isStepMode()}.</p>
	 *
	 * <p>If {@link Game#isStepMode()} is true {@link #executeStep()} is
	 * called only once. Otherwise, it is called until the activation phase
	 * is over.</p>
	 */
	private void continuePrograms() {
		do {
			executeNextStep();
		} while (game.getPhase() == Phase.ACTIVATION && !game.isStepMode());
	}

	/**
	 * <p>Executes the next step of the next player's program
	 * and calls {@link #subRoundComplete()} unless the command is
	 * interactive in which case the phase is set to {@link Phase#PLAYER_INTERACTION}
	 * and the method simply returns.</p>
	 *
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void executeNextStep() {
		Player currentPlayer = game.getCurrentPlayer();
		int step = game.getStep();

		if (game.getPhase() != Phase.ACTIVATION || currentPlayer == null) { // this should not happen
			assert false;
			return;
		}
		if (step < 0 || step >= Player.NO_REGISTERS) {
			assert false;
			return;
		}

		CommandCard card = currentPlayer.getProgramField(step).getCard();
		if (card != null) {
			Command command = card.command;
			if (command.isInteractive()) {
				game.setPhase(Phase.PLAYER_INTERACTION);
				return;
			}
			executeCommand(currentPlayer, command);
		}

		subRoundComplete();
	}


	/**
	 * <p>Handles what happens after a player instruction has been executed.</p>
	 * <p>If the last player of the round has been activated then the {@link ActivationElement}s
	 * are activated, the players are sorted and register № is incremented. If also the entire
	 * activation phase is completed the programming phase is started.
	 * No matter what, the next player is always set.</p>
	 *
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 * @see Game#getStep()
	 * @see #activateElements()
	 */
	private void subRoundComplete() {
		if (game.getPhase() != Phase.ACTIVATION) {
			assert false;
			return;
		}

		if (!game.isPlayerActivationQueueEmpty()) { //Not all players have been activated yet
			game.setCurrentPlayer(game.nextPlayer());
			return;
		}

		activateElements(); //All players have been activated, activate the board elements
		int step = game.getStep() + 1;
		game.playerQueueForceRepopulate();
		game.setCurrentPlayer(game.nextPlayer());

		if (step < Player.NO_REGISTERS) { //The activation phase is not complete
			makeProgramFieldVisible(step);
			game.setStep(step);
			return;
		}

		startProgrammingPhase();
	}

	/**
	 * <p>Handles activation of all {@link ActivationElement}s and robot lasers.</p>
	 *
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void activateElements() {
		//todo: This is very inefficient and should be done much better.
		// For starters it should not loop over the entire board; only the
		// spaces with players or lasers (which would hit something) will
		// be activated.
		// Maybe this should be done with some kind of event system?

		//todo: Currently does not activate all elements of same priority at once
		// fx a robot can be moved by a conveyor belt pushing another robot
		// of another conveyor belt element (of same priority). In reality
		// what should happen is that they should both be moved by their
		// conveyor belts simultaneously and not interact at all.
		Board board = game.getBoard();

		PriorityQueue<IActivateable> elementActivationQueue = new PriorityQueue<>();
		for (int x = 0; x < board.width; x++) {
			for (int y = 0; y < board.height; y++) {
				IActivateable[] activateables = board.getSpace(x, y).getActivationElementControllers();
				elementActivationQueue.addAll(Arrays.asList(activateables));
			}
		}
		for (Player player: game.getPlayers()) {
			Laser roboLaser = player.getLaser();
			if (roboLaser == null) continue;
			elementActivationQueue.add(roboLaser);
		}

		while (!elementActivationQueue.isEmpty()){
			elementActivationQueue.remove().activate();
		}

	}

	/**
	 * <p>Executes a {@link Command} on a given player</p>
	 *
	 * @param player  The player the command should be applied to
	 * @param command The command to execute
	 * @see #executeCommandAndContinue(Command)
	 */
	private void executeCommand(@NotNull Player player, Command command) {
		if (player != null && player.game == game && command != null) {
			// XXX This is a very simplistic way of dealing with some basic cards and
			//     their execution. This should eventually be done in a more elegant way
			//     (this concerns the way cards are modelled as well as the way they are executed).

			switch (command) {
				case FORWARD:
					player.playerController.moveForward();
					break;
				case RIGHT:
					player.playerController.turn();
					break;
				case LEFT:
					player.playerController.turnLeft();
					break;
				case FAST_FORWARD:
					player.playerController.fastForward();
					break;
				default:
					// DO NOTHING (for now)
			}
		}
	}


	/**
	 * <p>Executes a command on the current player and continues
	 * execution of players' programs respecting {@link Game#isStepMode}.</p>
	 * <p>This is different from {@link #executeCommand} which simply returns.</p>
	 *
	 * @param command the command which is to be executed
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 * @see #executeCommand(Player, Command)
	 */
	public void executeCommandAndContinue(@NotNull Command command) {
		Player currentPlayer = game.getCurrentPlayer();
		if (game.getPhase() != Phase.PLAYER_INTERACTION || currentPlayer == null) {
			assert false;
			return;
		}
		game.setPhase(Phase.ACTIVATION);

		executeCommand(currentPlayer, command);
		subRoundComplete();
		if (!game.isStepMode()) continuePrograms();
	}

	/**
	 * <p>Moves a {@link CommandCard} from one {@link CommandCardField} to another, if it is not already occupied.
	 * Returns true if the move was successful, false if it was not.</p>
	 *
	 * @param source the command card field which card it to be moved
	 * @param target the command card field which is to be moved to
	 * @return a boolean indicating if the move was successful
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
		CommandCard sourceCard = source.getCard();
		CommandCard targetCard = target.getCard();
		if (sourceCard == null || targetCard != null) {
			return false;
		}

		target.setCard(sourceCard);
		source.setCard(null);
		return true;
	}

	/**
	 * A method called when no corresponding controller operation is implemented yet. This
	 * should eventually be removed.
	 */
	public void notImplemented() {
		// XXX just for now to indicate that the actual method is not yet implemented
		assert false;
	}

	public AppController getAppController() {
		return appController;
	}

}
