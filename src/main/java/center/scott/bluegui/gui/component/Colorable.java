/**
 * @author Scott Maday
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package center.scott.bluegui.gui.component;

/**
 * GUI component that can be colored by the current {@link ColorMode}
 * @author Scott Maday
 */
public interface Colorable {
	
	/**
	 * This method is invoked every time the component or one of its ancestors is recolored
	 * @param colorMode that's being changed to.
	 * @return <code>true</code> if the component can be colored, <code>false</code> otherwise
	 */
	public boolean canColor(ColorMode colorMode);
}
