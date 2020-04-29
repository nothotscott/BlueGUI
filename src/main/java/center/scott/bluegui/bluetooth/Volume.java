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

import java.awt.EventQueue;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;

import org.freedesktop.dbus.types.UInt16;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import center.scott.bluegui.Audio;

/**
 * Volume control for master volume by bluetooth.
 * This class may seem silly, but it's useful for keeping track of the event queue when changing the master volume
 * @author Scott Maday
 */
public class Volume implements Runnable {
	private final static Logger LOGGER = LoggerFactory.getLogger(Volume.class);
	private final static Vector<Volume> VOLUME_EVENTS = new Vector<Volume>();
	
	private int mVolume = 0;
	private AtomicBoolean mIsBeingSet = new AtomicBoolean(false);
	
	/**
	 * Creates a representation of a volume value from a property.
	 * @param propertyObject
	 */
	public Volume(Object propertyObject) {
		if(propertyObject.getClass().equals(UInt16.class)) {
			short volume16 = ((UInt16)propertyObject).shortValue();
			mVolume = (volume16 * 100) / 0x7f;
		}else {
			LOGGER.warn("Property value is not UInt16");
		}
	}
	/**
	 * Creates a representation of a volume value
	 * @param volume as an int
	 */
	public Volume(int volume) {
		mVolume = volume;
	}
	
	/**
	 * Represents the volume as an integer
	 * @return int from 0 to 100
	 */
	public int asInt() {
		return mVolume;
	}
	/**
	 * Represents the volume as a float
	 * @return float from 0 to 1
	 */
	public float asFloat() {
		return (float)mVolume / 100f;
	}
	
	/**
	 * Lists this volume to be set as the master output volume.
	 * This will remove other volumes not currently being set
	 */
	public void setMasterOutputVolume() {
		Iterator<Volume> volumeEventIterator = VOLUME_EVENTS.iterator();
		while(volumeEventIterator.hasNext()) {
			Volume volume = volumeEventIterator.next();
			if(!volume.mIsBeingSet.get()) {
				volumeEventIterator.remove();
				VOLUME_EVENTS.remove(volume);
			}
		}
		VOLUME_EVENTS.add(this);
		EventQueue.invokeLater(this);
	}
	
	/**
	 * Sets master output volume to this volume synchronously if this volume object is in the event list. It will remove itself from the event list when finished
	 */
	@Override
	public void run() {
		mIsBeingSet.set(true);
		if(VOLUME_EVENTS.contains(this)) {
			try{
				Line line = Audio.getMasterOutputLine();
				if(Audio.open(line)) {
					FloatControl control = Audio.getVolumeControl(line);
					float range = control.getMaximum() - control.getMinimum();
					float value = (range * asFloat()) + control.getMinimum();
					//float db = 20f * (float) Math.log10(value);
					//System.out.println("Set " + value + "/" + range + ": " + db + "db");
					/**
					 * I have no idea how or why volume needs to be adjusted. This is the model I found best works.
					 * If anyone knows the proper function, please change it and let me know.
					 */
					float adjusted = (float)((Math.log(value) + Math.log(64))/Math.log(64));
					//System.out.println("Set " + value + "/" + range + ": " + adjusted);
					if(value <= 0) {
						control.setValue(0);
					} else {
						control.setValue(adjusted);
					}
					line.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			VOLUME_EVENTS.remove(this);
		}
		mIsBeingSet.set(false);
	}
	
	@Override
	public String toString() {
		return String.valueOf(asInt());
	}
}
