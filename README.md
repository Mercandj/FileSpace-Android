FileSpace - ANDROID
=====================

**Find on play store [here](https://play.google.com/store/apps/details?id=com.mercandalli.android.apps.files)**

<p align="center">
	<a margin="20px 0" href="https://play.google.com/store/apps/details?id=com.mercandalli.android.apps.files">
		<img  src="https://raw.github.com/Mercandj/FileSpace-Android/master/screenshot/play_store/filespace_wallp.png" width="560" />
	</a>
</p>

**_Unfinished project, still in development_** [API Required](https://github.com/Mercandj/FileSpace-API)

<br /><br />
**_Now you cannot compile this project without the 'com.mercandalli.android.library:baselibrary:1.0.0'_**
<br />
**_This library is not open source (not finished). If you have this library, copy and paste gradle.properties.template to gradle.properties and update "localMavenUrl" to your maven folder_**
<br /><br />


## PROJECT DESCRIPTION

* Name : FileSpace
* Description : Remote file manager
* Location : Paris
* Starting Date : October 2014

Remote and secure file manager.
* Upload files
* Download files
* Manage files
* Streaming Audio & Video

## ANDROID DESCRIPTION

* Android : SDK supported : min 16 (Jelly Bean), max 23 (Marshmallow)
* Material design app


## REQUIRED

* [FileSpace Rest API PHP](https://github.com/Mercandj/FileSpace-API)

## GRADLE

* Check [dependencies](https://github.com/ben-manes/gradle-versions-plugin): _gradlew dependencyUpdates -Drevision=release -DoutputFormatter=json_
* Check [methods count](https://github.com/KeepSafe/dexcount-gradle-plugin): Uncomment "Count methods." and launch _gradlew assembleDebug_
* Jenkins (Require authentication with the [Rest API](https://github.com/Mercandj/FileSpace-API) to upload the result): _gradlew :app:jenkins_

## DEVELOPER

* Mercandalli Jonathan


## LICENSE

OpenSource : just mention developer name if you use the code.

## DEV INFO

2016-04-02
Uncomment dexcount-gradle and launch ```gradlew :app:assembleDebug```
Total methods in app-debug.apk: 61501 (93,84% used)
Total fields in app-debug.apk:  35492 (54,16% used)
Methods remaining in app-debug.apk: 4034
Fields remaining in app-debug.apk:  30043

2016-05-18
Total methods in app-debug.apk: 61799 (94,30% used)
Total fields in app-debug.apk:  35665 (54,42% used)
Methods remaining in app-debug.apk: 3736
Fields remaining in app-debug.apk:  29870
