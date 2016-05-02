package spockgherkin.core.gherkin.model

import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includeNames = true, includePackage = false)
class ModelScenario implements IdentifiedModelStepContainer {
    String id
    String name
    String description
    List<String> comments // TODO: remove unless using for spec generation
    List<String> tags = []
    List<ModelStep> steps = []

    void addStep(ModelStep step) {
        steps << step
    }
}
