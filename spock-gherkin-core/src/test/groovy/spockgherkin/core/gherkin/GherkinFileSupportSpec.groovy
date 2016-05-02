package spockgherkin.core.gherkin

import spockgherkin.core.gherkin.file.FeatureFile
import spock.lang.Specification

class GherkinFileSupportSpec extends Specification {
    def "Supports reading feature information"() {
        when: "reading a feature file with all feature information present"
        def feature = FeatureFile.parse("""
        |# This is a comment describing
        |# stuff about my awesome feature.
        |@Awesome @SuperFast
        |Feature: My Awesome Feature
        |  This is some further detail about
        |  what my awesome feature does.
        |""".stripMargin())

        then: "the feature information is present in the model"
        feature.id == "my-awesome-feature"
        feature.name == "My Awesome Feature"
        feature.description ==
                "This is some further detail about\n" +
                "what my awesome feature does."
        feature.comments == [
                "# This is a comment describing",
                "# stuff about my awesome feature."
        ]
        feature.tags == ["@Awesome", "@SuperFast"]

        when: "reading a minimal feature file"
        feature = FeatureFile.parse("Feature: My Barebones Feature")

        then: "the feature model is mostly empty"
        feature.id == "my-barebones-feature"
        feature.name == "My Barebones Feature"
        feature.description == ""
        feature.comments == []
        feature.tags == []
    }

    def "Supports reading background information"() {
        when: "reading a feature file with background information"
        def feature = FeatureFile.parse("""
        |Feature: My Feature With Background
        |  # This is a comment about my
        |  # background for my feature.
        |  Background: Epic background stuff
        |    This is everything that is needed
        |    to prepare you for simple pizza cooking.
        |
        |    Given you have a working oven
        |    And you have a pizza pan
        |    * you have a pizza cutter
        |""".stripMargin())
        def background = feature.background

        then: "the background information is present in the model"
        background.name == "Epic background stuff"
        background.description ==
                "This is everything that is needed\n" +
                "to prepare you for simple pizza cooking."
        background.comments == [
                "# This is a comment about my",
                "# background for my feature."
        ]
        background.steps*.keyword == ["Given ", "And ", "* "]
        background.steps*.name == [
                "you have a working oven",
                "you have a pizza pan",
                "you have a pizza cutter"
        ]
    }

    // TODO: scenarios
    // TODO: outlines
    // TODO: scenario/outline names
    // TODO: detailed steps
    // TODO: parse failure
}
