package spockgherkin.core

import org.spockframework.runtime.extension.AbstractMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import spock.lang.Title
import spockgherkin.core.gherkin.model.ModelFeature
import spockgherkin.core.gherkin.model.StepChecker
import spockgherkin.core.report.FeatureMapping

import static org.hamcrest.CoreMatchers.hasItem
import static org.junit.Assert.*
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class MyMethodInterceptor extends AbstractMethodInterceptor {
    // TODO: rename class

    final GherkinConfiguration gherkinConfiguration
    final FeatureStorage featureStorage
    final List<FeatureMapping> featureMappings
    FeatureMapping currentFeatureMapping

    ModelFeature gherkinFeature

    MyMethodInterceptor(GherkinConfiguration gherkinConfiguration, List<FeatureMapping> featureMappings) {
        this.gherkinConfiguration = gherkinConfiguration
        this.featureStorage = gherkinConfiguration.featureStorage
        this.featureMappings = featureMappings
    }

//    String toGherkin(SpecInfo spec) {
//        StringBuilder sb = new StringBuilder()
//        sb.append("Feature: ${spec.name ?: 'Missing @Title'}\n")
//        if (spec.narrative) {
//            spec.narrative.eachLine { line ->
//                sb.append("  ${line}\n")
//            }
//        }
//        for (feature in spec.features) {
//            sb.append("  \n")
//            sb.append("  Scenario: ${feature.name}\n")
//            for (block in feature.blocks) {
//                if (block.kind == BlockKind.CLEANUP) {
//                    // Cleanup isn't supported in Gherkin, but it isn't really part of the feature... so ignore it
//                } else if (block.kind == BlockKind.WHERE) {
//                    def iterations = iterationsByFeature[feature]
//                    def lengths = scanLengths(feature, iterations)
//
//                    sb.append("    Examples:\n")
//                    sb.append("      |")
//                    feature.parameterNames.eachWithIndex { String parameterName, int i ->
//                        sb.append(" ${parameterName.padLeft(lengths[i])} |")
//                    }
//                    sb.append("\n")
//
//                    for (iteration in iterations) {
//                        sb.append("      |")
//                        iteration.dataValues.eachWithIndex { Object dataValue, int i ->
//                            sb.append(" ${dataValue.toString().padLeft(lengths[i])} |")
//                        }
//                        sb.append("\n")
//                    }
//                } else {
//                    String label
//                    switch (block.kind) {
//                        case BlockKind.SETUP: label = "Given"; break
//                        case BlockKind.EXPECT:
//                            // Gherkin doesn't support simultaneous stimulus and response, so this will never match.
//                            // However, it should be included in the generated output for clearer diagnostics.
//                            label = "Expect"; break
//                        case BlockKind.WHEN: label = "When"; break
//                        case BlockKind.THEN: label = "Then"; break
//                        default:
//                            label = "???"; break
//                    }
//                    block.texts.eachWithIndex { String entry, int i ->
//                        // TODO: match to either and or but
//                        entry.eachLine { String line, int lineNumber ->
//                            if (lineNumber == 0) {
//                                sb.append("    ${(i == 0 ? label : 'And').padLeft(5)} ${line}\n")
//                            } else {
//                                sb.append("    ${line.trim()}\n")
//                            }
//                        }
//                        // sb.append("    ${(i == 0 ? label : 'And').padLeft(5)} ${entry}\n")
//                    }
//                }
//            }
//        }
//        return sb.toString()
//    }

//    private static List<Integer> scanLengths(FeatureInfo feature, List<IterationInfo> iterations) {
//        List<Integer> lengths = []
//        feature.parameterNames.eachWithIndex { String parameterName, int i ->
//            def candidates = [parameterName]
//            for (iteration in iterations) {
//                candidates << iteration.dataValues[i].toString()
//            }
//            lengths << candidates*.length().max()
//        }
//        return lengths
//    }

//    private ModelFeature assembleFeature(SpecInfo spec) {
//        def modelFeature = new ModelFeature(name: spec.name, description: spec.narrative, tags: spec.reflection.annotations.findAll { it.annotationType().package.name.endsWith(".tag") }*.annotationType().collect { "@${it.simpleName}" })
//        spec.features.each { feature ->
//            // TODO: support scenario outlines
//            def modelScenario = new ModelScenario(name: feature.name)
//            modelFeature.scenarios << modelScenario
//        }
//        return modelFeature
//    }

    @Override
    void interceptSpecExecution(IMethodInvocation invocation) throws Throwable {
        def spec = invocation.spec
        gherkinFeature = featureStorage.readFeature(spec)

        // TODO: does it make sense to have a detailed feature mapping file/report if we're going to fail tests if anything is out-of-sync?
        currentFeatureMapping = new FeatureMapping(name: gherkinFeature.name, spockSpecPackage: spec.package, spockSpecFilename: spec.filename, spockSpecPresent: true, gherkinFeatureFilePath: gherkinFeature.filePath, featureFilePresent: gherkinFeature.featureFilePresent)
        featureMappings << currentFeatureMapping

        // TODO: consider adding support for allowExtraSpecifications
        assertTrue("Spock specification lacks a Gherkin source file; Expected ${gherkinFeature.filePath}; If the source file has a different location, specify it using @${FeatureFilename.simpleName}", gherkinFeature.featureFilePresent)
        if (gherkinFeature.featureFilePresent) {
            // TODO: should whitespace be required???
            assertEquals("Spock specification's name does not match the Gherkin source file's feature declaration; Either rename the specification class or specify it using @${Title.simpleName}", FeatureStorage.stripWhitespace(gherkinFeature.name), FeatureStorage.stripWhitespace(FeatureStorage.stripSpec(spec.name)))
            assertEquals("Spock specification's feature methods do not match the Gherkin source file", gherkinFeature.scenarioAndOutlineNames, spec.features*.name)
            //        def expectedFeatureCount = gherkinFeature.scenarioOutlines.size() + gherkinFeature.scenarios.size()
            //        def actualFeatureCount = spec.features.size()
            //        assertEquals("Wrong number of scenarios/scenario outlines in feature ${gherkinFeature.id}", expectedFeatureCount, actualFeatureCount)
            // TODO: consider terminology; use gherkin words or spock words?
            // TODO: should order matter?
            def spockFeatureNames = spec.features*.name
            for (scenario in gherkinFeature.scenarios) {
                assertThat("Spock specification does not contain a matching feature method for scenario ${scenario.id}", spockFeatureNames, hasItem(scenario.name))
            }
            for (scenarioOutline in gherkinFeature.scenarioOutlines) {
                assertThat("Spock specification does not contain a matching feature method for scenario outline ${scenarioOutline.id} not found in Spock specification", spockFeatureNames, hasItem(scenarioOutline.name))
            }
            // TODO: check for missing specs
            // TODO: check for missing methods
        }
        invocation.proceed()
    }

    @Override
    void interceptFeatureExecution(IMethodInvocation invocation) throws Throwable {
        invocation.proceed()
        def scenarioIndex = invocation.feature.declarationOrder
        // TODO: how does ordering work with intermingled scenarios and outlines?
        def gherkinScenario = gherkinFeature.stepContainers[scenarioIndex]
        def spockFeature = invocation.feature
        assertEquals("Name mismatch for scenario #${scenarioIndex+1} in ${gherkinFeature?.id}", gherkinScenario?.name, spockFeature?.name)
        new StepChecker(gherkinScenario.id, gherkinScenario.steps, spockFeature.blocks).check()
        //gherkinScenario.steps.eachWithIndex { ModelStep step, int stepIndex ->
            //def spockBlock = spockFeature.blocks[stepIndex]
            //def gherkinContent = step.keyword + " " + step.name
            //def spockContent = spockBlock.kind.name() + " " + spockBlock.texts.join("\n") // TODO: pretty sure this isn't right
            //Assert.assertEquals("Step mismatch for step #${stepIndex} in scenario ${gherkinScenario.id}", gherkinContent, spockContent)
            // TODO: make assertion work
//                Assert.assertEquals("Step mismatch for step #${stepIndex} in scenario ${gherkinScenario.id}", gherkinScenario.steps[stepIndex], spockFeature.blocks[stepIndex].texts)
        //}
        // TODO: store in report?
        //assert gherkinFeature == spockFeature
//            new ModelChecker(featureFile.path, gherkinFeature, spockFeature).check()
    }
}
