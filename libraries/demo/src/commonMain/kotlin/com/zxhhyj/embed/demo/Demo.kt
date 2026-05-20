package com.zxhhyj.embed.demo

import com.zxhhyj.embed.Resource
import com.zxhhyj.embed.ResourceDirectory

fun main() {
    val resourceDirectory = object : ResourceDirectory {
        override val key = "demo"
        override val allPaths = listOf("hello.txt")

        private val resources = mapOf(
            "hello.txt" to Resource(
                key = "hello_txt",
                chunks = listOf("SGVsbG8sIEVtYmVkIQ==")
            )
        )

        override fun get(path: String) = resources[path]
    }

    val resources = resourceDirectory.toResources()
    println(resources.asString("hello.txt"))
}