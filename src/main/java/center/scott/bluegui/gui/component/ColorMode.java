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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

/**
 * Represents a color theme for components
 * @author Scott Maday
 */
public enum ColorMode {
	LIGHT(Color.WHITE, Color.BLACK),
	DARK(Color.BLACK, Color.WHITE);
	
	public final static ColorMode DEFAULT_COLORMODE = ColorMode.LIGHT;
	
	private final Color mBackgroundColor;
	private final Color mForegroundColor;
	
	private ColorMode(Color backgroundColor, Color foregroundColor) {
		mBackgroundColor = backgroundColor;
		mForegroundColor = foregroundColor;
	}
	
	public Color getBackgroundColor() {
		return mBackgroundColor;
	}

	public Color getForegroundColor() {
		return mForegroundColor;
	}

	/**
	 * Recolors GUI to this color
	 * @param parent and all descendants that will be recolored, unless they implement {@link Colorable#canColor(ColorMode)} and return <code>false</code>
	 */
	public void recolorComponent(Container parent) {
		if(parent instanceof Colorable == false || ((Colorable)parent).canColor(this) == true) {
			parent.setBackground(mBackgroundColor);
			parent.setForeground(mForegroundColor);
			for(Component child : parent.getComponents()) {
				recolorComponent((Container)child);
			}
		}
	}
	
	public String toString() {
		return name().toLowerCase();
	}
}

