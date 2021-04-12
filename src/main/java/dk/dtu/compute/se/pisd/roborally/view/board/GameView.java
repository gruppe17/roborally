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
package dk.dtu.compute.se.pisd.roborally.view.board;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.model.board.Board;
import dk.dtu.compute.se.pisd.roborally.view.PlayerMatsView;
import dk.dtu.compute.se.pisd.roborally.view.ViewObserver;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;

/**
 * <p>The view for a {@link Game}. Contains a {@link BoardView},
 * a {@link PlayerMatsView} and a statusLabel.</p>
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class GameView extends VBox implements ViewObserver {


    /**
     * <p>The {@link Game} this is a view of.</p>
     */
    private Game game;

    /**
     * <p>The view of the {@link Board}</p>
     */
    private BoardView boardView;


    /**
     * <p>The view of the player mats.</p>
     */
    private PlayerMatsView playerMatsView;

    /**
     * <p>Displays the current status of the game.</p>
     */
    private Label statusLabel;

    /**
     * <p>The height of the board view relative to the
     * whole view. Note that the size of the the actual
     * view of the board itself, may be smaller than this.</p>
     */
    private static final double BOARD_VIEW_HEIGHT_PERCENT = 0.59;
    /**
     * <p>The height of the {@link #playerMatsView} relative
     * to the height of the whole view.</p>
     */
    private static final double PLAYER_MATS_HEIGHT_PERCENT = 0.38;
    /**
     * <p>The height of the {@link #statusLabel} relative
     * to the height of the whole view.</p>
     */
    private static final double STATUS_HEIGHT_PERCENT = 0.03;


    /**
     * <p>Create a new view for a game.</p>
     * @param gameController the game controller of the game that is to be displayed
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    public GameView(@NotNull GameController gameController) {
        game = gameController.game;

        initBoardView(gameController.game.getBoard());
        initPlayerMatsView(gameController);
        initStatusLabel();

        this.getChildren().add(boardView);
        this.getChildren().add(playerMatsView);
        this.getChildren().add(statusLabel);


        game.attach(this);
        update(game);
    }

    /**
     * <p>Initialize the {@link #boardView}.</p>
     * @param board the board of the game
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initBoardView(Board board){
        boardView = new BoardView(board);
        RoboRally.bindSize(boardView, this, 1, BOARD_VIEW_HEIGHT_PERCENT);
    }


    /**
     * <p>Initializes the {@link #playerMatsView}.</p>
     * @param gameController the controller of the game the player mats belongs to
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initPlayerMatsView(@NotNull GameController gameController) {
        playerMatsView = new PlayerMatsView(gameController);
        RoboRally.bindSize(playerMatsView, this, 1, PLAYER_MATS_HEIGHT_PERCENT);
    }

    /**
     * <p>Initializes the {@link #statusLabel}.</p>
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initStatusLabel() {
        statusLabel = new Label("<no status>");
        RoboRally.bindSize(statusLabel, this, 1, STATUS_HEIGHT_PERCENT);

    }

    @Override
    public void updateView(Subject subject) {
        if (subject != game) return;
        statusLabel.setText(getStatusMessage());
    }

    /**
     * <p>Returns a string describing the status of the
     * game.</p>
     * @return a string describing the status of the game
     */
    private String getStatusMessage() {
        return "Phase: " + game.getPhase().name() +
                ", Player = " + game.getCurrentPlayer().getName() +
                ", Step: " + game.getStep() +
                ", Energy Cubes: " + game.getCurrentPlayer().getEnergyCubes() +
                ", Last Checkpoint: " + game.getCurrentPlayer().getLastCheckpoint();
    }
}
