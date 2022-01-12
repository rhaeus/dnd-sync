# DNDSync
This App was developed to enable Do Not Disturb (DND) synchronization between my Pixel phone and the Galaxy Watch 4 
since this option was only available if paired with a Samsung phone.

If installed on phone and watch it enables either a 1-way sync or a 2-way sync, depending on the preferences.
I also added the functionality to automatically toggle Bedtime Mode. Use case: At night I put my phone into DND and I want my watch to automatically enable Bedtime Mode.
This functionality is realized via an Accessibility Service, since I couldn't find how to enable it programmatically (any hints are highly appreciated).

NOTE: For Bedtime mode toggle to work it is important that on the watch the Bedtime Mode button is on the first page of quick settings and the middle button in the first row!

_**Tested on Pixel 3a XL paired with a Galaxy Watch 4 (40mm)**_

<a href="https://youtu.be/rHy6kCBNOzA
" target="_blank"><img src="http://img.youtube.com/vi/rHy6kCBNOzA/0.jpg" 
alt="DNDSync demo" width="480" height="360" border="10" /></a>

Video link: https://youtu.be/rHy6kCBNOzA

## 

## Setup
_For now the App is not in the Play Store. Manual installation is required. The use of ADB is required._
* Download the .apk files from the release section (_dndsync_mobile.apk_ and _dndsync_wear.apk_)

### Phone

<img src="/images/mobile.png" width="300">

* Install _dndsync_mobile.apk_ on the phone `adb install dndsync_mobile.apk`
* Open the App and grant the permission for DND Access by clicking on the menu entry _DND Permission_.
This will open the permission screen. This Permission is required so that the app can read and write DND state.
Without this permission the sync will not work.
* With the switch _Sync DND state to watch_ you can enable and disable the sync. If enabled a DND change on the phone will lead to DND change on the watch.

### Watch
<p float="left">
  <img src="/images/wear_1.png" width="200" />
  <img src="/images/wear_2.png" width="200" /> 
  <img src="/images/wear_3.png" width="200" />
  <img src="/images/wear_4.png" width="200" />
</p>

Setting up the watch is a bit more tricky since the watch OS lacks the permission screen for DND access. I found a way to enable the permission via ADB.

Note: This is only tested on my Galaxy Watch 4 and it might not work on other devices!
* Connect the watch to your computer via adb (watch and computer have to be in the same network!)
  * enable Developer Options: Go to Settings -> About watch -> Software -> tap the Software version 5 times -> developer mode is on (you can disable it in the same way)
  * enable _ADB debugging_ and _Debug over WIFI_ (in Settings -> Developer Options)
  * note the watch IP address and port, something like `192.168.0.100:5555`
  * connect to the watch with `adb connect 192.168.0.100:5555` (_**insert your value!**_)
* install the app `adb install dndsync_wear.apk`
* grant permission for DND access  
`adb shell cmd notification allow_listener de.rhaeus.dndsync/de.rhaeus.dndsync.DNDNotificationService`  
This allows the app to listen to DND changes and changing the DND setting
* scroll to the permission section and check if DND permission says _access granted_ (you might need to tap on the menu entry for it to update)
* _**IMPORTANT: Disable ADB debugging after you are done because it drains the battery!**_
* If you want to use the Bedtime mode feature you have to enable the Accessibility service for the app. Clicking on _Accessibility Service_ will open the setting on your watch. 
Go to _Installed Services_ and enable _DNDSync_. The App will use this to simulate the following touch events on the screen: 
swipe down to open Quick Settings Panel, click the middle icon of the first row (put Bedtime Mode here) and finally close the panel.
You can enable this by enabling the _Bedtime Mode_ Setting in the App.
* If you enable the setting _Sync DND_ in the App a DND change on the watch will lead to a DND change on the phone
* If you enable the setting _Vibration_ in the App the watch will vibrate when it receives a DND sync request from the phone

# Feedback
Feel free to contact me for questions, feature ideas or bugs and I will see if I can do something about it

email: rhaeus.dev@gmail.com
