# anscapes
An utility registering some ansi escape code. Mainly for colored terminal output and cursor movement.
Also permits rendering images on a terminal.

### Grab it
Using your build manager of choice (here Gradle) and Jitpack repository : 
```groovy
repositories {
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
    implementation "com.github.Gui-Yom:anscapes:0.11.0"
}
```

### Image to Ansi converter
Allows you to convert an image to a string of characters and ansi sequences.

The bias option reduce the precision of the color equivalence algorithm (simple orthogonal distance),
effectively reducing the size of the generated output and increasing the speed of rendering.
Some sample results (`src/test/resources/shield.png`) :

| bias | render (ms) | % baseline | size (ko) | % baseline |
|------|-------------|------------|-----------|------------|
| 0    | 5.36        | 100        | 41.8      | 100        |
| 2    | 5.14        | 96         | 40.4      | 97         |
| 4    | 4.62        | 86         | 38.3      | 92         |
| 8    | 4.08        | 76         | 33.1      | 79         |
| 16   | 3.34        | 62         | 26.2      | 63         |
| 32   | 2.71        | 51         | 23.4      | 56         |

##### Why is size important here ?
Size is important since most of the overhead will come from the terminal itself when displaying 40Ko of data.
For a still image this is negligible but not when trying to display videos since the
terminal will try to render about 5Mo/s of characters.

This is highly inspired by multiple similar projects in other languages.
