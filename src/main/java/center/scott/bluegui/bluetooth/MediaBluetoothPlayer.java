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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.sound.sampled.FloatControl;

import org.bluez.Adapter1;
import org.bluez.MediaPlayer1;
import org.bluez.exceptions.BluezFailedException;
import org.bluez.exceptions.BluezNotSupportedException;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.connections.impl.DBusConnection.DBusBusType;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.handlers.AbstractPropertiesChangedHandler;
import org.freedesktop.dbus.interfaces.Properties.PropertiesChanged;
import org.freedesktop.dbus.types.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hypfvieh.DbusHelper;
import com.github.hypfvieh.bluetooth.DeviceManager;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothAdapter;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothDevice;

import center.scott.bluegui.Audio;
import center.scott.bluegui.stream.StreamActivityObserver;

/**
 * Concrete but expandable class for interacting with bluetooth interfaces.
 * @apiNote This class is meant for expansion via more specialized subclasses
 * @author Scott Maday
 */
public class MediaBluetoothPlayer {
	private final static Logger LOGGER = LoggerFactory.getLogger(MediaBluetoothPlayer.class);
	
	private Track mTrack = null;
	protected DeviceManager mDeviceManager = null;
	private boolean mPlaying = false;
	private List<BluetoothActivityObserver> mObservers = new ArrayList<BluetoothActivityObserver>();
	
	/**
	 * Constructs an interactable media player for bluetooth based on {@link org.bluez.MediaPlayer1}
	 * This should likely be followed by {@link #scanUntilDeviceConnected(int, boolean)}
	 * @throws UnsupportedOperatingSystemException when instantiated from an unsupported operating system.
	 * @see org.bluez.MediaPlayer1
	 */
	public MediaBluetoothPlayer() throws UnsupportedOperatingSystemException {
		if(canSupport() == true) {
			try {
				mDeviceManager = DeviceManager.getInstance();
			} catch (IllegalStateException e) {
				try {
					mDeviceManager = DeviceManager.createInstance(false);
				} catch (DBusException e1) {
					LOGGER.error("Error creating DeviceManager: {}", e1);
				}
			}
		} else {
			//LOGGER.error("This program cannot run on this operating system. Currently, only linux is supported");
			throw new UnsupportedOperatingSystemException();
		}
		DBusConnection connection = getConnection();
		if(connection != null) {
			try {
				connection.addSigHandler(PropertiesChanged.class, new BluetoothPropertiesChangedHandler());
			} catch (DBusException e) {
				LOGGER.error("Could not add PropertiesChanged signal handler: {}", e);
			}
		}
	}
	
