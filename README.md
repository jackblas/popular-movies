# popular-movies
Popular Movies Android app. Project for Udacity Android Developer Nanodegree program.

# TMDb API key
Enter your API key into the module's build file:
```
buildTypes.each {
  it.buildConfigField 'String', 'THEMOVIEDB_API_KEY', "\"paste-you-key-here\""
  }
```
