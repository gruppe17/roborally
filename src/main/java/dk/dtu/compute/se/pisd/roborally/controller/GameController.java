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
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
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
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX: V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX: V2
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX: V2
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: V2
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    //Todo: please fix
    //This really should not be here
    private Player[] prioritySortedPlayers;
    private int prioritySortedPlayersIndex = 0;

    private void executeNextStep() {
        if (prioritySortedPlayers == null) { //If this whole thing was permanent it should be set in constructor.
            prioritySortedPlayers = board.getSortedPlayerArray();
            board.setCurrentPlayer(prioritySortedPlayers[prioritySortedPlayersIndex]);
        }
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
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
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    /**
     * <p>Handles what happens after a player instruction has been executed.</p>
     * <p>If the last player of the round has been activated then the players are sorted and {@link Board#step} is incremented. If also the entire activation is completed the programming phase is started.</p>
     * <p>No matter what, the next player is always set.</p>
     */
    private void subRoundComplete() {
        if (board.getPhase() != Phase.ACTIVATION) {
            assert false;
            return;
        }

        if (++prioritySortedPlayersIndex < prioritySortedPlayers.length) { //The round is not over
            board.setCurrentPlayer(prioritySortedPlayers[prioritySortedPlayersIndex]);
        } else { //The round is over
            int step = board.getStep() + 1;
            prioritySortedPlayersIndex = 0;
            prioritySortedPlayers = board.getSortedPlayerArray(); //If it was permanent: It would be better to sort the array directly, rather make a new array.
            board.setCurrentPlayer(prioritySortedPlayers[0]);
            if (step < Player.NO_REGISTERS) {
                makeProgramFieldsVisible(step);
                board.setStep(step);
            } else {
                startProgrammingPhase();
            }
        }
    }

    // XXX: V2

    /**
     *
     */
    /*
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }
*/
    // XXX: V2
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    /**
     * <p>Moves the player in the direction of their current heading by the specified distance</p>
     * <p>The distance wraps around the map</p>
     *
     * @param player   The player to move
     * @param distance The amount of spaces to move in the current direction
     */
    public void moveForward(@NotNull Player player, int distance) {
        if (player == null) return; //This should never happen, but we test for it anyway?

        Space currentSpace = player.getSpace();
        if (currentSpace != null) {
            for (int i = 0; i < distance; i++) {
                Space target = currentSpace.board.getNeighbour(currentSpace, player.getHeading());
                if (target != null && target.getPlayer() == null) {
                    currentSpace = target;
                } else {
                    break;
                }
            }
            player.setSpace(currentSpace); //identical to target.setPlayer(player);
        }
    }

    /**
     * <p>Moves the player forward by one</p>
     * <p>Identical to {@code moveForward(player, 1)}</p>
     *
     * @param player the player to move
     */
    public void moveForward(@NotNull Player player) {
        moveForward(player, 1);
    }

    /**
     * <p>Moves the player forward by two</p>
     * <p>Identical to {@code moveForward(player, 2)}</p>
     *
     * @param player The player to move
     */
    public void fastForward(@NotNull Player player) {
        moveForward(player, 2);
    }

    /**
     * Turns a player heading by π/4 * {@code numTimes}
     *
     * @param player   Player to turn
     * @param numTimes Number of times to turn right
     */
    public void turnRight(@NotNull Player player, int numTimes) {
        Heading heading = player.getHeading();
        for (int i = 0; i < numTimes; i++) {
            heading = heading.next();
        }
        player.setHeading(heading);
    }

    /**
     * <p>Turns player/robot by π/4</p>
     *
     * @param player The player to move
     */
    public void turnRight(@NotNull Player player) {
        turnRight(player, 1);
    }

    /**
     * <p>Turns player/robot by -π/4</p>
     *
     * @param player The player to move
     */
    public void turnLeft(@NotNull Player player) {
        player.setHeading(player.getHeading().prev());
        //turnRight(player,3);
    }


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
