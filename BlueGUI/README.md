# BlueGUI
BlueGUI combines vlc and bluez-dbus into a simple GUI meant for 3.5" LCD screens on raspberry pis.
BlueGUI allows you to use your Linux device as a bluetooth speaker and listen to an audio stream. When an audio stream is detected, BlueGUI will automatically pause and resume your bluetooth audio.
The primary uses are for this project is combining automobile/home bluetooth audio systems and software defined radios.


## Setup
### Java
Ensure Oracle Java is installed `java -version`. javax.swing may not render correctly if you're using OpenJDK
If not, follow this guide https://java.com/en/download/help/linux_install.xml
### vlc
Install vlc `sudo apt-get install vlc`
### Bluetooth
Install bluez `sudo apt-get install bluez`
In order to turn your device into a bluetooth speaker, please follow this tutorial https://github.com/lukasjapan/bt-speaker
#### Bluetooth not working
Verify your bluetooth is on with `bluetoothctl show`

If your device is not powered, check `sudo nano /etc/bluetooth/main.conf` has `AutoEnable=true` after `[Policy]`

If that still doesn't work, `sudo -s`, `cd /var/lib/bluetooth/{ADAPTER MAC ADDRESS}`, `nano settings`, and add
```
[Policy]
AutoEnable=true
```
If you have any other bluetooth related issues, this documentation may prove helpful https://wiki.archlinux.org/index.php/Bluetooth

If you've had bluez before, `sudo apt-get remove bluez` and `sudo apt-install bluez` for a fresh reinstall if all else fails
### Build
Clone the repository to a conventient directory with `git clone blah`. `cd BlueGUI` and run `./gradlew clean build`

The jar file will be located at `BlueGUI/build/libs/BlueGUI.jar`

## Configurations
BlueGUI accepts the following configurations for vlc with the `-c` parameter
* OP25

More configurations will be added in future versions

## Execute
### Command Line
The jar can be ran by `java -jar BlueGUI/build/libs/BlueGUI.jar -uri <uri>`
### Shell scripts and OP25
For an all in one script to run OP25 and BlueGUI automatically, `sudo nano BlueGUI/run.sh` and make it look something like this (your configuration may vary)
```shell
#!/bin/bash
cd home/pi/Programs/op25/op25/gr-op25_repeater/apps
./rx.py --args 'rtl' -N 'LNA:39' --nocrypt -V -q 59 -d 0 -v 1 -S 1000000 -2 -w -W 127.0.0.1 -u 56112 -T trunk.tsv -2>stderr.2 &
cd /home/pi/Programs/BlueGUI
java -jar BlueGUI.jar -uri udp://@:56112 -c op25
```
Remember to make it executable `sudo chmod +x BlueGUI/run.sh`