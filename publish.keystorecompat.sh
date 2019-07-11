#!/usr/bin/env bash

#This deploy script is working only when there is proper BINTRAY_API_KEY exported in environment
#On MacOS add `export BINTRAY_API_KEY=replaceWithProperApiKey` in ~/.bash_profile

#There is some alternative with `bintray.properties` in `~/.gradle` but not tested for this project

./gradlew clean :android-keystore-compat-base:bintrayUpload
./gradlew clean :android-keystore-compat-19:bintrayUpload
./gradlew clean :android-keystore-compat-21:bintrayUpload
./gradlew clean :android-keystore-compat-23:bintrayUpload
./gradlew clean :android-keystore-compat-emplus:bintrayUpload
./gradlew clean :android-keystore-compat-elplus:bintrayUpload
./gradlew clean :android-keystore-compat-kplus:bintrayUpload