## Introduction
TransmitSensor is android app that runs a server in your phone. It reads the phone's rotation vector and GPS data, then delivers the data as a JSON response when a request is made to the server.\
The phone is fixed to the rover. Based on the sensor value we measured how much the rover has turned (left or right).

## Feature
1. Get real time sensor value (Rotation vector) and GPS data.
2. Transmit the data as API response.
3. Change port number of the Server url.
4. Automatically restart the server when internet connection is changed (Wifi to mobile or vice versa).
5. Runs in the background.

## How to use
Download the app from: <a href="/apk/Transmit Sensorv-0.0.1.apk" target="_blank">/apk/Transmit Sensorv-0.0.1.apk</a>\
Just open the app. Leave it as it is. Copy the URL.\
Open the browser of a device connected to the same network (Maybe hotspot of the phone itself).\
Go to that URL from the browser. You will see the transmitted data.\
In our python program. We used a while loop and a delay of 100ms to send request to the server to get the data periodically.

## Issues - Future plans
1. Add a few different sensor readings.
2. Make the background service efficient. Currently the server stills runs even after the app is closed. (Force stop the app if the server is running).
3. Refactor the codebase.
4. More customization. Let the client decide which data to send.

## Contributing
1. Fork the Project
2. Create your Feature Branch (git checkout -b feature/AmazingFeature)
3. Commit your Changes (git commit -m 'Add some AmazingFeature')
4. Push to the Branch (git push origin feature/AmazingFeature)
5. Open a Pull Request