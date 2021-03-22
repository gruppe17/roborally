package dk.dtu.compute.se.pisd.roborally.view.board.space;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.Utils.imageFinder.BoardElementImageFinder;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.BoardElement;
import dk.dtu.compute.se.pisd.roborally.view.ViewObserver;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.jetbrains.annotations.NotNull;

/**
 * <p>The view of a {@link BoardElement}. It is used by {@link SpaceView}
 * being responsible for loading and displaying the image of a {@link BoardElement}</p>
 *
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class BoardElementView extends Pane implements ViewObserver {

    private BoardElementImageFinder imageFinder;
    private ImageView imageView;
    private int rotation = 0;

    public BoardElementView(@NotNull BoardElement boardElement) {
        imageFinder = new BoardElementImageFinder();

        imageView = new ImageView();
        initImageView(boardElement);
        this.getChildren().add(imageView);

        //Should listen for changes to the board element.
        boardElement.attach(this);
    }

    /**
     * <p>Initializes {@link #imageView} corresponding to the passed {@link BoardElement}.</p>
     *
     * @param boardElement the BoardElement to be displayed
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    protected void initImageView(BoardElement boardElement) {
        imageView.setImage(imageFinder.getImage(boardElement));
        updateRotation(boardElement);
        fitImageSize();
    }

    /**
     * <p>Binds the size of {@link #imageView} to the size of this BoardElementView.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    protected void fitImageSize() {
        //imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(this.widthProperty());
        imageView.fitHeightProperty().bind(this.heightProperty());
    }

    /**
     * <p>Update the rotation of the image to align with the
     * {@link BoardElement#getDirection()} of the argument.
     * If the direction is null, the rotation will be 0°.</p>
     *
     * @param boardElement the element to align with
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    protected void updateRotation(BoardElement boardElement) {
        if (boardElement.getDirection() != null) rotation = boardElement.getDirection().ordinal();
        imageView.setRotate(rotation * 90);
    }


    @Override
    public void updateView(Subject subject) {
        if (!(subject instanceof BoardElement)) return;
        updateRotation((BoardElement) subject);
    }

}
