package dk.dtu.compute.se.pisd.roborally.Utils.imageFinder;

import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.BoardElement;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.Wall;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements.BoardLaser;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements.EnergySpace;
import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements.MoveHazard;
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
	final private static String MOVEHAZARD = "movement/";
	final private static String CHECKPOINTS = "checkpoints/";

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

		if (boardElement instanceof EnergySpace){
			return BASE_DIRECTORY + "energySpace.png";
		}

		if (boardElement instanceof BoardLaser){
			return BASE_DIRECTORY + "laserEmitter.png";
		}

		if (boardElement instanceof MoveHazard){
			String moveHazardName = moveHazardName((MoveHazard) boardElement);
			if (moveHazardName == null) return "";
			return BASE_DIRECTORY + MOVEHAZARD + moveHazardName;
		}

		if (boardElement instanceof Checkpoint){
			int number = ((Checkpoint) boardElement).getNumber();
			return BASE_DIRECTORY + CHECKPOINTS + "checkpoint" + number + ".png";
		}

		return "";

	}

	private String moveHazardName (MoveHazard moveHazard){
		if (moveHazard.getDistance() == 0 && moveHazard.getRotation() == 0) return null;
		switch (moveHazard.getMoveHazardType()){
			case CONVEYOR -> {
				return "conveyorBelts/conveyor" + (moveHazard.getDistance() == 1 ? "Green" : "Blue") + ".png";
			}
			case GEAR -> {
				return "gears/gear" + (moveHazard.getRotation() < 0 ? "Anti" : "") + ".png";
			}
			case PUSH_PANEL -> {
				return "pushPanel.png";
			}
			default -> throw new IllegalStateException("Unexpected value: " + moveHazard.getMoveHazardType());
		}
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
