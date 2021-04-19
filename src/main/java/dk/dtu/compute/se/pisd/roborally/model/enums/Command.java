/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.model.enums;

import java.util.List;

/**
 * <p>A Command that takes a name and other commands as options.
 * A lone command, a command with no options, represents an
 * instruction given to and performed by a robot or board element.
 * A command containing other commands represents the choice of
 * one of those commands.</p>
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Tobias Maneschijn, s205422@student.dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 */
public enum Command {
	/**
	 * Move forward by one.
	 */
	FORWARD("Fwd"),
	/**
	 * Turn -π/4, i.e. 90° clockwise.
	 */
	RIGHT("Turn Right"),
	/**
	 * Turn π/4, ie 90° anti-clockwise.
	 */
	LEFT("Turn Left"),
	/**
	 * Move forward by two.
	 */
	FAST_FORWARD("Fast Fwd"),
	/**
	 * Player choice between {@link #LEFT} and {@link #RIGHT}.
	 */
	OPTION_LEFT_RIGHT("Left OR Right", LEFT, RIGHT);

	/**
	 * <p>The name of the command</p>
	 */
	final public String displayName;

	/**
	 * <p>The list of commands that can be chosen between.</p>
	 */
	final private List<Command> options;

	/**
	 * <p>Creates an instance of Command</p>
	 *
	 * @param displayName The display name of the command
	 * @param options     A list of Command objects representing the possible commands to choose between
	 */
	Command(String displayName, Command... options) {
		this.displayName = displayName;
		this.options = List.of(options);
	}

	/**
	 * <p>Returns true if the commands is interactive.</p>
	 * <p>An interactive command lets the player select between options.</p>
	 *
	 * @return A boolean indicating if the command is interactive
	 */
	public boolean isInteractive() {
		return !options.isEmpty();
	}

	/**
	 * <p>Returns the commands that can be selected between.</p>
	 *
	 * @return A list of possible commands
	 */
	public List<Command> getOptions() {
		return options;
	}

}