package cn.hanlyjiang.gradle.analysis;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ExternalModuleDependency;
import org.gradle.api.artifacts.SelfResolvingDependency;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DependenceOutput extends DefaultTask {

    private String distDir = null;
    private WritableWorkbook workbook;
    private WritableSheet sheet;

    private int rowCount = 0;

    /*
        getProject().getDependencies() for add dept,getConfigurations() manager
        https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html#getDependencies--
     */
    @TaskAction
    void analysisDependencies() {
        LogUtils.w("Root Project:" + getProject().getRootProject().getName() + "\n");

        Project rootProject = getProject().getRootProject();

        // Collect
        List<Dependency> dependencies = new ArrayList<>();
        if (rootProject != null) {
            rootProject.getAllprojects().forEach(project -> collectProjectDept(project, dependencies));
        }
        // Export
        writeDependencies(dependencies);
    }

    private void writeDependencies(List<Dependency> dependencies) {
        distDir = new File(getProject().getRootProject().getBuildDir(), "distributions" + File.separator + "dependencies.xls").getAbsolutePath();
        workbook = WorkBookUtils.getWorkSheet(distDir);
        sheet = workbook.createSheet("Statistic", 0);

        writeHeader();
        dependencies.forEach(dependency -> writeOneDependence(dependency));

        try {
            workbook.write();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeHeader() {
        try {
            sheet.addCell(new Label(0, rowCount++, "dependencies"));
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }


    private void writeOneDependence(Dependency dependency) {
        List<String> info = deptInfo2Str(dependency);
        if (info == null || info.isEmpty()) {
            return;
        }
        for (String item : info) {
            LogUtils.i(item);
            try {
                sheet.addCell(new Label(0, rowCount++, item));
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }
    }

    private void collectProjectDept(Project project, List<Dependency> dependencies) {
        project.getConfigurations().forEach(files -> files.getAllDependencies().forEach(dependency -> {
            if (!dependencies.contains(dependency)) {
                dependencies.add(dependency);
            }
        }));
    }


    protected List<String> deptInfo2Str(Dependency dependency) {
        List<String> result = new ArrayList<>();
        if (dependency instanceof SelfResolvingDependency) {
            Set<File> resolve = ((SelfResolvingDependency) dependency).resolve(true);
            StringBuffer stringBuffer = new StringBuffer();
            int i = 0;
            for (File file : resolve) {
                LogUtils.w("name:" + file.getName() + "i=" + i + "," + "size=" + resolve.size());
                result.add(formatDept(dependency.getGroup(), file.getName(), dependency.getVersion()));
            }
        } else if (dependency instanceof ExternalModuleDependency) {
            ExternalModuleDependency dept = (ExternalModuleDependency) dependency;
            dept.getArtifacts().forEach(dependencyArtifact -> {
//                LogUtils.w("-----" + deptArtifactInfo(dependencyArtifact));
            });
            result.add(formatDept(dependency.getGroup(), dependency.getName(), dependency.getVersion()));
        } else {
            result.add(formatDept(dependency.getGroup(), dependency.getName(), dependency.getVersion()));
        }
        return result;
    }


    private String formatDept(String group, String name, String version) {
        if (isEmpty(group) && isEmpty(name) && isEmpty(version)) {
            return null;
        }
        return String.format("%s%s%s",
                isEmpty(group) ? "" : group + ":",
                isEmpty(name) ? "" : name,
                isEmpty(version) ? "" : ":" + version
        );
    }

    private boolean isEmpty(String str) {
        return str == null || str.isEmpty() || "unspecified".equals(str) || "null".equals(str);
    }

}