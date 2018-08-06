# Etiqet Example Project

This is a simple Etiqet project that uses the REST component to make some example API calls to the Oanda test API; this also uses the FIX component to interact with the Neueda simulator running locally on port 9899 (and the accompanying web service on port 5000).

To run these examples, you must specify the Etiqet Global Config using the option: `-Detiqet.global.config=src\test\resources\etiqet.config.xml`

The REST example runs through 4 simple API calls sending different API requests, some with and without HTTP headers and data sent to the API. It also shows the ability to check the HTTP response code and data in the body using the Cdr format.

The FIX Examples show a range of different functionality from sending and receiving FIX messages with different values, to purging an order book using the Neueda Simulator web service.

## JSON Payload for REST messages

When sending data to the API, the Cdr format is converted into JSON. For example, the Cdr format

`order->units=100,order->instrument=EUR_USD,order->timeInForce=FOK,order->type=MARKET,order->positionFill=DEFAULT`

is converted into the following JSON:

```
{
    "order": {
        "units": 100,
        "instrument": "EUR_USD",
        "timeInForce": "FOK",
        "type": "MARKET",
        "positionFill": "DEFAULT" 
    }
}
```