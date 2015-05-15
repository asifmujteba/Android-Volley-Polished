# Android-Volley-Polished
Android Volley networking library polished/sharpened by adding OKHttp 2.3 stack, Multipart Requests, Gson and much more..

<h2>Feature Set:</h2>
- `OkHttp` + `okio` Integration for fast networking
- Handful of `Gson` Requests
- Handful of `Multipart` Requests
- Custom Header and Params Support Added in new Requests
- Default `LruBitmapCache` implementation Added.
-  Just extend `AbstractVolleyManager` class which is easily customizable and automatically creates a request queue, image loader and plenty of helper methods like request cancelling, request tracking, retry policies etc to ease our lives
-  Does not include Volley's code which can be used from here [AndroidVolleyMirror](https://github.com/asifmujteba/AndroidVolleyMirror) which makes it so easy to update the Volley's code from Google repository whenever needed without having to merge the changes
-  More coming soon

<h2>Installation</h2>
- For the time being you need to download the code, Soon it will be available through gradle

<h2>Author</h2>
Asif Mujteba, asifmujteba@gmail.com

<h2>Credits</h2>
Okhttp: Square

Volley: Google

OkHttpStack Code taken from: https://gist.github.com/bryanstern/4e8f1cb5a8e14c202750

<h2>License</h2>
Android-Volley-Polished is available under the Apache 2.0 license. See the LICENSE file for more info.
