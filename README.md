# anscapes
An utility registering some ansi escape code. 
Mainly for colored terminal output.

### Grab it
Using Jitpack and your build manager of choice (here Gradle) : 
```groovy
repositories {
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
    implementation "com.github.LimeiloN:anscapes:0.7.0"
}
```

### Image to Ansi converter
Allows you to convert an Image to a colored String.
This is highly inspired from [https://github.com/fenwick67/term-px](https://github.com/fenwick67/term-px),
a nice JS lib made by [Drew Harwell](https://github.com/fenwick67).
I started from his code, removed what I did not need and added some optimizations.
The process is configurable.
