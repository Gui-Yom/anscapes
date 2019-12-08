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
    implementation "com.github.LimeiloN:anscapes:0.8.0"
}
```

### Image to Ansi converter
Allows you to convert an image to a string of characters and ansi sequences (named TerminalImage).

This is highly inspired from [https://github.com/fenwick67/term-px](https://github.com/fenwick67/term-px),
a nice JS lib made by [Drew Harwell](https://github.com/fenwick67).

