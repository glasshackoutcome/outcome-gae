Google Mirror API's Quickstart for Java
========================

The documentation for this quickstart is maintained on developers.google.com.
Please see here for more information:
https://developers.google.com/glass/quickstart/java

## AppEngine Fork

The goal of this fork is to make the sample app run on Google AppEngine.

### List of Notable Changes

* Added AppEngine dependencies to Maven POM
* Dropped direct Jetty dependency in favour of AppEngine
* Replaced deprecated shared memory credential store with AppEngine's own based on [Datastore](https://developers.google.com/appengine/docs/java/storage#app_engine_datastore)

### Usage

Edit appengine-web.xml and replace placeholders with your app ID and version.
You also need to get oauth credentials to use Mirror API.

Build and test app on your computer with `mvn appengine:devserver` command.

Deploy your application into the cloud with `mvn appengine:update` command.

Enjoy!


## License
Code for this project is licensed under [APL 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
and content is licensed under the
[Creative Commons Attribution 3.0 License](http://creativecommons.org/licenses/by/3.0/).
