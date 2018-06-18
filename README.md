# Exoplayer Sample App

ExoPlayer is an application level media player built on top of Android's low level media APIs. 
ExoPlayer has a number of advantages over Android's built in MediaPlayer and supports many of the 
same media formats as MediaPlayer plus the adaptive formats DASH and SmoothStreaming. ExoPlayer is 
highly customizable and extendable and can be adapted closely to specific use cases. 
It is used by Google apps like YouTube and Google Play Movies, and is developed as an open source 
project hosted on GitHub.

ExoPlayer is the video player running in the Android YouTube app.

## How the application is structured

After the build has finished you'll see two modules: the app module (of type application aka apk module) 
and the player-lib module (of type library). The app module is actually empty; having only a manifest 
and merges in everything from the player-lib module by a simple gradle dependency.

It makes sense to have our media player Activity separated in a library project so we can share it 
among different apks like for mobile or Android TV. Specifically with Android Instant Apps (AIA) 
it's a common requirement for Android apps to be modularized in so called feature splits or atoms. 
So we are better off with a little bit of more complexity to start with.
