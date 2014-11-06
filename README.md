tdk-navi
========

Submarine Navigation System developed for www.tief-dunkel-kalt.org.


Implementation of a submarine computer showing dive data during the dive.

GPS Signal is meassured by a bouy and rendered on a screen. Additional data will be
collected by various sensors during a dive.

![](https://github.com/oprobst/tdk-navi/tree/master/src/site/Major_Components.png)

The arduino is responsible for collecting and aggregating all data. It is transmitted 
by an own protocol implementation to the Raspberry Pi. The Raspberry is responsible for 
rendering, geo related calculations and logging.