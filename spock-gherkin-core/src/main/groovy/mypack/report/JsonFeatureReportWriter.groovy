package mypack.report

import groovy.json.JsonOutput

class JsonFeatureReportWriter implements FeatureReportWriter {
    @Override
    void write(int errorCount, List<FeatureMapping> featureMappings, File file) {
        file.parentFile.mkdirs()
        file.text = JsonOutput.prettyPrint(JsonOutput.toJson([errorCount: errorCount, details: featureMappings]))
    }
}
