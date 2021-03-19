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
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.Wall;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import dk.dtu.compute.se.pisd.roborally.model.enums.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static dk.dtu.compute.se.pisd.roborally.model.enums.Phase.INITIALISATION;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;

    private final Space[][] spaces;

    /**
     * <p>The space containing the priority antenna</p>
     */
    private final Space prioritySpace; //A direct reference is stored as it is needed frequently.

    private final List<Player> players = new ArrayList<>();

    /**
     * <p>The current activation queue of the players. The players are queued in order of
     * proximity to the priority antenna. In case of two players equidistant to the
     * priority antenna they are ordered arbitrarily.</p>
     *
     * @see Player#getDistanceToPrioritySpace()
     */
    private final Queue<Player> playerActivationQueue = new PriorityQueue<>(6, Comparator.comparingInt(Player::getDistanceToPrioritySpace));

    private Player current;

    private Phase phase = INITIALISATION;

    /**
     * <p>What step of the players' programs are being executed, i.e. the register №</p>
     */
    private int step = 0;

    /**
     * <p>Indicates whether the players' programs should be single-stepped through</p>
     */
    private boolean stepMode;

    public Board(int width, int height, @NotNull String boardName) {
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

        spaces[0][0].addBoardElement(new Wall(Heading.NORTH));
        spaces[5][5].addBoardElement(new Wall(Heading.EAST));
        spaces[3][1].addBoardElement(new Wall(Heading.EAST));
        spaces[7][0].addBoardElement(new Wall(Heading.WEST));
        spaces[1][2].addBoardElement(new Wall(Heading.SOUTH));

        //TODO: implement this for real
        prioritySpace = spaces[1][1];
        this.stepMode = false;
    }

    public Board(int width, int height) {
        this(width, height, "defaultboard");
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
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

    public int getPlayersNumber() {
        return players.size();
    }

    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    public Player getCurrentPlayer() {
        return current;
    }

    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    /**
     * Indicates if the players' program is being single-stepped through.
     *
     * @return a boolean indicating if only the next instruction of the next player should be executed
     */
    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
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

        return getSpace(x, y);
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

    /**
     * <p>Returns a new array containing all the players ordered by proximity to the priority antenna.</p>
     * <p>In case of two players equidistant to the priority antenna they are ordered according to their location in the original array.</p>
     *
     * @return an array of players containing all the players on the board in order of priority
     * @author Rasmus Nylander, s205418@student.dtu.dk
     * @deprecated
     */
    public Player[] getSortedPlayerArray() {
        Player[] sortedPlayers = players.toArray(new Player[0]);
        Arrays.sort(sortedPlayers, Comparator.comparingInt(Player::getDistanceToPrioritySpace));
        return sortedPlayers;
    }

    /**
     * <p>Returns the next player of the {@link #playerActivationQueue} and
     * removes them from the queue. If the queue is empty, returns null.</p>
     *
     * @return the next player in the queue. If the queue is empty, returns null
     * @author Rasmus Nylander, s205418@student.dtu.dk
     * @see #playerQueueForceRepopulate()
     */
    public Player nextPlayer() {
        if (playerActivationQueue.peek() == null) {
            return null;
            //playerQueue.addAll(players);
        }
        return playerActivationQueue.remove();
    }

    /**
     * <p>Returns a boolean indicating whether {@link #playerActivationQueue}
     * is empty or the next element is null.</p>
     *
     * @return Returns a boolean indicating whether the activation queue is empty or the next element is null
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    public boolean isPlayerActivationQueueEmpty() {
        return playerActivationQueue.isEmpty() || playerActivationQueue.peek() == null;
    }

    /**
     * <p>Forces the player queue to be emptied and repopulated.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    public void playerQueueForceRepopulate() {
        playerActivationQueue.clear();
        playerActivationQueue.addAll(players);
    }

}
