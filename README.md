URL-pad
=======
[![Build Status](https://travis-ci.org/arocketman/URL-pad.svg?branch=master)](https://travis-ci.org/arocketman/URL-pad)

URL pad is a link fetcher and saver. It is used to store your links as you copy them and to retrieve them in the future.

# Features 

* Automatic link saving from clipboard.
* Save the pad onto the hard drive through json.
* Sort links by tags.
* Edit entries .

# TODO 

* Save a snapshot or image of the page along with its name and description or better the current image system.
* Add loading screen when loading a json.
* Lock a pad, encrypt it and secure it with a password.
* Possibility to add custom notes on a saved url.
* Background mode, the program won't bother you at every link being saved.

# Dependencies 

jsoup, gson. Using Maven : 

```
 <dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.8.1</version>
 </dependency>
  <dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.3</version>
  </dependency>
```

# Pull requests and issues

Every help is more than welcome! Feel free to open an issue and make your pull request at any time.
