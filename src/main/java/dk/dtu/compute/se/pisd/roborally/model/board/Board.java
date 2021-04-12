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

import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import org.jetbrains.annotations.NotNull;


/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 *
 */
public class Board {

	public final int width;

	public final int height;

	private final String boardName;

	private final Space[][] spaces;

	private int checkpointAmount = 0;


	/**
	 * <p>The space containing the priority antenna</p>
	 */
	private final Space prioritySpace; //A direct reference is stored as it is needed frequently.

	public Board(int width, int height, @NotNull String boardName) {
		super();
		this.boardName = boardName;
		this.width = width;
		this.height = height;
		spaces = new Space[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Space space = new Space(this, x, y, null);
				spaces[x][y] = space;
			}
		}

		//TODO: implement this for real
		prioritySpace = spaces[1][1];
	}

	public Board(int width, int height) {
		this(width, height, "defaultboard");
	}

	public Space getSpace(int x, int y) {
		if (x >= 0 && x < width &&
				y >= 0 && y < height) {
			return spaces[x][y];
		} else {
			return null;
		}
	}

	/**
	 * <p>Returns the {@link #prioritySpace}</p>
	 *
	 * @return A space containing the priority antenna
	 */
	public Space getPrioritySpace() {
		return prioritySpace;
	}

	/**
	 * Returns the neighbour of the given space of the board in the given heading.
	 * The neighbour is returned only, if it can be reached from the given space
	 * (no walls or obstacles in either of the involved spaces); otherwise,
	 * null will be returned.
	 *
	 * @param space   the space for which the neighbour should be computed
	 * @param heading the heading of the neighbour
	 * @return the space in the given direction; null if there is no (reachable) neighbour
	 */
	public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
		//todo: Maybe a space should know its own neighbours?
		if (space.containsObstacleTo(heading)) return null;
		int x = space.x;
		int y = space.y;
		switch (heading) {
			case SOUTH:
				y = (y + 1) % height;
				break;
			case WEST:
				x = (x + width - 1) % width;
				break;
			case NORTH:
				y = (y + height - 1) % height;
				break;
			case EAST:
				x = (x + 1) % width;
				break;
		}

		Space neighbor = getSpace(x, y);
		if (neighbor.containsObstacleFrom(heading.prev().prev())) return null;
		return neighbor;
	}

	/**
	 * <p>Returns the rectilinear distance between two spaces as an {@code int}. Any obstacles in the way are ignored.</p>
	 *
	 * @param from the space from which is measured
	 * @param to   the space measured to
	 * @return an int representing the rectilinear distance between the spaces "from" and "to" ignoring any obstacles
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	public int getRectilinearDistance(Space from, Space to) {
		return Math.abs(to.x - from.x) + Math.abs(to.y - from.y);
	}

	/**
	 * <p>Returns the rectilinear distance between a {@code Space} and the {@link #prioritySpace} as an {@code int}. Any obstacles in the way are ignored.</p>
	 * <p>This is identical to {@link #getRectilinearDistance(Space from, Space prioritySpace)}</p>
	 *
	 * @param from the space from which is measured
	 * @return an int representing the rectilinear distance between the priority antenna and "from" ignoring any obstacles
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 * @see #getRectilinearDistance(Space, Space)
	 */
	public int getRectilinearDistanceToPrioritySpace(Space from) {
		return getRectilinearDistance(from, prioritySpace);
	}

	public String getBoardName() {
		return boardName;
	}

	public void setCheckpointAmount(int amountCheckpoints) {
  		checkpointAmount = amountCheckpoints;
	}

	public int getCheckpointAmount() {
		return checkpointAmount;
	}
}
