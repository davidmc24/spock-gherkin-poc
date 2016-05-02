package spockgherkin.core

import spockgherkin.core.GherkinConfiguration
import spockgherkin.core.MyGlobalExtension
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class MyGlobalExtensionSpec extends Specification {
    @Rule
    TemporaryFolder temporaryFolder

    File featureDir
    File reportsDir
    GherkinConfiguration gherkinConfiguration
    MyGlobalExtension myGlobalExtension

    def setup() {
        // TODO: cleanup
        featureDir = temporaryFolder.newFolder("features")
        reportsDir = temporaryFolder.newFolder("reports")
        gherkinConfiguration = new GherkinConfiguration(baseDir: temporaryFolder.root, featuresDir: featureDir, reportsDir: reportsDir)
        myGlobalExtension = new MyGlobalExtension(gherkinConfiguration: gherkinConfiguration)
    }

    def "All feature files are required to have a matching specifications"() {
        given: "An enabled configuration"
        gherkinConfiguration.enabled = true

        and: "A feature file without a matching specification"
        new File(featureDir, "test.feature").bytes = getClass().getResource("serve_coffee.feature").bytes

        when: "A test suite is run"
        myGlobalExtension.start()
        myGlobalExtension.stop()

        then: "Reports indicate that the feature was missing a specification"
        new File(reportsDir, "featureTest.json").text.trim() == '''
        |{
        |    "errorCount": 1,
        |    "details": [
        |        {
        |            "spockSpecFilename": null,
        |            "featureFilePresent": true,
        |            "spockSpecPresent": false,
        |            "spockSpecPackage": null,
        |            "gherkinFeatureFilePath": "features/test.feature",
        |            "name": "Serve coffee"
        |        }
        |    ]
        |}'''.stripMargin().trim()
        new File(reportsDir, "featureTest.txt").text.trim() == '''
        |Features with errors: 1
        |
        |Details:
        |Serve coffee; features/test.feature; null; null; true; false
        |'''.stripMargin().trim()
    }
}
