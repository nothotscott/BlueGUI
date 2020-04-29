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
package center.scott.bluegui.gui.component;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link javax.swing.JButton} that both dispalys an {@link ImageResource} and is {@link Colorable}
 * @author Scott Maday
 */
public class JButtonColorable extends JButton implements Colorable {
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = LoggerFactory.getLogger(JButtonColorable.class);
	
	private ColorMode mLastColorMode = ColorMode.DEFAULT_COLORMODE;
	private ImageResource mImageResource;
	private float mTransparency = 1f;
	
	public JButtonColorable() {
		setContentAreaFilled(false);
		setFocusPainted(false);
		setFont(new Font("Dialog", Font.PLAIN, 12));
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						setIcon();
					}
				});
			}
        });
	}
	public JButtonColorable(ImageResource imageResource) {
		this();
		mImageResource = imageResource;
	}
	public JButtonColorable(ImageResource imageResource, String text) {
		this();
		mImageResource = imageResource;
		setText(text);
	}
	public JButtonColorable(String text) {
		this();
		setText(text);
	}
	
	public Dimension getSizeWithInsets() {
		Dimension size = getSize();
        Insets insets = getInsets();
        size.width -= insets.left + insets.right;
        size.height -= insets.top + insets.bottom;
        if (size.width > size.height) {
            size.width = size.height;
        } else {
            size.height = size.width;
        }
        return size;
	}
	
	/**
	 * Sets icon based on the colorMode
	 * @param colorMode that will set the icon from
	 * @return <code>true</code> if successful, else <code>false</code>
	 */
	public boolean setIcon(ColorMode colorMode) {
		if(mImageResource == null ) {
			return false;
		}
		try {
			Dimension size = getSizeWithInsets();
			Image resource = mImageResource.getimage(colorMode);
			if(resource == null) {
				return false;
			}
			Image scaled = resource.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
			if(mTransparency >= 1 || size.width <= 0 || size.height <= 0) {
				setIcon(new ImageIcon(scaled));
			} else {
				BufferedImage bimage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D gfx = bimage.createGraphics();
				gfx.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, mTransparency));
			    gfx.drawImage(scaled, 0, 0, null);
			    gfx.dispose();
			    setIcon(new ImageIcon(bimage));
			}
			return true;
		} catch (Exception e) {
			LOGGER.error("Error setting icon: ", e);
		}
		return false;
	}
	/**
	 * Sets icon to the last known color mode
	 * @return <code>true</code> if successful, else <code>false</code>
	 */
	public boolean setIcon() {
		return setIcon(mLastColorMode);
	}
	
	/**
	 * Sets the image resource. Redraw the image with {@link #setIcon()}
	 * @param imageResource that will be set to
	 */
	public void setImageResource(ImageResource imageResource) {
		mImageResource = imageResource;
	}
	/**
	 * Gets the image resource
	 * @return ImageResource in use
	 */
	public ImageResource getImageResource() {
		return mImageResource;
	}
	
	/**
	 * Sets transparency from 0(completely transparent) to 1(completely opaque)
	 * Use {@link #setIcon()} to redraw
	 * @param transparency of the button's image
	 */
	public void setTransparency(float transparency) {
		this.mTransparency = transparency;
	}
	/**
	 * Gets transparency
	 * @return
	 */
	public float getTransparency() {
		return mTransparency;
	}
	
	/**
	 * Invokes {@link setIcon(ColorMode)} with the colorMode
	 * @return <code>true</code> always
	 */
	@Override
	public boolean canColor(ColorMode colorMode) {
		mLastColorMode = colorMode;
		setIcon(colorMode);
		return true;
	}
	
	
}
