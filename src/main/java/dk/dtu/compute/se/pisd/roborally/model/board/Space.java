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
package dk.dtu.compute.se.pisd.roborally.model.board;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements.ActivationElement;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.BoardElement;

import java.util.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 *
 */
public class Space extends Subject {

    /**
     * <p>The board which this space is a part of.</p>
     */
    public final Board board;

    /**
     * <p>The x coordinate of the space.</p>
     */
    public final int x;
    /**
     * <p>The y coordinate of the space.</p>
     */
    public final int y;

    /**
     * <p>Represents the elements on this space.</p>
     */
    private final ArrayList<BoardElement> elements;

    /**
     * <p>A list of only the {@link ActivationElement}s on this space
     * sorted by priority.</p>
     *
     * @see #elements
     * @see ActivationElement#getPriority()
     */
    private final ArrayList<ActivationElement> activationElements;


    /**
     * <p>Represent the player currently on this space.
     * If no player is on this space it is null.</p>
     */
    private Player player;

    public Space(Board board, int x, int y, BoardElement... elements) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;

        this.elements = new ArrayList<>();
        activationElements = new ArrayList<>();

        if (elements == null) return;
        Collections.addAll(this.elements, elements);

        for (BoardElement element : elements) {
            if (element instanceof ActivationElement) activationElements.add((ActivationElement) element);
        }
        activationElements.sort(Comparator.comparingInt(ActivationElement::getPriority));
    }

    /**
     * <p>Returns an array of all {@link BoardElement}s on this
     * space. The returned array is safe; any changes to the
     * array will not be reflected in the {@link #elements}
     * of this space.</p>
     *
     * @return a safe array of all the board elements on this space
     */
    public BoardElement[] getElements() {
        if (elements == null || elements.size() < 1) return new BoardElement[0];
        return elements.toArray(new BoardElement[0]);
    }

    /**
     * <p>Adds a {@link BoardElement} to this space.</p>
     *
     * @param boardElement the board element to add
     */
    public void addBoardElement(BoardElement boardElement) {
        if (boardElement == null) return;
        elements.add(boardElement);
        if (boardElement instanceof ActivationElement) {
            activationElements.add((ActivationElement) boardElement);
            activationElements.sort(Comparator.comparingInt(ActivationElement::getPriority));
        }
        notifyChange();
    }

    /**
     * <p>Removes a {@link BoardElement} to this space.</p>
     *
     * @param boardElement the board element to remove
     */
    public void removeBoardElement(BoardElement boardElement) {
        if (boardElement == null || elements == null) return;
        elements.remove(boardElement);
        if (boardElement instanceof ActivationElement) activationElements.remove(boardElement);
        notifyChange();
    }

    /**
     * <p>Returns an array of all {@link ActivationElement}s on this
     * space sorted by priority. The returned array is safe; any changes
     * to the array will not be reflected in the {@link #activationElements}
     * of this space.</p>
     *
     * @return a safe array of all the activation elements on this space sorted by priority or null
     */
    public ActivationElement[] getActivationElements() {
        if (activationElements == null || activationElements.size() < 1) return new ActivationElement[0];
        return activationElements.toArray(new ActivationElement[0]);
    }

    /**
     * <p>Returns the player currently on this space.
     * If there is no player on this space returns null.</p>
     *
     * @return the player currently on this space
     */
    public Player getPlayer() {
        return player;
    }


    /**
     * <p>Sets the {@link #player} currently on this space to the argument
     * and, if the argument is not null, set the space of said player to this.
     * If a different player already is on this space then the space of the old
     * player is set to null.</p>
     *
     * @param player the player that should be on this space
     * @author Rasmus Nylander, s205418@student.dtu.dk
     * @see Player#setSpace(Space)
     */
    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player == oldPlayer) return;
        if (player != null && player.board != board) return;

        this.player = player;
        if (oldPlayer != null) {
            // this should actually not happen
            oldPlayer.setSpace(null);
        }
        if (player != null) {
            player.setSpace(this);
        }
        notifyChange();
    }


    /**
     * <p>Returns a boolean indicating whether <b>this</b> space contains
     * an obstacle preventing movement in the heading specified by the argument.</p>
     *
     * @param heading moving from this space the heading which should be checked
     * @return A boolean indicating whether this space prevents movement in the specified direction
     * @author Rasmus Nylander, s205418@student.dtu.dk
     * @see #containsObstacleFrom(Heading)
     */
    public boolean containsObstacleTo(Heading heading) {
        return containsObstacleFrom(heading.next().next());
    }

    /**
     * <p>Returns a boolean indicating whether <b>this</b> space contains
     * an obstacle preventing movement moving onto this space from the heading
     * specified by the argument.</p>
     *
     * @param heading moving to this space the heading which should be checked
     * @return A boolean indicating whether this space prevents
     * movement onto this space in the specified direction
     * @author Rasmus Nylander, s205418@student.dtu.dk
     * @see #containsObstacleTo(Heading)
     */
    public boolean containsObstacleFrom(Heading heading) {
        for (BoardElement boardElement : elements) {
            for (Heading direction : boardElement.getImpassableFrom()) {
                if (direction == heading) return true;
            }
        }
        return containsObstacleTo(heading.next().next());
    }

    public void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }
}
