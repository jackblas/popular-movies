# popular-movies
Popular Movies Android app. Project for Udacity Android Developer Nanodegree program.

# TMDb API key
Enter your API key into the module's build file:
```
buildTypes.each {
  it.buildConfigField 'String', 'THEMOVIEDB_API_KEY', "\"paste-you-key-here\""
  }
```
# Stage Two:
 * Implement two-pane layout for tablets
 * Add trailers and reviews to the Details fragment
 * Allow users to mark a movie as a favorite. Save favorite movies in local database
   to make them available off-line.
 * Access to both on-line and off-line data is managed by Content Provider
    and handled by Cursor Loader
