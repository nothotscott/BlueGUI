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
package center.scott.bluegui.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import center.scott.bluegui.Audio;
import center.scott.bluegui.MediaController;
import center.scott.bluegui.SettingsOptions;
import center.scott.bluegui.bluetooth.BluetoothActivityObserver;
import center.scott.bluegui.bluetooth.MediaBluetoothPlayer;
import center.scott.bluegui.bluetooth.Status;
import center.scott.bluegui.bluetooth.Track;
import center.scott.bluegui.bluetooth.Volume;
import center.scott.bluegui.gui.component.ColorMode;
import center.scott.bluegui.gui.component.ImageResource;
import center.scott.bluegui.gui.component.JButtonColorable;
import center.scott.bluegui.stream.StreamActivityObserver;

import java.awt.GridBagLayout;
import javax.swing.JProgressBar;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.Insets;
import javax.swing.SwingConstants;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main frame for controlling media
 * @author Scott Maday
 */
public class MainFrame extends JFrame implements StreamActivityObserver, BluetoothActivityObserver, Runnable {
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);
	
	private MediaController mController;
	private boolean mFullScreen;
	private ColorMode mColorMode = ColorMode.DEFAULT_COLORMODE;
	
	private ImageResource mSoundMutedImageResource = new ImageResource("volume-muted.png");
	private ImageResource mSoundImageResource = new ImageResource("volume-high.png");
	private ImageResource mPauseImageResource = new ImageResource("pause.png");
	private ImageResource mPlayImageResource = new ImageResource("play.png");
	private ImageResource mMediaImageResource = new ImageResource("network.png");
	private ImageResource mMediaMutedImageResource = new ImageResource("network-muted.png");
	private ImageResource mBluetoothImageResource = new ImageResource("bluetooth.png");
	private ImageResource mBluetoothStoppedImageResource = new ImageResource("bluetooth-stopped.png");
	
	private JPanel contentPane;
	private JLabel mTitleLabel;
	private JLabel mArtistLabel;
	private JLabel mAlbumLabel;
	private JPanel mediaPanel;
	private JButtonColorable mPrevButton;
	private JButtonColorable mNextButton;
	private JButtonColorable mPlayPauseButton;
	private JPanel sourcePanel;
	private JButtonColorable mMediaButton;
	private JButtonColorable mBluetoothButton;
	private JPanel configurationPanel;
	private JPanel volumePanel;
	private JButtonColorable mVolumeButton;
	private JProgressBar mVolumeBar;
	private JButtonColorable mColorModeButton;
	private JButtonColorable mBluetoothConfigButton;
	private JButtonColorable mCloseButton;

	/**
	 * Creates the main frame
	 * @param controller
	 * @param fullScreen
	 */
	public MainFrame(MediaController controller, boolean fullScreen) {
		mController = controller;
		mFullScreen = fullScreen;
		
		if(mController != null) {
			if(mController.getStreamPlayer() != null) {
				mController.getStreamPlayer().addObserver(this);
			}
			if(mController.getBluetoothPlayer() != null) {
				mController.getBluetoothPlayer().addObserver(this);
			}
		}
		
		init();
		EventQueue.invokeLater(this);
	}
	
	private void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 480, 320); // 3.5in lcd
		if(mFullScreen) {
			setExtendedState(JFrame.MAXIMIZED_BOTH); 
			setUndecorated(true);
			setVisible(true);
		}
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{40, 0, 0, 0, 50, 50, 40, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(mController != null) {
					mController.disposeMediaPlayers();
				}
			}
		});
		
		volumePanel = new JPanel();
		GridBagConstraints gbc_volumePanel = new GridBagConstraints();
		gbc_volumePanel.insets = new Insets(0, 0, 5, 0);
		gbc_volumePanel.fill = GridBagConstraints.BOTH;
		gbc_volumePanel.gridx = 0;
		gbc_volumePanel.gridy = 0;
		contentPane.add(volumePanel, gbc_volumePanel);
		GridBagLayout gbl_volumePanel = new GridBagLayout();
		gbl_volumePanel.columnWidths = new int[]{100, 0, 0};
		gbl_volumePanel.rowHeights = new int[]{40, 0};
		gbl_volumePanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_volumePanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		volumePanel.setLayout(gbl_volumePanel);
		
		mVolumeButton = new JButtonColorable(mSoundImageResource);
		GridBagConstraints gbc_mVolumeButton = new GridBagConstraints();
		gbc_mVolumeButton.fill = GridBagConstraints.BOTH;
		gbc_mVolumeButton.insets = new Insets(0, 0, 0, 5);
		gbc_mVolumeButton.gridx = 0;
		gbc_mVolumeButton.gridy = 0;
		mVolumeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(Audio.getMasterOutputVolume() > 0) {
					new Volume(0).setMasterOutputVolume();
					mVolumeButton.setImageResource(mSoundMutedImageResource);
					mVolumeButton.setIcon();
				}
			}
		});
		volumePanel.add(mVolumeButton, gbc_mVolumeButton);
		
		mVolumeBar = new JProgressBar(0, 100);
		GridBagConstraints gbc_mVolumeBar = new GridBagConstraints();
		gbc_mVolumeBar.fill = GridBagConstraints.BOTH;
		gbc_mVolumeBar.gridx = 1;
		gbc_mVolumeBar.gridy = 0;
		volumePanel.add(mVolumeBar, gbc_mVolumeBar);
		
		mTitleLabel = new JLabel("No Title");
		mTitleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
		mTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_mTitleLabel = new GridBagConstraints();
		gbc_mTitleLabel.insets = new Insets(0, 0, 5, 0);
		gbc_mTitleLabel.gridx = 0;
		gbc_mTitleLabel.gridy = 1;
		contentPane.add(mTitleLabel, gbc_mTitleLabel);
		
		mArtistLabel = new JLabel("No Album");
		mArtistLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mArtistLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_mArtistLabel = new GridBagConstraints();
		gbc_mArtistLabel.insets = new Insets(0, 0, 5, 0);
		gbc_mArtistLabel.gridx = 0;
		gbc_mArtistLabel.gridy = 2;
		contentPane.add(mArtistLabel, gbc_mArtistLabel);
		
		mAlbumLabel = new JLabel("No Artist");
		mAlbumLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mAlbumLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		GridBagConstraints gbc_mAlbumLabel = new GridBagConstraints();
		gbc_mAlbumLabel.insets = new Insets(0, 0, 5, 0);
		gbc_mAlbumLabel.gridx = 0;
		gbc_mAlbumLabel.gridy = 3;
		contentPane.add(mAlbumLabel, gbc_mAlbumLabel);
		
		mediaPanel = new JPanel();
		GridBagConstraints gbc_mediaPanel = new GridBagConstraints();
		gbc_mediaPanel.insets = new Insets(0, 0, 5, 0);
		gbc_mediaPanel.fill = GridBagConstraints.BOTH;
		gbc_mediaPanel.gridx = 0;
		gbc_mediaPanel.gridy = 4;
		contentPane.add(mediaPanel, gbc_mediaPanel);
		mediaPanel.setLayout(new GridLayout(1, 0, 0, 0));
		
		mPrevButton = new JButtonColorable(new ImageResource("previous.png"));
		mPrevButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(mController != null && mController.getBluetoothPlayer() != null) {
					mController.getBluetoothPlayer().previous();
				}
			}
		});
		mediaPanel.add(mPrevButton);
		
		mPlayPauseButton = new JButtonColorable(mPlayImageResource);
		mPlayPauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(mController != null && mController.getBluetoothPlayer() != null) {
					if(mController.getBluetoothPlayer().isPlaying()) {
						mController.getBluetoothPlayer().pause();
					}else {
						mController.getBluetoothPlayer().play();
					}
				}
			}
		});
		mediaPanel.add(mPlayPauseButton);
		
		mNextButton = new JButtonColorable(new ImageResource("next.png"));
		mNextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(mController != null && mController.getBluetoothPlayer() != null) {
					mController.getBluetoothPlayer().next();
				}
			}
		});
		mediaPanel.add(mNextButton);
		
		sourcePanel = new JPanel();
		GridBagConstraints gbc_sourcePanel = new GridBagConstraints();
		gbc_sourcePanel.insets = new Insets(0, 0, 5, 0);
		gbc_sourcePanel.fill = GridBagConstraints.BOTH;
		gbc_sourcePanel.gridx = 0;
		gbc_sourcePanel.gridy = 5;
		contentPane.add(sourcePanel, gbc_sourcePanel);
		sourcePanel.setLayout(new GridLayout(1, 0, 0, 0));
		
		mMediaButton = new JButtonColorable(mMediaImageResource);
		mMediaButton.setTransparency(0.5f);
		mMediaButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(mController != null && mController.getStreamPlayer() != null) {
					boolean newMute = !mController.getStreamPlayer().isMute();
					mController.getStreamPlayer().setMute(newMute);
					if(newMute) {
						mMediaButton.setImageResource(mMediaMutedImageResource);
					}else {
						mMediaButton.setImageResource(mMediaImageResource);
					}
					if(mController.getSwitcher() != null) {
						mController.getSwitcher().setShouldYield(!newMute);
						if(mController.getBluetoothPlayer() != null) {
							if(newMute) {
								mBluetoothButton.setImageResource(mBluetoothStoppedImageResource);
							}else {
								mBluetoothButton.setImageResource(mBluetoothImageResource);
							}
							mBluetoothButton.setIcon();
						}
					}
					mMediaButton.setIcon();
				}
			}
		});
		sourcePanel.add(mMediaButton);
		
		mBluetoothButton = new JButtonColorable();
		if(mController != null && mController.getBluetoothPlayer() != null && MediaBluetoothPlayer.canSupport()) {
			mBluetoothButton.setImageResource(mBluetoothImageResource);
		}else {
			mBluetoothButton.setImageResource(mBluetoothStoppedImageResource);
		}
		mBluetoothButton.setTransparency(0.5f);
		mBluetoothButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(mController != null && mController.getSwitcher() != null) {
					boolean newYeild = !mController.getSwitcher().getShouldYield();
					mController.getSwitcher().setShouldYield(newYeild);
					if(newYeild) {
						mBluetoothButton.setImageResource(mBluetoothImageResource);
						if(mController.getStreamPlayer() != null && mController.getStreamPlayer().isMute()) {
							mController.getStreamPlayer().setMute(false);
							mMediaButton.setImageResource(mMediaImageResource);
							mMediaButton.setIcon();
						}
					}else {
						mBluetoothButton.setImageResource(mBluetoothStoppedImageResource);
					}
					mBluetoothButton.setIcon();
				}
			}
		});
		sourcePanel.add(mBluetoothButton);
		
		configurationPanel = new JPanel();
		GridBagConstraints gbc_configurationPanel = new GridBagConstraints();
		gbc_configurationPanel.fill = GridBagConstraints.BOTH;
		gbc_configurationPanel.gridx = 0;
		gbc_configurationPanel.gridy = 6;
		contentPane.add(configurationPanel, gbc_configurationPanel);
		configurationPanel.setLayout(new GridLayout(1, 0, 0, 0));
		
		mColorModeButton = new JButtonColorable("Color Mode");
		mColorModeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ColorMode nextMode = ColorMode.values()[0];
				for(int i = 0; i < ColorMode.values().length - 1; i++) {
					if(mColorMode == ColorMode.values()[i]) {
						nextMode = ColorMode.values()[i+1];
						break;
					}
				}
				setColorMode(nextMode);
				recolor();
			}
		});
		configurationPanel.add(mColorModeButton);
		
		mBluetoothConfigButton = new JButtonColorable("Bluetooth");
		mBluetoothConfigButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(mController != null && mController.getBluetoothPlayer() != null) {
					mController.getBluetoothPlayer().scanUntilDeviceConnectedThread(SettingsOptions.SCANMAX_DEFAULT, false).start();
				}
			}
		});
		configurationPanel.add(mBluetoothConfigButton);
		
		mCloseButton = new JButtonColorable(new ImageResource("close.png"), "Close");
		mCloseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchEvent(new WindowEvent(MainFrame.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		configurationPanel.add(mCloseButton);
	}
	
	/**
	 * Sets the color mode
	 * @param colorMode to set
	 */
	public void setColorMode(ColorMode colorMode) {
		mColorMode = colorMode;
	}
	/**
	 * Gets the color mode
	 * @return {@link ColorMode} of component
	 */
	public ColorMode getColorMode() {
		return mColorMode;
	}
	
	/**
	 * Recolors this MainFrame with the current color mode
	 * @see ColorMode#recolorComponent(Container)
	 */
	public void recolor() {
		mColorMode.recolorComponent(this);
	}
	/**
	 * Same as {@link #recolor()}
	 */
	@Override
	public void run() {
		recolor();
	}

	/*
	 * Stream observer components
	 */
	@Override
	public void streamActivated() {
		if(mMediaButton == null) {
			return;
		}
		mMediaButton.setTransparency(1);
		mMediaButton.setIcon();
	}

	@Override
	public void streamInactivated() {
		if(mMediaButton == null) {
			return;
		}
		mMediaButton.setTransparency(0.5f);
		mMediaButton.setIcon();
	}
	
	/*
	 * Bluetooth observer components
	 */
	@Override
	public void trackChanged(Track track) {
		if(mTitleLabel == null || mArtistLabel == null || mAlbumLabel == null) {
			return;
		}
		mTitleLabel.setText(track.getTitle());
		mArtistLabel.setText(track.getArtist());
		mAlbumLabel.setText(track.getAlbum());
	}
	@Override
	public void statusChanged(Status status) {
		if(mPlayPauseButton == null || mBluetoothButton == null) {
			return;
		}
		if(status == Status.PLAYING) {
			mPlayPauseButton.setImageResource(mPauseImageResource);
			mPlayPauseButton.setIcon();
			mBluetoothButton.setTransparency(1);
			mBluetoothButton.setIcon();
		} else if (status == Status.STOPPED || status == Status.PAUSED) {
			mPlayPauseButton.setImageResource(mPlayImageResource);
			mPlayPauseButton.setIcon();
			mBluetoothButton.setTransparency(0.5f);
			mBluetoothButton.setIcon();
		}
	}
	@Override
	public void volumeChanged(Volume volume) {
		if(mVolumeBar == null || mVolumeButton == null) {
			return;
		}
		mVolumeBar.setValue(volume.asInt());
		if(volume.asInt() > 0 && !mVolumeButton.getImageResource().equals(mSoundImageResource)) {
			mVolumeButton.setImageResource(mSoundImageResource);
			mVolumeButton.setIcon();
		} else if(volume.asInt() <= 0 && !mVolumeButton.getImageResource().equals(mSoundMutedImageResource)) {
			mVolumeButton.setImageResource(mSoundMutedImageResource);
			mVolumeButton.setIcon();
		}
	}
}
