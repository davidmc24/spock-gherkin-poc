package spockgherkin.core.gherkin.model

import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includeNames = true, includePackage = false)
class ModelFeature {
    File file
    String filePath

    String id
    String name
    String description
    List<String> comments // TODO: remove unless using for spec generation
    List<String> tags = []
    ModelBackground background
    List<ModelScenario> scenarios = [] // TODO: remove if not useful
    List<ModelScenarioOutline> scenarioOutlines = [] // TODO: remove if not useful
    List<String> scenarioAndOutlineNames = [] // TODO: remove if not useful
    List<IdentifiedModelStepContainer> stepContainers = []

    boolean isFeatureFilePresent() {
        return file.file
    }
}
