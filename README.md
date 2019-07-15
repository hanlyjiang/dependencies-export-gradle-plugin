# Export project dependencies to excel file

## About
This is a gradle plugin for export all project's (include subprojects) dependencies to an excel file；

## Usage 
1. apply plugin 
 ```groovy
plugins {
    id 'cn.hanlyjiang.gradle.analysis'
}
```

2. execute `AnalysisDependencies` task , this will generate an excel file named `dependencies.xls` file at `$rootProject.buildDir/distributions/`