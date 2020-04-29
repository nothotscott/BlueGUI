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
 * 
 * @author Scott Maday
 */
public class UnsupportedOperatingSystemException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public UnsupportedOperatingSystemException(){
	}

	@Override
	public String toString() {
		return "This program cannot run on this operating system. Currently, only linux is supported";
	}
}
