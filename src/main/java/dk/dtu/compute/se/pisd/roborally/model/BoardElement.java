package dk.dtu.compute.se.pisd.roborally.model;

/**
 * Represents the element on the board, e.g. walls, push panels, the priority antenna, etc.
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public abstract class BoardElement {
    public boolean passable;
    public boolean isOpaque;

    protected BoardElement(boolean passable, boolean isOpaque){
        this.passable = passable;
        this.isOpaque = isOpaque;
    }


    public boolean isPassable(){
        return passable;
    }

    public boolean isOpaque() {
        return isOpaque;
    }
}
