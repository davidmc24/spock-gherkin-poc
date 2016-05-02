package mypack

import groovy.io.FileType
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import mypack.gherkin.file.FeatureFile
import mypack.gherkin.model.ModelFeature
import mypack.report.FeatureMapping
import org.spockframework.runtime.model.SpecInfo

class FeatureStorage {
    File baseDir
    File featureDir

    ModelFeature readFeature(SpecInfo specInfo) {
        def file = determineFile(specInfo)
        def feature = file.file ? FeatureFile.readFeature(file) : new ModelFeature(name: specInfo.name)
        feature.file = file
        feature.filePath = relativePath(file)
        return feature
    }

    public void eachUnmappedFeatureFile(Collection<FeatureMapping> featureMappings, @ClosureParams(value=SimpleType.class,options="java.io.File") Closure handler) {
        eachFeatureFile { file ->
            if (!featureMappings.any { new File(baseDir, it.gherkinFeatureFilePath) == file }) {
                handler(file)
            }
        }
    }

    private void eachFeatureFile(@ClosureParams(value=SimpleType.class,options="java.io.File") Closure fileHandler) {
        featureDir.eachFileRecurse(FileType.FILES) { file ->
            if (file.name.endsWith(".feature")) {
                fileHandler(file)
            }
        }
    }

    private File determineFile(SpecInfo specInfo) {
        // TODO: cleanup
        // "${specInfo.name.replaceAll(/\s/, "_")}.feature"
        String filename = specInfo.properties["featureFilename"] ?: toUnderscore(stripSpec(specInfo.name)) + ".feature"
        return new File(featureDir, filename)
    }

    // TODO: java 6 support

    String relativePath(File file) {
        return relativize(baseDir, file)
    }

    private static String relativize(File base, File file) {
        // TODO: better way to do this?
        if (!base) {
            return file.path
        }
        return file.canonicalPath.substring(base.canonicalPath.length() + 1)
    }

    // TODO: refactor this into a different class
    public static String stripSpec(String str) {
        def toRemove = ["Spec", "Specification"]
        toRemove.each {
            if (str.endsWith(it)) {
                str = str.substring(0, str.length() - it.length())
            }
        }
        return str
    }

    // TODO: refactor this into a different class
    public static String stripWhitespace(String str) {
        return str.replaceAll(/ /, '')
    }

    private static String toUnderscore(String str) {
        str.replaceAll(/(\B[A-Z])/,'_$1').toLowerCase().replaceAll(/ /, '_')
    }
}
