package dk.dtu.compute.se.pisd.roborally.view.board;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.board.Board;
import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.view.board.space.SpaceView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 * <p>The view for a {@link Board}.</p>
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 *
 */
public class BoardView extends StackPane {
    /**
     * <p>Reference to the {@link Board} which the
     * view is a view for.</p>
     */
    private Board board;

    /**
     * <p>Container for the views of the spaces.</p>
     *
     * @see #spaces
     */
    private GridPane boardPane;

    /**
     * <p>The {@link SpaceView}s of this board's spaces.</p>
     *
     * @see #boardPane
     */
    private SpaceView[][] spaces;

    public BoardView(Board board){
        this.board = board;
        initBoardPane();
        this.getChildren().add(boardPane);
    }


    /**
     * <p>Initializes the {@link #boardPane} and
     * {@link #spaces}.</p>
     */
    private void initBoardPane() {
        boardPane = new GridPane();
        boardPane.setHgap(0);
        boardPane.setVgap(0);

        spaces = new SpaceView[board.width][board.height];
        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Space space = board.getSpace(x, y);
                SpaceView spaceView = new SpaceView(space);
                spaces[x][y] = spaceView;
                boardPane.add(spaceView, x, y);
            }
        }

        for (SpaceView[] spaceViews : spaces) {
            for (SpaceView spaceView : spaceViews) {
                RoboRally.bindSize(spaceView, boardPane, ((double) (1)) / boardPane.getColumnCount(), 1f / boardPane.getRowCount());
            }
        }

        this.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            setBoardPaneSize();
        });

        this.heightProperty().addListener((obs, oldWidth, newWidth) -> {
            setBoardPaneSize();
        });
    }

    /**
     * <p>Sets the size of {@link #boardPane} taking
     * into account the height and width of the board area.</p>
     */
    private void setBoardPaneSize() {
        //todo: handle the number of spaces in each dimension being different
        //todo: write this better and more compactly
        //assume width/height = height/width = 1
        double desiredWidthToHeight = (boardPane.getColumnCount() / ((double) boardPane.getRowCount()));
        double desireHeightToWidth = (((double) boardPane.getRowCount()) / boardPane.getColumnCount());

        if (this.getWidth() <= this.getHeight()) {
            boardPane.setMaxWidth(this.getWidth());
            boardPane.setPrefWidth(this.getWidth());
            boardPane.setMinWidth(this.getWidth());
        } else {
            boardPane.setMaxWidth(this.getHeight());
            boardPane.setPrefWidth(this.getHeight());
            boardPane.setMinWidth(this.getHeight());
        }

        if (this.getHeight() <= this.getWidth()) {
            boardPane.setMaxHeight(this.getHeight());
            boardPane.setPrefHeight(this.getHeight());
            boardPane.setMinHeight(this.getHeight());
        } else {
            boardPane.setMaxHeight(this.getWidth());
            boardPane.setPrefHeight(this.getWidth());
            boardPane.setMinHeight(this.getWidth());
        }
    }
}
