package spockgherkin.core.gherkin.file

import gherkin.parser.Parser
import spockgherkin.core.gherkin.model.ModelFeature

class FeatureFile {
    static String readFeatureName(File file) {
        return readFeature(file)?.name ?: file.name
    }

    static ModelFeature readFeature(File file) {
        return parse(file.text, file.toURI().toString())
    }

    static ModelFeature parse(String gherkin, String featureURI = "<unknown>") {
        def formatter = new ModelFormatter()
        new Parser(formatter).parse(gherkin, featureURI, 0)
        return formatter.modelFeature
    }
}
