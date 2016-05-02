package mypack.gherkin.model

import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includeNames = true, includePackage = false)
class ModelStep {
    String keyword
    String name
    List<String> comments // TODO: remove unless using for spec generation
    List<String> docString
    ModelTable rows

    String getNormalizedKeyword() {
        return keyword.trim()
    }
}
