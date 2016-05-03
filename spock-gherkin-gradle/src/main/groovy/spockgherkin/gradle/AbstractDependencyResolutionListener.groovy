package spockgherkin.gradle

import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies

class AbstractDependencyResolutionListener implements DependencyResolutionListener {
    @Override
    void beforeResolve(ResolvableDependencies resolvableDependencies) { }

    @Override
    void afterResolve(ResolvableDependencies resolvableDependencies) { }
}
