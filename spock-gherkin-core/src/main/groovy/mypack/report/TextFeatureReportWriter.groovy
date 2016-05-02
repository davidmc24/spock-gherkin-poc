package mypack.report

class TextFeatureReportWriter implements FeatureReportWriter {
    @Override
    void write(int errorCount, List<FeatureMapping> featureMappings, File file) {
        file.parentFile.mkdirs()
        file.withPrintWriter { pw ->
            pw.println("Features with errors: ${errorCount}")
            pw.println()
            pw.println("Details:")
            featureMappings.each { featureMapping ->
                pw.println("${featureMapping.name}; ${featureMapping.gherkinFeatureFilePath}; ${featureMapping.spockSpecPackage}; ${featureMapping.spockSpecFilename}; ${featureMapping.featureFilePresent}; ${featureMapping.spockSpecPresent}")
            }
        }
        //Map<SpecInfo, File> specToFeatureFile
//        def featureFileReader = new FeatureFileReader()
//        List<File> nonFeatureFiles = []
//        List<File> unmatchedFeatureFiles = []
//        featureDir.eachFileRecurse(FileType.FILES) {
//            if (it.name.endsWith(".feature")) {
//                if (!specToFeatureFile.values().contains(it)) {
//                    unmatchedFeatureFiles << it
//                }
//            } else {
//                nonFeatureFiles << it
//            }
//        }
//        new File("spock-gherkin-report.txt").withPrintWriter { pw ->
//            pw.println("Features:")
//            specToFeatureFile.each { specInfo, file ->
//                def specClassName = "${specInfo.package}.${specInfo.filename.replaceAll(/\.groovy/, '')}"
//                if (file.file) {
//                    pw.println("${specInfo.name} (${file.path}; ${specClassName})")
//                } else {
//                    pw.println("${specInfo.name} (MISSING ${file.path}; ${specClassName})")
//                }
//            }
//            unmatchedFeatureFiles.each { file ->
//                def featureName = featureFileReader.readFeatureName(file)
//                pw.println("${featureName} (${file.path}; MISSING specification)")
//            }
//            if (nonFeatureFiles) {
//                pw.println()
//                pw.println("Non-feature files:")
//                nonFeatureFiles.each { file ->
//                    pw.println("${file.path}")
//                }
//            }
//        }
    }
}
