package dk.dtu.compute.se.pisd.roborally.view.board.space;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.Wall;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;

public class WallPane extends StackPane {
    public static final double wallWidthPercent = 23d/113d;
    public static final double wallLengthPercent = 109d/113d;
    BorderPane wallPane;
    
    ArrayList<Wall> walls;
    
    public WallPane(){
        walls = new ArrayList<>();

        wallPane = new BorderPane();
        RoboRally.bindSize(wallPane, this, 1,1);
        this.getChildren().add(wallPane);
    }

    public void addWall(Wall wall){
        BoardElementView wallElementView = new BoardElementView(wall);
        //wallPane.setCenter(wallElementView);
        ObjectProperty<Node> wallRegion = getWallPaneRegionFromHeading(wall.getPosition());
        wallRegion.set(wallElementView);

        RoboRally.bindSize(wallElementView, wallPane, wallLengthPercent, wallWidthPercent);


        walls.add(wall);
    }

    public void removeWall(Wall wall){
        if (!walls.contains(wall)) return;
        walls.remove(wall);
        //This should probably be done better.
        wallPane.getChildren().clear();
        for (Wall w: walls) {
            addWall(w);
        }
    }

    public void clearWalls(){
        walls.clear();
        wallPane.getChildren().clear();
    }

    private ObjectProperty<Node> getWallPaneRegionFromHeading(Heading heading){
        switch (heading){
            case NORTH -> {
                return wallPane.topProperty();
            }
            case EAST -> {
                return wallPane.rightProperty();
            }
            case SOUTH -> {
                return wallPane.bottomProperty();
            }
            case WEST -> {
                return wallPane.leftProperty();
            }
            default -> throw new IllegalStateException("Unexpected value: " + heading);
        }
    }
    
}
