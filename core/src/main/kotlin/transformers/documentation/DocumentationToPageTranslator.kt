package org.jetbrains.dokka.transformers.documentation

import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.Model.DocumentationNode
import org.jetbrains.dokka.Model.Module
import org.jetbrains.dokka.pages.ModulePageNode
import org.jetbrains.dokka.pages.PageNode
import org.jetbrains.dokka.plugability.DokkaContext

interface DocumentationToPageTranslator {
    operator fun invoke(module: Module, context: DokkaContext): ModulePageNode
}