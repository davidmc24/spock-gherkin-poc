package spockgherkin.core

import groovy.transform.PackageScope
import spock.config.ConfigurationObject

@ConfigurationObject("gherkin")
class GherkinConfiguration {
    // TODO: test config
    boolean enabled = Boolean.getBoolean("gherkin.enabled")
    // TODO: remove as spock config if not usable that way
    String configurationName = System.getProperty("gherkin.configurationName", "featureTest") // TODO: is "configuration" really the right thing to call this?
    File baseDir = new File(System.getProperty("gherkin.baseDir", "."))
    File featuresDir = new File(System.getProperty("gherkin.featuresDir", "src/featureTest/resources"))
    File reportsDir = new File(System.getProperty("gherkin.reportsDir", "build/reports/gherkin"))

    private transient FeatureStorage featureStorage

    @PackageScope
    FeatureStorage getFeatureStorage() {
        if (!featureStorage) {
            featureStorage = new FeatureStorage(baseDir: baseDir, featureDir: featuresDir)
        }
        return featureStorage
    }
}
