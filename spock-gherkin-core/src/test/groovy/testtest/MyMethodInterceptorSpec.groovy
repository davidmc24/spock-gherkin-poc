package testtest

import mypack.FeatureStorage
import mypack.GherkinConfiguration
import mypack.MyMethodInterceptor
import mypack.report.FeatureMapping
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Specification

class MyMethodInterceptorSpec extends Specification {
    @Rule
    TemporaryFolder temporaryFolder

    File featureDir
    GherkinConfiguration gherkinConfiguration
    IMethodInvocation mockMethodInvocation
    SpecInfo specInfo
    MyMethodInterceptor methodInterceptor
    List<FeatureMapping> featureMappings = []

    def setup() {
        featureDir = temporaryFolder.newFolder("features")
        gherkinConfiguration = new GherkinConfiguration(baseDir: temporaryFolder.root, featuresDir: featureDir)
        mockMethodInvocation = Mock(IMethodInvocation)
        specInfo = new SpecInfo(name: "The feature works")
        mockMethodInvocation.spec >> specInfo
        methodInterceptor = new MyMethodInterceptor(gherkinConfiguration, featureMappings)
    }

    def "Specifications are required to have matching feature files"() {
        given: "An enabled configuration"
        gherkinConfiguration.enabled = true

        when: "A specification without a matching feature file is started"
        methodInterceptor.interceptSpecExecution(mockMethodInvocation)

        then: "An assertion is failed with a descriptive message"
        def ex = thrown(AssertionError)
        ex.message == "Spock specification lacks a Gherkin source file; Expected features/the_feature_works.feature; If the source file has a different location, specify it using @FeatureFilename"
    }

    // TODO: should depend on hamcrest?

    // TODO: consider adding support for allowExtraSpecifications

    // TODO: tests for feature mapping behaviors
}
