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
 * Status specified by bluez
 * @author Scott Maday
 * @see <a href="https://git.kernel.org/pub/scm/bluetooth/bluez.git/tree/doc/media-api.txt">/bluez.git/tree/doc/media-api.txt</a>
 */
public enum Status {
	PLAYING,
	STOPPED,
	PAUSED,
	FORWARD_SEEK,
	REVERSE_SEEK,
	ERROR;
	
	public static Status fromString(String value) {
		value = value.toUpperCase().replace('-', '_');
		for(Status status : Status.values()) {
			if(status.name().toUpperCase().equals(value)) {
				return status;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return name().toLowerCase().replace('_', '-');
	}
}
