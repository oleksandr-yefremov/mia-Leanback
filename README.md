[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16) [![Build Status](https://travis-ci.org/7factory/mia-Leanback.svg?branch=master)](https://travis-ci.org/7factory/mia-Leanback) [![Release](https://jitpack.io/v/7factory/mia-Leanback.svg)](https://jitpack.io/#7factory/mia-Leanback)
[![License](http://img.shields.io/:license-mit-brightgreen.svg?style=flat)](https://raw.githubusercontent.com/7factory/mia-Leanback/master/LICENSE)

## Using Gradle

Add the following lines to your root build.gradle:

``` gradle
allprojects {
    repositories {
        [...]
        maven { url "https://jitpack.io" }
    }
}
```

Then reference the library from your module's build.gradle:

``` gradle
dependencies {
    [...]
    compile 'com.github.7factory:mia-leanback:x.y'
}
```

## Integration

1. Extend from `LeanbackActivity`.

 ``` java
 public class MainActivity extends LeanbackActivity {
     [...]
 }
 ```

2. Configure your leanback experience, f.e. enter fullscreen on landscape.

 ``` java
 @Override
 protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_main);
     
     // Enter fullscreen on landscape
     forceFullscreenOnLandscape(true); 
 
     [...] 
 }
 ```

3. Leanback! 
 
 :tv:
