<<<<<<< HEAD
### Table of Contents

1. [Etiqet](#Etiqet)

2. [Getting Started](#Getting-Started)

    2.1. [Protocol Configuration](#Protocol-Configuration)

    2.2. [Client Configuration](#Client-Configuration)

    2.3. [Message Configuration](#Message-Configuration)

3. [Using Etiqet](#Using-Etiqet)

4. [Building and Running](#Building-and-Running)

# Etiqet

Etiqet is a testing framework that allows your to run test interfaces with a generic client, message and protocol. The aim of this is to allow end users to write tests in plain English, such as:
=======
[![Build Status](https://travis-ci.org/blu-corner/etiqet.svg?branch=master)](https://travis-ci.org/blu-corner/etiqet)
[![Latest Version @ Cloudsmith](https://api-prd.cloudsmith.io/badges/version/neueda/etiqet/maven/etiqet-core/latest/x/?render=true)](https://cloudsmith.io/~neueda/repos/etiqet/packages/detail/maven/etiqet-core/latest/)

### Table of Contents

- [Etiqet](#Etiqet)
- [Getting Started](#Getting-Started)
    - [Maven Projects](#Maven-Projects)
    - [Configuration](#Configuration)
        - [Protocol Configuration](#Protocol-Configuration)
        - [Client Configuration](#Client-Configuration)
        - [Message Configuration](#Message-Configuration)
- [Building Etiqet](#Building-and-Running)
- [The Library (of Test Steps)](docs/teststeps.md)

# Etiqet

Etiqet is a test automation framework designed to facilitate testing of
messaging applications, and specifically targetting FIX applications. It allows
users to create tests in plain english in a self documenting format. Below
is an example test case that displays some of the available test steps.
>>>>>>> 8318df64405cebdc0a933803b90ba2952f0eb7a4

```gherkin
Scenario: Principal Pricing and Trading Flows - Quote - Ended by Client  
  Given a "fix" client
    And filter out "Logon" message
<<<<<<< HEAD
  When client is logged on
  Then send a "QuoteRequest" message with "QuoteReqID=ABC-123#L0" as "mq"
    And wait for a "MassQuote" message
  Then send a "QuoteResponse" message with "QuoteRespID=1,QuoteRespType=1,QuoteReqID=mq->QuoteReqID,Symbol=EURUSD"
    And stop client
```

Etiqet has three key components:

- Protocols
- Clients
- Messages

#### Protocol

`-` A Protocol defines the default behaviour for test steps - by defining the client implementation, dictionary, and message definitions.

#### Clients

`-` A Client provides the interface for sending and receiving messages. Etiqet allows for the use of client 'delegates' which can perform actions before / after sending / receiving messages.

#### Messages

`-` Etiqet allows you to define message types and default values for those messages.



# Getting Started

An example repository displaying how users can pull Etiqet into their projects can be found at https://github.com/blu-corner/etiqet-example.

## Configuring Etiqet

To run Etiqet, you will need to create an Etiqet Configuration file. This is an XML document that defines the protocols, clients and messages that are going to be used in the test steps. You *must* specify where this global configuration file is by using the option `-Detiqet.global.config=/path/to/etiqet.config.xml` when running Etiqet.

A sample config can be found here: https://github.com/blu-corner/etiqet/blob/master/config/etiqet.config.xml

A Dictionary is used to look up message names / types. Here we look at a generic FIX dictionary:

```xml
<dictionary handler="com.neueda.etiqet.fix.message.dictionary.FixDictionary">/path/to/dictionary/FIX50SP2.xml</dictionary>
```

The package used to create components within messages is defined:

```xml
<components_package>quickfix.fix44.component</components_package>
```

The Class Etiqet will used to wrap around concrete message types:

```xml
<messageClass>com.neueda.etiqet.fix.message.FIXMsg</messageClass>
```



### Protocol Configuration

Within the etiqet.config.xml a Protocol needs to be defined. In the example below a protocol named "fix" is defined:
=======
   When client is logged on
   Then send a "QuoteRequest" message with "QuoteReqID=ABC-123#L0" as "qr"
    And wait for a "MassQuote" message
   Then send a "QuoteResponse" message with "QuoteRespID=1,QuoteRespType=1,QuoteReqID=qr->QuoteReqID,Symbol=EURUSD"
    And stop client
```

This example displays one of the key features of Etiqet - named parameters.
Messages and clients can be assigned names allowing them to be referenced later
in the test. In this example a QuoteRequest is sent named qr, later when the
QuoteResponse is returned the QuoteReqID is retrieved from the Quote Request
using qr->QuoteReqID. This allows users to build highly complex test cases
easily.

Etiqet has three key components:

- **Client** - provides the interface for sending and receiving messages. 
Etiqet includes client 'delegates' which can perform actions before/after send/receive
- **Message** - the message interface that defines setters/getters for fields and
serialisation/desiarlization prior to sending. Messages support definition of defaults
to reduce the required setup within tests
- **Protocol** - brings together the client and message

Users can easily implement their own bespoke Protocols and access the entire
suite of Etiqet test steps by inheriting from the Client and Message interfaces
and creating their own implementations.

# Getting Started

Etiqet is designed to be pulled into test projects as a dependency
meaning users do not need to build the etiqet project themselves.

An example repository displaying how users can pull Etiqet into their test 
projects can be found at https://github.com/blu-corner/etiqet-example.

Etiqet is distributed as 3 modules available as individual jars:

- **etiqet-core** - contains the building blocks required to extend and create a
new Client, Message, and other customer modules required. The library of test
steps are also contained in core.
- **etiqet-fix** - provides the FIX Client and Message implementations required
to execute FIX test cases
- **etiqet-rest** - provides a REST Client and JSON Message implementation for
testing restful web interfaces

## Maven Projects

Etiqet jar files are hosted on the cloudsmith repository. To include this 
repository in a project add the following lines to the pom.xml

```xml
	<repositories>
      ...
	  <repository>
	    <id>etiqet</id>
	    <url>https://dl.cloudsmith.io/public/neueda/etiqet/maven</url>
	  </repository>
      ...
	</repositories>
```

To include the etiqet modules add the following stanzas as required.

```xml

    <dependencies>
        ...
        <!--etiqet-fix-->
        <dependency>
            <groupId>com.neueda.etiqet</groupId>
            <artifactId>etiqet-fix</artifactId>
            <version>1.0</version>
        </dependency>

        <!--etiqet-rest-->
        <dependency>
            <groupId>com.neueda.etiqet</groupId>
            <artifactId>etiqet-rest</artifactId>
            <version>1.0</version>
        </dependency>

        <!--etiqet-core-->
        <dependency>
            <groupId>com.neueda.etiqet</groupId>
            <artifactId>etiqet-core</artifactId>
            <version>1.0</version>
        </dependency>
        ...
    </dependencies>
```

## Configuration

To run Etiqet, you will need to create an Etiqet Configuration file. This is an
XML document that defines the protocols, clients and messages that are going to
be used in the test steps. You *must* specify where this global configuration
file is by using the option `-Detiqet.global.config=/path/to/etiqet.config.xml`
when running Etiqet.

A sample config can be found here: https://github.com/blu-corner/etiqet-example/blob/master/src/test/resources/etiqet.config.xml

### Protocol Configuration

Within the etiqet.config.xml a Protocol needs to be defined. In the example below a
protocol named "fix" is defined that will use the FixClient (client impl) and
FIXMsg classes (messageClass). As QuickFIXJ is used to provide the underlying 
FIX session handling/messaging a FIX Dictionary must be configured
>>>>>>> 8318df64405cebdc0a933803b90ba2952f0eb7a4

```xml
<?xml version="1.0" encoding="UTF-8"?>
<etiqetConfiguration xmlns="http://www.neueda.com/etiqet">
    <protocols>
        <protocol name="fix">
<<<<<<< HEAD
=======
            <!-- 
                impl = client implementation class
                defaultConfig = quick fix configuration file
             -->
>>>>>>> 8318df64405cebdc0a933803b90ba2952f0eb7a4
            <client impl = "com.neueda.etiqet.fix.client.FixClient"
                    defaultConfig="/path/to/client.cfg"
                    extensionsUrl="http://localhost:5000/">
                <delegates>
                    <delegate key="default" impl="com.neueda.etiqet.core.client.delegate.SinkClientDelegate"/>
                    <delegate key="logger" impl="com.neueda.etiqet.core.client.delegate.LoggerClientDelegate"/>
                    <delegate key="fix" impl="com.neueda.etiqet.fix.client.delegate.FixClientDelegate"/>
                    <delegate key="fix-logger" impl="com.neueda.etiqet.fix.client.delegate.FixLoggerClientDelegate"/>
                    <delegate key="ordering" impl="com.neueda.etiqet.fix.client.delegate.OrderParamFixClientDelegate"/>
                    <delegate key="ordering" impl="com.neueda.etiqet.fix.client.delegate.ReplaceParamFixClientDelegate"/>
                </delegates>
            </client>
<<<<<<< HEAD
            <messages>
            <message name="NewOrderSingle" admin="N">
                    <implementation>quickfix.fix44.NewOrderSingle</implementation>
                    <fields>
                        <field name="ClOrdID" type="string"
                               utilclass="com.neueda.etiqet.fix.message.FIXUtils" method="genClientOrderID"/>
                        <field name="Symbol" type="string">CSCO</field>
                        <field name="Side" type="integer">1</field>
                        <field name="OrderQty" type="integer">100</field>
                        <field name="OrdType" type="string">2</field>
                        <field name="TransactTime" type="string"
                               utilclass="com.neueda.etiqet.fix.message.FIXUtils" method="getDateTime"/>
                    </fields>
                </message>
            </messages>
=======
            <!-- quickfix dictionary file -->
            <dictionary handler="com.neueda.etiqet.fix.message.dictionary.FixDictionary">${user.dir}/src/test/resources/fix-config/FIX50SP2.xml</dictionary>
            <components_package>quickfix.fix44.component</components_package>
            <!-- message implementation to be used by the protocol -->
            <messageClass>com.neueda.etiqet.fix.message.FIXMsg</messageClass>
            <!-- path to the messages helper file -->
            <messages ref="${user.dir}/src/test/resources/fix-config/etiqet-fix-messages.xml"/>
>>>>>>> 8318df64405cebdc0a933803b90ba2952f0eb7a4
        </protocol>
    <protocols>
</etiqetConfiguration>    
```

<<<<<<< HEAD


### Client Configuration
The client configuration for the `fix` protocol should point to a Quickfix configuration file - see [official Quickfix documentation](https://www.quickfixj.org/usermanual/2.0.0//usage/configuration.html) for more information on this. 

The protocol defined above will use a Client implementation, an example of a Client configuration is shown below:

```xml
<client impl = "com.neueda.etiqet.fix.client.FixClient"
        defaultConfig="/path/to/client.cfg"
	extensionsUrl="http://localhost:5000/">
=======
### Client Configuration
The client configuration for the `fix` protocol should point to a Quickfix configuration file - see [official Quickfix documentation](https://www.quickfixj.org/usermanual/2.0.0//usage/configuration.html) for more information on this. 

```xml
<client impl = "com.neueda.etiqet.fix.client.FixClient"
        defaultConfig="/path/to/client.cfg"
	    extensionsUrl="http://localhost:5000/">
>>>>>>> 8318df64405cebdc0a933803b90ba2952f0eb7a4
	<delegates>
            <delegate key="default" impl="com.neueda.etiqet.core.client.delegate.SinkClientDelegate"/>
            <delegate key="logger" impl="com.neueda.etiqet.core.client.delegate.LoggerClientDelegate"/>
            <delegate key="fix" impl="com.neueda.etiqet.fix.client.delegate.FixClientDelegate"/>
            <delegate key="fix-logger" impl="com.neueda.etiqet.fix.client.delegate.FixLoggerClientDelegate"/>
            <delegate key="ordering" impl="com.neueda.etiqet.fix.client.delegate.OrderParamFixClientDelegate"/>
            <delegate key="ordering" impl="com.neueda.etiqet.fix.client.delegate.ReplaceParamFixClientDelegate"/>
	</delegates>
</client>
```

The "fix" protocol will use the client implementation com.neueda.etiqet.fix.client.FixClient which uses the config file listed in the defaultConfig path.

The delegates section defines the chain of client delegates which are called before and after sending and receiving messages. 

<<<<<<< HEAD
Users can create pre-defined clients. Below a client named "testClient1" is created:

```xml
<clients>
    <client name="testClient1" impl="fix" extensionsUrl="http://localhost:5000">
=======
Users can create pre-defined clients with specific configurations. Below a client
named "fixClient1" is created, that will use the fix protocol that has been
configured:

```xml
<clients>
    <client name="fixClient1" impl="fix" extensionsUrl="http://localhost:5000">
>>>>>>> 8318df64405cebdc0a933803b90ba2952f0eb7a4
	    <primary configPath="/path/to/other/client.cfg"/>
    </client>
</clients>
```

<<<<<<< HEAD
"testClient1" is created using the "fix" protocol, overriding the default configuration defined.
=======
"fixClient1" is created using the "fix" protocol, overriding the default configuration defined.
>>>>>>> 8318df64405cebdc0a933803b90ba2952f0eb7a4

Creating clients gives users the ability to use them in test steps:

```gherkin
<<<<<<< HEAD
Given client "testClient1" is started
=======
Given client "fixClient1" is started
>>>>>>> 8318df64405cebdc0a933803b90ba2952f0eb7a4
```

### Message Configuration

<<<<<<< HEAD
This section of the config is where messages are defined for use in test steps. The message defined below is "NewOrderSingle", which can then be used in a test:

```gherkin
Then send a "NewOrderSingle" message with "AccountType=3,ReceivedDeptID=EQ" as "order"
```

Default values can be set for messages. Below, the "Symbol" has a default value of "CSCO".

Static functions can also be used, such as the "genClientOrderID" method used for the "ClOrdID" field.
=======
Etiqet provides a number of helpers surrounding message creation and message
validation. When a user creates a new message Etiqet will return a message that
is prepopulated with a set of sensible defaults to minimise the amount of 
setup a user is required to complete within test cases e.g. a new order single
will have a ClOrdId generated, a default Side, Price and instrument populated.
The default values Etiqet returns are configurable. In the following config
section the default value for the Symbol field will be CSCO, and the ClOrdID
will be populated by the static function genClientOrderID from the FIXUtils
class.
>>>>>>> 8318df64405cebdc0a933803b90ba2952f0eb7a4

```xml
<messages>
    <message name="NewOrderSingle" admin="N">
        <implementation>quickfix.fix44.NewOrderSingle</implementation>
	    <fields>
                <field name="ClOrdID" type="string" utilclass="com.neueda.etiqet.fix.message.FIXUtils" method="genClientOrderID"/>
                <field name="Symbol" type="string">CSCO</field>
                <field name="Side" type="integer">1</field>
                <field name="OrderQty" type="integer">100</field>
                <field name="OrdType" type="string">2</field>	 
                <field name="TransactTime" type="string" utilclass="com.neueda.etiqet.fix.message.FIXUtils" method="getDateTime"/>
            </fields>
    </message>
</messages>    
```

<<<<<<< HEAD
Etiqet will perform validation on received message in line with the 'required' and 'allowedValues' attributes. In the below example the OrdStatus field is configured as required and the allowedValues defined. If a message is missing OrdStatus or the value is out of bounds, the test will fail.

```xml
<field name="OrdStatus" type="string" required="Y" allowedValues="0,1,2,3,4,5,6,7,8,9,A,B,C,D,E">A</field>
```



Messages can also be defined in a separate messages.xml file to reduce the size of the Etiqet configuration file. The messages.xml can then be referenced in the global config file:

```xml
<messages ref="/path/to/messages.xml"/>
```

An example of messages.xml can be found here: https://github.com/blu-corner/etiqet/blob/master/etiqet-fix/src/test/resources/config/etiqet-fix-messages.xml


## Using Etiqet

Etiqet is currently made up of 3 components available as individual jar files hosted on Maven repository. Users can pull in the jar files required as shown in the following sections. 

An Etiqet example project is available at https://github.com/blu-corner/etiqet-example for reference.

* Etiqet-core is the platform with the main verbs to write automated tests. 

* Etiqet-fix is dependent on Etiqet-core and gives the ability to create fix sessions and analyse and validate fix messages to and from trading engines.

* Etiqet-rest is also dependent on Etiqet-core and allows the use of REST APIs.

### etiqet-fix

Etiqet Fix is a module designed to allow you to run a FIX (Financial Information eXchange) client and test different scenarios against a FIX process.

```xml
<dependency>
    <groupId>com.neueda.etiqet</groupId>
    <artifactId>etiqet-fix</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### etiqet-rest

Etiqet REST is a module designed to allow you to run a REST client and test against a generic JSON API.

```xml
<dependency>
    <groupId>com.neueda.etiqet</groupId>
    <artifactId>etiqet-rest</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

The client configuration for the `rest` protocol should point to a properties file which contains a `baseUrl` parameter.

### etiqet-core

Etiqet Core contains the building blocks for you to extend and create your own Client, Message, and other pieces needed to create your own Etiqet component. To create your own etiqet component, simply include etiqet-core as a Maven dependency:

```xml
<dependency>
    <groupId>com.neueda.etiqet</groupId>
    <artifactId>etiqet-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Building and Running

### Maven
=======
As a result if a user calls the following test step:

```gherkin
Then send a "NewOrderSingle" message with "AccountType=3,ReceivedDeptID=EQ" as "order"
```

A NewOrderSingle with Symbol=CSCO, Side=1, OrderQty=100, AccountType=3, ReceivedDeptID=EQ etc.

Additionally Etiqet can perform validation on received message in line with the
'required' and 'allowedValues' attributes. In the below example the OrdStatus
field is configured as required and the allowedValues defined. If a message is
missing OrdStatus or the value is out of bounds, the test will fail.

```xml
<field name="OrdStatus" type="string" required="Y" allowedValues="0,1,2,3,4,5,6,7,8,9,A,B,C,D,E">A</field>
```

Messages can be defined in a separate messages.xml file to reduce the size of the Etiqet configuration file. The messages.xml can then be referenced in the global config file:

```xml
<messages ref="/path/to/messages.xml"/>
```

An example of messages.xml can be found here: https://github.com/blu-corner/etiqet/blob/master/etiqet-fix/src/test/resources/config/etiqet-fix-messages.xml

# Building Etiqet

>>>>>>> 8318df64405cebdc0a933803b90ba2952f0eb7a4
To build Etiqet using Maven in Command line, run the following command:
```
cd blu-corner/etiqet
mvn clean install
```
To run tests using Maven in command line, run the following command from the etiqet directory:
```
mvn test -Detiqet.global.config=src\test\resources\etiqet.config.xml
<<<<<<< HEAD
```
=======
```
>>>>>>> 8318df64405cebdc0a933803b90ba2952f0eb7a4
