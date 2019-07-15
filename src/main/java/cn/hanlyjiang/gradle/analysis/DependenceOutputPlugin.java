package cn.hanlyjiang.gradle.analysis;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class DependenceOutputPlugin implements Plugin<Project> {

    public void apply(Project project) {
        project.getTasks().create("AnalysisDependencies", DependenceOutput.class, (task) -> task.setGroup("Statistic"));
    }
}