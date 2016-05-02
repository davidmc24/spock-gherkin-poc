package spockgherkin.core

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.SpecInfo

class FeatureFilenameExtension extends AbstractAnnotationDrivenExtension<FeatureFilename> {
    @Override
    void visitSpecAnnotation(FeatureFilename annotation, SpecInfo spec) {
        spec.metaClass.featureFilename = annotation.value()
    }
}
