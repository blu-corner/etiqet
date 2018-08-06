# Etiqet
[![Build Status](https://travis-ci.org/blu-corner/etiqet.svg?branch=master)](https://travis-ci.org/blu-corner/etiqet)

## What is Etiqet
Etiqet is a testing framework that allows your to run test interfaces with a generic client, message and protocol. The
aim of this is to allow end users to write tests in plain English, such as:

```
Scenario: Principal Pricing and Trading Flows - Quote – Ended by Client
  Given a "fix" client
    And filter out "Logon" message
  When client is logged on
  Then send a "QuoteRequest" message with "QuoteReqID=ABC-123#L0" as "mq"
    And wait for a "MassQuote" message
  Then send a "QuoteResponse" message with "QuoteRespID=1,QuoteRespType=1,QuoteReqID=mq->QuoteReqID,Symbol=EURUSD"
    And stop client
```

Etiqet has three key parts:

* Protocols
* Clients
* Messages

### Protocol

A Protocol defines the default behaviour for test steps - by defining the client implementation, dictionary, and
message definitions.

### Clients

A Client provides the interface for sending and receiving messages. Etiqet allows for the use of client 'delegates' which
can perform actions before / after sending / receiving messages.

### Messages

Etiqet allows you to define message types and default values for those messages.  

## Configuring Etiqet
To run Etiqet, you will need to create an Etiqet Configuration file. This is an XML document that defines the protocols,
clients and messages that are going to be used in the test steps. You *must* specify where this global configuration file is
by using the option `-Detiqet.global.config=/path/to/etiqet.config.xml` when running Etiqet.

The following is an configuration for Etiqet that provides a FIX protocol and a client named testClient1. Comments in the XML
below describe what each element does

```xml
<?xml version="1.0" encoding="UTF-8"?>
<etiqetConfiguration xmlns="http://www.neueda.com/etiqet">
    <protocols>
        <protocol name="fix">
            <!--
              Define a protocol named fix which uses the client implementation com.neueda.etiqet.fix.client.FixClient
              which uses the config file listed if not specified otherwise
             -->
            <client impl = "com.neueda.etiqet.fix.client.FixClient"
                    defaultConfig="/path/to/client.cfg"
                    extensionsUrl="http://localhost:5000/">
                <delegates>
                    <!--
                      Defines the chain of client delegates which are called before and after sending and receiving messages
                    -->
                    <delegate key="default" impl="com.neueda.etiqet.core.client.delegate.SinkClientDelegate"/>
                    <delegate key="logger" impl="com.neueda.etiqet.core.client.delegate.LoggerClientDelegate"/>
                    <delegate key="fix" impl="com.neueda.etiqet.fix.client.delegate.FixClientDelegate"/>
                    <delegate key="fix-logger" impl="com.neueda.etiqet.fix.client.delegate.FixLoggerClientDelegate"/>
                    <delegate key="ordering" impl="com.neueda.etiqet.fix.client.delegate.OrderParamFixClientDelegate"/>
                    <delegate key="ordering" impl="com.neueda.etiqet.fix.client.delegate.ReplaceParamFixClientDelegate"/>
                </delegates>
            </client>
            <!-- Dictionary is used to look up message names / types. In this case we're looking at a generic FIX dictionary -->
            <dictionary handler="com.neueda.etiqet.fix.message.dictionary.FixDictionary">/path/to/dictionary/FIX50SP2.xml</dictionary>
            <!-- The package used to create components within messages -->
            <components_package>quickfix.fix44.component</components_package>
            <!-- Class Etiqet will use to wrap around concrete message types (below) -->
            <messageClass>com.neueda.etiqet.fix.message.FIXMsg</messageClass>
            <!--
              Default message implementations. It should be noted that these can be stored in a separate file and referenced (e.g.
              <messages ref="/path/to/messages.xml"/>) in order to reduce the size of the Etiqet Configuration file
            -->
            <messages>
                <!--
                  Defines a message type 'NewOrderSingle' which can be used in test steps
                  (e.g. Then send a "NewOrderSingle" message with "AccountType=3,ReceivedDeptID=EQ" as "order")
                -->
                <message name="NewOrderSingle" admin="N">
                    <implementation>quickfix.fix44.NewOrderSingle</implementation>
                    <fields>
                        <!--
                          Default values for a NewOrderSingle message. Can specify a static value (e.g. Symbol) or use a static
                          function such as `genClientOrderID`
                        -->
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
                <message name="ExecutionReport" admin="N">
                    <implementation>quickfix.fix44.ExecutionReport</implementation>
                    <fields>
                        <!--
                          Default values for "ExecutionReport" messages. When the client receives an ExecutionReport message,
                          Etiqet will perform validation in line with the `required` and `allowedValues` attributes
                        -->
                        <field name="SendingTime" type="date"
                               utilclass="com.neueda.etiqet.fix.message.FIXUtils" method="getDateTime"/>
                        <field name="MsgSeqNum" type="integer" required="Y">0</field>
                        <field name="CumQty" type="integer" required="Y">0</field>
                        <field name="LeavesQty" type="integer">0</field>
                        <field name="ExecID" type="integer">0</field>
                        <field name="OrderID" type="integer">1</field>
                        <field name="OrdStatus" type="string" required="Y" allowedValues="0,1,2,3,4,5,6,7,8,9,A,B,C,D,E">A</field>
                        <field name="ExecType" type="string" required="Y" allowedValues="0,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I">A</field>
                        <field name="LeavesQty" type="integer" required="Y">0</field>
                        <field name="Side" type="integer" required="Y" allowedValues="1,2,3,4,5,6">1</field>
                    </fields>
                </message>
            </messages>
        </protocol>
    </protocols>
    <!--
      Pre-defined clients available for use immediately.
      Etiqet users can create a client explicitly in test steps
        `Given a "fix" client "clientAlias" with config "/path/to/client.cfg"
          And "clientAlias" is started`
      Or by creating a pre-defined client below. This gives users the ability to use the client like
        `Given client "testClient1" is started`
    -->
    <clients>
        <!-- Creates "testClient1" using the "fix" protocol, overriding the default configuration defined -->
        <client name="testClient1" impl="fix" extensionsUrl="http://localhost:5000">
            <!-- A separate dictionary can be defined for this client, if not specified will use the dictionary specified in the protocol -->
            <primary configPath="/path/to/other/client.cfg"/>
            <!-- A secondary configuration file can be defined to allow the client to failover -->
        </client>
    </clients>
</etiqetConfiguration>
```

## Using Etiqet
Etiqet is currently made up of 3 components.

### etiqet-core
Etiqet Core contains the building blocks for you to extend and create your own Client, Message, and other pieces needed
to create your own Etiqet component. To create your own etiqet component, simply include etiqet-core as a Maven
dependency:

```xml
<dependency>
    <groupId>com.neueda.etiqet</groupId>
    <artifactId>etiqet-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### etiqet-fix
Etiqet Fix is a module designed to allow you to run a FIX (Financial Information eXchange) client and test different
scenarios against a your FIX process.

```xml
<dependency>
    <groupId>com.neueda.etiqet</groupId>
    <artifactId>etiqet-fix</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

The client configuration for the `fix` protocol should point to a Quickfix configuration file - see [official Quickfix documentation](https://www.quickfixj.org/usermanual/2.0.0//usage/configuration.html)
for more information on this.

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

