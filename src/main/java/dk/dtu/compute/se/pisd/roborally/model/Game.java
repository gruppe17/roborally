package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.board.Board;
import dk.dtu.compute.se.pisd.roborally.model.enums.Phase;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static dk.dtu.compute.se.pisd.roborally.model.enums.Phase.INITIALISATION;

/**
 * <p>...</p>
 *
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class Game extends Subject {

	private Integer gameId;

	private Board board;


	/**
	 * <p>Indicates whether the players' programs should be single-stepped through</p>
	 */
	protected boolean stepMode;

	/**
	 * <p>What step of the players' programs are being executed, i.e. the register №</p>
	 */
	private int step = 0;


	private final List<Player> players = new ArrayList<>(); //Should be PlayerControllers and not players?

	/**
	 * <p>The current activation queue of the players. The players are queued in order of
	 * proximity to the priority antenna. In case of two players equidistant to the
	 * priority antenna they are ordered arbitrarily.</p>
	 *
	 * @see Player#getDistanceToPrioritySpace()
	 */
	private Queue<Player> playerActivationQueue = new PriorityQueue<>(6, Comparator.comparingInt(Player::getDistanceToPrioritySpace));

	private Player current;

	private Phase phase = INITIALISATION;

	public Game(Board board) {
		this.board = board;
		this.stepMode = false;
	}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		if (this.gameId != null) {
			if (!this.gameId.equals(gameId)) {
				throw new IllegalStateException("A game with a set id may not be assigned a new id!");
			}
			return;
		}
		this.gameId = gameId;
	}

	public Board getBoard(){
		return board;
	}

	public int getNumPlayers() {
		return players.size();
	}

	public void addPlayer(@NotNull Player player) {
		if (player.game != this || players.contains(player)) return;
		players.add(player);
		notifyChange();
	}

	public Player getPlayer(int i) {
		if (i >= 0 && i < players.size()) {
			return players.get(i);
		} else {
			return null;
		}
	}

	public Player[] getPlayers(){
		return players.toArray(new Player[0]);
	}

	public Player getCurrentPlayer() {
		return current;
	}

	public void setCurrentPlayer(Player player) {
		if (player == this.current || !players.contains(player)) return;
		this.current = player;
		notifyChange();
	}

	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		if (phase == this.phase) return;
		this.phase = phase;
		notifyChange();
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		if (step == this.step) return;
		this.step = step;
		notifyChange();
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
		if (stepMode == this.stepMode) return;
		this.stepMode = stepMode;
		notifyChange();
	}

	public int getPlayerNumber(@NotNull Player player) {
		if (player.game == this) {
			return players.indexOf(player);
		} else {
			return -1;
		}
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
	 * <p>Returns a safe array of the players currently
	 * in the activation queue in the correct order.</p>
	 * @return a new array of the players in the activation queue in order
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	public Player[] getPlayerActivationQueue(){
		return playerActivationQueue.toArray(new Player[0]);
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

	public void setPlayerActivationQueue(PriorityQueue<Player> activationQueue){
		this.playerActivationQueue = activationQueue;
	}


}
