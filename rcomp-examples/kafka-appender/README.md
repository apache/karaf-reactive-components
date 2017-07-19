## Decanter kafka appender example

This example shows how to leverage reactive components to create an alternative kafka appender for decanter.
The kafka-appender bundle creates a flux that listens on eventadmin (rcomp-eventadmin) and sends to a kafka server (rcomp-kafka).  
This is a proof of concept that we could base a future decanter version on reactive components.

## Environment

Install and start kafka server locally

```
bin/kafka-server-start.sh config/server.properties
```

## Install

```
config:property-set -p appender.kafka topic decanter
feature:repo-add mvn:net.lr.reactive.component/rcomp-features/1.0.0-SNAPSHOT/xml/features
feature:install decanter-collector-log rcomp-decanter-appender-kafka decanter-collector-jmx
```

## Test

Start a kafka consumer to listen on the decanter topic. You should see decanter messages for log messages and jmx beans.

```
bin/kafka-console-consumer.sh --consumer.config config/consumer.properties --bootstrap-server=localhost:9092 --topic decanter
```
