FileSpace
=====================

### Deprecated version, see the up-to-date repo [here](https://github.com/Mercandj/file-android)

----

**Find on play store [here](https://play.google.com/store/apps/details?id=com.mercandalli.android.apps.files)**

<p align="center">
	<a margin="20px 0" href="https://play.google.com/store/apps/details?id=com.mercandalli.android.apps.files">
		<img  src="https://raw.github.com/Mercandj/FileSpace-Android/master/screenshot/play_store/filespace_wallp.png" width="560" />
	</a>
</p>

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

* Android : SDK supported : min 10 (Gingerbread), max 24 (Nougat)
* Material design app


## REQUIRED

* [Optional: FileSpace Rest API PHP](https://github.com/Mercandj/FileSpace-API)

## GRADLE

* Check [dependencies](https://github.com/ben-manes/gradle-versions-plugin): _gradlew dependencyUpdates -Drevision=release -DoutputFormatter=json_
* Check [methods count](https://github.com/KeepSafe/dexcount-gradle-plugin): Uncomment "Count methods." and launch _gradlew assembleDebug_
* Jenkins (Require authentication with the [Rest API](https://github.com/Mercandj/FileSpace-API) to upload the result): _gradlew :app:jenkins_

## DEVELOPER

* Mercandalli Jonathan


## LICENSE

Mention developer name if you use the code.

```
Copyright (C) 2016 mercandalli.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

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
