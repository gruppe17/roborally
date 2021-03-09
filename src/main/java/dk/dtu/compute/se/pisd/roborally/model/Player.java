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
import dk.dtu.compute.se.pisd.roborally.interfaces.ILaser;
import org.jetbrains.annotations.NotNull;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class Player extends Subject implements ILaser {
    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;
    final public PlayerController playerController;
    final public Board board;

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
    private CommandCardField[] cards;

    public Player(@NotNull Board board, String color, @NotNull String name) {
        playerController = new PlayerController(this);

        this.board = board;
        this.name = name;
        this.color = color;

        this.space = null;

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
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
     * <p>Sets the {@link #space} the player currently is on to the argument
     * and sets the player on the argument space to this player.
     * If the player previously was on a space then that space's player
     * is set to null.</p>
     *
     * @param space the space that this player should be on
     * @see Space#setPlayer(Player)
     */
    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
                distanceToPrioritySpace = board.getRectilinearDistanceToPrioritySpace(space);
            } else distanceToPrioritySpace = -1;
            notifyChange();
        }
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

    public CommandCardField getCardField(int i) {
        return cards[i];
    }


    /**
     * <p>Fire in the direction the owner
     * robot is facing. Their range has no
     * limit. Any robot in the line of sight is
     * shot. Robot lasers cannot fire through
     * walls or shoot more than one robot.</p>
     */
    @Override
    public void fire() {
        Space currentSpace = getSpace();
        Space lastSpace = currentSpace;

        /* Abort if player is not in a space*/
        if (lastSpace == null)
            return;

        while (true) {
            /* get next space */
            lastSpace = board.getNeighbour(lastSpace, getHeading());
            Player playerAtSpace = lastSpace.getPlayer();
            /* remember to add the right elements to prevent hitting walls and stuff here */
           /* if(lastSpace.element == WallBoardElement || lastSpace.element == PriorityAntennaBoardElement){
                break;
            }*/

            /* exit if nothing is hit */
            if (lastSpace == null) {
                break;
            }

            lastSpace.element.isOpaque = true;
            /* We should add fx to the spaces that are hit*/

            /* If player is hit, then damage it and do stuff. */
            if (playerAtSpace != null && playerAtSpace != this) {
                break;
            }
        }

        currentSpace = getSpace();
        lastSpace = currentSpace;

        while (true) {
            /* get next space */
            lastSpace = board.getNeighbour(lastSpace, getHeading());
            if (lastSpace == null) {
                break;
            }

            lastSpace.element.isOpaque = false;

        }
        /*Maybe do some cleanup of fx here?*/
    }

}