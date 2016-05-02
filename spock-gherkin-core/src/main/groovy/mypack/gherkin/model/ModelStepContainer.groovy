package mypack.gherkin.model

interface ModelStepContainer {
    String getName()
    List<ModelStep> getSteps()
    void addStep(ModelStep step)
}
