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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import center.scott.bluegui.bluetooth.BluetoothActivityObserver;
import center.scott.bluegui.bluetooth.Status;
import center.scott.bluegui.bluetooth.Track;
import center.scott.bluegui.bluetooth.Volume;
import center.scott.bluegui.stream.StreamActivityObserver;

/**
 * Switcher that yields a bluetooth player to {@link StreamActivityObserver#streamActivated()} and {@link StreamActivityObserver#streamInactivated()}
 * @author Scott Maday
 */
public class MediaSwitcher implements StreamActivityObserver, BluetoothActivityObserver {
	private final static Logger LOGGER = LoggerFactory.getLogger(MediaSwitcher.class);
	
	private MediaController mController;
	private boolean mShouldYield;
	private Volume mVolume;
	
	/**
	 * Creates a media switcher
	 * @param controller containing the media players
	 * @param shouldYield to {@link StreamActivityObserver} initially
	 */
	public MediaSwitcher(MediaController controller, boolean shouldYield) {
		mController = controller;
		mShouldYield = shouldYield;
	}
	/**
	 * Creates a media switcher with a default yield parameter of <code>true</code>
	 * @param controller containing the media players
	 */
	public MediaSwitcher(MediaController controller) {
		this(controller, true);
	}
	
	/**
	 * Sets if the bluetooth player should yield to {@link StreamActivityObserver}
	 * @param shouldYield to the stream
	 */
	public void setShouldYield(boolean shouldYield) {
		mShouldYield = shouldYield;
	}
	/**
	 * Gets if the bluetooth player is yielding to {@link StreamActivityObserver}
	 * @return <code>true</code> if the switcher yeilds to the stream, <code>false</code> otherwise
	 */
	public boolean getShouldYield() {
		return mShouldYield;
	}
	
	@Override
	public void streamActivated() {
		if(mShouldYield && mController != null && mController.getStreamPlayer() != null && mController.getBluetoothPlayer() != null) {
			mController.getBluetoothPlayer().pause();
			int ampVolume = mController.getAmplifyVolume();
			if(mVolume != null && ampVolume > 0) {
				new Volume(ampVolume).setMasterOutputVolume();
			}
		}
	}

	@Override
	public void streamInactivated() {
		if(mShouldYield && mController != null && mController.getStreamPlayer() != null && mController.getBluetoothPlayer() != null) {
			mController.getBluetoothPlayer().play();
			if(mVolume != null && mController.getAmplifyVolume() > 0) {
				mVolume.setMasterOutputVolume();
			}
		}
	}
	
	@Override
	public void volumeChanged(Volume volume) {
		mVolume = volume;
	}
	
	@Override
	public void trackChanged(Track track) {
	}
	@Override
	public void statusChanged(Status status) {
	}
}
