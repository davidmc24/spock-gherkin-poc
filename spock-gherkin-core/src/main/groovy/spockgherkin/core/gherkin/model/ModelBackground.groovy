package spockgherkin.core.gherkin.model

import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includeNames = true, includePackage = false)
class ModelBackground implements ModelStepContainer {
    String name
    String description
    List<String> comments // TODO: remove unless using for spec generation
    List<ModelStep> steps = []

    @Override
    void addStep(ModelStep step) {
        steps << step
    }
}
