package dk.dtu.compute.se.pisd.roborally.Utils.imageFinder;

import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.BoardElement;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.Wall;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;
import javafx.scene.image.Image;

/**
 * <p>This class is responsible for determining and finding
 * the correct image for a {@link BoardElement}.</p>
 *
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class BoardElementImageFinder {
    final private static String BASE_DIRECTORY = "images/tiles/boardElements/";
    final private static String WALLS = "walls/";

    /**
     * <p>Determines the correct image for a given {@link BoardElement} and
     * returns the path to the corresponding image file. If the correct image
     * can not be determined or if the file cannot be found, the returned string
     * will be empty.</p>
     *
     * @param boardElement the BoardElement for which an image should be determined
     * @return A string containing the path of an image file relative to the resources directory, or,
     * if the correct image file could not be found, an empty string.
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    private String getImagePath(BoardElement boardElement) {
        if (boardElement == null) return "";

        //TODO: this should be done correctly rather than a bunch of if statements

        if (boardElement instanceof Wall){
            Heading direction = boardElement.getDirection();
            if (direction == Heading.NORTH) return BASE_DIRECTORY + WALLS + "wallShadeEast.png";
            return BASE_DIRECTORY + WALLS + "wallShadeNorth.png";
        }

        return "";

    }

    /**
     * <p>Determines the correct image for a given {@link BoardElement} and
     * returns a new {@link Image} of the corresponding image file. If the correct image
     * can not be determined or the file containing the image cannot be found an empty
     * image is returned.</p>
     *
     * @param boardElement the BoardElement for which an image should be retrieved
     * @return A javafx Image object string corresponding to the passed BoardElement
     * @author Rasmus Nylander, s205418@student.dtu.dk
     */
    public Image getImage(BoardElement boardElement) {
        return new Image(getImagePath(boardElement));
    }
}
