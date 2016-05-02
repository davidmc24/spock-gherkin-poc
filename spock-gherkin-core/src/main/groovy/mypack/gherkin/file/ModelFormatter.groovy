package mypack.gherkin.file

import gherkin.formatter.model.Background
import gherkin.formatter.model.Examples
import gherkin.formatter.model.Feature
import gherkin.formatter.model.Scenario
import gherkin.formatter.model.ScenarioOutline
import gherkin.formatter.model.Step
import groovy.transform.PackageScope
import mypack.gherkin.model.ModelBackground
import mypack.gherkin.model.ModelFeature
import mypack.gherkin.model.ModelScenario
import mypack.gherkin.model.ModelScenarioOutline
import mypack.gherkin.model.ModelStep
import mypack.gherkin.model.ModelStepContainer
import mypack.gherkin.model.ModelTable

@PackageScope
class ModelFormatter extends AbstractFormatter {
    ModelFeature modelFeature
    private ModelStepContainer currentStepContainer

    @Override
    void syntaxError(String state, String event, List<String> legalEvents, String uri, Integer line) {
    }

    @Override
    void feature(Feature feature) {
        modelFeature = new ModelFeature(id: feature.id, name: feature.name, description: feature.description, comments: feature.comments*.value, tags: feature.tags*.name)
    }

    @Override
    void scenarioOutline(ScenarioOutline scenarioOutline) {
        modelFeature.scenarioAndOutlineNames << scenarioOutline.name
        ModelScenarioOutline modelScenarioOutline = new ModelScenarioOutline(id: scenarioOutline.id, name: scenarioOutline.name)
        modelFeature.scenarioOutlines << modelScenarioOutline
        modelFeature.stepContainers << modelScenarioOutline
        currentStepContainer = modelScenarioOutline
        // TODO: description, tags, comments
    }

    @Override
    void examples(Examples examples) {
        super.examples(examples)
//        def table = new ModelTable()
//         TODO: populate from rows
//        modelFeature.scenarioOutlines.last().examples = table
    }

    @Override
    void startOfScenarioLifeCycle(Scenario scenario) {
    }

    @Override
    void background(Background background) {
        ModelBackground modelBackground = new ModelBackground(name: background.name, description: background.description, comments: background.comments*.value)
        modelFeature.background = modelBackground
        currentStepContainer = modelBackground
    }

    @Override
    void scenario(Scenario scenario) {
        modelFeature.scenarioAndOutlineNames << scenario.name
        ModelScenario modelScenario = new ModelScenario(id: scenario.id, name: scenario.name, description: scenario.description, comments: scenario.comments*.value, tags: scenario.tags*.name)
        modelFeature.scenarios << modelScenario
        modelFeature.stepContainers << modelScenario
        currentStepContainer = modelScenario
    }

    @Override
    void step(Step step) {
        def modelStep = new ModelStep(keyword: step.keyword, name: step.name, comments: step.comments*.value, docString: step.docString*.value, rows: ModelTable.from(step.rows))
        currentStepContainer.addStep(modelStep)
    }

    @Override
    void endOfScenarioLifeCycle(Scenario scenario) {
    }
}
