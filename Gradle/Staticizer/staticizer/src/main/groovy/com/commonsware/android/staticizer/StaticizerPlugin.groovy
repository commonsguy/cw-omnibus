/***
 Copyright (c) 2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.staticizer

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin

public class StaticizerPlugin implements Plugin<Project> {
  @Override
  public void apply(Project target) {
    def isApp=target.plugins.hasPlugin AppPlugin
    def isLib=target.plugins.hasPlugin LibraryPlugin

    if (!isApp && !isLib) {
      throw new
        IllegalStateException("This plugin depends upon the com.android.application or com.android.library plugins")
    }

    target.extensions.create('staticizer', StaticizerConfig)

    def variants

    if (isApp) {
      variants=target.android.applicationVariants
    }
    else {
      variants=target.android.libraryVariants
    }

    variants.all { variant ->
      def task=
          target.tasks.create("staticize${variant.name.capitalize()}",
              StaticizerTask)

      task.outputDir=
          new File("${target.buildDir}/generated/source/staticizer/${variant.name}")
      task.group="commonsware"
      task.description="Generate ${variant.name} Java code from JSON"

      variant.javaCompile.dependsOn task
      variant.registerJavaGeneratingTask task, task.outputDir
    }
  }
}

class StaticizerConfig {
  def String packageName
}