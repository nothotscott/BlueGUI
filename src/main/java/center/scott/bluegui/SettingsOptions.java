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

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * 
 * @author Scott Maday
 */
public class SettingsOptions extends Options {
	private static final long serialVersionUID = 1L;
	
	public static final String APP_USAGE = "-uri <uri> [-param1 <arg1> ...]";
	public static final int SCANMAX_DEFAULT = 10000;
	
	public SettingsOptions() {
		addOption("h",		"help",			false,	"Prints help");
		addOption("f",		"fullscreen", 	false,	"Sets the main window to fullscreen mode");
		
		addOption("uri",					true,	"The uri to listen to on the media stream.", true);
		addOption("c",		"config", 		true,	"Configures vlc with premade configuarion parameters");
		addOption("s",		"scanmax", 		true,	"The maximum amount of time in miliseconds for the default bluetooth dongle to initally scan for devices. The default is " + SCANMAX_DEFAULT);
		addOption("a",		"amplify", 		true,	"Optionally amplifies the stream to the specified volume, 0-100");
	}
	
	/**
	 * Adds an option based off the constructor {@link org.apache.commons.cli.Option#Option(String, boolean, String)}
	 * @param required sets if the option is required
	 * @see org.apache.commons.cli.Option#Option(String, boolean, String)
	 * @see org.apache.commons.cli.Option#setRequired(boolean)
	 */
	public Options addOption(String opt, boolean hasArg, String description, boolean required) {
		Option option = new Option(opt, hasArg, description);
		option.setRequired(required);
		return addOption(option);
	}
}
