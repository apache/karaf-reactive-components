# Prototype of messaging components and reactive streams

## Example for combining reactor and mqtt

The example [MqttExampleComponent](src/main/java/reactortest/MqttExampleComponent.java) receives Integers from the topic "input",
computes the average over a sliding window of 2 elements and writes the results to the topic "output".

This example show how to combine reactor with an protocols in a loosely coupled way that does not strictly couple your user code to the protocol.

## Build

Start local kafka server and mqtt server with defaults for tests to work.

```
mvn clean install
```

## Environment

Install and start a MQTT server. I recommend using mosquitto.
You also need a MQTT client.

## Install

```
feature:install scr
config:property-set -p component.mqtt.MqttComponent serverUrl tcp://localhost:1883
config:property-set -p appender.kafka topic decanter

install -s mvn:org.reactivestreams/reactive-streams/1.0.0
install -s mvn:io.projectreactor/reactor-core/3.0.7.RELEASE
install -s wrap:mvn:io.projectreactor.addons/reactor-extra/3.0.7.RELEASE

install -s mvn:net.lr.reactive.component/rcomp-api/1.0.0-SNAPSHOT

install -s mvn:org.eclipse.paho/org.eclipse.paho.client.mqttv3/1.1.1
install -s mvn:net.lr.reactive.component/rcomp-mqtt/1.0.0-SNAPSHOT

install -s mvn:net.lr.reactive.component/rcomp-eventadmin/1.0.0-SNAPSHOT

install -s mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.kafka-clients/0.11.0.0_1
install -s mvn:net.lr.reactive.component/rcomp-kafka/1.0.0-SNAPSHOT
install -s mvn:net.lr.reactive.component/kafka-appender/1.0.0-SNAPSHOT

install -s mvn:javax.mail/mail/1.5.0-b01
install -s mvn:net.lr.reactive.component/rcomp-mail/1.0.0-SNAPSHOT

install -s mvn:net.lr.reactive.component/rcomp-examples/1.0.0-SNAPSHOT
```

## Test

## Mqtt
Start mqtt client

Subscribe to topic "output". 
Send two messages containing the values "1", "2" and "3" to the topic "input".

You should receive the following on the topic output: "1.5", "2.5"

# EventAdmin

```
event:send input a=b
log:tail
```

The log should show that the event was received.