	/**
	 * @return <code>true</code> if the operating system is supported (Linux), <code>false</code> otherwise
	 */
	public static boolean canSupport() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0;
	}
	
	
	/**
	 * Gets the system DBus connection
	 * @return {@link org.freedesktop.dbus.connections.impl.DBusConnection}, possibly null
	 * @see org.freedesktop.dbus.connections.impl.DBusConnection#getConnection(DBusBusType)
	 */
	protected DBusConnection getConnection() {
		try {
			/*
			 * HACK get the connection with DBusConnection.getConnection
			 * instead of mDeviceManager.getDBusConnection();
			 * This is only for com.github.hypfvieh:bluez-dbus:0.1.1
			 */
			return mDeviceManager != null ? DBusConnection.getConnection(DBusBusType.SYSTEM) : null;
		} catch (DBusException e) {
			LOGGER.error("Could not get DBusConnection: {}", e);
		}
		return null;
	}
	
	/**
	 * Scans for bluetooth devices until a device that's paired and a successful connection is made to it
	 * @param maxTime in milliseconds for the adapter to scan until it gives up and returns <code>false</code>
	 * This parameter is based on the total time for the scan and getting every device. 10000 is what works well for my Raspberry Pi 2b and USB bluetooth adapter.
	 * @param mustTrust enforces the device to be a trust device in order to connect (unless already connected)
	 * @return <code>true</code> if the scan came across a paired device, else <code>false</code> if the time limit was reached or there was an issue
	 * @see com.github.hypfvieh.bluetooth.DeviceManager#scanForBluetoothDevices(int)
	 */
	public boolean scanUntilDeviceConnected(int maxTime, boolean mustTrust) {
		DBusConnection connection = getConnection();
		if(connection == null) {
			return false;
		}
		BluetoothAdapter adapter = mDeviceManager.getAdapter();
		adapter.startDiscovery();
		boolean deviceFound = false;
		int totalTime = 0;
        while(!deviceFound && totalTime <= maxTime) {
        	long scanStartTime = System.currentTimeMillis();
        	for(BluetoothDevice device : mDeviceManager.getDevices(false)) {
        		LOGGER.debug("Bluetooth Device: " + device.getName() + ":" + device.getAddress() + " Paired: " + device.isPaired() + " Trusted: " + device.isTrusted() + " Connected: " + device.isConnected());
        		deviceFound = device.isConnected();
        		if(deviceFound) {
        			break;
        		}
        		if((device.isPaired() && mustTrust == false) || (device.isPaired() && mustTrust == true && device.isTrusted())) {
        			try {
        				deviceFound = device.connect();
        			} catch(Exception e) {
        				LOGGER.error("Could not connect to bluetooth device {}:{}", device.getName(), device.getAddress());
        			}
        		}
        	}
        	totalTime += System.currentTimeMillis() - scanStartTime;
        }
        LOGGER.debug("Total scan time: ", totalTime);
        
        if(adapter.isDiscovering()) {
        	adapter.stopDiscovery();
        }
        return deviceFound;
	}
	/**
	 * Wraps the {@link #scanUntilDeviceConnected(int, boolean)} method in a thread complete with logging.
	 * @return Thread that wraps the method. The thread is not invoked in this method.
	 * @see #scanUntilDeviceConnected(int, boolean)
	 */
	public Thread scanUntilDeviceConnectedThread(int maxTime, boolean mustTrust) {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				LOGGER.info("Scanning for bluetooth devices");
				boolean connected = scanUntilDeviceConnected(maxTime, mustTrust);
				if(connected) {
					LOGGER.info("Found bluetooth device");
				}else {
					LOGGER.warn("Could not find a connected device in time");
				}
			}
		});
	}
	
	/**
	 * Gets the first bluetooth device paired and connected to the default adapter
	 * @return {@link com.github.hypfvieh.bluetooth.wrapper#BluetoothDevice}, possibly null
	 * @see com.github.hypfvieh.bluetooth.DeviceManager#getDevices()
	 */
	protected BluetoothDevice getDevice() {
		if(mDeviceManager == null) {
			return null;
		}
		synchronized (mDeviceManager) {
			Iterator<BluetoothDevice> deviceIterator = mDeviceManager.getDevices(true).iterator();
			while(deviceIterator.hasNext()) {
				BluetoothDevice device = deviceIterator.next();
				if(device.isConnected()) {
					//System.out.println("Got device: " + device.getName() + " : " + device.getAddress());
					return device;
				}
				deviceIterator.remove();
			}
		}
		return null;
	}
	
	/**
	 * Gets the {@link org.bluez.MediaPlayer1} for the default connection and device 
	 * @return {@link org.bluez.MediaPlayer1}, possibly null
	 * @see #getConnection()
	 * @see #getDevice()
	 * @see com.github.hypfvieh.DbusHelper#getRemoteObject(DBusConnection, String, Class)
	 */
	protected MediaPlayer1 getMediaPlayer() {
		DBusConnection connection = getConnection();
		BluetoothDevice device = getDevice();
		return connection != null && device != null ? (MediaPlayer1)DbusHelper.getRemoteObject(connection, device.getDbusPath() + "/player0", MediaPlayer1.class) : null;
	}
	
	
	/**
	 * Plays the bluez {@link org.bluez.MediaPlayer1} based on the default connection and device
	 * @return <code>true</code> if playback successful, <code>false</code> otherwise
	 * @see org.bluez.MediaPlayer1#Play()
	 */
	public boolean play() {
		MediaPlayer1 mediaPlayer = getMediaPlayer();
		if(mediaPlayer == null) {
			return false;
		}
		try {
			mediaPlayer.Play();
			return true;
		} catch (Exception e) {
			LOGGER.error("Could not play: ", e.toString());
		}
		return false;
	}
	
	/**
	 * Pauses the bluez {@link org.bluez.MediaPlayer1} based on the default connection and device
	 * @see org.bluez.MediaPlayer1#Pause()
	 */
	public void pause() {
		MediaPlayer1 mediaPlayer = getMediaPlayer();
		if(mediaPlayer == null) {
			return;
		}
		try {
			mediaPlayer.Pause();
		} catch (Exception e) {
			LOGGER.error("Could not pause: ", e.toString());
		}
	}
	
	/**
	 * Moves the track to the next item
	 * @see org.bluez.MediaPlayer1#Next()
	 */
	public void next() {
		MediaPlayer1 mediaPlayer = getMediaPlayer();
		if(mediaPlayer == null) {
			return;
		}
		try {
			mediaPlayer.Next();
		} catch (Exception e) {
			LOGGER.error("Could not move to the next track: ", e.toString());
		}
	}
	
	/**
	 * Moves the track to the previous item
	 * @see org.bluez.MediaPlayer1#Previous()
	 */
	public void previous() {
		MediaPlayer1 mediaPlayer = getMediaPlayer();
		if(mediaPlayer == null) {
			return;
		}
		try {
			mediaPlayer.Previous();
		} catch (Exception e) {
			LOGGER.error("Could not move to previous track: ", e.toString());
		}
	}
	
	/**
	 * Stops playback
	 * @see org.bluez.MediaPlayer1#Stop()
	 */
	public void stop() {
		MediaPlayer1 mediaPlayer = getMediaPlayer();
		if(mediaPlayer == null) {
			return;
		}
		try {
			mediaPlayer.Stop();
		} catch (Exception e) {
			LOGGER.error("Could not stop: ", e.toString());
		}
	}
	
	/**
	 * Gets if there's media playing.
	 * @return <code>true</code> if it's certain that there's media playing
	 */
	public boolean isPlaying() {
		return getMediaPlayer() != null && mPlaying;
	}
	
	/**
	 * Disconnects and releases bluetooth resources
	 * @see {@link org.freedesktop.dbus.connections.impl.DBusConnection#disconnect}
	 */
	public void dispose() {
		DBusConnection connection = getConnection();
		if(connection != null) {
			connection.disconnect();
		}
		if(mDeviceManager != null) {
			mDeviceManager.closeConnection();
		}
		mDeviceManager = null;
	}
	
	/**
	 * Adds the bluetooth observer that will be receiving notifications
	 * @param observer to add
	 * @return <code>true</code> if the observer was removed successfully
	 * @see BluetoothActivityObserver
	 * @see java.util.List#add(Object)
	 */
	public void addObserver(BluetoothActivityObserver observer) {
		mObservers.add(observer);
	}
	
	/**
	 * Removes the bluetooth observer from receiving notifications
	 * @param observer to remove
	 * @return <code>true</code> if the observer was removed successfully
	 * @see BluetoothActivityObserver
	 * @see java.util.List#remove(Object)
	 */
	public boolean removeObserver(BluetoothActivityObserver observer) {
		return mObservers.remove(observer);
	}
	
	
	
	private class BluetoothPropertiesChangedHandler extends AbstractPropertiesChangedHandler {
		@Override
		public void handle(PropertiesChanged changed) {
			if(changed == null || !changed.getPath().contains("/org/bluez") || !changed.getInterface().contains("org.freedesktop.DBus.Properties")) {
				return;
			}
			Set<Entry<String, Variant<?>>> properties = changed.getPropertiesChanged().entrySet();
			for(Entry<String, Variant<?>> property : properties) {
				String key = property.getKey();
				Variant<?> variant = property.getValue();
				Object value = variant.getValue();
				Iterator<BluetoothActivityObserver> observerIterator = mObservers.iterator();
				switch(key) {
					case "Track": 
						Track track = new Track(value);
						if(!track.equals(mTrack)) {
							mTrack = track;
							while(observerIterator.hasNext()) {
								observerIterator.next().trackChanged(track);
							}
						}
						break;
					case "Status":
						Status status = Status.fromString((String)value);
						if(status == Status.PLAYING) {
							mPlaying = true;
						}else if (status == Status.PAUSED || status == Status.STOPPED) {
							mPlaying = false;
						}
						while(observerIterator.hasNext()) {
							observerIterator.next().statusChanged(status);
						}
						break;
					case "Volume":
						try {
							Volume volume = new Volume(value);
							while(observerIterator.hasNext()) {
								observerIterator.next().volumeChanged(volume);
							}
						} catch(Exception e) {
							LOGGER.error("Error converting or setting bluetooth volume: ", e);
						}
						break;
					case "Percentage":
						System.out.println("Bluetooth Percentage: " + variant.getType() + " : " + value.toString());
						break;
					default:
						//System.out.println("Bluetooth: " + key);
				}
			}
		}
	}
	
}
