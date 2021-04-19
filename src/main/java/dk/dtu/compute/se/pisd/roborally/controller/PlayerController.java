package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.model.enums.Command;
import dk.dtu.compute.se.pisd.roborally.model.enums.DamageType;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;

/**
 * <p>The controller for a {@link Player}.</p>
 *
 * @author Rasmus Nylander, s205418@student.dtu.dk
 * @author Tobias Maneschijn, s205422@student.dtu.dk
 */
public class PlayerController {
	Player player;

	public PlayerController(Player player) {
		this.player = player;
	}

	/*
	****************
		Movement
	****************
	*/

	/**
	 * <p>Moves the player in a certain direction by the specified distance.
	 * If another player is in the way, they are pushed along by the robot.</p>
	 * <p>The distance wraps around the map.</p>
	 *
	 * @param direction The direction in which to move
	 * @param distance  The amount of spaces to move
	 * @return the distance moved by the player
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 * @see #moveForward(int)
	 */
	public int move(@NotNull Heading direction, int distance) {
		Space currentSpace = player.getSpace();
		if (currentSpace == null) return -1;

		int distanceMoved;
		for (distanceMoved = 0; distanceMoved < distance; distanceMoved++) {
			Space target = currentSpace.board.getNeighbour(currentSpace, direction);
			if (target == null) break;

			if (!pushRobots(target, direction, distance - distanceMoved)) break;
			currentSpace = target;
		}
		player.setSpace(currentSpace); //identical to target.setPlayer(player);
		return distanceMoved;
	}

	/**
	 * <p>Tries to push the robot on the specified space in the
	 * specified direction by the specified distance distance.
	 * Returns the whether the space was cleared regardless of
	 * whether the it was already vacant.</p>
	 *
	 * @param space     the space whose robot is to be pushed
	 * @param direction the direction in which to push the robot
	 * @param distance  the distance to push the robot
	 * @return true no robot remains on the specified space
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private boolean pushRobots(@NotNull Space space, @NotNull Heading direction, int distance) {
		if (space.getPlayer() == null) return true;

		space.getPlayer().playerController.move(direction, distance);
		return space.getPlayer() == null;
	}

	/**
	 * <p>Moves the player in the direction of their current heading by the specified distance.
	 * If another player is in the way, they are pushed along by the robot.</p>
	 * <p>The distance wraps around the map.</p>
	 *
	 * @param distance The amount of spaces to move in the current direction
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 * @see #move(Heading, int)
	 */
	public void moveForward(int distance) {
		move(player.getDirection(), distance);
	}

	/**
	 * <p>Moves the player forward by one</p>
	 * <p>Identical to {@code moveForward(1)}</p>
	 *
	 * @see #moveForward(int)
	 */
	public void moveForward() {
		moveForward(1);
	}

	/**
	 * <p>Moves the player forward by two</p>
	 * <p>Identical to {@code moveForward(2)}</p>
	 *
	 * @see #moveForward(int)
	 * @deprecated
	 */
	public void fastForward() {
		moveForward(2);
	}

	/**
	 * <p>Turns the player by π/4 * {@code numTimes}. If the argument is
	 * negative the player is turned to the left.</p>
	 *
	 * @param numTimes Number of times to turn right
	 */
	public void turn(int numTimes) {
		numTimes %= 4;
		if (numTimes < 0) numTimes += 4;
		Heading heading = player.getDirection();
		for (int i = 0; i < numTimes; i++) {
			heading = heading.next();
		}

		player.setDirection(heading);
	}

	/**
	 * <p>Turns player/robot by π/4</p>
	 */
	public void turn() {
		turn(1);
	}

	/**
	 * <p>Turns player/robot by -π/4</p>
	 */
	public void turnLeft() {
		player.setDirection(player.getDirection().prev());
	}


	/*
	**************
		Cards
	**************
	*/


	/**
	 * Returns a random CommandCard
	 *
	 * @return
	 * @author Tobias Maneschijn, s205422@student.dtu.dk
	 */
	public CommandCard generateRandomCommandCard() {
		Command[] commands = Command.values();
		int random = (int) (Math.random() * commands.length);
		return new CommandCard(commands[random]);
	}

	/**
	 * <p>Adds a {@link CommandCard} to the player's deck</p>
	 *
	 * @param card the card to be added to the player's deck
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 * @return true if the deck changed
	 */
	public boolean addCardToDeck(CommandCard card) {
		return player.getDeck().add(card);
	}

	/**
	 * <p>Adds a {@link CommandCard} to the player's discard pile</p>
	 *
	 * @param card the card to be added to the player's discard pile
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 * @return true if the deck changed
	 */
	public boolean addCardToDiscardPile(CommandCard card) {
		return player.getDiscardPile().add(card);
	}

	/**
	 * Adds a random card to an empty card field if possible
	 *
	 * @author Tobias Maneschijn, s205422@student.dtu.dk
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	public boolean drawCard() {
		CommandCardField emptyCardField = player.getEmptyCardField();
		if (emptyCardField == null) return false;
		LinkedList<CommandCard> deck = player.getDeck();
		if (deck.size() == 0) shuffleDeck();
		emptyCardField.setCard(deck.remove());
		return true;
	}

	/**
	 * <p>Empties the discard pile into the player's deck
	 * and shuffles the list.</p>
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	private void shuffleDeck() {
		LinkedList<CommandCard> deck = player.getDeck();
		LinkedList<CommandCard> discardPile = player.getDiscardPile();
		deck.addAll(discardPile);
		discardPile.clear();
		Collections.shuffle(deck);
	}

	/**
	 * <p>Draws cards until the player's hand is full.</p>
	 *
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	public void fillHand() {
		while (drawCard());
	}

	/**
	 * <p>Discards the player's current hand.</p>
	 *
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	public void discardHand() {
		for (CommandCardField cCField : player.getHand()) {
			player.getDiscardPile().add(cCField.getCard());
			cCField.setCard(null);
		}
	}

	/**
	 * <p>Discards the player's current program.
	 * That is, discards the cards currently in
	 * the player's registers.</p>
	 *
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	public void discardProgram() {
		for (CommandCardField cCField : player.getProgram()) {
			player.getDiscardPile().add(cCField.getCard());
			cCField.setCard(null);
		}
	}

	/*
	*************
		Energy
	*************
	*/

	/**
	 * Try to pay with energy cubes
	 *
	 * @param amount the amount of cubes to pay with
	 * @return true if payment was successful
	 * @author Tobias Maneschijn, s205422@student.dtu.dk
	 */
	public boolean payWithEnergyCubes(int amount) {
		if (player.getEnergyCubes() >= amount) {
			player.setEnergyCubes(player.getEnergyCubes() - amount);
			return true;
		} else {
			return false;
		}
	}

	/*
	***************
		Damage
	***************
	*/

	/**
	 *
	 * @param damageType
	 * @return
	 */
	public int damage(DamageType damageType){
		return -1;
	}
}
