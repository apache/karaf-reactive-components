# Prototype of reactive components and reactor based streams

## Scope

I recently experimented with reactive streams and a small component framework.
See https://github.com/cschneider/streaming-osgi

The goal is to have a small API that can encapsulate a protocol and transport. The code using a reactive component should not directly depend on the specifics of the transport or protocol. Another goal is to have reactive features like backpressure. Ultimately I am searching for something like Apache Camel Components but with a lot less coupling. In camel the big problem is that components depend on camel core which unfortunately is much more than a component API. So any camel component is coupled quite tightly to all of camel core.

## Build

```
mvn clean install -DskipTests
```

## Examples

* [Mqtt and Eventadmin](rcomp-examples/rcomp-examples)
* [Alternative Decanter Kafka appender](rcomp-examples/kafka-appender)

## Component API

I was trying to find the simplest API that would allow similar components to camel in one way mode.
This is what I currently use (https://github.com/cschneider/streaming-osgi/blob/master/rcomp-api/src/main/java/component/api/MComponent.java).

```
public interface RComponent {
    <T> Publisher<T> from(String destination, Class<T> type);
    <T> Subscriber<T> to(String destination, Class<T> type);
}
```

A component is a factory for Publishers and Subscribers. From and to have the same meaning as in camel. The component can be given a source / target type to produce / consume. So with the OSGi Converter spec this would allow to have type safe messaging without coding the conversion in every component. Each component is exposed as a service which encapsulates most of the configuration. All endpoint specific configuration can be done using the destination String.

Publisher and Subscriber are interfaces from the reactive streams api (http://www.reactive-streams.org/). So they are well defined and have zero additional dependencies.

One example for such a component is the MqttComponent https://github.com/cschneider/streaming-osgi/blob/master/rcomp-mqtt/src/main/java/component/mqtt/MqttComponent.java.

## Possible use cases

Two big use cases are reactive microservices that need messaging integrations as well as plain camel like integrations.
Another case are the Apache Karaf decanter collectors and appenders. Currently they use a decanter specific API but they could easily be converted into the more general rcomp api.
We could also create a bridge to camel components to leverage the many existing camel components using the rcomp API as well as offering rcomp components to camel.

Components alone are of course not enough. One big strength of Apache Camel is the DSL. In case of rcomp I propose to not create our own DSL and instead use existing DSLs that work well in OSGi. Two examples:

* [Akka and reactive streams](https://de.slideshare.net/ktoso/reactive-integrations-with-akka-streams)
* [Reactor and reactive streams](https://de.slideshare.net/StphaneMaldini/reactor-30-a-reactive-foundation-for-java-8-and-spring)

It is already possible to integrate CXF Rest services with reactive streams using some adapters but we could have native integration.

## Risks and Opportunities

The main risk I see is not gathering a critical mass of components to draw more people. 
Another risk is that the RComponent API or the reactor streams have some unexpected limitations. 
The big opportunity I see is that the rcomp API is very simple so the barrier of entry is low. 
I also hope that this might become a new foundation for a simpler and more modern Apache Camel.

So this all depends on getting some support by you all. 
