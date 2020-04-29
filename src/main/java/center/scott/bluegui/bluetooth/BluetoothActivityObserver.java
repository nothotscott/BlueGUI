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
package center.scott.bluegui.bluetooth;

/**
 * Interface for receiving notifications based on when bluetooth activity changes
 * @author Scott Maday
 */
public interface BluetoothActivityObserver {
	
	/**
	 * Notification for when a track has been changed
	 * @param track that has been changed to
	 */
	public void trackChanged(Track track);
	
	/**
	 * Notification for when the status has been changed
	 * @param status that has been changed to.
	 */
	public void statusChanged(Status status);
	
	/**
	 * Notification for when the volume has been changed
	 * @param volume that the output is changing to
	 */
	public void volumeChanged(Volume volume);
}
