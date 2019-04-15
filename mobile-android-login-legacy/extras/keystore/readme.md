# Encode keystore file
openssl base64 -A -in debugKeystore.jks > DEBUG_KEYSTORE_BASE64.txt

# Add content of encoded keystore as environment variable of CI
DEBUG_KEYSTORE_BASE64.txt -> $DEBUG_KEYSTORE_BASE64 on Travis CI (see .travis.yml)

# Complete post about variety of configuration approaches
https://android.jlelse.eu/where-to-store-android-keystore-file-in-ci-cd-cycle-2365f4e02e57