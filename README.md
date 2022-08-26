# It's compiling

https://xkcd.com/303/

![compiling](https://imgs.xkcd.com/comics/compiling.png)

## How to use

Just apply it to your root project and your total sitting around time will be recorded in a `slowMachine.properties`
file.

PS.: Don't forget to add this file to the your `.gitignore`

```kotlin
plugins {
    id("io.github.budius.slow-machine")
}
```
