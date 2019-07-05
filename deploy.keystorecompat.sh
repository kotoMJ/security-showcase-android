#!/usr/bin/env bash

#This deploy script is working only when there is proper BINTRAY_API_KEY exported in environment
#On MacOS add `export BINTRAY_API_KEY=replaceWithProperApiKey` in ~/.bash_profile

#There is some alternative with `bintray.properties` in `~/.gradle` but not tested for this project

./gradlew clean :android-keystore-compat:bintrayUpload