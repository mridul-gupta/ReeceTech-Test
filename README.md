## ReeceTech Test

### High level design
Repository (to manange local & remote data sources)
  - allows changing or adding data sources
  
Presenter

UI (main.kt)



### Data layer
Repository: Abstracts local/remote/cache. Manages getting data from data sources.

Localdata: gets data from local lists.

### Presenter
Prepares and stores data.

main.kt used as test UI



### Testing

defaultRepository- create repository with injected fake data source to test. Tests repository in isolation.

presenter- inject fake repository to test behavior. Fake repository can return error/success as needed. Tests presenter with provided repository.

localDataSource - tests local data source in isolation. As repository may be testing with fake localdatasource, this is required to be tested separately.


### Usage
clone and build using gradle (intellij/kotlin/jvm)

run main.kt to test using command line
