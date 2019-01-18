# Etiqet

Etiqet allows for the creation and use of named clients as well as the use of default clients. To create a named client, follow this example:

```gherkin
Given a "fix" client as "clientOne"
When client "clientOne" is logged on
```

By naming clients it allows for more complex test cases to be developed. For example, a default "fix" client could be initialised along with another named "fix" client, called "fixTwo" for instance. Doing this allows for multiple messages to be sent at once. Using FIX, a Buy Order could be sent from one client and a Sell Order from the other. These orders could then cross and separate Execution Reports sent back to the respective clients.

# Etiqet Steps

## Server Messages

```gherkin
Given a server type “<serverType>”
```

​   Creates a server

```gherkin
Given a server type “<serverType>” with configuration “<serverConfig>”
```

​	Creates a server with a defined configuration	

```gherkin
Given a “<serverType>” as “<serverName>” with configuration “<serverConfig>”
```

​	Creates a server with a defined configuration and an alias

```gherkin
Given a started server type “<serverType>”
```

​	Creates and starts a server

```gherkin
Given a started server type “<serverType>” with configuration “<serverConfig>”
```

​	Creates and starts a server with a defined configuration

```gherkin
Given a started “<serverType>” as “<serverName>” with configuration “<serverConfig>”
```

​	Creates and starts a server with a defined configuration and an alias

```gherkin
Given server “<serverName>” is started
```

​	Starts server

## Client Messages

```gherkin
Given an initialised “<implementation>” client
```

​	Creates the default client


```gherkin
Given an initialised “<implementation>” client as “<clientName>”
```

​	Creates the default client with an alias


```gherking
When client is started
```

​	Starts the default client


```gherkin
When client “<clientName>” is started
```

​	Starts a specific named client. 


```gherkin
Given a “<implementation>” client
```

​	Starts default client


```gherkin
Given a “<implementation>” client as “<clientName>”
```

​	Starts named client


```gherkin
Given a “<implementation>” client with configuration file “<configFile>”
```

​	Starts default client with a specific configuration file

```gherkin
Given a “<implementation>” client “<clientName>” with config “<configFile>”
```

​	Starts a named client with a specific configuration file


```gherkin
Given a “<implementation>” client with primary config “<primaryConfig>” and secondary config “<secondaryConfig>”
```

​	Starts a client with a secondary config

```gherkin
Given a “<implementation>” client “<clientName>” with primary config “<primaryConfig>” and secondary config “<secondaryConfig>”
```

​	Starts a named client with a secondary config

```gherkin
Then failover
```

​	Fails over to the secondary connection defined for the client in use

























