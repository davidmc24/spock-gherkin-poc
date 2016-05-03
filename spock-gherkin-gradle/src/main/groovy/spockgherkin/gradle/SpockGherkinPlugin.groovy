package spockgherkin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaModel

class SpockGherkinPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.plugins.apply(GroovyPlugin)
        def javaConvention = project.convention.getPlugin(JavaPluginConvention)
        def reportingExtension = project.extensions.getByType(ReportingExtension)

        // TODO: consider configurability
        def sourceSet = javaConvention.sourceSets.create("featureTest")
        def featureFiles = sourceSet.resources.matching { include("**/*.feature") }
        def featureDir = only(sourceSet.resources.srcDirs)
        def reportFile = reportingExtension.file("gherkin/${sourceSet.name}.json")
        def reportsDir = reportFile.parentFile // TODO: would it make more sense to just pass the path to the intended file to the test task?
        project.dependencies.add("featureTestCompile", "spockgherkin:spock-gherkin-core:0.0.1-SNAPSHOT")
        def testTask = project.tasks.create("testFeature", Test)
        testTask.conventionMapping.map("testClassesDir") { sourceSet.output.classesDir }
        testTask.conventionMapping.map("classpath") { sourceSet.runtimeClasspath }
        testTask.conventionMapping.map("testSrcDirs") { new ArrayList(sourceSet.groovy.srcDirs) }
        testTask.description = "Runs the feature tests."
        testTask.group = "verification"
        testTask.inputs.sourceDir(featureDir) // Otherwise, will consider the task up-to-date when there are only feature files
        testTask.jvmArgs("-Dgherkin.enabled=true", "-Dgherkin.configurationName=${sourceSet.name}", "-Dgherkin.baseDir=${project.rootDir.absolutePath}", "-Dgherkin.featuresDir=${featureDir.absolutePath}", "-Dgherkin.reportsDir=${reportsDir.absolutePath}")
        testTask.doLast(new CheckSpockGherkinReportAction(featureFiles, reportFile, sourceSet.name))
        project.tasks.getByName("check").dependsOn(testTask)

        // TODO: test this
        project.plugins.withType(IdeaPlugin) {
            def model = project.extensions.getByType(IdeaModel)
            model.module.scopes.TEST.plus += [ project.configurations.featureTestRuntime ]
            model.module.testSourceDirs += sourceSet.groovy.srcDirs
            model.module.testSourceDirs += sourceSet.resources.srcDirs
        }

        // TODO: test this
        project.dependencies.add("featureTestCompile", "com.athaydes:spock-reports:1.2.8")
    }

    // Attempts to apply in a way that doesn't make assumptions about the project... work in progress
    void applyGeneric(Project project) { // TODO: use or remove
        def javaConvention = project.convention.getPlugin(JavaPluginConvention)
        project.gradle.addListener(new AbstractDependencyResolutionListener() {
            @Override
            void beforeResolve(ResolvableDependencies resolvableDependencies) {
                def spockPresent = resolvableDependencies.dependencies.find { it.group == "org.spockframework" && it.name == "spock-core" }
                if (spockPresent) {
                    project.dependencies.add(resolvableDependencies.name, "com.commercehub:spock-gherkin-core:0.0.1-SNAPSHOT")
                }
            }
        })
        def reportingExtension = project.extensions.getByType(ReportingExtension)
        javaConvention.sourceSets.all { sourceSet ->
            // TODO: would this ever match anything other than the default test task name?
            // TODO: how to register custom task names?
            def testTaskName = sourceSet.name == "test" ? "test" : sourceSet.getTaskName("test", null)
            def testTask = project.tasks.findByName(testTaskName)
            if (testTask && testTask instanceof Test) {
                testTask.jvmArgs("-Dgherkin.enabled=true")
                // TODO: pass info to test task
                sourceSet.resources.srcDirs.each { testTask.inputs.sourceDir(it) }
                def featureFiles = sourceSet.resources.matching { include("**/*.feature") }
                def reportFile = reportingExtension.file("gherkin/${sourceSet.name}.json") // TODO: need to lazy-resolve?
                // TODO: does changing the report dir after applying the plugin break things?
                testTask.doLast(new CheckSpockGherkinReportAction(featureFiles, reportFile, sourceSet.name))
            }
        }
    }

    // TODO: extract to utility class
    private static <T> T only(Collection<? extends T> collection) {
        if (collection.size() == 1) {
            return collection.first()
        }
        throw new IllegalStateException("Too many elements")
    }
}
