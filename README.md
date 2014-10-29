Spring Integration - AMQP Sample
================================

# Overview

This sample demonstrates basic functionality of the **Spring Integration AMQP Adapter**, which uses the [Advanced Message Queuing Protocol](http://www.amqp.org/) (AMQP) to send and retrieve messages.
As AMQP Broker implementation the sample uses [RabbitMQ](http://www.rabbitmq.com/).
Additionally this example will dequeue the messages from the rabbit instance and store them on a local mongo instance.

# How to Run the Sample

Check src class org.springframework.integration.smsblaster.GenericOptions to find out the run options

> In order to run the example you will need a running  instance of RabbitMQ and a running instance of mongo db. Configure the properties at /resources/mongo.properties and /resources/rabbit.properties.

# Used Spring Integration components

### Spring Integration Modules (Maven dependencies)

* spring-integration-core
* spring-integration-amqp
* spring-integration-stream
* spring-data-mongodb

### Spring Integration Adapters

* int-stream:stdin-channel-adapter
* **int-amqp:outbound-channel-adapter**
* **int-amqp:inbound-channel-adapter**
* int-stream:stdout-channel-adapter
* int:poller
* int:channel
* int:interceptors
* int:wire-tap
* logging-channel-adapter

# Resources

For further help please take a look at the Spring Integration documentation:

* [http://static.springsource.org/spring-integration/reference/htmlsingle/#amqp](http://static.springsource.org/spring-integration/reference/htmlsingle/#amqp)

Some further resources:

* RabbitMQ -  [http://www.rabbitmq.com/](http://www.rabbitmq.com/)
* Spring AMQP - [http://www.springsource.org/spring-amqp](http://www.springsource.org/spring-amqp)
