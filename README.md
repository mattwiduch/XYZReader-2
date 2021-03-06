# XYZReader 2 - Android News Reader

<img src="https://cloud.githubusercontent.com/assets/15446842/18227186/f5353726-7213-11e6-8da7-ad971cf4249f.png"/>

XYZReader 2 is news reader application for Android tablets and smartphones running Android 4.1 (API level 16) or newer. It demonstrates use of [Material
Design](https://material.google.com/) principles to create engaging user interface.

**New Features:**

- Brand new article list layout optimised for tablets and landscape mode
- Brand new layout for detail activity featuring collapsing toolbar
- Shared element transitions between list and article screens
- Status bar changes color to match article image
- Custom PageTransformer for article ViewPager
- Full bleed images with fade-in animations
- Vertical swipe refresh for list
- New, enhanced colour theme
- And many more

## Try it out
To install the app on a connected device or running emulator, run:

```gradle
git clone https://github.com/mattwiduch/XYZReader2.git
cd XYZReader2
./gradlew installDebug
```

## Dependencies
XYZReader 2 uses following third-party libraries:
- [Volley](https://android.googlesource.com/platform/frameworks/volley/)
- [OkHttp](http://square.github.io/okhttp/)

## License
```
Copyright (c) 2016 Mateusz Widuch

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

