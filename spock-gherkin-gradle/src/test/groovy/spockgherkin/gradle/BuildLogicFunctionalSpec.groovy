package spockgherkin.gradle

import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class BuildLogicFunctionalSpec extends Specification {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile
    File testSourceFolder
    File testResourceFolder

    // TODO: verify that you can run individual tests in IDE

    // TODO: update core to have features under resources; sub-folder vs. root?
    // TODO: test non-default configurations

    List<File> pluginClasspath

    def setup() {
        buildFile = testProjectDir.newFile("build.gradle")
        testSourceFolder = testProjectDir.newFolder("src", "featureTest", "groovy")
        testResourceFolder = testProjectDir.newFolder("src", "featureTest", "resources")
        pluginClasspath = findPluginClasspathUrl().readLines().collect { new File(it) }

        buildFile << """
            plugins {
                id "spockgherkin.gradle"
            }
            repositories {
                jcenter()
                mavenLocal()
            }
        """
    }

    private URL findPluginClasspathUrl() {
        def url = getClass().classLoader.getResource("plugin-classpath.txt")
        if (!url) {
            def file = new File("build/createClasspathManifest/plugin-classpath.txt")
            if (file.exists()) {
                url = file.toURI().toURL()
            }
        }
        if (!url) {
            throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.")
        }
        return url
    }

    // TODO: cleanup

//    def "does nothing when spock not present in dependencies"() {
//        given: "a build file that includes Groovy and JUnit (but not Spock)"
//        buildFile << """
//            dependencies {
//                testCompile "org.codehaus.groovy:groovy-all:2.4.1"
//                testCompile "junit:junit:4.12"
//            }
//        """
//        and: "a JUnit test"
//        new File(testSourceFolder, "SimpleTest.groovy") << """
//            class SimpleTest {
//                @org.junit.Test
//                void testIt() { }
//            }
//        """
//
//        when: "running the test task"
//        def result = GradleRunner.create()
//            .withProjectDir(testProjectDir.root)
//            .withPluginClasspath(pluginClasspath)
//            .withArguments("test")
//            .build()
//
//        then: "the tests pass"
//        result.task(":test").outcome == SUCCESS
//
//        when: "checking the project dependencies"
//        result = GradleRunner.create()
//            .withProjectDir(testProjectDir.root)
//            .withPluginClasspath(pluginClasspath)
//            .withArguments("dependencies")
//            .build()
//
//        then: "no Spock-related dependencies were added"
//        !result.output.contains("spock")
//    }
//
//    def "adds testCompile dependency on spock-gherkin-core when spock is present"() {
//        given: "a build file that includes Spock"
//        buildFile << """
//            dependencies { testCompile "org.spockframework:spock-core:1.0-groovy-2.4" }
//        """
//        and: "a JUnit test"
//        new File(testSourceFolder, "SimpleTest.groovy") << """
//            class SimpleTest {
//                @org.junit.Test
//                void testIt() { }
//            }
//        """
//
//        when: "running the test task"
//        def result = GradleRunner.create()
//            .withProjectDir(testProjectDir.root)
//            .withPluginClasspath(pluginClasspath)
//            .withArguments("test")
//            .build()
//
//        then: "the tests pass"
//        result.task(":test").outcome == SUCCESS
//
//        when: "checking the project dependencies"
//        result = GradleRunner.create()
//            .withProjectDir(testProjectDir.root)
//            .withPluginClasspath(pluginClasspath)
//            .withArguments("dependencies", "--configuration", "testCompile")
//            .build()
//
//        then: "a dependency on spock-gherkin-core was added to the testCompile configuration"
//        result.output.contains("spock-gherkin-core")
//    }

    def "creates configuration with dependency on spock-gherkin-core"() {
        when: "checking the project dependencies"
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath(pluginClasspath)
            .withArguments("dependencies", "--configuration", "featureTestCompile")
            .build()

        then: "a dependency on spock-gherkin-core was added to the featureTestCompile configuration"
        result.output.contains("spockgherkin:spock-gherkin-core")
        and: "a dependency on gherkin was added to the featureTestCompile configuration"
        result.output.contains("info.cukes:gherkin")
        and: "a dependency on spock-core was added to the featureTestCompile configuration"
        result.output.contains("org.spockframework:spock-core")
    }

    def "passes when feature and specs match"() {
        given: "a feature"
        new File(testResourceFolder, "manage_articles.feature") << """
            Feature: Manage Articles
                In order to make a blog
                As an author
                I want to create and manage articles

                Scenario: Articles List
                    Given I have articles titled Pizza, Breadsticks
                    When I go to the list of articles
                    Then I should see "Pizza"
                    And I should see "Breadsticks"
        """
        and: "a specification that matches the feature"
        new File(testSourceFolder, "ManageArticlesSpec.groovy") << """
            class ManageArticlesSpec extends spock.lang.Specification {
                def 'Articles List'() {
                    given: 'I have articles titled Pizza, Breadsticks'
                    when: 'I go to the list of articles'
                    then: 'I should see "Pizza"'
                    and: 'I should see "Breadsticks"'
                }
            }
        """

        when: "running the testFeature task"
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath(pluginClasspath)
            .withArguments("testFeature")
            .build()

        then: "the test passes"
        result.task(":testFeature").outcome == SUCCESS
    }

    def "fails when features present but no tests"() {
        given: "a feature file (but no tests)"
        new File(testResourceFolder, "manage_articles.feature") << """
            Feature: Manage Articles
                In order to make a blog
                As an author
                I want to create and manage articles

                Scenario: Articles List
                    Given I have articles titled Pizza, Breadsticks
                    When I go to the list of articles
                    Then I should see "Pizza"
                    And I should see "Breadsticks"
        """

        when: "running the test task"
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath(pluginClasspath)
            .withArguments("testFeature")
            .buildAndFail()

        then: "the testFeature task fails with a message indicating that the features weren't tested and that more information is available"
        result.task(":testFeature").outcome == FAILED
        result.output.contains("Feature files were found, but no Gherkin run report was found; perhaps you didn't run any specifications? Run with --info to get more log output.")

        when: "running the testFeature task with --info"
        result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath(pluginClasspath)
            .withArguments("--info", "testFeature")
            .buildAndFail()

        then: "the testFeature task fails with a message indicating that the features weren't tested, where the report was expected, and which features weren't tested"
        result.task(":testFeature").outcome == FAILED
        result.output.contains("Did not find report file: build/reports/gherkin/featureTest.json")
        result.output.contains("Found feature files: src/featureTest/resources/manage_articles.feature")
        result.output.contains("Feature files were found, but no Gherkin run report was found; perhaps you didn't run any specifications?")
    }

    def "fails when features and tests present but no specs"() {
        given: "a feature file"
        new File(testResourceFolder, "manage_articles.feature") << """
            Feature: Manage Articles
                In order to make a blog
                As an author
                I want to create and manage articles

                Scenario: Articles List
                    Given I have articles titled Pizza, Breadsticks
                    When I go to the list of articles
                    Then I should see "Pizza"
                    And I should see "Breadsticks"
        """
        and: "a JUnit test (but no Spock tests)"
        new File(testSourceFolder, "SimpleTest.groovy") << """
            class SimpleTest {
                @org.junit.Test
                void testIt() { }
            }
        """

        when: "running the testFeature task"
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath(pluginClasspath)
            .withArguments("testFeature")
            .buildAndFail()

        then: "the testFeature task fails with a message indicating that the features weren't tested and that more information is available"
        result.task(":testFeature").outcome == FAILED
        result.output.contains("Feature files were found, but no Gherkin run report was found; perhaps you didn't run any specifications? Run with --info to get more log output.")
    }

    def "fails when unmatched feature"() {
        given: "a feature with a matching specification"
        new File(testResourceFolder, "manage_articles.feature") << """
            Feature: Manage Articles
                In order to make a blog
                As an author
                I want to create and manage articles

                Scenario: Articles List
                    Given I have articles titled Pizza, Breadsticks
                    When I go to the list of articles
                    Then I should see "Pizza"
                    And I should see "Breadsticks"
        """
        new File(testResourceFolder, "article_modification.feature") << """
            Feature: Article Modification
                Scenario: Add Article
                    Given I have articles titled Pizza, Breadsticks
                    When I add an article titled Calzone
                    Then I should see "Pizza"
                    And I should see "Breadsticks"
                    And I should see "Calzone"
        """
        and: "a feature without a matching specification"
        new File(testSourceFolder, "ManageArticlesSpec.groovy") << """
            class ManageArticlesSpec extends spock.lang.Specification {
                def 'Articles List'() {
                    given: 'I have articles titled Pizza, Breadsticks'
                    when: 'I go to the list of articles'
                    then: 'I should see "Pizza"'
                    and: 'I should see "Breadsticks"'
                }
            }
        """

        when: "running the testFeature task"
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath(pluginClasspath)
            .withArguments("testFeature")
            .buildAndFail()

        then: "the testFeature task fails with a message indicating that not all features had matching specifications"
        result.task(":testFeature").outcome == FAILED
        // TODO: html report
        println result.output // TODO
        result.output.contains("Some features did not have matching specifications run. See report at: build/reports/gherkin/featureTest.txt")
        and: "the reports contain appropriate contents"
        new File(testProjectDir.root, "build/reports/gherkin/featureTest.json").text.trim() == """
            |{
            |    "errorCount": 1,
            |    "details": [
            |        {
            |            "spockSpecFilename": null,
            |            "featureFilePresent": true,
            |            "spockSpecPresent": false,
            |            "spockSpecPackage": null,
            |            "gherkinFeatureFilePath": "src/featureTest/resources/article_modification.feature",
            |            "name": "Article Modification"
            |        },
            |        {
            |            "spockSpecFilename": "ManageArticlesSpec.groovy",
            |            "featureFilePresent": true,
            |            "spockSpecPresent": true,
            |            "spockSpecPackage": "ManageArticlesSpec",
            |            "gherkinFeatureFilePath": "src/featureTest/resources/manage_articles.feature",
            |            "name": "Manage Articles"
            |        }
            |    ]
            |}
        """.stripMargin().trim()
        // TODO: better report formatting
        new File(testProjectDir.root, "build/reports/gherkin/featureTest.txt").text.trim() == """
            |Features with errors: 1
            |
            |Details:
            |Article Modification; src/featureTest/resources/article_modification.feature; null; null; true; false
            |Manage Articles; src/featureTest/resources/manage_articles.feature; ManageArticlesSpec; ManageArticlesSpec.groovy; true; true
        """.stripMargin().trim()
    }

    def "fails when unmatched spec"() {
        given: "a specification without a matching feature"
        new File(testSourceFolder, "ManageArticlesSpec.groovy") << """
            class ManageArticlesSpec extends spock.lang.Specification {
                def 'Articles List'() {
                    given: 'I have articles titled Pizza, Breadsticks'
                    when: 'I go to the list of articles'
                    then: 'I should see "Pizza"'
                    and: 'I should see "Breadsticks"'
                }
            }
        """

        when: "running the testFeature task"
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath(pluginClasspath)
            .withArguments("testFeature")
            .buildAndFail()

        then: "the testFeature fails with a message that the feature is missing"
        result.task(":testFeature").outcome == FAILED
        def testResults = new XmlSlurper().parse(new File(testProjectDir.root, "build/test-results/TEST-ManageArticlesSpec.xml"))
        // TODO: standardize terminology; Gherkin source file vs. feature file
        testResults.testcase.failure.@message == "java.lang.AssertionError: Spock specification lacks a Gherkin source file; Expected src/featureTest/resources/manage_articles.feature; If the source file has a different location, specify it using @FeatureFilename"
    }

    // TODO: consider adding support for allowExtraSpecifications

    def "fails when feature out-of-sync with spec"() {
        given: "a feature"
        new File(testResourceFolder, "manage_articles.feature") << """
            Feature: Manage Articles
                In order to make a blog
                As an author
                I want to create and manage articles

                Scenario: Articles List
                    Given I have articles titled Pizza, Calzone, Breadsticks
                    When I go to the list of articles
                    Then I should see "Pizza"
                    And I should see "Calzone"
                    And I should see "Breadsticks"
        """
        and: "a specification for the feature with mismatched content"
        new File(testSourceFolder, "ManageArticlesSpec.groovy") << """
            class ManageArticlesSpec extends spock.lang.Specification {
                def 'Articles List'() {
                    given: 'I have articles titled Pizza, Breadsticks'
                    when: 'I go to the list of articles'
                    then: 'I should see "Pizza"'
                    and: 'I should see "Breadsticks"'
                }
            }
        """

        when: "running the testFeature task"
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath(pluginClasspath)
            .withArguments("testFeature")
            .buildAndFail()

        then: "the testFeature fails with a message that it didn't match the feature"
        result.task(":testFeature").outcome == FAILED
        def testResults = new XmlSlurper().parse(new File(testProjectDir.root, "build/test-results/TEST-ManageArticlesSpec.xml"))
        println testResults
        // TODO: standardize terminology; Gherkin source file vs. feature file
        testResults.testcase.failure.@message == "org.junit.ComparisonFailure: Mismatch in step content for step #1 in scenario manage-articles;articles-list expected:<...icles titled Pizza, [Calzone, ]Breadsticks> but was:<...icles titled Pizza, []Breadsticks>"
    }

    // TODO: testing of all types of mismatch scenarios... possibly in core instead of here
}
