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
package center.scott.bluegui.stream;

/**
 * Interface for receiving notifications based on whether a stream has been activated or not
 * @author Scott Maday
 */
public interface StreamActivityObserver {
	
	/**
	 * Notification for when a stream is receiving data and playing audio
	 */
	public void streamActivated();
	
	/**
	 * Notification for when a stream stops receiving data after an inactivity threshold has been exceeded
	 */
	public void streamInactivated();
}
