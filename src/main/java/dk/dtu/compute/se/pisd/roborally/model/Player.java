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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.PlayerController;
import dk.dtu.compute.se.pisd.roborally.model.board.Board;
import dk.dtu.compute.se.pisd.roborally.model.board.Game;
import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import org.jetbrains.annotations.NotNull;

import static dk.dtu.compute.se.pisd.roborally.model.enums.Heading.SOUTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class Player extends Subject {
    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;
    final public PlayerController playerController;
    final public Game game;

    private String name;
    private String color;

    /**
     * The space the player currently is on.
     */
    private Space space;

    /**
     * The distance to the priority space.
     *
     * @see Board#getPrioritySpace()
     * @see Board#getRectilinearDistanceToPrioritySpace(Space)
     */
    private int distanceToPrioritySpace;
    private Heading heading = SOUTH;

    private CommandCardField[] program;
    private CommandCardField[] hand;

    public Player(@NotNull Game game, String color, @NotNull String name) {
        playerController = new PlayerController(this);

        this.game = game;
        this.name = name;
        this.color = color;

        this.space = null;

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        hand = new CommandCardField[NO_CARDS];
        for (int i = 0; i < hand.length; i++) {
            hand[i] = new CommandCardField(this);
        }
    }

    public String getName() {
        return name;
    }

    /**
     * <p>Sets the name of the player.</p>
     * @param name a string containing the name of the player
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    public void setName(@NotNull String name) {
        if (name == null || name.equals(this.name)) return;
        this.name = name;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    /**
     * <p>Returns the space the player currently is on.
     * This can be null, in which case the player is not on the board.</p>
     *
     * @return the space the player currently is on
     */
    public Space getSpace() {
        return space;
    }

    /**
     * <p>Sets the {@link #space} the player currently is on to the specified
     * {@link Space} and sets the player on the specified {@link Space} to this
     * player. If the player previously was on a space then that space's player
     * is set to null.</p>
     *
     * @param space the space that this player should be on
     * @author Rasmus Nylander, s205418@student.dtu.dk
     * @see Space#setPlayer(Player)
     */
    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space == oldSpace) return;
        if (space != null && space.board != game.getBoard()) return;

        this.space = space;
        if (oldSpace != null) {
            oldSpace.setPlayer(null);
        }
        if (space != null) {
            space.setPlayer(this);
            distanceToPrioritySpace = game.getBoard().getRectilinearDistanceToPrioritySpace(space);
        } else distanceToPrioritySpace = -1;
        notifyChange();
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * <p>Returns the rectilinear distance to the priority antenna</p>
     *
     * @return an int representing the rectilinear distance to the priority antenna
     * @see #distanceToPrioritySpace
     */
    public int getDistanceToPrioritySpace() {
        return distanceToPrioritySpace;
    }

    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    /**
     * <p>Returns the player's current program.</p>
     * @return a CommandCardField array containing the player's current program
     */
    public CommandCardField[] getProgram(){
        return program;
    }

    public CommandCardField getHandField(int i) {
        return hand[i];
    }

    /**
     * <p>Returns the player's current hand.</p>
     * @return a CommandCardField array containing the player's current hand
     */
    public CommandCardField[] getHand(){
        return hand;
    }

    /**
     * <p>Get an empty CommandCardField in players deck.</p>
     *
     * @return first empty card field or null if there isn't any.
     * @author Tobias Maneschijn, s205422@student.dtu.dk
     */
    public CommandCardField getEmptyCardField() {
        for (CommandCardField cardField : hand) {
            if (cardField.getCard() == null) {
                return cardField;
            }
        }
        return null;
    }
}