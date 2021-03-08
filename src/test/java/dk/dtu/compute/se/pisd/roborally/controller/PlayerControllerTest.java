package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null,"Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.playerController.moveForward();

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void testFastForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.playerController.fastForward();

        Assertions.assertEquals(current, board.getSpace(0, 2).getPlayer(), "Player " + current.getName() + " should beSpace (0,2)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void testMoveForwardByN() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.playerController.moveForward(3);
        Assertions.assertEquals(current, board.getSpace(0, 3).getPlayer(), "Player " + current.getName() + " should beSpace (0,3)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");

        current.playerController.moveForward( 4);
        Assertions.assertEquals(current, board.getSpace(0, 7).getPlayer(), "Player " + current.getName() + " should beSpace (0,7)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 3).getPlayer(), "Space (0,3) should be empty!");

        current.playerController.moveForward(2);
        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 7).getPlayer(), "Space (0,7) should be empty!");


/*
        int totalMoved = 0;
        for (int i = 3; i < 6; i++) {
            gameController.moveForward(current, i);
            totalMoved += i;
            if (totalMoved >= TEST_HEIGHT) totalMoved = TEST_HEIGHT;
            Assertions.assertEquals(current, board.getSpace(0, totalMoved - 1).getPlayer(), "Player " + current.getName() + " should beSpace (0," + totalMoved + ")!");
            Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
            Assertions.assertNull(board.getSpace(0, totalMoved - i).getPlayer(), "Space (0," + (totalMoved - i) + ") should be empty!");
        }
        */
    }

    /*
        N         W         S         E
      W   E     S   N     E   W     N   S
        S         E         N         W
        0         1         2         3
     */
    @Test
    void testTurnRightByN() {
        Player current = gameController.board.getCurrentPlayer();
        gameController.turnRight(current, 2);
        Assertions.assertEquals(Heading.NORTH, current.getHeading());

        gameController.turnRight(current, 3);
        Assertions.assertEquals(Heading.WEST, current.getHeading());

        gameController.turnRight(current, 4);
        Assertions.assertEquals(Heading.WEST, current.getHeading());

        gameController.turnRight(current, 5);
        Assertions.assertEquals(Heading.NORTH, current.getHeading());
    }

    @Test
    void testTurnRight() {
        Player current = gameController.board.getCurrentPlayer();
        current.playerController.turn();
        Assertions.assertEquals(Heading.WEST, current.getHeading());
    }

    @Test
    void testTurnLeft() {
        Player current = gameController.board.getCurrentPlayer();
        current.playerController.turnLeft();
        Assertions.assertEquals(Heading.EAST, current.getHeading());
    }
}