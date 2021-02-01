# Comp 599 Android Google Sign In Example

This is a basic app and server to illustrate how Google sign in works with and Android app.

## Setting up your environment
1. Install node (LTS) from https://nodejs.org/en/
2. Install Android studio
3. Configure a Google API Console Project [here](https://developers.google.com/identity/sign-in/android/start-integrating#configure_a_project). From this you will get your web clientId, web clientSecret, and Android clientId.

## Launching the server
1. Naviage to `/server`
2. Run `npm install`
3. Insert your web clientSecret and web clientId into the `start` script located in `/server/package.json`
4. Run `npm run start`

## Launching the app
1. Insert your Android clientId into the tag with the name `server_client_id` in `/A1CodeDemo/app/res/values/strings.xml`.
2. Select your device and edit your run configs if desired.
3. Run or debug the app. 