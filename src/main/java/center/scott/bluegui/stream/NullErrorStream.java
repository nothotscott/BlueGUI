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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class dedicated to throwing away error spam from {@link uk.co.caprica.vlcj.factory.MediaPlayerFactory}
 * @author Scott Maday
 * @see uk.co.caprica.vlcj.factory.MediaPlayerFactory
 */
public class NullErrorStream {
	private final static Logger LOGGER = LoggerFactory.getLogger(NullErrorStream.class);
	
	private static PrintStream mStdErr;
	private static PrintStream mTmpErr;
	
	static {
		mTmpErr = new PrintStream(new ByteArrayOutputStream());
	}
	
	/**
	 * Disregards the system error stream until {@link reregardErrorStream()} is called
	 * @return <code>true</code> if successfully switched the error stream to disregard, or <code>false</code> if the error stream {@link #isDisregarded()}
	 * @see #reregardErrorStream()
	 * @see #isDisregarded()
	 */
	public static boolean disregardErrorStream() {
		if(isDisregarded()) {
			return false;
		}
		mStdErr = System.err;
		System.setErr(mTmpErr);
		return true;
	}
	
	/**
	 * Reregards the system error stream prior to {@link #disregardErrorStream()} being called
	 * @return <code>true</code> if successfully switched the error stream to reregard, or <code>false</code> if {@link #disregardErrorStream()} has not been called
	 * @see #disregardErrorStream()
	 * @see #isDisregarded()
	 */
	public static boolean reregardErrorStream() {
		if(!isDisregarded()) {
			return false;
		}
		System.setErr(mStdErr);
		return true;
	}
	
	/**
	 * @return <code>true</code> if the system error stream is being disregarded or <code>false</code> if it's not
	 */
	public static boolean isDisregarded() {
		return System.err.equals(mTmpErr);
	}
	
}
