package dk.dtu.compute.se.pisd.roborally.controller.PlayerControllerTests;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.model.board.Board;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerControllerTest {

    private final int TEST_WIDTH = 16;
    private final int TEST_HEIGHT = 16;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        Game game = new Game(board);
        gameController = new GameController(game, null);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(game, null,"Player " + i);
            game.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setDirection(Heading.values()[i % Heading.values().length]);
        }
        game.setCurrentPlayer(game.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void moveForward() {
        Board board = gameController.game.getBoard();
        Player current = gameController.game.getCurrentPlayer();

        current.playerController.moveForward();

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getDirection(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void testFastForward() {
        Board board = gameController.game.getBoard();
        Player current = gameController.game.getCurrentPlayer();

        current.playerController.fastForward();

        Assertions.assertEquals(current, board.getSpace(0, 2).getPlayer(), "Player " + current.getName() + " should beSpace (0,2)!");
        Assertions.assertEquals(Heading.SOUTH, current.getDirection(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void testMoveForwardByN() {
        Board board = gameController.game.getBoard();
        Player current = gameController.game.getCurrentPlayer();

        int totalMoved = 0;
        for (int i = 1; i < 5; i++) {
            current.playerController.moveForward(i);
            totalMoved += i;
            Assertions.assertEquals(current, board.getSpace(0, totalMoved).getPlayer(), "Player " + current.getName() + " should beSpace (0," + totalMoved + ")!");
            Assertions.assertEquals(Heading.SOUTH, current.getDirection(), "Player 0 should be heading SOUTH!");
            Assertions.assertNull(board.getSpace(0, totalMoved - i).getPlayer(), "Space (0," + (totalMoved - i) + ") should be empty!");
        }
    }

    /*
        N         W         S         E
      W   E     S   N     E   W     N   S
        S         E         N         W
        0         1         2         3
     */
    @Test
    void testTurn() {
        Player current = gameController.game.getCurrentPlayer();
        Heading[] headings = {Heading.EAST, Heading.EAST, Heading.SOUTH, Heading.NORTH, Heading.WEST, Heading.WEST, Heading.NORTH, Heading.SOUTH, Heading.EAST, Heading.EAST, Heading.SOUTH};
        for (int i = -5; i < 6; i++) {
            current.playerController.turn(i);
            Assertions.assertEquals(headings[i + 5], current.getDirection(), "Turn by " + i + ". Should be " + headings[i + 5] + " but is " + current.getDirection());
        }
    }

    @Test
    void testTurnRight() {
        Player current = gameController.game.getCurrentPlayer();
        current.playerController.turn();
        Assertions.assertEquals(Heading.WEST, current.getDirection());
    }

    @Test
    void testTurnLeft() {
        Player current = gameController.game.getCurrentPlayer();
        current.playerController.turnLeft();
        Assertions.assertEquals(Heading.EAST, current.getDirection());
    }

    @Test
    void walkIntoWall(){
        //todo: walkIntoWall test
    }

    @Test
    void pushRobot(){
        //todo: pushRobot test
    }


    @Test
    void pushMultipleDiscontinuousRobots(){
        //todo: pushMultipleDiscontinuousRobots test
        // OOOOOOO00000
        // OXOXXOXXX0X0
        // OOOOOOO00000
    }

    @Test
    void pushRobotIntoWall(){
        //todo: pushRobotIntoWall test

    }

    @Test
    void pushRobotsIntoWall(){
        //todo: pushRobotsIntoWall test

    }
}