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
package dk.dtu.compute.se.pisd.roborally;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.view.BoardView;
import dk.dtu.compute.se.pisd.roborally.view.RoboRallyMenuBar;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class RoboRally extends Application {
    private static final int MIN_APP_WIDTH = 550;
    private static final int MIN_APP_HEIGHT = 750;

    private Stage stage;
    private BorderPane boardRoot;
    // private RoboRallyMenuBar menuBar;

    // private AppController appController;

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        AppController appController = new AppController(this);

        // create the primary scene with the a menu bar and a pane for
        // the board view (which initially is empty); it will be filled
        // when the user creates a new game or loads a game
        RoboRallyMenuBar menuBar = new RoboRallyMenuBar(appController);
        boardRoot = new BorderPane();
        VBox vbox = new VBox(menuBar, boardRoot);
        vbox.setMinWidth(MIN_APP_WIDTH);

        bindWidth(menuBar, vbox.widthProperty(), 1);

        bindWidth(boardRoot, vbox.widthProperty(), 1);
        boardRoot.minHeightProperty().bind(vbox.heightProperty().subtract(menuBar.heightProperty()));
        boardRoot.maxHeightProperty().bind(vbox.heightProperty().subtract(menuBar.heightProperty()));

        Scene primaryScene = new Scene(vbox);
        //bindSize(vbox, primaryStage.widthProperty(), primaryStage.heightProperty(), 1, 1);
        vbox.maxHeightProperty().bind(primaryScene.heightProperty());
        vbox.maxWidthProperty().bind(primaryScene.widthProperty());

        stage.setScene(primaryScene);
        stage.setTitle("RoboRally");
        stage.setOnCloseRequest(
                e -> {
                    e.consume();
                    appController.exit();} );
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    public void createBoardView(GameController gameController) {
        // if present, remove old BoardView
        boardRoot.getChildren().clear();

        if (gameController != null) {
            ((VBox)boardRoot.parentProperty().get()).setMinHeight(MIN_APP_HEIGHT);
            // create and add view for new board
            BoardView boardView = new BoardView(gameController);
            bindSize(boardView, boardRoot, 1, 1);
            boardRoot.setCenter(boardView);
        }

        stage.sizeToScene();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        // XXX just in case we need to do something here eventually;
        //     but right now the only way for the user to exit the app
        //     is delegated to the exit() method in the AppController,
        //     so that the AppController can take care of that.
    }

    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Temporary method. Binds the size of one region to another. Shouldn't be here (and should probably be done differently).
     */

    public static void bindSize(Region a, Region b, double widthPercent, double heightPercent){
        bindSize(a, b.widthProperty(), b.heightProperty(), widthPercent, heightPercent);
    }

    public static void bindSize(Region a, ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height, double widthPercent, double heightPercent){
        bindWidth(a, width, widthPercent);
        bindHeight(a, height, heightPercent);
    }

    public static void bindWidth(Region a, ReadOnlyDoubleProperty b, double widthPercent){
        a.prefWidthProperty().bind(b.multiply(widthPercent));
        a.minWidthProperty().bind(b.multiply(widthPercent));
        a.maxWidthProperty().bind(b.multiply(widthPercent));
    }

    public static void bindHeight(Region a, ReadOnlyDoubleProperty b, double heightPercent){
        a.prefHeightProperty().bind(b.multiply(heightPercent));
        a.minHeightProperty().bind(b.multiply(heightPercent));
        a.maxHeightProperty().bind(b.multiply(heightPercent));
    }

}