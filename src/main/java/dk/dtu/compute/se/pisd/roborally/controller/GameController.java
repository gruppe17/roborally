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

import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.board.Board;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements.ActivationElement;
import dk.dtu.compute.se.pisd.roborally.model.enums.Command;
import dk.dtu.compute.se.pisd.roborally.model.enums.Phase;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    // XXX: V2
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX: V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX: V2
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.playerQueueForceRepopulate();
        board.setCurrentPlayer(board.nextPlayer());
        board.setStep(0);
    }

    /**
     * <p>Makes the specified program field visible.</p>
     * @param register the index of the program field to make visible
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void makeProgramFieldVisible(int register) {
        if (register < 0 || register >= Player.NO_REGISTERS) return;

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            board.getPlayer(i).getProgramField(register).setVisible(true);
        }
    }

    /**
     * <p>Makes all the program fields invisible.</p>
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                player.getProgramField(j).setVisible(false);
            }
        }
    }



    /**
     * <p>Runs the entirety of the players' programs.</p>
     *
     * @see #executeStep()
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    /**
     * <p>Runs the next instruction of the next player's program.</p>
     *
     * @see #executePrograms()
     */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    /**
     * <p>Continues, or starts, the execution of the players' programs in
     * accordance with {@link Board#isStepMode()}.</p>
     *
     * <p>If {@link Board#isStepMode()} is true {@link #executeStep()} is
     * called only once. Otherwise, it is called until the activation phase
     * is over.</p>
     */
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
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
        Player currentPlayer = board.getCurrentPlayer();
        int step = board.getStep();

        if (board.getPhase() != Phase.ACTIVATION || currentPlayer == null) { // this should not happen
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
                board.setPhase(Phase.PLAYER_INTERACTION);
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
     * @see Board#getStep()
     * @see #activateElements()
     */
    private void subRoundComplete() {
        if (board.getPhase() != Phase.ACTIVATION) {
            assert false;
            return;
        }

        if (!board.isPlayerActivationQueueEmpty()) { //Not all players have been activated yet
            board.setCurrentPlayer(board.nextPlayer());
            return;
        }

        activateElements();
        int step = board.getStep() + 1;
        board.playerQueueForceRepopulate();
        board.setCurrentPlayer(board.nextPlayer());

        if (step < Player.NO_REGISTERS) { //The activation phase is not complete
            makeProgramFieldVisible(step);
            board.setStep(step);
            return;
        }

        startProgrammingPhase();
    }

    /**
     * <p>Handles activation of all {@link ActivationElement}s and robot lasers.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void activateElements(){
        /*
        PriorityQueue<IActivateable> priorityQueue = new PriorityQueue<>(6, Comparator.comparingInt(e -> {
            if (e instanceof ActivationElement) {
                return ((ActivationElement) e).getPriority();
            } else return 6;//if (e instanceof RobotLaser){return RobotLaser.getPriority();}
        }));
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            ActivationElement[] activationElements = board.getPlayer(i).getSpace().getActivationElements();
            if (activationElements != null && activationElements.length > 0)
                priorityQueue.addAll(Arrays.asList(activationElements));
        }
        priorityQueue.forEach(IActivateable::activate);
         */

    }

    /**
     * <p>Executes a {@link Command} on a given player</p>
     *
     * @param player  The player the command should be applied to
     * @param command The command to execute
     * @see #executeCommandAndContinue(Command)
     */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
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
     * execution of players' programs respecting {@link Board#isStepMode}.</p>
     * <p>This is different from {@link #executeCommand} which simply returns.</p>
     *
     * @param command the command which is to be executed
     * @author Rasmus Nylander, s205418@student.dtu.dk
     * @see #executeCommand(Player, Command)
     */
    public void executeCommandAndContinue(@NotNull Command command) {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() != Phase.PLAYER_INTERACTION || currentPlayer == null) {
            assert false;
            return;
        }
        board.setPhase(Phase.ACTIVATION);

        executeCommand(currentPlayer, command);
        subRoundComplete();
        if (!board.isStepMode()) continuePrograms();
    }

    /**
     * <p>Moves a {@link CommandCard} from one {@link CommandCardField} to another, if it is not already occupied.
     * Returns true if the move was successful, false if it was not.</p>
     *
     * @param source the command card field which card it to be moved
     * @param target the command card field which is to be moved to
     * @return a boolean indicating if the move was successful
     */
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

}
