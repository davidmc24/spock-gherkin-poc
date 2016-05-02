package mypack.gherkin.model

import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includeNames = true, includePackage = false)
class ModelScenarioOutline implements IdentifiedModelStepContainer {
    String id
    String name
    List<ModelStep> steps = []
    ModelTable examples

    void addStep(ModelStep step) {
        steps << step
    }
}
