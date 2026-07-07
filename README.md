

# Tsikuri

Set of tools to automate UI with jvm code.


To use:

```
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.petrukhnov:tsikuri:0.0.2")
}
```

then in code:

```kotlin
    ClickHelper.waitAndClickImage("button.png")
```


