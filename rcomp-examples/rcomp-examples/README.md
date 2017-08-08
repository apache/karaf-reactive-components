## Example for combining reactor and mqtt

This example shows how to build some integrations using reactor and reactive components.

[MqttEmitter](src/main/java/reactortest/MqttEmitter.java) creates an unbounded list of Integers count up from 0 and sends them to the mqtt topic "input".

[MqttExampleComponent](src/main/java/reactortest/MqttExample.java) receives Integers from the topic "input",
computes the average over a sliding window of 2 elements and writes the results to the topic "output".

[MqttReceiver](src/main/java/reactortest/MqttReceiver.java) listens on the topic "output" and writes the message to the log.

This example show how to combine reactor with an protocols in a loosely coupled way that does not strictly couple your user code to the protocol.

[EventAdminExample](src/main/java/reactortest/EventAdminExample.java) listens on the topic eainput and sends to eaoutput. You can use the karaf
event admin commands to test it. 

### Environment

Install and start a MQTT server. I recommend using mosquitto.
You also need a MQTT client.

### Install

```
config:property-set -p component.mqtt.MqttComponent serverUrl tcp://localhost:1883
feature:repo-add mvn:org.apache.karaf.rcomp/rcomp-features/1.0.0-SNAPSHOT/xml/features
feature:install rcomp-examples
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
