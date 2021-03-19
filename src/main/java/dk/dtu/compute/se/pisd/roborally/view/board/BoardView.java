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
import dk.dtu.compute.se.pisd.roborally.model.board.Board;
import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.model.enums.Phase;
import dk.dtu.compute.se.pisd.roborally.view.PlayerMatsView;
import dk.dtu.compute.se.pisd.roborally.view.ViewObserver;
import dk.dtu.compute.se.pisd.roborally.view.board.space.SpaceView;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;

/**
 * <p>The view for a {@link Board}. And the Player Mats. And the statusLabel.
 * Maybe it should be renamed to GameView. Or maybe split into to separate
 * classes: GameView and BoardView. This would equally apply to the {@link Board}
 * class though.</p>
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 *
 */
public class BoardView extends VBox implements ViewObserver {

    /**
     * <p>Reference to the {@link Board} which the
     * view is a view for.</p>
     */
    private Board board;
    /**
     * <p>The area which contains the board view
     * itself. Should probably be the entire class.</p>
     *
     * @see #mainBoardPane
     */
    private StackPane boardAreaPane;
    /**
     * <p>Container for the views of the spaces.</p>
     *
     * @see #spaces
     */
    private GridPane mainBoardPane;
    /**
     * <p>The {@link SpaceView}s of this board's spaces.</p>
     *
     * @see #mainBoardPane
     */
    private SpaceView[][] spaces;

    /**
     * <p>The view of the player mats. Should probably be
     * in a different class.</p>
     */
    private PlayerMatsView playerMatsView;

    /**
     * <p>Displays the current status of the game.
     * Should probably be in a different class.</p>
     */
    private Label statusLabel;

    /**
     * <p>The height of the board area relative to the
     * whole view. Note that the size of the {@link #mainBoardPane},
     * the actual view of the board itself, may be smaller
     * than this.</p>
     */
    private static final double BOARD_AREA_HEIGHT_PERCENT = 0.59;
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
    public BoardView(@NotNull GameController gameController) {
        board = gameController.board;

        initBoardAreaPane();

        initPlayerMatsView(gameController);

        initStatusLabel();

        this.getChildren().add(boardAreaPane);
        this.getChildren().add(playerMatsView);
        this.getChildren().add(statusLabel);


        board.attach(this);
        update(board);
    }

    /**
     * <p>Initializes the {@link #statusLabel}.</p>
     */
    private void initStatusLabel() {
        statusLabel = new Label("<no status>");
        RoboRally.bindSize(statusLabel, this, 1, STATUS_HEIGHT_PERCENT);
    }

    /**
     * <p>Initializes the {@link #playerMatsView}.</p>
     * @param gameController the controller of the game the player mats belongs to
     */
    private void initPlayerMatsView(@NotNull GameController gameController) {
        playerMatsView = new PlayerMatsView(gameController);
        RoboRally.bindSize(playerMatsView, this, 1, PLAYER_MATS_HEIGHT_PERCENT);
    }

    /**
     * <p>Initializes the {@link #boardAreaPane}. This
     * includes creating its children.</p>
     */
    private void initBoardAreaPane() {
        boardAreaPane = new StackPane();
        RoboRally.bindSize(boardAreaPane, this, 1, BOARD_AREA_HEIGHT_PERCENT);

        initMainBoardPane();
        boardAreaPane.getChildren().add(mainBoardPane);
    }

    /**
     * <p>Initializes the {@link #mainBoardPane} and
     * {@link #spaces}.</p>
     */
    private void initMainBoardPane() {
        mainBoardPane = new GridPane();
        mainBoardPane.setHgap(0);
        mainBoardPane.setVgap(0);

        spaces = new SpaceView[board.width][board.height];
        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Space space = board.getSpace(x, y);
                SpaceView spaceView = new SpaceView(space);
                spaces[x][y] = spaceView;
                mainBoardPane.add(spaceView, x, y);
            }
        }

        for (SpaceView[] spaceViews : spaces) {
            for (SpaceView spaceView : spaceViews) {
                RoboRally.bindSize(spaceView, mainBoardPane, ((double) (1)) / mainBoardPane.getColumnCount(), 1f / mainBoardPane.getRowCount());
            }
        }

        boardAreaPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            setMainBoardPaneSize();
        });

        boardAreaPane.heightProperty().addListener((obs, oldWidth, newWidth) -> {
            setMainBoardPaneSize();
        });
    }


    /**
     * <p>Sets the size of {@link #mainBoardPane} taking
     * into account the height and width of the board area.</p>
     */
    private void setMainBoardPaneSize() {
        //todo: handle the number of spaces in each dimension being different
        //todo: write this better and more compactly
        //assume width/height = height/width = 1
        double desiredWidthToHeight = (mainBoardPane.getColumnCount() / ((double) mainBoardPane.getRowCount()));
        double desireHeightToWidth = (((double) mainBoardPane.getRowCount()) / mainBoardPane.getColumnCount());

        if (boardAreaPane.getWidth() <= boardAreaPane.getHeight()) {
            mainBoardPane.setMaxWidth(boardAreaPane.getWidth());
            mainBoardPane.setPrefWidth(boardAreaPane.getWidth());
            mainBoardPane.setMinWidth(boardAreaPane.getWidth());
        } else {
            mainBoardPane.setMaxWidth(boardAreaPane.getHeight());
            mainBoardPane.setPrefWidth(boardAreaPane.getHeight());
            mainBoardPane.setMinWidth(boardAreaPane.getHeight());
        }

        if (boardAreaPane.getHeight() <= boardAreaPane.getWidth()) {
            mainBoardPane.setMaxHeight(boardAreaPane.getHeight());
            mainBoardPane.setPrefHeight(boardAreaPane.getHeight());
            mainBoardPane.setMinHeight(boardAreaPane.getHeight());
        } else {
            mainBoardPane.setMaxHeight(boardAreaPane.getWidth());
            mainBoardPane.setPrefHeight(boardAreaPane.getWidth());
            mainBoardPane.setMinHeight(boardAreaPane.getWidth());
        }
    }

    @Override
    public void updateView(Subject subject) {
        if (subject != board) return;
        Phase phase = board.getPhase();
        statusLabel.setText(getStatusMessage());
    }

    /**
     * <p>Returns a string describing the status of the
     * game.</p>
     * @return a string describing the status of the game
     */
    private String getStatusMessage() {
        return "Phase: " + board.getPhase().name() +
                ", Player = " + board.getCurrentPlayer().getName() +
                ", Step: " + board.getStep();
    }
}
