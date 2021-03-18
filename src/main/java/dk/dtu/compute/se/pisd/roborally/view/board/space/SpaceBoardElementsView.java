package dk.dtu.compute.se.pisd.roborally.view.board.space;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.BoardElement;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.Wall;
import dk.dtu.compute.se.pisd.roborally.view.ViewObserver;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;

/**
 * <p>The view of the {@link BoardElement}s of a {@link SpaceView}.</p>
 *
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class SpaceBoardElementsView extends StackPane implements ViewObserver {
    /**
     * <p>A reference to the {@link Space} whose
     * {@link BoardElement}s this is a view of.
     * This is used to ensure that the subject
     * calling {@link #updateView(Subject)} is
     * the actual space of this view.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private final Space space;

    private WallPane wallPane;

    private StackPane nonWallPane;

    //private final BorderPane wallPane;

    /**
     * <p>A list of all the {@link BoardElementView}s.</p>
     */
    private ArrayList<BoardElementView> boardElementViews;

    public SpaceBoardElementsView(Space space){
        this.space = space;
        initWallView();
        this.getChildren().add(wallPane);


        initBoardElementViews();
        space.attach(this);
        update(space);
    }

    private void initWallView(){
        wallPane = new WallPane();
        RoboRally.bindSize(wallPane, this, 1, 1);
    }


    /**
     * <p>Initializes {@link #boardElementViews}. This includes creating the
     * the {@link BoardElementView}s./p>
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initBoardElementViews(){
        if (boardElementViews == null) boardElementViews = new ArrayList<>();

        BoardElement[] boardElements = space.getElements();
        if (boardElements == null || boardElements.length < 1) return;
        for (BoardElement boardElement: boardElements) {
            addElement(boardElement);
        }
    }

    private void addElement(BoardElement boardElement){
        if (boardElement == null) return;

        if (boardElement instanceof Wall){
            wallPane.addWall((Wall) boardElement);
            return;
        }

        addThis(boardElement);

    }

    private void addThis(BoardElement boardElement){
        BoardElementView boardElementView = new BoardElementView(boardElement);
        boardElementViews.add(boardElementView);
        this.getChildren().add(boardElementView);
        RoboRally.bindSize(boardElementView, this, 1, 1);
    }


    /**
     * <p>Updates the {@link BoardElementView}s of the space.</p>
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void updateBoardElements(){
        //This should be done more intelligently in the future.

        initBoardElementViews();

    }

    private void clearView(){
        this.getChildren().clear();
        wallPane.clearWalls();
        this.getChildren().add(wallPane);
    }


    @Override
    public void updateView(Subject subject) {
        if (subject != this.space) return;
        updateBoardElements();
    }

}
