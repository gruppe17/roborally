package dk.dtu.compute.se.pisd.roborally.view.board.space;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.Wall;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * <p>A container for the walls of a {@link SpaceBoardElementsView}.
 * I.e. a view of the walls of a {@link SpaceView}.</p>
 * <p><i>Note that this class should not be used directly;
 * <u>This class should only be used by {@link SpaceBoardElementsView}.</u></i></p>
 *
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class WallPane extends StackPane {
    /**
     * <p>The depth of a wall relative to the the
     * size of a {@link SpaceView}.</p>
     */
    public static final double WALL_DEPTH_PERCENT = 23d / 113d;
    /**
     * <p>The length of a wall relative o the the
     * size of a {@link SpaceView}.</p>
     */
    public static final double WALL_LENGTH_PERCENT = 109d / 113d;

    /**
     * <p>An array of {@link StackPane}s responsible for
     * containing the {@link Wall}s {@link BoardElementView}s.
     * Indexes are in the same order as {@link Heading}, i.e.
     * index 0 is the southern wall, index 1 is the western wall
     * and so on.</p>
     */
    StackPane[] wallPanes;

    /**
     * <p>A list of all the walls displayed in this WallPane.</p>
     */
    ArrayList<Wall> walls;

    /**
     * <p>Creates a new instance of a {@link WallPane}.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    public WallPane() {
        walls = new ArrayList<>();
        initWallPanes();
        this.getChildren().addAll(wallPanes);
    }

    /**
     * <p>Initializes {@link #wallPanes}.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initWallPanes() {
        wallPanes = new StackPane[4];
        Heading heading = Heading.NORTH;
        do {

            wallPanes[heading.ordinal()] = new StackPane();
            RoboRally.bindSize(wallPanes[heading.ordinal()], this, WALL_LENGTH_PERCENT, WALL_DEPTH_PERCENT);
            //If it is a western or eastern wall, then it is rotated.
            //This is once again done in BoardElementView
            //wallPanes[heading.ordinal()].setRotate(90 * (heading.ordinal() % Heading.NORTH.ordinal() == 0 ? 0 : 1));

            heading = heading.next();
        } while (heading != Heading.NORTH);


        //Listeners that update the position of the walls when the size of the space changes.
        //todo: extract to their own method.
        this.widthProperty().addListener(((observable, newWidth, oldWidth) -> {
            for (int i = 0; i < wallPanes.length; i++) {//Loop through all the wallPanes.
                if (i % Heading.NORTH.ordinal() != 0)//If it is either the eastern or western wall
                    wallPanes[i].translateXProperty().set(//move it (relative to the center of this)
                            (((this.getWidth()) - this.getWidth() * WALL_DEPTH_PERCENT) / 2)//by half the width of a space minus the width of a wall
                                    * (i == Heading.WEST.ordinal() ? -1 : 1));//To the left if it is the western wall and otherwise to the right
            }
        }));

        this.heightProperty().addListener((observable, newHeight, oldHeight) -> {
            for (int i = 0; i < wallPanes.length; i++) {//Loop through all the wallPanes.
                if (i % Heading.NORTH.ordinal() == 0)//If it is either the northern or southern wall
                    wallPanes[i].translateYProperty().set(//move it (relative to the center of this)
                            ((this.getWidth() - this.getWidth() * WALL_DEPTH_PERCENT) / 2)//by half the width of a space minus the width of a wall
                                    * (i == Heading.NORTH.ordinal() ? -1 : 1));//Up if it is the northern wall and otherwise down
            }
        });
    }


    /**
     * <p>Adds a {@link Wall} to this {@link WallPane}. If another {@link Wall} with the
     * the same position already is contained in the {@link WallPane} the
     * specified {@link Wall} is still added, and will be the one to be displayed.</p>
     *
     * @param wall the wall object to be added, may not be null
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    public void addWall(@NotNull Wall wall) {
        BoardElementView wallElementView = new BoardElementView(wall);

        Pane wallPane = wallPanes[wall.getPosition()[0].ordinal()];
        wallPane.getChildren().add(wallElementView);
        RoboRally.bindSize(wallElementView, wallPane, 1, 1);

        walls.add(wall);
    }

    /**
     * <p>Removes the first occurrence of the specified wall this wall pane.
     * If the WallPane does not contain the specified wall, it is unchanged.
     * Returns {@code true} if the wall pane contained the specified wall.</p>
     *
     * @param wall wall to be removed from the wall pane if present
     * @return {@code true} if the wall pane contained the specified wall
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    public boolean removeWall(Wall wall) {
        if (!walls.remove(wall)) return false;
        //This should probably be done better.
        for (Pane wallPane : wallPanes) {
            wallPane.getChildren().clear();
        }
        for (Wall w : walls) {
            addWall(w);
        }
        return true;
    }

    /**
     * Returns {@code true} if the specified wall is contained
     * within the {@link WallPane}.
     *
     * @param wall the wall which presence is to be determined.
     * @return {@code true} if the wall is contained within the wall pane
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    public boolean containsWall(Wall wall) {
        return walls.contains(wall);
    }

    /**
     * <p>Removes all the {@link Wall}s from the {@link WallPane}.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    public void clearWalls() {
        walls.clear();
        for (Pane wallPane : wallPanes) {
            wallPane.getChildren().clear();
        }
    }

}
