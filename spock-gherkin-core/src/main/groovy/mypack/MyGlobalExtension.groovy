package mypack

import mypack.gherkin.file.FeatureFile
import mypack.report.FeatureMapping
import mypack.report.JsonFeatureReportWriter
import mypack.report.TextFeatureReportWriter
import org.spockframework.runtime.extension.AbstractGlobalExtension
import org.spockframework.runtime.model.SpecInfo

class MyGlobalExtension extends AbstractGlobalExtension {
    // TODO: can use magical config objects???

    GherkinConfiguration gherkinConfiguration

    // TODO: tag support using RunnerConfiguration; runnerConfiguration.exclude.annotations*.name

    final List<FeatureMapping> featureMappings = []

    @Override
    void visitSpec(SpecInfo spec) {
        if (gherkinConfiguration.enabled) {
            // Uses IMethodInterceptor instead of IRunListener to allow injecting test failures
            def interceptor = new MyMethodInterceptor(gherkinConfiguration, featureMappings)
            spec.addInterceptor(interceptor)
            spec.features.each { it.addInterceptor(interceptor) }
        }
    }

    @Override
    void stop() {
        if (gherkinConfiguration.enabled) {
            def featureStorage = gherkinConfiguration.featureStorage
            FeatureFile featureFileReader = new FeatureFile()
            featureStorage.eachUnmappedFeatureFile(featureMappings) { file ->
                def featureMapping = new FeatureMapping()
                featureMapping.name = featureFileReader.readFeatureName(file)
                featureMapping.featureFilePresent = true
                featureMapping.gherkinFeatureFilePath = featureStorage.relativePath(file)
                featureMapping.spockSpecPresent = false
                featureMappings << featureMapping
            }

            featureMappings.sort(true) { it.name }

            generateReports()
        }
    }

    private void generateReports() {
        // TODO: consider adding support for allowExtraSpecifications
        int errorCount = featureMappings.count { !it.featureFilePresent || !it.spockSpecPresent }

        // TODO: allow configuring formats
        // TODO: html report
        new JsonFeatureReportWriter().write(errorCount, featureMappings, new File(gherkinConfiguration.reportsDir, "${gherkinConfiguration.configurationName}.json"))
        new TextFeatureReportWriter().write(errorCount, featureMappings, new File(gherkinConfiguration.reportsDir, "${gherkinConfiguration.configurationName}.txt"))
    }
}
