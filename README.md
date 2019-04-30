# anscapes
An utility registering some ansi escape code. 
Mainly for colored console output.

### Grab it
Using Jitpack and your manager of choice (here Gradle) : 
```groovy
repositories {
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
    implementation "com.github.LimeiloN:anscapes:0.3.0"
}
```

### Image to Ansi converter
Allow easy conversion from image to colored ansi.
Warning ! This is stolen from [https://github.com/fenwick67/term-px](https://github.com/fenwick67/term-px),
a nice JS lib made by [Drew Harwell](https://github.com/fenwick67).
I translated his code from JS to Java fur use into one of my projects.
