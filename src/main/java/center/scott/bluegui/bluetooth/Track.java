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

import org.freedesktop.dbus.DBusMap;
import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Track specified by bluez in https://git.kernel.org/pub/scm/bluetooth/bluez.git/tree/doc/media-api.txt
 * @author Scott Maday
 * @see org.freedesktop.dbus.DBusMap
 * @see <a href="https://git.kernel.org/pub/scm/bluetooth/bluez.git/tree/doc/media-api.txt">/bluez.git/tree/doc/media-api.txt</a>
 */
public class Track {
	private final static Logger LOGGER = LoggerFactory.getLogger(Track.class);
	
	private String mTitle;
	private String mArtist;
	private String mAlbum;
	private String mGenre;
	private int mNumberOfTracks;
	private int mTrackNumber;
	private int mDuration;
	
	/**
	 * Create track based off of a property value
	 * @param propertyValue of the property
	 */
	public Track(Object propertyValue) {
		if(propertyValue.getClass().equals(DBusMap.class)) {
			try {
				DBusMap<String, Variant<?>> dict = (DBusMap<String, Variant<?>>)propertyValue;
				mTitle = ((Variant<String>)dict.get("Title")).getValue();
				mArtist = ((Variant<String>)dict.get("Artist")).getValue();
				mAlbum = ((Variant<String>)dict.get("Album")).getValue();
				mGenre = ((Variant<String>)dict.get("Genre")).getValue();
				mNumberOfTracks = (((Variant<UInt32>)dict.get("TrackNumber")).getValue()).intValue();
				mTrackNumber = (((Variant<UInt32>)dict.get("TrackNumber")).getValue()).intValue();
				mDuration = (((Variant<UInt32>)dict.get("Duration")).getValue()).intValue();
			} catch (Exception e) {
				LOGGER.warn("Could not cast " + e);
			}
		}
	}
	public Track(String title, String artist, String album, String genre, int numberOfTracks, int trackNumber, int duration) {
		mTitle = title;
		mArtist = artist;
		mAlbum = album;
		mGenre = genre;
		mNumberOfTracks = numberOfTracks;
		mTrackNumber = trackNumber;
		mDuration = duration;
	}
	public String getTitle() {
		return mTitle;
	}
	
	public String getArtist() {
		return mArtist;
	}
	
	public String getAlbum() {
		return mAlbum;
	}
	
	public String getGenre() {
		return mGenre;
	}
	
	public int getNumberOfTracks() {
		return mNumberOfTracks;
	}
	
	public int getTrackNumber() {
		return mTrackNumber;
	}
	
	public int getDuration() {
		return mDuration;
	}
	
	public String toString() {
		return "Title=" + mTitle + ", Artist=" + mArtist + ", Album=" + mAlbum + ", Genre=" + mGenre + ", NumberOfTracks=" + mNumberOfTracks + ", TrackNumber=" + mTrackNumber + ", Duration=" + mDuration;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj.getClass().equals(Track.class)) {
			Track track = (Track)obj;
			return track.getTitle().equals(getTitle()) && track.getArtist().equals(getArtist()) && track.getAlbum().equals(getAlbum()) && track.getGenre().equals(getGenre());
			// Too strict to compare numeric properties?
			//	&& track.getNumberOfTracks() == getNumberOfTracks() && track.getTrackNumber() == getTrackNumber() && track.getDuration() == getDuration();
		}
		return false;
	}
}
