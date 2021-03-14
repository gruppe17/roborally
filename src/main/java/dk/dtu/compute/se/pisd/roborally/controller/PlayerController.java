package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.interfaces.ILaser;
import dk.dtu.compute.se.pisd.roborally.model.*;

public class PlayerController implements ILaser {
    Player player;

    public PlayerController(Player player) {
        this.player = player;
    }

    /**
     * <p>
     * Moves the player in a certain direction by the specified distance. If another
     * player is in the way, they are pushed along by the robot.
     * </p>
     * <p>
     * The distance wraps around the map.
     * </p>
     *
     * @param direction The direction in which to move
     * @param distance  The amount of spaces to move
     * @author Rasmus Nylander, s205418@student.dtu.dk
     * @see #moveForward(int)
     */
    public void move(Heading direction, int distance) {
        Space currentSpace = player.getSpace();
        if (currentSpace != null) {
            for (int i = 0; i < distance; i++) {
                Space target = currentSpace.board.getNeighbour(currentSpace, direction);
                if (target != null) {
                    if (target.getPlayer() != null) {
                        target.getPlayer().playerController.move(direction, distance - i);
                        if (target.getPlayer() != null) {
                            break;
                        }
                    }
                    currentSpace = target;
                } else {
                    break;
                }
            }
            player.setSpace(currentSpace); // identical to target.setPlayer(player);
        }
    }

    /**
     * <p>
     * Moves the player in the direction of their current heading by the specified
     * distance. If another player is in the way, they are pushed along by the
     * robot.
     * </p>
     * <p>
     * The distance wraps around the map.
     * </p>
     *
     * @param distance The amount of spaces to move in the current direction
     * @author Rasmus Nylander, s205418@student.dtu.dk
     * @see #move(Heading, int)
     */
    public void moveForward(int distance) {
        move(player.getHeading(), distance);
    }

    /**
     * <p>
     * Moves the player forward by one
     * </p>
     * <p>
     * Identical to {@code moveForward(1)}
     * </p>
     */
    public void moveForward() {
        moveForward(1);
    }

    /**
     * <p>
     * Moves the player forward by two
     * </p>
     * <p>
     * Identical to {@code moveForward(2)}
     * </p>
     *
     *
     */
    public void fastForward() {
        moveForward(2);
    }

    /**
     * <p>
     * Turns the player by π/4 * {@code numTimes}. If the argument is negative the
     * player is turned to the left.
     * </p>
     *
     * @param numTimes Number of times to turn right
     */
    public void turn(int numTimes) {
        numTimes %= 4;
        if (numTimes < 0)
            numTimes += 4;
        Heading heading = player.getHeading();
        for (int i = 0; i < numTimes; i++) {
            heading = heading.next();
        }

        player.setHeading(heading);
    }

    /**
     * <p>
     * Turns player/robot by π/4
     * </p>
     */
    public void turn() {
        turn(1);
    }

    /**
     * <p>
     * Turns player/robot by -π/4
     * </p>
     */
    public void turnLeft() {
        player.setHeading(player.getHeading().prev());
    }

    /**
     * Returns a random CommandCard
     * 
     * @author Tobias Maneschijn, s205422@student.dtu.dk
     * @return
     */
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /**
     * adds a card to an empty card field if possible
     * 
     * @author Tobias Maneschijn, s205422@student.dtu.dk
     */
    public void addCard(CommandCard card) {
        CommandCardField emptyCardField = player.getEmptyCardField();

        if (emptyCardField != null && card != null) {
            emptyCardField.setCard(card);
        }

    }

    /**
     * adds a random card to an empty card field if possible
     * 
     * @author Tobias Maneschijn, s205422@student.dtu.dk
     */
    private void drawCard() {
        CommandCardField emptyCardField = player.getEmptyCardField();
        if (emptyCardField != null) {
            emptyCardField.setCard(generateRandomCommandCard());
        }

    }

    /**
     * <p>
     * Fire in the direction the owner robot is facing. Their range has no limit.
     * Any robot in the line of sight is shot. Robot lasers cannot fire through
     * walls or shoot more than one robot.
     * </p>
     * 
     * @author Tobias Maneschijn, s205422@student.dtu.dk
     */
    @Override
    public void fire() {
        Space currentSpace = player.getSpace();
        Space lastSpace = currentSpace;

        /* Abort if player is not in a space */
        if (lastSpace == null)
            return;
        while (true) {
            /* get next space */
            lastSpace = player.board.getNeighbour(lastSpace, player.getHeading());
            Player playerAtSpace = lastSpace.getPlayer();
            /* remember to add the right elements to prevent hitting walls and stuff here */
            /*
             * if(lastSpace.element == WallBoardElement || lastSpace.element ==
             * PriorityAntennaBoardElement){ break; }
             */

            /* exit if nothing is hit */
            if (lastSpace == null) {
                break;
            }
            /* We should add fx to the spaces that are hit */

            /* If player is hit, then damage it and do stuff. */
            if (playerAtSpace != null && playerAtSpace != player) {

                // hit player should take a SPAM damage card.

                // Change this to SPAM Command Card
                playerAtSpace.playerController.addCard(new CommandCard(Command.FORWARD));

                break;
            }
        }

        /* Reset last space */
        currentSpace = player.getSpace();
        lastSpace = currentSpace;

        /* Maybe do some cleanup of fx here? */
        while (true) {
            /* get next space */
            lastSpace = player.board.getNeighbour(lastSpace, player.getHeading());
            if (lastSpace == null) {
                break;
            }
        }
    }

    /**
     * <p>
     * Reboots the robot
     * </p>
     * 
     * @author Tobias Maneschijn, s205422@student.dtu.dk
     */
    public void reboot() {

        //  Discard 2 SPAM cards to discard pile

        //  Cancel programming and discard all cards in registeres and hand

        //  Place robot on the reboot token that is on the same board where the robot was 
        //  when it rebooted. You may turn your robot to face any direction. If you rebooted from 
        //  the start board, place your robot on the space where you started the game.
        

    
    }

}
