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

import center.scott.bluegui.stream.MediaStreamPlayer;
import center.scott.bluegui.stream.StreamConfiguration;
import ch.qos.logback.classic.Level;

import java.awt.EventQueue;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import center.scott.bluegui.bluetooth.MediaBluetoothPlayer;
import center.scott.bluegui.bluetooth.UnsupportedOperatingSystemException;
import center.scott.bluegui.gui.MainFrame;

public class Main implements Runnable, MediaController {
	private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	private MainFrame mMainFrame;
	private MediaStreamPlayer mMediaPlayer;
	private MediaBluetoothPlayer mBluetoothPlayer; 
	private MediaSwitcher mSwitcher;
	private CommandLine mCmd;
	
	public static void main(String[] args){
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.INFO);
		
		Main main = new Main(args);
	}
	
	public Main(String[] args) {
		// Parse arguments
		SettingsOptions settingsOptions = new SettingsOptions();
		CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
			mCmd = parser.parse(settingsOptions, args);
		} catch (ParseException e) {
			LOGGER.error("Could not parse cli arguments: ", e);
			System.out.println("Command Help: ");
			formatter.printHelp(SettingsOptions.APP_USAGE, settingsOptions);
			System.exit(1);
		}
        if(mCmd.hasOption("help")) {
        	formatter.printHelp(SettingsOptions.APP_USAGE, settingsOptions);
        }
        StreamConfiguration config = null;
        if(mCmd.hasOption("config")) {
        	String configuration = mCmd.getOptionValue("config");
        	config = StreamConfiguration.fromString(configuration);
        	if(config == null) {
        		LOGGER.error("Configuration '{}', does not exist. Running with no configuration", configuration);
        	}
        }
        
		// Initialize
        if(config == null) {
        	mMediaPlayer = new MediaStreamPlayer(mCmd.getOptionValue("uri"));
        }else {
        	mMediaPlayer = new MediaStreamPlayer(mCmd.getOptionValue("uri"), config);
        }
		try {
			mBluetoothPlayer = new MediaBluetoothPlayer();
		} catch (UnsupportedOperatingSystemException e) {
			LOGGER.error(e.toString());
		}
		
		// Create GUI
		EventQueue.invokeLater(this);
		
		// Configure
		mMediaPlayer.play();
		mMediaPlayer.setMute(false);
		mMediaPlayer.setVolume(100);
		if(mBluetoothPlayer != null) {
			mSwitcher = new MediaSwitcher(this);
			mMediaPlayer.addObserver(mSwitcher);
			mBluetoothPlayer.addObserver(mSwitcher);
			int scanmax = SettingsOptions.SCANMAX_DEFAULT;
			if(mCmd.hasOption("scanmax")) {
				try {
					scanmax = Integer.parseInt(mCmd.getOptionValue("scanmax"));
				} catch (NumberFormatException e) {
					LOGGER.error("scanmax parameter could not be convered to an integer");
				}
			}
			mBluetoothPlayer.scanUntilDeviceConnectedThread(scanmax, false).start();
		} else {
			LOGGER.warn("No bluetooth player. This program will not run as intended.");
		}
		
		mMediaPlayer.join();
	}
	
	@Override
	public void run() {
		try {
			mMainFrame = new MainFrame(this, mCmd.hasOption("fullscreen"));
			mMainFrame.setVisible(true);
		} catch (Exception e) {
			LOGGER.error("Could not set Main Frame: ", e);
		}
	}
	
	public MediaStreamPlayer getStreamPlayer() {
		return mMediaPlayer;
	}

	public MediaBluetoothPlayer getBluetoothPlayer() {
		return mBluetoothPlayer;
	}
	
	@Override
	public MediaSwitcher getSwitcher() {
		return mSwitcher;
	}
	
	@Override
	public int getAmplifyVolume() {
		int volume = 0;
		if(mCmd.hasOption("amplify")) {
			try {
				volume = Integer.parseInt(mCmd.getOptionValue("amplify"));
			} catch (NumberFormatException e) { }
		}
		return volume;
	}

	public void disposeMediaPlayers() {
		if(mMediaPlayer != null) {
			//mMediaPlayer.stop();
			mMediaPlayer.dispose();
			mMediaPlayer = null;
		}
		if(mBluetoothPlayer != null) {
			mBluetoothPlayer.dispose();
			mBluetoothPlayer = null;
		}
	}
	
}
