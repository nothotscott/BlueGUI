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

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource to pull images from for all {@link ColorMode}s
 * @author Scott Maday
 */
public class ImageResource {
	private final static Logger LOGGER = LoggerFactory.getLogger(ImageResource.class);
	
	private String mImageString;
	private HashMap<ColorMode, Image> mImageMap = new HashMap<ColorMode, Image>();
	
	/**
	 * Create image resource
	 * @param imageString of the file, including the extension.
	 */
	public ImageResource(String imageString) {
		mImageString = imageString;
		
		for(ColorMode colorMode : ColorMode.values()) {
			String pathColored = String.format("/images/%s/%s", colorMode.toString(), mImageString);
			String pathNormal = String.format("/images/%s", mImageString);
			try {
				URL location = ImageResource.class.getResource(pathColored);
				if(location == null) {
					location = ImageResource.class.getResource(pathNormal);
				}
				if(location != null) {
					Image resource = ImageIO.read(location);
					mImageMap.put(colorMode, resource);
				}
			} catch(Exception e) {
				LOGGER.error("Could not resolve image location: ", e);
			}
		}
	}
	
	/**
	 * Gets the image string
	 * @return imageString set in the constructor
	 */
	public String getImageString() {
		return mImageString;
	}
	
	/**
	 * Gets the {@link java.awt.Image} for the specified ColorMode
	 * @param colorMode to search for the image
	 * @return {@link java.awt.Image}
	 * @see java.util.HashMap#get(Object)
	 */
	public Image getimage(ColorMode colorMode) {
		return mImageMap.get(colorMode);
	}
	
}
