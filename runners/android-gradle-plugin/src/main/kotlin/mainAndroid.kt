package org.jetbrains.dokka.gradle

import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.util.GradleVersion
import org.jetbrains.dokka.DokkaConfiguration.ExternalDocumentationLink.Builder
import org.jetbrains.dokka.DokkaConfiguration.SourceRoot
import java.io.File

open class DokkaAndroidPlugin : DokkaPlugin() {
    override fun apply(project: Project) {
        DokkaVersion.loadFrom(javaClass.getResourceAsStream("/META-INF/gradle-plugins/org.jetbrains.dokka.properties"))

        val dokkaRuntimeConfiguration = project.configurations.create("dokkaRuntime")
        val defaultDokkaRuntimeConfiguration = project.configurations.create("defaultDokkaRuntime")
        val taskName = "dokka"

        defaultDokkaRuntimeConfiguration.defaultDependencies{ dependencies -> dependencies.add(project.dependencies.create("org.jetbrains.dokka:dokka-fatjar:${DokkaVersion.version}")) }

        if(GradleVersion.current() >= GradleVersion.version("4.10")) {
            project.tasks.register(taskName, DokkaAndroidTask::class.java).configure {
                it.moduleName = project.name
                it.outputDirectory = File(project.buildDir, taskName).absolutePath
            }
        } else {
            project.tasks.create(taskName, DokkaAndroidTask::class.java).apply {
                moduleName = project.name
                outputDirectory = File(project.buildDir, taskName).absolutePath
            }
        }

        project.tasks.withType(DokkaAndroidTask::class.java) { task ->
            task.multiplatform = project.container(GradlePassConfigurationImpl::class.java)
            task.configuration = GradlePassConfigurationImpl()
            task.dokkaRuntime = dokkaRuntimeConfiguration
            task.defaultDokkaRuntime = defaultDokkaRuntimeConfiguration
        }
    }
}

private val ANDROID_REFERENCE_URL = Builder("https://developer.android.com/reference/").build()

open class DokkaAndroidTask : DokkaTask() {

    @Input var noAndroidSdkLink: Boolean = false

    override fun collectSuppressedFiles(sourceRoots: List<SourceRoot>): List<String> {
        val generatedRoot = project.buildDir.resolve("generated").absoluteFile
        return sourceRoots
            .map { File(it.path) }
            .filter { it.startsWith(generatedRoot) }
            .flatMap { it.walk().toList() }
            .map { it.absolutePath }
    }

    init {
        project.afterEvaluate {
            if (!noAndroidSdkLink) externalDocumentationLinks.add(ANDROID_REFERENCE_URL)
        }
    }
}
