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
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jetbrains.annotations.NotNull;

import java.util.Random;


/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 75; // 60; // 75;
    final public static int SPACE_WIDTH = 75;  // 60; // 75;

    final private static String FACTORY_FLOOR_IMAGE_PATH = "images/tiles/factoryFloor.png";

    public final Space space;
    public final ImageView imageView;
    private BoardElementView boardElementView;

    private Random random = new Random();

    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        imageView = new ImageView(FACTORY_FLOOR_IMAGE_PATH);
        this.getChildren().add(imageView);
        setImageSize(imageView);
        rotateToRandomDirection(imageView);

        initBoardElementView();


        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    /**
     * <p>Initializes {@link #boardElementView}. Simply returns if the
     * {@link dk.dtu.compute.se.pisd.roborally.model.boardElement.BoardElement}
     * of {@link #space} is {@code null}</p>
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initBoardElementView(){
        if (space.element == null) return;
        boardElementView = new BoardElementView(space.element);
        this.getChildren().add(boardElementView);
        RoboRally.bindSize(boardElementView, imageView.fitWidthProperty(), imageView.fitHeightProperty(), 1, 1);
    }

    /**
     * <p>Randomly sets an {@link ImageView}'s rotation to be either 0°, 90°, 180° or 270°.</p>
     * @param imageView the ImageView to rotate
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void rotateToRandomDirection(ImageView imageView){
        imageView.setRotate(random.nextInt(4));
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

    private void updatePlayer() {
        //When it is established what a player is on the board, then it can simply be checked
        // if the last element is a player and then only remove that element
        this.getChildren().clear();
        this.getChildren().add(imageView);
        if (space.element != null){
            if (boardElementView == null) initBoardElementView();
            this.getChildren().add(boardElementView);
        }

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

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updatePlayer();
        }
    }

}