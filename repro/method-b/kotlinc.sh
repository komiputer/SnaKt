#!/usr/bin/env bash
# Minimal kotlinc driver over the pinned kotlin-compiler-embeddable 2.3.0 from the Gradle cache.
# Usage: kotlinc.sh <src-dir-or-file> <extra-classpath-or-empty> <out-dir>
M=~/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin
K=$M/kotlin-compiler-embeddable/2.3.0/dfa7ae7e29f042fe2abf0708315aa367989bb07d/kotlin-compiler-embeddable-2.3.0.jar
S=$M/kotlin-stdlib/2.3.0/ebc4eb2b6e6c91b6c844c1e3183920d86f2ef656/kotlin-stdlib-2.3.0.jar
R=$M/kotlin-reflect/2.3.0/a723c4fbeeb7b48910a0f82b8ea826da1b17dd0b/kotlin-reflect-2.3.0.jar
SR=$M/kotlin-script-runtime/2.3.0/fb89e0ab064ea19d08c6d6bc9f044d16c5487fc4/kotlin-script-runtime-2.3.0.jar
CO=~/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlinx/kotlinx-coroutines-core-jvm/1.8.0/ac1dc37a30a93150b704022f8d895ee1bd3a36b3/kotlinx-coroutines-core-jvm-1.8.0.jar
TR=~/.gradle/caches/modules-2/files-2.1/org.jetbrains.intellij.deps/trove4j/1.0.20200330/3afb14d5f9ceb459d724e907a21145e8ff394f02/trove4j-1.0.20200330.jar
AN=~/.gradle/caches/modules-2/files-2.1/org.jetbrains/annotations/23.0.0/8cc20c07506ec18e0834947b84a864bfc094484e/annotations-23.0.0.jar
export KOTLIN_STDLIB=$S
java -cp "$K:$S:$R:$SR:$CO:$TR:$AN" org.jetbrains.kotlin.cli.jvm.K2JVMCompiler \
  -no-stdlib -cp "$S${2:+:$2}" -d "$3" "$1"
