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

import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Generic vlc Media Stream. Keep a class instance of any new objects to prevent garbage collection.
 * This follows whats similar in <a href="http://capricasoftware.co.uk/projects/vlcj-4/tutorials/basic-audio-player">http://capricasoftware.co.uk/projects/vlcj-4/tutorials/basic-audio-player</a>
 * @author Scott Maday
 */
public class MediaStreamPlayer {
	private final static Logger LOGGER = LoggerFactory.getLogger(MediaStreamPlayer.class);
	private final static int STREAM_INACTIVATION_REFRESH_INTERVAL = 100;
	
	private final String mUri;
	private final AudioPlayerComponent mAudioPlayerComponent;
	
	private Thread mThread;
	private boolean mReleased = false;
	private boolean mMuted = false;
	private int mVolume = 50;
	private int mStreamInactivationThreshold = 2000;
	private AtomicBoolean mStreamActive = new AtomicBoolean(false);
	private List<StreamActivityObserver> mObservers = new ArrayList<StreamActivityObserver>();
	
	/**
	 * Creates a new media stream with no options
	 * @param uri the mrl
	 * @see uk.co.caprica.vlcj.factory.MediaPlayerFactory
	 * @see uk.co.caprica.vlcj.player.component.AudioPlayerComponent
	 */
	public MediaStreamPlayer(String uri) {
		mUri = uri;
		MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
		mAudioPlayerComponent = new AudioPlayerComponent(mediaPlayerFactory);
		init();
	}
	/**
	 * Creates a new media stream with options from a predefined {@link StreamConfiguration}
	 * @param uri the mrl
	 * @param configuration a {@link StreamConfiguration} 
	 * @see uk.co.caprica.vlcj.factory.MediaPlayerFactory
	 * @see uk.co.caprica.vlcj.player.component.AudioPlayerComponent
	 */
	public MediaStreamPlayer(String uri, StreamConfiguration configuration) {
		mUri = uri;
		MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(configuration.getOptions());
		mAudioPlayerComponent = new AudioPlayerComponent(mediaPlayerFactory);
		init();
	}
	/**
	 * Creates a new media stream with options
	 * @param uri the mrl
	 * @param options Options that will be used by {@link uk.co.caprica.vlcj.factory.MediaPlayerFactory#MediaPlayerFactory(String... libvlcArgs)}
	 * @see uk.co.caprica.vlcj.factory.MediaPlayerFactory
	 * @see uk.co.caprica.vlcj.player.component.AudioPlayerComponent
	 */
	public MediaStreamPlayer(String uri, String[] options) {
		mUri = uri;
		MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(options);
		mAudioPlayerComponent = new AudioPlayerComponent(mediaPlayerFactory);
		init();
	}
	
