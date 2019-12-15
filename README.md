# bitmapconverter
A library to convert Android Bitmap object into a .bmp file format byte array.

The output data are gray scaled as shown in the following picture excerpt from the sample app.

<img width="227" height="480" align="center" src="https://i.ibb.co/TcqKLc1/device-2019-12-15-121339.png"/>

# Try it out

### Gradle dependency
```groovy
implementation 'com.github.yoanngoular:bitmapconverter:0.2.0'
```

### Maven dependency
```xml
<dependency>
  <groupId>com.github.yoanngoular</groupId>
  <artifactId>bitmapconverter</artifactId>
  <version>0.2.0</version>
  <type>aar</type>
</dependency>
```

### Get Started
Use `BitmapConverter` instance to convert `android.graphics.Bitmap` object into byte array to .bmp file format.

Instantiate BitmapConverter:

```java
BitmapConverter bitmapConverter = new BitmapConverter();
```

Convert Bitmap to default format (24-bit color):
```java
byte [] bmpFile = bitmapConverter.convert(bitmap);
```

You can also specify the format of the output array by using the second argument of `convert(Bitmap, BitmapFormat)` method.
There is currently only 2 available format (8-bit color and 24-bit color).

```java
byte [] bmpFile = bitmapConverter.convert(bitmap, BitmapFormat.BITMAP_8_BIT_COLOR);
```

For those interested in the bmp header format, BitmapConverter generates a header using the most common Windows format : [BITMAPINFOHEADER](https://msdn.microsoft.com/en-us/library/windows/desktop/dd183376(v=vs.85).aspx)

# Contributing
Contributions you say?  Yes please!

### Bug report? 
- If at all possible, please attach a *minimal* sample project or code which reproduces the bug. 
- Screenshots are also a huge help if the problem is visual.
### Send a pull request!
- If you're fixing a bug, please add a failing test or code that can reproduce the issue.

### Why this lib?
I personally needed that library to create gray scaled .bmp file to 8-bit color format. 
I dealt with fingerprints raw data and it was necessary for me to have this specific file format to work with some SDK.
There already is an existing library that does a similar conversion to 24-bit color format and save it to a file but I needed a more generic one ([the existing one](https://github.com/ultrakain/AndroidBitmapUtil)).
I also wished to see the whole process of creating and publishing a library as an open source project. Those are the two main reasons why I created this library.
I am not sure that it will be useful to someone but I'd love to know if it is. 
If some of you really need a specific output format or even the possibility to store colored images, I might work on providing you this if this is a recurrent demand. 

Please hit me up at ygoular@gmail.com for any feedback or issues you may encounter.
