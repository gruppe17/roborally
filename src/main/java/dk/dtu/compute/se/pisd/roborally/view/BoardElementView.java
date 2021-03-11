package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.boardElement.BoardElement;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.jetbrains.annotations.NotNull;

/**
 * <p>The view of a {@link BoardElement}. It is used by {@link SpaceView}
 * being responsible for determining the correct image for a {@link BoardElement}, loading it
 * and displaying it.</p>
 *
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class BoardElementView extends BorderPane implements ViewObserver {
    final private static String BASE_DIRECTORY = "images/tiles/boardElements";

    private ImageView imageView;

    public BoardElementView(@NotNull BoardElement boardElement) {
        imageView = new ImageView();
        initImageView(boardElement);
        this.setCenter(imageView);

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
        imageView.setImage(new Image(getImagePath(boardElement)));
        fitImageSize();
        updateRotation(boardElement);
    }

    /**
     * <p>Determines the correct image for a given {@link BoardElement} and
     * returns the path to the corresponding image file. If the correct image
     * can not be determined or if the file cannot be found, the returned string
     * will be empty.</p>
     *
     * @param boardElement the BoardElement for which an image should be determined
     * @return  A string containing the path of an image file relative to the resources directory, or,
     *          if the correct image file could not be found, an empty string.
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    protected String getImagePath(BoardElement boardElement) {
        if (boardElement == null) return "";
        return BASE_DIRECTORY + "/priorityAntenna.png";
    }

    /**
     * <p>Binds the size of {@link #imageView} to the size of this BoardElementView.</p>
     *
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    protected void fitImageSize() {
        imageView.fitWidthProperty().bind(this.widthProperty());
        imageView.fitHeightProperty().bind(this.heightProperty());
        imageView.setPreserveRatio(true);
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
        int direction = 0;
        if (boardElement.getDirection() != null) direction = boardElement.getDirection().ordinal();
        imageView.setRotate(direction * 90);
    }


    @Override
    public void updateView(Subject subject) {
        if (!(subject instanceof BoardElement)) return;
        updateRotation((BoardElement) subject);
    }

}
