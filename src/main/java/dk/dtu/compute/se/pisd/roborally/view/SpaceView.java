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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.BoardElement;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;


/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {
    final private static String FACTORY_FLOOR_IMAGE_PATH = "images/tiles/factoryFloor.png";

    public final Space space;
    public final ImageView imageView;

    /**
     * <p>Contains all the {@link #boardElementViews}</p>
     */
    private StackPane boardElementViewPane;

    /**
     * <p>A list of all the {@link BoardElementView}s of this space.</p>
     */
    private ArrayList<BoardElementView> boardElementViews;

    private Random random = new Random();

    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        imageView = new ImageView(FACTORY_FLOOR_IMAGE_PATH);
        setImageSize(imageView);
        rotateToRandomDirection(imageView);

        initBoardElementViewPane();


        addBackChildren();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }


    /**
     * <p>Initializes {@link #boardElementViewPane}. This includes
     * creating its children.</p>
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initBoardElementViewPane(){
        boardElementViewPane = new StackPane();
        RoboRally.bindSize(boardElementViewPane, imageView.fitWidthProperty(), imageView.fitHeightProperty(), 1, 1);
        initBoardElementViews();
    }

    /**
     * <p>Initializes {@link #boardElementViews}. Simply returns if the
     * {@link BoardElement}s of {@link #space} is {@code null}</p>
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initBoardElementViews(){
        if (space.getElements() == null) return;
        boardElementViews = new ArrayList<>(space.getElements().length);
        for (BoardElement boardElement: space.getElements()) {
            BoardElementView boardElementView = new BoardElementView(boardElement);
            boardElementViews.add(boardElementView);
            boardElementViewPane.getChildren().add(boardElementView);
            RoboRally.bindSize(boardElementView, boardElementViewPane, 1, 1);
        }
    }

    /**
     * <p>Randomly sets an {@link ImageView}'s rotation to be either 0°, 90°, 180° or 270°.</p>
     * @param imageView the ImageView to rotate
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void rotateToRandomDirection(ImageView imageView){
        imageView.setRotate(random.nextInt(4) * 90);
    }

    /**
     * <p>Sets the size of an {@link ImageView}. This is done by binding it the this {@link SpaceView}</p>
     * @param imageView the ImageView to set the size of
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void setImageSize(ImageView imageView){
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(this.widthProperty());
        imageView.fitHeightProperty().bind(this.heightProperty());
    }

    /**
     * <p>Updates the {@link BoardElementView}s of the space</p>
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void updateBoardElements(){
        //This should be done more intelligently in the future.
        boardElementViewPane.getChildren().clear();
        boardElementViews.clear();
        initBoardElementViews();
    }

    /**
     * <p>Draws or removes the player where needed.</p>
     */
    private void updatePlayer() {
        //When it is established what a player is on the board, then it can simply be checked
        // if the last element is a player and then only remove that element
        this.getChildren().clear();
        addBackChildren();

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0);
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90 * player.getHeading().ordinal()) % 360);
            this.getChildren().add(arrow);
        }

    }

    /**
     * <p>Adds the children of this space excluding a potential player.</p>
     */
    private void addBackChildren(){
        this.getChildren().add(imageView);
        this.getChildren().add(boardElementViewPane);
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updateBoardElements();
            updatePlayer();
        }
    }

}