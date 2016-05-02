package mypack.gherkin.model

import groovy.transform.Canonical
import groovy.transform.TypeChecked
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.spockframework.runtime.model.BlockInfo
import org.spockframework.runtime.model.BlockKind
import org.spockframework.util.Matchers

import static org.hamcrest.CoreMatchers.anyOf
import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertThat
import static org.junit.Assert.fail

@TypeChecked
class StepChecker {
    private final String scenarioId
    private final List<ModelStep> gherkinSteps
    private final List<SpockStep> spockSteps

    StepChecker(String scenarioId, List<ModelStep> gherkinSteps, List<BlockInfo> spockBlocks) {
        this.scenarioId = scenarioId
        this.gherkinSteps = gherkinSteps
        this.spockSteps = spockBlocks.collectMany { block -> block.texts.collect { text -> new SpockStep(block.kind, text) } }
    }

    void check() {
        // TODO: multiple approaches to assertion error?
        // matches everything except one new step at the end
        // matches everything except one new step at the beginning/middle
        // matches everything but one step removed (beginning, middle, end)
        // matches everything except on name changed
        // matches everything but order of two steps swapped?
        def previousGherkinKeyword = null
//        assertEquals("Wrong number of steps in scenario ${scenarioId}", gherkinSteps.size(), spockSteps.size())
        // TODO: base on rows/data/etc.
        for (stepIndex in 0..gherkinSteps.size()-1) {
            def gherkinStep = gherkinSteps[stepIndex]
            def spockStep = spockSteps[stepIndex]
            assertNotNull("Too few steps in scenario ${scenarioId}; expected step #${stepIndex+1} to match '${gherkinStep.keyword}${gherkinStep.name}'", spockStep)
            def blockKind = spockStep.kind
            def gherkinKeyword = gherkinStep.normalizedKeyword
            if (gherkinKeyword in ["*", "And", "But"]) {
                Assert.assertTrue("First step in a scenario shouldn't be '${gherkinKeyword}'", stepIndex > 0)
                gherkinKeyword = previousGherkinKeyword
            }
            switch (gherkinKeyword) {
                case "Given":
                    assertEquals("Block kind mismatch for step #${stepIndex+1} in scenario ${scenarioId}", BlockKind.SETUP, blockKind); break
                case "When":
                    assertEquals("Block kind mismatch for step #${stepIndex+1} in scenario ${scenarioId}", BlockKind.WHEN, blockKind); break
                case "Then":
                    assertThat("Block kind mismatch for step ${stepIndex+1} in scenario ${scenarioId}", blockKind, anyOf(equalTo(BlockKind.THEN), equalTo(BlockKind.EXPECT))); break
                default:
                    fail("Unhandled keyword '${gherkinKeyword}' in scenario ${scenarioId}"); break
            }
            assertEquals("Mismatch in step content for step #${stepIndex+1} in scenario ${scenarioId}", gherkinStep.name, spockStep.text)
            previousGherkinKeyword = gherkinKeyword
        }
        assertEquals("Scenario ${scenarioId} contains too many steps", gherkinSteps.size(), spockSteps.size())
    }

    @Canonical
    private static class SpockStep {
        BlockKind kind
        String text
    }
}
