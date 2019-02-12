[![Build Status](https://travis-ci.org/blu-corner/etiqet.svg?branch=master)](https://travis-ci.org/blu-corner/etiqet)
[![Latest Version @ Cloudsmith](https://api-prd.cloudsmith.io/badges/version/neueda/etiqet/maven/etiqet-core/latest/x/?render=true)](https://cloudsmith.io/~neueda/repos/etiqet/packages/detail/maven/etiqet-core/latest/)

### Table of Contents

- [Etiqet](#Etiqet)
- [Getting Started](#Getting-Started)
    - [Maven Projects](#Maven-Projects)
    - [Configuration](#Configuration)
        - [XML configuration](#XML-Configuration)
            - [Protocol Configuration](#Protocol-Configuration)
            - [Client Configuration](#Client-Configuration)
            - [Message Configuration](#Message-Configuration)
        - [Java Configuration](#Java-Configuration)
- [Building Etiqet](#Building-Etiqet)
- [The Library (of Test Steps)](docs/teststeps.md)

# Etiqet

Etiqet is a test automation framework designed to facilitate testing of messaging applications, and specifically
targeting FIX applications. It allows users to create tests in plain english in a self documenting format. Below is an
example test case that displays some of the available test steps.

```gherkin
Scenario: Principal Pricing and Trading Flows - Quote - Ended by Client  
  Given a "fix" client
    And filter out "Logon" message
   When client is logged on
   Then send a "QuoteRequest" message with "QuoteReqID=ABC-123#L0" as "qr"
    And wait for a "MassQuote" message
   Then send a "QuoteResponse" message with "QuoteRespID=1,QuoteRespType=1,QuoteReqID=qr->QuoteReqID,Symbol=EURUSD"
    And stop client
```

This example displays one of the key features of Etiqet - named parameters. Messages and clients can be assigned names
allowing them to be referenced later in the test. In this example a `QuoteRequest` is sent named `qr`, later when the
`QuoteResponse` is returned the `QuoteReqID` is retrieved from the `QuoteRequest` using `qr->QuoteReqID`. This allows
users to build highly complex test cases easily.

Etiqet has three key components:

- **Client** - provides the interface for sending and receiving messages. Etiqet includes client 'delegates' which can
perform actions before/after send/receive
- **Message** - the message interface that defines setters/getters for fields and serialisation/desiarlization prior to
sending. Messages support definition of defaults to reduce the required setup within tests
- **Protocol** - brings together the client and message

Users can easily implement their own bespoke Protocols and access the entire suite of Etiqet test steps by inheriting
from the Client and Message interfaces and creating their own implementations.

# Getting Started

Etiqet is designed to be pulled into test projects as a dependency meaning users do not need to build the Etiqet project
themselves.

An example repository displaying how users can pull Etiqet into their test projects can be found at
https://github.com/blu-corner/etiqet-example.

Etiqet is distributed as 3 modules available as individual jars:

- **etiqet-core** - contains the building blocks required to extend and create a new Client, Message, and other customer
modules required. The library of test steps are also contained in core.
- **etiqet-fix** - provides the FIX Client and Message implementations required to execute FIX test cases
- **etiqet-rest** - provides a REST Client and JSON Message implementation for testing restful web interfaces

## Maven Projects

Etiqet jar files are hosted on the cloudsmith repository. To include this  repository in a project add the following
lines to the pom.xml

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

To run Etiqet, you can use the `EtiqetTestRunner` class to define a suite of tests. This provides the ability to define
your configuration via a configuration class, or by a configuration file.

```java
@RunWith(EtiqetTestRunner.class)
@EtiqetOptions(
        // configFile = "/path/to/etiqet/config.xml",           // path to Etiqet configuration file
        configClass = EtiqetConfiguration.class,                // class to configure Etiqet
        features = {"/path/to/feature/files"},                  // paths to any features
        additionalFixtures = {"com.example.etiqet.fixtures"},   // path to any custom Etiqet Fixtures
        plugin = {"pretty"}                                     // any Cucumber plugins to be used
)
public class EtiqetTests {}
```

If neither `configFile` or `configClass` fields are specified within the `@EtiqetOptions` annotation, then Etiqet
tries to read the path to a configuration file from the a set property `etiqet.global.config` (e.g. passing is the
argument `-Detiqet.global.config=/path/to/etiqet.config.xml` to the JVM). If this is also not set, Etiqet will fail to
run any tests. 

### XML Configuration

This is an XML document that defines the protocols, clients and messages that are going to be used in the test steps.
You *must* specify where this global configuration file is by using the option `-Detiqet.global.config=/path/to/etiqet.config.xml`
when running Etiqet.

A sample config can be found here:
https://github.com/blu-corner/etiqet-example/blob/master/src/test/resources/etiqet.config.xml

#### Protocol Configuration

Within the your configuration file a Protocol needs to be defined. In the example below a protocol named "fix" is
defined that will use the `FixClient` (client impl). To assist with parsing messages, a dictionary class can be defined
to read tags / fields within messages - in this instance, we have defined a `FixDictionary` as this is a protocol for a
`FixClient`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<etiqetConfiguration xmlns="http://www.neueda.com/etiqet">
    <protocols>
        <protocol name="fix">
            <!-- 
                impl = client implementation class
                defaultConfig = quick fix configuration file
             -->
            <client impl = "com.neueda.etiqet.fix.client.FixClient"
                    defaultConfig="/path/to/client.cfg">
                <delegates>
                    <delegate key="logger" impl="com.neueda.etiqet.core.client.delegate.LoggerClientDelegate"/>
                    <delegate key="fix" impl="com.neueda.etiqet.fix.client.delegate.FixClientDelegate"/>
                    <delegate key="fix-logger" impl="com.neueda.etiqet.fix.client.delegate.FixLoggerClientDelegate"/>
                    <delegate key="ordering" impl="com.neueda.etiqet.fix.client.delegate.OrderParamFixClientDelegate"/>
                    <delegate key="ordering" impl="com.neueda.etiqet.fix.client.delegate.ReplaceParamFixClientDelegate"/>
                </delegates>
            </client>
            <!-- quickfix dictionary file -->
            <dictionary handler="com.neueda.etiqet.fix.message.dictionary.FixDictionary">${user.dir}/src/test/resources/fix-config/FIX50SP2.xml</dictionary>
            <components_package>quickfix.fix44.component</components_package>
            <!-- path to the messages helper file -->
            <messages ref="${user.dir}/src/test/resources/fix-config/etiqet-fix-messages.xml"/>
        </protocol>
    </protocols>
</etiqetConfiguration>    
```

#### Client Configuration
The client configuration for the `fix` protocol should point to a Quickfix configuration file - see
[official Quickfix documentation](https://www.quickfixj.org/usermanual/2.0.0//usage/configuration.html) for more
information on this. 

```xml
<client impl = "com.neueda.etiqet.fix.client.FixClient"
        defaultConfig="/path/to/client.cfg">
    <delegates>
        <delegate key="logger" impl="com.neueda.etiqet.core.client.delegate.LoggerClientDelegate"/>
        <delegate key="fix" impl="com.neueda.etiqet.fix.client.delegate.FixClientDelegate"/>
        <delegate key="fix-logger" impl="com.neueda.etiqet.fix.client.delegate.FixLoggerClientDelegate"/>
        <delegate key="ordering" impl="com.neueda.etiqet.fix.client.delegate.OrderParamFixClientDelegate"/>
        <delegate key="ordering" impl="com.neueda.etiqet.fix.client.delegate.ReplaceParamFixClientDelegate"/>
    </delegates>
</client>
```

The "fix" protocol will use the client implementation `com.neueda.etiqet.fix.client.FixClient` which uses the config
file listed in the defaultConfig path.

Client Delegates are classes that perform actions before and after sending and receiving messages 

Users can create pre-defined clients with specific configurations. Below a client named "fixClient1" is created, that
will use the fix protocol that has been configured:

```xml
<clients>
    <client name="fixClient1" impl="fix">
	    <primary configPath="/path/to/other/client.cfg"/>
    </client>
</clients>
```

"fixClient1" is created using the "fix" protocol, overriding the default configuration defined.

Creating clients gives users the ability to use them in test steps without having to explicitly create them within test
steps:

```gherkin
Given client "fixClient1" is started
```

#### Message Configuration

Etiqet provides a number of helpers surrounding message creation and message validation. When a user creates a new
message Etiqet will return a message that is pre-populated with a set of sensible defaults to minimise the amount of 
setup a user is required to complete within test cases e.g. a new order single will have a ClOrdId generated,
a default Side, Price and instrument populated.

The default values Etiqet returns are configurable. In the following config section the default value for the Symbol
field will be CSCO, and the ClOrdID will be populated by the static function genClientOrderID from the FIXUtils class.

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

As a result if a user calls the following test step:

```gherkin
Then send a "NewOrderSingle" message with "AccountType=3,ReceivedDeptID=EQ" as "order"
```

A `NewOrderSingle` with `Symbol=CSCO`, `Side=1`, `OrderQty=100`, `AccountType=3`, `ReceivedDeptID=EQ` etc.

Additionally Etiqet can perform validation on received message in line with the `required` and `allowedValues`
attributes. In the below example the `OrdStatus` field is configured as required and the `allowedValues` defined. If a
message is missing `OrdStatus` or the value is not one of the comma-separated values provided, the test will fail.

```xml
<field name="OrdStatus" type="string" required="Y" allowedValues="0,1,2,3,4,5,6,7,8,9,A,B,C,D,E">A</field>
```

Messages can be defined in a separate file to reduce the size of the Etiqet configuration file. The messages.xml can
then be referenced in the global config file:

```xml
<messages ref="/path/to/messages.xml"/>
```

An example of `messages.xml` can be found here: https://github.com/blu-corner/etiqet/blob/master/etiqet-fix/src/test/resources/config/etiqet-fix-messages.xml

### Java Configuration

To configure Etiqet using Java Configuration you must annotate a class with
`@com.neueda.etiqet.core.config.annotations.Configuration`. This tells Etiqet that this class contains protocol and
client definitions to be configured in test steps.

An example configuration class can be seen below

```java
@Configuration
public class ExampleConfiguration {

    /**
     * The @EtiqetProtocol annotation tells Etiqet that this returns a protocol definition. This annotation requires a
     * value - this is the name that the protocol will be accessed under.
     * 
     * Note that the name specified in the annotation will override any name given within the method itself
     * 
     * Methods using this annotation *MUST* return the type `com.neueda.etiqet.core.config.dtos.Protocol` 
     */ 
    @EtiqetProtocol("testProtocol")
    public Protocol getTestProtocol() throws EtiqetException {
        Protocol protocol = new Protocol();
        
        // Sets the client for this protocol 
        protocol.setClient(getFixClient());
        
        // Defines the dictionary class. This must extend `com.neueda.etiqet.core.message.config.AbstractDictionary`
        protocol.setDictionary(TestDictionary.class);
        
        // Messages can be defined by returning a List<com.neueda.etiqet.core.config.dtos.Message>,
        // or by using an XML file and passing the path.
        String messageConfiguration = getClass().getClassLoader().getResource("protocols/testMessages.xml").getPath();
        protocol.setMessages(messageConfiguration);
        
        return protocol;
    }

    /**
     * This is a helper function to return an instance of `com.neueda.etiqet.core.config.dtos.Client` 
     * 
     * @return client DTO that specifies that Etiqet will use `TestClient` and will be configured with the file provided
     */
    private Client getFixClient() {
        Client client = new Client();
        client.setImplementationClass(TestClient.class);
        client.setDefaultConfig("${etiqet.directory}/etiqet-core/src/test/resources/properties/test.properties");
        return client;
    }

    /**
     * The @NamedClient annotation tells Etiqet to instantiate a client with the name "testClient" that uses the
     * "testProtocol defined above, but overrides the default configuration file with the value defined with when
     * setting the primary configuration.
     * 
     * Note that the fields specified within the annotation will override any values for the same fields set within the
     * method itself.
     * 
     * @return instance of `com.neueda.etiqet.core.config.dtos.ClientImpl`
     */
    @NamedClient(name = "testClient1", impl = "testProtocol")
    public ClientImpl getClient1() {
        ClientImpl client = new ClientImpl();
        String config = getClass().getClassLoader().getResource("properties/test.properties").getPath();
        client.setPrimaryConfig(config);
        return client;
    }
    
  }
```

# Building Etiqet

To build Etiqet using Maven in Command line, run the following command:
```
cd blu-corner/etiqet
mvn clean install
```

To run tests using Maven in command line, run the following command from the etiqet directory:
```
mvn test
```
