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
 * Premade enumeration of options 
 * @author Scott Maday
 */
public enum StreamConfiguration {
	OP25("--clock-jitter=500 --network-caching=0 --demux=rawaud --rawaud-channels 1 --rawaud-samplerate 8000");
	
	private final String mOptions;
	
	private StreamConfiguration(String options) {
		mOptions = options;
	}
	
	/**
	 * Attempts to get the StreamConfiguration from a string
	 * @param configuration case insensitive string to get the StreamConfiguration
	 * @return {@link StreamConfiguration} if the configuration is valid, null otherwise
	 */
	public static StreamConfiguration fromString(String configuration) {
		String configUpper = configuration.toUpperCase();
		for(StreamConfiguration config : StreamConfiguration.values()) {
			if(config.name().toUpperCase().equals(configUpper)) {
				return config;
			}
		}
		return null;
	}
	
	public String[] getOptions() {
		return mOptions.split(" ");
	}
}
