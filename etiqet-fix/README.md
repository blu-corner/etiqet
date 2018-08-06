# Etiqet FIX

## FIX Client Configuration
A FIX Client should be configured using the Quickfix configuration format (see [official Quickfix documentation](https://www.quickfixj.org/usermanual/2.0.0//usage/configuration.html)
for more information on this). See below for an example configuration file:

```
#Connection
SocketConnectHost=<TODO>
SocketConnectPort=<TODO>

# Request access to the simulator by sending an email to Neueda simulator support
Password=<TODO>

# SSL properties
## Generate a self signed certificate (e.g. openssl and java keytool)
SocketUseSSL=N
#SocketKeyStore=<File path to generated key store>
#SocketKeyStorePassword=<Key store's password>

[SESSION]
BeginString=FIX.4.4
SenderCompID=<TODO>
TargetCompID=<TODO>
SenderSubID=<TODO>
TargetSubID=<TODO>
```

