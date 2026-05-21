package com.zxhhyj.embed

data class ResourceMapping(val path: String, val chunkVariableName: String, val key: String)

object ResourceDirectoryGenerator {
    fun generate(packageName: String, mappings: List<ResourceMapping>): String = buildString {
        appendLine("package $packageName")
        appendLine()
        appendLine("import com.zxhhyj.embed.Resource")
        appendLine("import com.zxhhyj.embed.ResourceDirectory")
        appendLine()
        appendLine("internal object ResourceDirectory : ResourceDirectory {")
        appendLine("    override val key: String = \"${packageName.replace('.', '-')}\"")
        appendLine("    private val resources: Map<String, Resource> = mapOf(")
        if (mappings.isNotEmpty()) {
            for (mapping in mappings) {
                appendLine("        \"${mapping.path.replace("\\", "/")}\" to Resource(\"${mapping.key}\", ${mapping.chunkVariableName}),")
            }
        }
        appendLine("    )")
        appendLine("    override val allPaths: List<String> = resources.keys.toList()")
        appendLine("    override operator fun get(path: String): Resource? = resources[path]")
        appendLine("}")
    }
}