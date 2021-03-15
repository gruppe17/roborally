package dk.dtu.compute.se.pisd.roborally.model.boardElement;

import dk.dtu.compute.se.pisd.roborally.model.Heading;


public class wallElement extends BoardElement{


    public wallElement(Heading posistion){
        super(northEastSouthWestArray(), northEastSouthWestArray(), posistion);
    }

    public Heading getPosition(){
        return direction;
    }

    /**
     * <p>Returns a new {@link Heading} array containing
     * {{@link Heading#NORTH}, {@link Heading#EAST},{@link Heading#SOUTH},
     * {@link Heading#WEST}}. This is done, because there seems to be a
     * problem with importing, resulting in ridiculously long names.</p>
     * @return A new Heading array containing the four cardinal directions
     */
    private static Heading[] northEastSouthWestArray(){
        return new Heading[] {
            dk.dtu.compute.se.pisd.roborally.model.Heading.NORTH, dk.dtu.compute.se.pisd.roborally.model.Heading.EAST,
                    dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH, dk.dtu.compute.se.pisd.roborally.model.Heading.WEST
        };
    }
}