	private void init() {
		mAudioPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaStreamEventAdapter());
	}
	
	
	/**
	 * Begins listening and outputting the stream on a separate thread.
	 * Audio may still be muted from last session, using {@link #setMute(boolean)} and {@link #setVolume(int)} afterwards is recommended if you wish to ensure an audible stream.
	 * @return <code>true</code> on successfully playing the stream, else <code>false</code> if there was an error, if the steam {@link #isReleased()}, or if the stream {@link #isPlaying()}
	 * @see #isPlaying()
	 * @see uk.co.caprica.vlcj.player.base.MediaApi#play(String mrl, String... options)
	 */
	public boolean play() {
		if(isReleased() || isPlaying()) {
			return false;
		}
		mThread = new Thread();
		boolean result = mAudioPlayerComponent.mediaPlayer().media().play(mUri);
		try {
			mThread.join();
		} catch (InterruptedException e) {
			LOGGER.error("Could not join MediaStreamPlayer thread to AudioPlayerComponent thread: ", e);
			result = false;
		}
		return result;
	}
	
	/**
	 * Joins the current thread calling this method to the thread that this {@link MediaStreamPlayer} is using
	 * @return <code>true</code> if successfully joined or <code>false</code> otherwise
	 * @see java.lang.Thread#currentThread()
	 * @see java.lang.Thread#join()
	 */
	public boolean join() {
		try {
			Thread.currentThread().join();
			return true;
		} catch (InterruptedException e) {
			LOGGER.error("Could not join current thread to MediaStreamPlayer thread: ", e);
		}
		return false;
	}
	
	/**
	 * Stops the listening outputting of the stream
	 * @see uk.co.caprica.vlcj.player.base.MediaApi#stop()
	 */
	public void stop() {
		if(isReleased()) {
			return;
		}
		mAudioPlayerComponent.mediaPlayer().controls().stop();
	}
	
	/**
	 * @return <code>true</code> if the stream is playing, else <code>false</code> if not or the stream {@link #isReleased()}
	 * @see #play()
	 * @see uk.co.caprica.vlcj.player.base.StatusApi#isPlaying()
	 */
	public boolean isPlaying() {
		if(isReleased()) {
			return false;
		}
		return mAudioPlayerComponent.mediaPlayer().status().isPlaying();
	}
	
	/**
	 * Sets the mute of the stream output
	 * @param mute <code>true</code> to mute, <code>false</code> to unmute
	 * @see uk.co.caprica.vlcj.player.base.AudioApi#setMute(boolean)
	 */
	public void setMute(boolean mute) {
		mMuted = mute;
		mAudioPlayerComponent.mediaPlayer().audio().setMute(mute);
	}
	/**
	 * @return <code>true</code> If the stream is muted, <code>false</code> otherwise
	 */
	public boolean isMute() {
		return mMuted;
	}
	
	/**
	 * Sets the stream volume
	 * @param volume percentage between 0 and 100, anything past 100 may cause distortion.
	 * @return <code>true</code> if the stream is successful, else <code>false</code> if not or the stream {@link #isReleased()}
	 * @see uk.co.caprica.vlcj.player.base.AudioApi#setVolume(int)
	 */
	public boolean setVolume(int volume) {
		mVolume = volume;
		if(isReleased()) {
			return false;
		}
		return mAudioPlayerComponent.mediaPlayer().audio().setVolume(volume);
	}
	/**
	 * Gets the stream volume
	 * @return volume percentage between 0 and 200, or -1 if the stream {@link #isReleased()}
	 */
	public int getVolume() {
		if(isReleased()) {
			return -1;
		}
		return mVolume;
	}
	
	/**
	 * Releases media components
	 * @see uk.co.caprica.vlcj.player.component.AudioPlayerComponent#release()
	 */
	public void dispose() {
		mReleased = true;
		mAudioPlayerComponent.mediaPlayer().submit(new Runnable() {
            @Override
            public void run() {
            	mAudioPlayerComponent.mediaPlayer().release();
            }
        });
	}
	
	/**
	 * This method will only return <code>true</code> if {@link #dispose()} has been called prior
	 * @return <code>true</code> if the stream has been released
	 * @see #dispose()
	 */
	public boolean isReleased() {
		return mReleased;
	}
	
	/**
	 * Sets the threshold where the stream is deemed inactive
	 * @param streamInactivationThreshold the threshold in miliseconds
	 */
	public void setStreamInactivationThreshold(int streamInactivationThreshold) {
		mStreamInactivationThreshold = streamInactivationThreshold;
	}
	
	/**
	 * Adds the stream observer that will be receiving notifications
	 * @param observer to be removed
	 * @return <code>true</code> if the observer was removed successfully
	 * @see StreamActivityObserver
	 * @see java.util.List#add(Object)
	 */
	public void addObserver(StreamActivityObserver observer) {
		mObservers.add(observer);
	}
	
	/**
	 * Removes the stream observer from receiving notifications
	 * @param observer to be removed
	 * @return <code>true</code> if the observer was removed successfully
	 * @see StreamActivityObserver
	 * @see java.util.List#remove(Object)
	 */
	public boolean removeObserver(StreamActivityObserver observer) {
		return mObservers.remove(observer);
	}
	
	
	private class MediaStreamEventAdapter extends MediaPlayerEventAdapter implements Runnable {
		private long lastSignalTime;
		
		@Override
		public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
			lastSignalTime = System.currentTimeMillis();
			if(mStreamActive.get() == false) {
				mStreamActive.set(true);
				
				if(mAudioPlayerComponent.mediaPlayer().audio().isMute() != mMuted) {
					mAudioPlayerComponent.mediaPlayer().audio().setMute(mMuted);
				}
				if(mVolume >= 0 && mAudioPlayerComponent.mediaPlayer().audio().volume() != mVolume) {
					mAudioPlayerComponent.mediaPlayer().audio().setVolume(mVolume);
				}
				
				new Thread(this).start();
				new MediaStreamTimeChangedEventThread().start();
			}
		}
		@Override
		public void run() {
			Iterator<StreamActivityObserver> observersIterator = mObservers.iterator();
			while(observersIterator.hasNext()) {
				observersIterator.next().streamActivated();
			}
		}
		
		
		private class MediaStreamTimeChangedEventThread extends Thread {
			@Override
			public void run() {
				LOGGER.debug("Beginning {} from MediaStreamEventAdapter", getName());
				while(mStreamActive.get()) {
					try {
						Thread.sleep(STREAM_INACTIVATION_REFRESH_INTERVAL);
					} catch (InterruptedException e) {
						LOGGER.error("Error in MediaStreamTimeChangedEventThread worker suspending the thread: {}", e);
						break;
					}
					
					if(mStreamActive.get() == false) {
						break;
					}
					long currentTime = System.currentTimeMillis();
					mStreamActive.set(currentTime - lastSignalTime <= mStreamInactivationThreshold && mStreamActive.get());
				}
				
				Iterator<StreamActivityObserver> observersIterator = mObservers.iterator();
				while(observersIterator.hasNext()) {
					observersIterator.next().streamInactivated();
				}
				
			}
		}
		
		
	}
	
}
