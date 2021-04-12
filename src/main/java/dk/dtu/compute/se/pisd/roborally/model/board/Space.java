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
import dk.dtu.compute.se.pisd.roborally.controller.boardElementController.IBoardElementController;
import dk.dtu.compute.se.pisd.roborally.interfaces.IActivateable;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements.ActivationElement;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.BoardElement;
import org.jetbrains.annotations.NotNull;

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
	 * <p>A list of the {@link IBoardElementController}s of
	 * the {@link ActivationElement}s on this space sorted
	 * in ascending order by priority.</p>
	 *
	 * @see #elements
	 * @see IBoardElementController#getPriority()
	 */
	private final ArrayList<IBoardElementController> activationElementsControllers;


	/**
	 * <p>Represent the player currently on this space.
	 * If no player is on this space it is null.</p>
	 */
	private Player player;

	/**
	 * ...
	 * @param board
	 * @param x
	 * @param y
	 * @param elements
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	public Space(Board board, int x, int y, BoardElement... elements) {
		this.board = board;
		this.x = x;
		this.y = y;
		player = null;

		this.elements = new ArrayList<>();
		activationElementsControllers = new ArrayList<>();

		if (elements == null) return;
		Collections.addAll(this.elements, elements);
		for (BoardElement element : elements) {
			if (element instanceof ActivationElement)
				addActivationElementControllers(((ActivationElement) element).getController());
		}

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
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	public void addBoardElement(BoardElement boardElement) {
		if (boardElement == null) return;
		elements.add(boardElement);
		if (boardElement instanceof ActivationElement) {
			addActivationElementControllers(((ActivationElement) boardElement).getController());
		}
		notifyChange();
	}

	/**
	 * <p>Adds one or more {@link IBoardElementController}s to
	 * {@link #activationElementsControllers} and then sorts
	 * the list.</p>
	 * @param iBoardElementControllers the controllers to be added
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void addActivationElementControllers(IBoardElementController... iBoardElementControllers){
		if (iBoardElementControllers.length == 1) activationElementsControllers.add(iBoardElementControllers[0]);
		else activationElementsControllers.addAll(Arrays.asList(iBoardElementControllers));
		activationElementsControllers.sort(IActivateable::compareTo);
	}

	/**
	 * <p>Removed one or more {@link IBoardElementController}s
	 * from {@link #activationElementsControllers}.</p>
	 * @param iBoardElementControllers the controller to be removed
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void removeActivationElementControllers(IBoardElementController... iBoardElementControllers){
		if (iBoardElementControllers.length == 1) activationElementsControllers.remove(iBoardElementControllers[0]); //Should this if be here?
		else activationElementsControllers.removeAll(Arrays.asList(iBoardElementControllers));
	}

	/**
	 * <p>Removes a {@link BoardElement} to this space.</p>
	 *
	 * @param boardElement the board element to remove
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	public void removeBoardElement(BoardElement boardElement) {
		if (boardElement == null || elements == null) return;
		elements.remove(boardElement);
		if (boardElement instanceof ActivationElement) removeActivationElementControllers(((ActivationElement) boardElement).getController());
		notifyChange();
	}

	/**
	 * <p>Returns an array of the {@link IBoardElementController}
	 * of all {@link ActivationElement}s on this space sorted by
	 * priority. The returned array is safe; any changes to the
	 * array will not be reflected in
	 * {@link #activationElementsControllers} of this space.</p>
	 *
	 * @return a safe array of all the activation elements on this space sorted by priority
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	@NotNull
	public IBoardElementController[] getActivationElementControllers() {
		if (activationElementsControllers == null || activationElementsControllers.size() < 1) return new IBoardElementController[0];
		return activationElementsControllers.toArray(new IBoardElementController[0]);
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
		if (player != null && player.game.getBoard() != board) return;

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
		//todo: this and containsObstacleFrom should probably be consolidated.
		for (BoardElement boardElement : elements) {
			if (!boardElement.isAtPosition(heading)) continue;
			if (boardElement.isImpassableFrom(heading.next().next())) return true;
		}
		return false;
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
			if (!boardElement.isAtPosition(heading)) continue;
			if (boardElement.isImpassableFrom(heading)) return true;
		}
		return false;
	}


	/**
	 * <p>Returns the neighbor of the space in the specified
	 * direction reachable by either physical objects or light.
	 * f no such space exists, returns null.</p>
	 * @param direction the direction in which to search for a neighbor
	 * @param isLight whether the neighbor only needs to be reachable by light
	 * @return  a space reachable by either light or physical objects
	 *          neighboring the space in the specified direction
	 *          or null if no such space exists
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 * @see Board#getNeighbour(Space, Heading) 
	 */
	public Space getNeighbor(Heading direction, boolean isLight){
		//todo: this does not play nice with light; it is assumed that containsObstacleTo
		// indicates whether light is blocked (It does not).
		return board.getNeighbour(this, direction);
	}

	/**
	 * <p>Returns the neighbor of the space in
	 * the specified direction reachable by physical
	 * objects. If no such space exists, returns null.</p>
	 * @param direction the direction in which to search for a neighbor
	 * @return  a space reachable by physical objects neighboring
	 *          the space or null if no such space exists.
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 * @see #getNeighbor(Heading, boolean) 
	 */
	public Space getNeighbor(Heading direction){
		return getNeighbor(direction, false);
	}

	public void playerChanged() {
		// This is a minor hack; since some views that are registered with the space
		// also need to update when some player attributes change, the player can
		// notify the space of these changes by calling this method.
		notifyChange();
	}
}
