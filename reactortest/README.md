# Example for combining reactor and mqtt

The example [MqttExampleComponent](src/main/java/reactortest/MqttExampleComponent.java) receives Integers from the topic "input",
computes the average over a sliding window of 2 elements and writes the results to the topic "output".

This example show how to combine reactor with an protocols in a loosely coupled way that does not strictly couple your user code to the protocol.

## Build

```
mvn clean install
```

## Environment

Install and start a MQTT server. I recommend using mosquitto.
You also need a MQTT client.

## Install

```
config:property-set -p component.mqtt.MqttComponent serverUrl tcp://localhost:1883
install -s mvn:org.eclipse.paho/org.eclipse.paho.client.mqttv3/1.1.1
install -s mvn:org.reactivestreams/reactive-streams/1.0.0
install -s mvn:io.projectreactor/reactor-core/3.0.7.RELEASE
install -s wrap:mvn:io.projectreactor.addons/reactor-extra/3.0.7.RELEASE
install -s mvn:net.lr/reactortest/0.0.1-SNAPSHOT
```

## Test

Start mqtt client

Subscribe to topic "output". 
Send two messages containing the values "1", "2" and "3" to the topic "input".

You should receive the following on the topic output: "1.5", "2.5"


