package dk.dtu.compute.se.pisd.roborally;

import dk.dtu.compute.se.pisd.roborally.model.boardElement.BoardElement;

/**
 * <p>This class is responsible for determining and finding
 * the correct image for a {@link BoardElement}.</p>
 *
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class BoardElementImageFinder {
    final private static String BASE_DIRECTORY = "images/tiles/boardElements";

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
    public String getImagePath(BoardElement boardElement) {
        if (boardElement == null) return "";
        return BASE_DIRECTORY + "/priorityAntenna.png";
    }
}
