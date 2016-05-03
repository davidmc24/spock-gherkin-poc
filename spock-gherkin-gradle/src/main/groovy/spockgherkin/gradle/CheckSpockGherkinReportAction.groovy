package spockgherkin.gradle

import groovy.json.JsonSlurper
import org.gradle.api.Action
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.testing.Test

class CheckSpockGherkinReportAction implements Action<Test> {
    private final FileTree featureFiles
    private final File reportFile
    private final String configurationName // TODO: is this the right name for this concept?

    CheckSpockGherkinReportAction(FileTree featureFiles, File reportFile, String configurationName) {
        this.featureFiles = featureFiles
        this.reportFile = reportFile
        this.configurationName = configurationName
    }

    @Override
    void execute(Test task) {
        // TODO: if we're only supporting a hardcoded sourceset, maybe we can assume that feature files should exist
        if (!featureFiles.empty) {
            if (!reportFile.exists()) {
                // TODO: consider generating a pretty report
                def project = task.project
                if (project.logger.infoEnabled) {
                    def reportFilePath = project.relativePath(reportFile)
                    def featureFilePaths = featureFiles.files.collect { project.relativePath(it) }.sort()
                    task.logger.info("Did not find report file: ${reportFilePath}")
                    task.logger.info("Found feature files: ${featureFilePaths.join(", ")}")
                    throw new FeatureMismatchException("Feature files were found, but no Gherkin run report was found; perhaps you didn't run any specifications?")
                } else {
                    throw new FeatureMismatchException("Feature files were found, but no Gherkin run report was found; perhaps you didn't run any specifications? Run with --info to get more log output.")
                }
            }
            def data = new JsonSlurper().parse(reportFile)
            data.details.each { featureMapping ->
                if (!featureMapping.spockSpecPresent) {
                    throw new FeatureMismatchException("Some features did not have matching specifications run. See report at: build/reports/gherkin/${configurationName}.txt")
                }
            }
        }
    }
}
