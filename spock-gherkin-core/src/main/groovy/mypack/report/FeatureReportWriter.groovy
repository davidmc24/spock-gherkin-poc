package mypack.report

interface FeatureReportWriter {
    void write(int errorCount, List<FeatureMapping> featureMappings, File file)
}
