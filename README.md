# Description

Proof-of-concept implementation of tooling to synchronize between Gherkin feature files and Spock specifications.

There are two parts to this approach:

* A Spock extension that hooks into the execution of your Spock specifications, and verifies that their structure matches (some caveats here) a directory of Gherkin feature files.
* A Gradle plugin that checks that each Gherkin feature file had an associated Spock specification run.

Together, this supports a workflow where you want to be sure that:

* If you create a new feature file, your build fails if there isn't an associated test.
* If you add a new feature to a feature file, your build fails if there isn't an associated test.
* If you change a feature in a feature file, your build fails if it doesn't match the feature in the specification.
* If you change a feature in a specification, your build fails if it doesn't match the feature in the feature file.
* If you create a new specification, your build fails if there isn't an associated feature file.
  * Currently, the expectation is that you only enable this logic for a "featureTest" configuration, and within that configuration, require a one-to-one mapping between feature files and specifications.
  * It would be reasonable to consider adding an "allowExtraSpecifications" setting or something similar, if we wanted to support a mix of feature tests and unit tests (for example) in a single configuration.

It is possible to run tests with spock-gherkin enabled in an IDE.  By default, it will be disabled.  To enable it, a system property "gherkin.enabled=true" to your run configuration.  When running this way, it will not enforce that all features must have a matching specification executed, allowing for specifications to be run in isolation.

While this demonstrates the basic concept of automated enforcement of a matched workflow, there are various aspects that are unfinished:

* Support for data driven testing
* Support for Gherkin table parameters
* Support for tags
* Support for comments
* Support for "allowExtraSpecifications" option
* Exploration of automatic generation of feature files based on specifications, or specifications based on feature files
* Richer reporting
* Tighter IDE integration
* More test coverage
* Various code cleanup

# How do I run a demo?

1. Clone the repo
2. `(cd spock-gherkin-core && ./gradlew clean build publishToMavenLocal)`
3. `(cd spock-gherkin-gradle && ./gradlew clean build publishToMavenLocal)`
4. `(cd spock-gherkin-demo && ./gradlew clean build)`

If desired, open the demo project in your IDE.

Now that you (hopefully) have it working, try making some changes in the demo project, and re-run the tests with `./gradlew testFeature`.  Here are some scenarios you can try out to test the behavior:

* No features: remove `src/featureTest/resources/*.feature`
* No specifications: remove `src/featureTest/groovy/*Spec.groovy`
* Specification without a feature file: remove a single file matching `src/featureTest/resources/*.feature`
* Feature file without a specification: remove a single file matching `src/featureTest/groovy/*Spec.groovy`
* Scenario/feature method mismatch: edit `src/featureTest/resources/manage_articles.feature` to include "Scenario: Something else" or change "Articles List" to "Articles Listing"
* Step count mismatch: edit `src/featureTest/resources/manage_articles.feature` to add "And Pizza is awesome" at the end
* Block type mismatch: edit `src/featureTest/resources/manage_articles.feature` to change "Given I have articles titled Pizza, Breadsticks" to "Then I have articles titled Pizza, Breadsticks"
* Block label mismatch: edit `src/featureTest/resources/manage_articles.feature` to change "Given I have articles titled Pizza, Breadsticks" to "Given I have articles titled Pizza; Breadsticks"
* Mismatched file name: rename `src/featureTest/groovy/ManageArticlesSpec.groovy` or change/remove the FeatureFilename annotation in `src/featureTest/groovy/SalarySpec.groovy`
