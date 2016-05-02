# Overview

TODO

# Features

* Ability to ensure that all Gherkin source files have associated Spock specifications
* Ability to ensure that Gherkin source files are in synch with their associated Spock specification
* Optional: Ability to ensure that there are no Spock specifications without an associated Gherkin source file

# Caveats

* All tag annotations must be in a "tag" package, and the tag is not taken into account in matching
* Tag ordering and case are taken into account when matching
* Comments (in both gherkin and spock) are ignored for matching purposes (TODO: consider adding a Spock annotation?)
* Background (presence, name, and steps) keywords are currently ignored for matching purposes (TODO: consider adding a Spock annotation for `setup` method?)
* DocString content types are ignored for matching purposes
* "*", "And", and "But" (both in Gherkin and Spock) are treated the same as the previous keyword/block kind for matching

* Spock `expect` and `cleanup` blocks are not considered during comparison to Gherkin source files
* Spock `setup`, `cleanup`, `setupSpec` and `cleanupSpec` methods are not considered during comparison to Gherkin source files
* Gherkin `background` keywords are not considered during comparison to Spock specifications
* Gherkin table arguments are not considered during comparison to Spock specifications (but `examples` are)
* TODO??? Tags (either in Gherkin or Spock) are not supported
