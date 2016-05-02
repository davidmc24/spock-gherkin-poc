package mypack.gherkin.model

import org.junit.Assert

/**
 * Checks that the specified features "match".
 *
 * Additional assertion clauses are used to provide context to errors.
 */
class ModelChecker {
    private final String featureFile
    private final ModelFeature gherkinFeature
    private final ModelFeature spockFeature

    // TODO: consider directly using spock objects

    ModelChecker(String featureFile, ModelFeature gherkinFeature, ModelFeature spockFeature) {
        this.featureFile = featureFile
        this.gherkinFeature = gherkinFeature
        this.spockFeature = spockFeature
    }

    void check() {
        checkFeatureName()
        checkFeatureDescription()
        checkFeatureTags()
//        checkFeatureComments()
//        checkFeatureBackground()
        checkScenarios()
        // TODO: more
    }

    private void checkFeatureName() {
        def gherkinFeatureName = gherkinFeature.name
        def spockFeatureName = spockFeature.name
        assert featureFile && gherkinFeatureName == spockFeatureName
    }

    private void checkFeatureDescription() {
        def gherkinFeatureDescription = gherkinFeature.description
        def spockFeatureDescription = spockFeature.description
        assert featureFile && gherkinFeatureDescription == spockFeatureDescription
    }

    private void checkFeatureTags() {
        def gherkinFeatureTags = gherkinFeature.tags
        def spockFeatureTags = spockFeature.tags
        assert featureFile && gherkinFeatureTags == spockFeatureTags
    }

    private void checkFeatureComments() {
        // TODO: consider adding annotations
        def gherkinFeatureComments = gherkinFeature.comments
        def spockFeatureComments = spockFeature.comments
        assert featureFile && gherkinFeatureComments == spockFeatureComments
    }

    private void checkFeatureBackground() {
        // TODO: consider adding annotations
        def gherkinFeatureBackgroundName = gherkinFeature.background?.name
        def spockFeatureBackgroundName = spockFeature.background?.name
        def gherkinFeatureBackgroundSteps = gherkinFeature.background?.steps
        def spockFeatureBackgroundSteps = spockFeature.background?.steps
        assert featureFile && gherkinFeatureBackgroundName == spockFeatureBackgroundName
        assert featureFile && gherkinFeatureBackgroundSteps == spockFeatureBackgroundSteps
    }

    private void checkScenarios() {
        def gherkinScenarioCount = gherkinFeature.scenarios.size()
        def spockScenarioCount = spockFeature.scenarios.size()
        assert featureFile && gherkinScenarioCount == spockScenarioCount
        for (i in 1..gherkinScenarioCount) {
            def gherkinScenario = gherkinFeature.scenarios[i-1]
            def spockScenario = spockFeature.scenarios[i-1]
            def gherkinScenarioName = gherkinScenario.name
            def spockScenarioName = spockScenario.name
            Assert.assertEquals("${featureFile} scenario ${i} name", gherkinScenario.name, spockScenario.name)
            //assert featureFile && "scenario ${i}" && gherkinScenarioName == spockScenarioName
        }
        assert false
    }
}
