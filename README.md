Clarifai Java Client
==================
[![Release](https://img.shields.io/maven-central/v/com.clarifai.clarifai-api2/core.svg?style=flat-square)](http://mvnrepository.com/artifact/com.clarifai.clarifai-api2/core)

|        | Travis | AppVeyor |
|--------|--------|----------|
| master | [![Build Status](https://travis-ci.org/Clarifai/clarifai-java.svg?branch=master)](https://travis-ci.org/Clarifai/clarifai-java) | [![Build Status](https://ci.appveyor.com/api/projects/status/93a843ujrp35u6a6?svg=true)](https://ci.appveyor.com/project/robertwenquan/clarifai-java) |

A simple client for the Clarifai v2 API.

* Try the Clarifai demo at: https://clarifai.com/demo
* Sign up for a free account at: https://clarifai.com/developer/signup/
* Read the developer guide at: https://clarifai.com/developer/guide/
* Read the full Javadocs at: https://jitpack.io/com/github/clarifai/clarifai-java/core/2.4.0/javadoc/

**NOTE**: If you are planning to contribute to this API client, please see the [CONTRIBUTING.md](CONTRIBUTING.md) file.

Installation
------------
### Gradle:

Add the following to the dependencies section of your `build.gradle`:

```groovy
// Add the client to your dependencies:
dependencies {
    compile 'com.clarifai.clarifai-api2:core:[version]'
}

// Make sure you have the Maven Central Repository in your Gradle File
repositories {
    mavenCentral()
}
```

### Maven:

Add the following to your dependencies:

```xml
<dependency>
  <groupId>com.clarifai.clarifai-api2</groupId>
  <artifactId>core</artifactId>
  <version>[version]</version>
</dependency>
```

Getting Started
---------------
There are two authentication approaches. The recommended is to use [API Keys](http://blog.clarifai.com/introducing-api-keys-a-safer-way-to-authenticate-your-applications/). You may also use the now deprecated app ID & secret pair. See the [Authentication section](https://clarifai.com/developer/guide/authentication#authentication) of the docs for more info.

To create a `ClarifaiClient` instance with an API Key do the following:

```java
final ClarifaiClient client = new ClarifaiBuilder("apiKey").buildSync();
```

If using an app ID & secret instead, pass both to the constructor:

```java
final ClarifaiClient client = new ClarifaiBuilder("appID", "appSecret").buildSync();
```

The `ClarifaiBuilder` optionally allows you to pass in a custom `OkHttpClient` (allowing for user-defined parameters
such as connection timeouts, etc):

```java
final ClarifaiClient client = new ClarifaiBuilder(apiKey)
    .client(new OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(new HttpLoggingInterceptor(logger::info).setLevel(HttpLoggingInterceptor.Level.BASIC))
        .build()
    )
    .buildSync();
```

Making API requests
---------------------------------------
Network operations using the API client only occur by calling `.executeSync()` or `.executeAsync(...)` on a
`ClarifaiRequest<T>` object.

All methods on the `ClarifaiClient` will either return a `ClarifaiRequest<T>` or `ClarifaiPaginatedRequest<T>`, or a
custom object that allows you to specify parameters that go into ultimately building a `ClarifaiRequest<T>` or
`ClarifaiPaginatedRequest<T>`.

Using `.executeSync()` will block the current thread and return a `ClarifaiResponse<T>`, where `T` is the
returned data type. `ClarifaiResponse<T>` has methods to check the success or failure status of the method, and methods
that mimic Java 8 `Optional<T>` to safely retrieve the returned data.

Using `.executeAsync()` returns `void`, but allows the user to pass in callback(s) to handle successful responses,
failed responses, and/or network errors.

`ClarifaiPaginatedRequest<T>` objects should be thought of as factories that create `ClarifaiRequest<T>`s. When building
a `ClarifaiPaginatedRequest<T>`, you have the option of specifying a `perPage` (the number of elements in each page
of the response).

Once a `ClarifaiPaginatedRequest<T>` is built, you can call `ClarifaiPaginatedRequest#getPage(int)` to get back a
`ClarifaiRequest<T>` for the specified page. Pages are 1-indexed. Currently, the API does not indicate how many elements
there are in a paginated request in total, but this is planned for the future.

Example Requests
----------------
Predict the contents of an image:
```java
Model<Concept> generalModel = client.getDefaultModels().generalModel();

PredictRequest<Concept> request = generalModel.predict().withInputs(
        ClarifaiInput.forImage("https://samples.clarifai.com/metro-north.jpg")
    );
List<ClarifaiOutput<Concept>> result = request.executeSync().get();
```
Predict the contents of a video:
```java
Model<Frame> generalVideoModel = client.getDefaultModels().generalVideoModel();

PredictRequest<Frame> videoRequest = generalVideoModel.predict().withInputs(
    ClarifaiInput.forVideo("https://samples.clarifai.com/beer.mp4")
);
List<ClarifaiOutput<Frame>> videoResults = videoRequest.executeSync().get();
```

See the [developer guide](https://clarifai.com/developer/guide/) for more detailed examples
and examples of other features such as custom training and search.

Using API responses
------------------
All responses from the API are immutable data types (constructed using AutoValue). Some of these types, such as
`ClarifaiModel`, are also used as parameters to make requests (for example, you can either get a model as a response
from the API, or pass a model to the API to create it in your account). Builders are exposed to the user for all
data types that they can use as request params.

Some convenience methods are provided as well on data types; eg: `myModel.predict()` on `ClarifaiModel`.

This allows you to make requests in a fluent, object-oriented way. For example:

```java
client.getModelByID("myID").executeAsync(
    model -> model.predict()
        .withInputs(input)
        .executeAsync(
            outputs -> System.out.println("First output of this prediction is " + outputs.get(0))
        ),
    code -> System.err.println("Error code: " + code + ". Error msg: " + message),
    e -> { throw new ClarifaiException(e); }
);
```


Requirements
------------
JDK 7 or later.


Android
---------
The client will work on Android Gingerbread and higher (minSdkVersion 9).

You need to add the INTERNET permission to your `AndroidManifest.xml`, as follows:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

The Android Linter may also give an "InvalidPackage" error. This error may be safely ignored, and is caused by OkHttp
using Java 8 methods when they are available (which will not occur on Android). To suppress these linter errors, do
*NOT* disable your linter. Simply follow the instructions
[here](https://guides.codepath.com/android/Consuming-APIs-with-Retrofit#issues).
