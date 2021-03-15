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

/**
 * A heading along a basis vector.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Rasmus Nylander, s205418@student.dtu.dk
 *
 */
public enum Heading {
    /**
     * <p>Represents the vector</p>
     * <table cellspacing="0" cellpadding="0"><tbody><tr>
     *         <td><nobr><font style="font-size:3.1em; font-weight:1" >(</font></nobr></td>
     *         <td><table><tbody>
     *             <tr><td align="center">0</td></tr>
     *             <tr><td align="center">&minus;1</td></tr>
     *         </tbody></table></td>
     *     <td><font style="font-size:3.1em">)</font></td>
     * </tr></tbody></table>
     *
     */
    SOUTH,
    /**
     * Represents the vector (-1, 0)
     */
    WEST,
    /**
     * Represents the vector (0, 1)
     */
    NORTH,
    /**
     * Represents the vector (1, 0)
     */
    EAST;

    /**
     * <p>Returns the {@link Heading} resulting from a turn of -π/4, i.e. 90° clockwise.</p>
     * <p><pre>
     *         NORTH
     *      ↗         ↘
     *  WEST           EAST
     *      ↖         ↙
     *         SOUTH
     * </pre></p>
     *
     * @return A heading turned -π/4, i.e. 90° clockwise, from this one
     */
    public Heading next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    /**
     * <p>Returns the {@link Heading} resulting from a turn of π/4, i.e. 90° anti-clockwise.</p>
     * <p><pre>
     *         NORTH
     *      ↙         ↖
     *  WEST           EAST
     *      ↘         ↗
     *         SOUTH
     * </pre></p>
     *
     * @return A heading turned π/4, i.e. 90° anti-clockwise, from this one
     */
    public Heading prev() {
        return values()[(this.ordinal() + values().length - 1) % values().length];
    }
}
