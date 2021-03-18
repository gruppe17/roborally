package dk.dtu.compute.se.pisd.roborally.view.board.space;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.BoardElement;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.Wall;
import dk.dtu.compute.se.pisd.roborally.view.ViewObserver;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

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
     */
    private final Space space;

    /**
     * <p>The view of the {@link Wall}s.</p>
     *
     * @see #nonWallPane
     */
    private WallPane wallPane;

    /**
     * <p>A container for the views of all other
     * {@link BoardElement}s than {@link Wall}s.</p>
     *
     * @see #wallPane
     */
    private StackPane nonWallPane;

    /**
     * <p>A list of all the {@link BoardElementView}s.</p>
     */
    private ArrayList<BoardElementView> boardElementViews;

    /**
     * <p>Creates a new view for the {@link BoardElement}s of the
     * specified {@link Space}.</p>
     * @param space the space for which a view of its board elements is desired
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    public SpaceBoardElementsView(Space space) {
        this.space = space;
        initWallView();
        this.getChildren().add(wallPane);


        initBoardElementViews();
        space.attach(this);
        update(space);
    }

    /**
     * <p>Instantiates {@link #wallPane} and bind its size to the size of this.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initWallView() {
        wallPane = new WallPane();
        RoboRally.bindSize(wallPane, this, 1, 1);
    }


    /**
     * <p>Initializes {@link #boardElementViews}. This includes creating the
     * the {@link BoardElementView}s./p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void initBoardElementViews() {
        if (boardElementViews == null) boardElementViews = new ArrayList<>();

        BoardElement[] boardElements = space.getElements();
        if (boardElements == null || boardElements.length < 1) return;
        for (BoardElement boardElement : boardElements) {
            addElement(boardElement);
        }
    }

    //todo: explain further
    /**
     * <p>Adds the specified {@link BoardElement} to the view.</p>
     * @param boardElement the board element to be added
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void addElement(@NotNull BoardElement boardElement) {

        if (boardElement instanceof Wall) {
            wallPane.addWall((Wall) boardElement);
            return;
        }
        addThis(boardElement);
    }

    /**
     * <p>Adds the specified {@link BoardElement} directly to this view.
     * <b>Should not be called directly!</b></p>
     * @param boardElement the board element to be added directly to this
     * @author Rasmus Nylander, s205418@student.dtu.dk
     * @see #addElement(BoardElement)
     */
    private void addThis(@NotNull BoardElement boardElement) {
        BoardElementView boardElementView = new BoardElementView(boardElement);

        boardElementViews.add(boardElementView);
        this.getChildren().add(boardElementView);
        RoboRally.bindSize(boardElementView, this, 1, 1);
    }


    /**
     * <p>Updates the {@link BoardElementView}s of the space.</p>
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void updateBoardElements() {
        //This should be done more intelligently in the future.
        clearView();
        initBoardElementViews();
    }

    /**
     *  <p>Clear the view. This means clearing all the
     *  children of this view, clearing all the children
     *  of the {@link #wallPane} and then adding back the
     *  {@link #wallPane}} as a child of this view.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private void clearView() {
        this.getChildren().clear();
        wallPane.clearWalls();
        this.getChildren().add(wallPane);
    }


    /**
     *
     * @param subject
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    @Override
    public void updateView(Subject subject) {
        if (subject != this.space) return;
        updateBoardElements();
    }

}
