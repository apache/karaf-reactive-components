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
config:property-set -p component.mqtt.MqttComponent serverUrl tcp://localhost:1883
feature:repo-add mvn:net.lr.reactive.component/rcomp-features/1.0.0-SNAPSHOT/xml/features
feature:install rcomp-examples
```

Decanter kafka appender example

This example shows how to leverage reactive components to create an alternative kafka appender for decanter.
The kafka-appender bundle creates a flux that listens on eventadmin (rcomp-eventadmin) and sends to a kafka server (rcomp-kafka).  

This is a proof of concept that we could base a future decanter version on reactive components.

```
config:property-set -p appender.kafka topic decanter
feature:repo-add mvn:net.lr.reactive.component/rcomp-features/1.0.0-SNAPSHOT/xml/features
feature:install decanter-collector-log rcomp-decanter-appender-kafka decanter-collector-jmx
```

## Test

## Mqtt
Start mqtt client

Subscribe to topic "output". 
You should receive the following on the topic output: "1.5", "2.5", ...

This can also be seen in the karaf log.

# EventAdmin

```
event:send input a=b
log:tail
```

The log should show that the event was received.
