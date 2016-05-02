package mypack.gherkin.file

import gherkin.formatter.Formatter
import gherkin.formatter.model.Background
import gherkin.formatter.model.Examples
import gherkin.formatter.model.Feature
import gherkin.formatter.model.Scenario
import gherkin.formatter.model.ScenarioOutline
import gherkin.formatter.model.Step
import groovy.transform.PackageScope

@PackageScope
class AbstractFormatter implements Formatter {
    @Override
    void syntaxError(String state, String event, List<String> legalEvents, String uri, Integer line) { }

    @Override
    void uri(String uri) { }

    @Override
    void feature(Feature feature) { }

    @Override
    void scenarioOutline(ScenarioOutline scenarioOutline) { }

    @Override
    void examples(Examples examples) { }

    @Override
    void startOfScenarioLifeCycle(Scenario scenario) { }

    @Override
    void background(Background background) { }

    @Override
    void scenario(Scenario scenario) { }

    @Override
    void step(Step step) { }

    @Override
    void endOfScenarioLifeCycle(Scenario scenario) { }

    @Override
    void done() { }

    @Override
    void close() { }

    @Override
    void eof() { }
}