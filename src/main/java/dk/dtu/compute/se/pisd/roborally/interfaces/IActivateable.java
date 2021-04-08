package dk.dtu.compute.se.pisd.roborally.interfaces;

import dk.dtu.compute.se.pisd.roborally.model.board.boardElement.activationElements.ActivationElement;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Represents something that can be activated, e.g. an {@link ActivationElement} or a robot laser.</p>
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public interface IActivateable extends Comparable<IActivateable>{

    /**
     * <p>Activates the activateable object.</p>
     */
    public void activate();

    /**
     * <p>Returns the activation priority of the activateable object.</p>
     * @return an int representing the activation priority of the activateable object.
     */
    public int getPriority();

    /**
     *
     * <p>Implements {@link Comparable#compareTo} comparing the activation
     * priority of this object to the specified object. It will return
     * identically to
     * {@code Integer.compare(getPriority, activateable.getPriority())}</p>
     * @param activateable the IActivateable to compare to
     * @return  A negative integer, zero, or positive integer for the
     *          priority of this object being smaller than, equal to,
     *          or greater than the specified object's priority
     *          respectively.
     * @see Integer#compare(int, int)
     * @see #getPriority
     */
    default public int compareTo(@NotNull IActivateable activateable){
        return Integer.compare(getPriority(), activateable.getPriority());
    }


}
