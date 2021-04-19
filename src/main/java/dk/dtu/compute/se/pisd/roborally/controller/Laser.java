package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.interfaces.Directional;
import dk.dtu.compute.se.pisd.roborally.interfaces.IActivateable;
import dk.dtu.compute.se.pisd.roborally.interfaces.Spacebound;
import dk.dtu.compute.se.pisd.roborally.model.board.Space;
import dk.dtu.compute.se.pisd.roborally.model.enums.DamageType;
import dk.dtu.compute.se.pisd.roborally.model.enums.Heading;

/**
 * Represents a laser. e.g. a robot laser or board laser.
 *
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public class Laser implements Observer, IActivateable, Spacebound, Directional {
	/**
	 * <p>The activation priority of this laser</p>
	 */
	private int priority;
	/**
	 * <p>Whether the laser is actually a laser, and not
	 * something like a plasma gun</p>
	 */
	protected boolean isEM;
	/**
	 * <p>The damage type of the laser. Should
	 * generally be {@link DamageType#LASER}.</p>
	 */
	protected DamageType damageType;
	/**
	 * <p>The current position of the laser.</p>
	 */
	protected transient Space position;
	/**
	 * <p>The direction the laser is currently pointing.</p>
	 * <p>Heading is not {@code transient} as it may be separate
	 * from the heading of the wielder of the laser.</p>
	 */
	protected Heading direction;

	public Laser(int priority, boolean isEM, DamageType damageType){
		this.priority = priority;
		this.isEM = isEM;
		this.damageType = damageType;
	}

	public Laser(int priority, boolean isEM, DamageType damageType, Subject subject){
		this(priority, isEM, damageType);
		if (!(subject instanceof Spacebound) || !(subject instanceof Directional)) {
			assert false;
			return;
		}
		subject.attach(this);
		update(subject);
	}

	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isEM() {
		return isEM;
	}

	public Space getSpace() {
		return position;
	}
	public void setSpace(Space position) {
		this.position = position;
	}

	@Override
	public Heading getDirection() {
		return direction;
	}

	public void setDirection(Heading direction) {
		this.direction = direction;
	}

	/**
	 * <p>Fires the laser in the direction it is currently
	 * pointing.</p>
	 * @author Rasmus Nylander, s205418@student.dtu.dk
	 */
	@Override
	public void activate() {
		Space currentSpace = getSpace();
		if (currentSpace == null) return;

		while (true){
			currentSpace = currentSpace.getNeighbor(direction, isEM);
			if (currentSpace == null) return;
			if (currentSpace.getPlayer() == null) continue;

			currentSpace.getPlayer().playerController.damage(damageType);
			return;
		}
	}

	/**
	 * <p>Updates the current position and direction to
	 * align with the of the subject.</p>
	 * @param subject the subject which changed
	 */
	@Override
	public void update(Subject subject) {
		if (!(subject instanceof Directional) || !(subject instanceof Spacebound)) {
			return;
		}
		setSpace(((Spacebound) subject).getSpace());
		setDirection(((Directional) subject).getDirection());
	}


}
