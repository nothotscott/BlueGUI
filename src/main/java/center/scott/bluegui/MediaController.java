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
package center.scott.bluegui;

import center.scott.bluegui.bluetooth.MediaBluetoothPlayer;
import center.scott.bluegui.stream.MediaStreamPlayer;

/**
 * An item that is used to control media players. More specifically, a class that has a "has-a" relationship with media players or another MediaController.
 * @author Scott Maday
 */
public interface MediaController {
	
	/**
	 * Gets the current MediaStreamPlayer
	 * @return a {@link MediaStreamPlayer}, or null if there is none or one hasn't been instantiated
	 */
	public MediaStreamPlayer getStreamPlayer();
	
	/**
	 * Gets the current MediaBluetoothPlayer
	 * @return a {@link MediaBluetoothPlayer}, or null if there is none or one hasn't been instantiated
	 */
	public MediaBluetoothPlayer getBluetoothPlayer();
	
	/**
	 * Gets the MediaSwitcher responsible for switching the media players
	 * @return the current {@link MediaSwitcher}, or null if there is none or one hasn't been instantiated
	 */
	public MediaSwitcher getSwitcher();
	
	/**
	 * Gets how much volume there should be for {@link #getStreamPlayer()}
	 * @return int from 1 to 100 of the the volume for media played by {@link #getStreamPlayer()}, or 0 for no amplification
	 */
	public int getAmplifyVolume();
	
	/**
	 * Disposes all media players
	 */
	public void disposeMediaPlayers();
}
