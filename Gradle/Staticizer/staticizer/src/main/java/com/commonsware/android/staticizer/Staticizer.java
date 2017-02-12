/***
 Copyright (c) 2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.staticizer;

import com.google.common.base.CaseFormat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.lang.model.element.Modifier;

class Staticizer {
  void generate(File input, File outputDir,
                String packageName) throws IOException {
    Type type=
      new TypeToken<LinkedHashMap<String,Object>>() {}.getType();
    LinkedHashMap<String, Object> data=
      new Gson().fromJson(new FileReader(input), type);
    String basename=removeExtension(input.getAbsolutePath());
    TypeSpec.Builder builder=TypeSpec.classBuilder(basename)
      .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

    for (Map.Entry<String, Object> entry : data.entrySet()) {
      String fieldName=
        CaseFormat.LOWER_CAMEL
          .to(CaseFormat.UPPER_UNDERSCORE, entry.getKey());
      FieldSpec.Builder field;

      if (entry.getValue() instanceof Float) {
        field=FieldSpec.builder(TypeName.FLOAT, fieldName)
          .initializer("$L", entry.getValue());
      }
      else if (entry.getValue() instanceof Double) {
        field=FieldSpec.builder(TypeName.DOUBLE, fieldName)
          .initializer("$L", entry.getValue());
      }
      else if (entry.getValue() instanceof Integer) {
        field=FieldSpec.builder(TypeName.INT, fieldName)
          .initializer("$L", entry.getValue());
      }
      else if (entry.getValue() instanceof Long) {
        field=FieldSpec.builder(TypeName.LONG, fieldName)
          .initializer("$L", entry.getValue());
      }
      else if (entry.getValue() instanceof Boolean) {
        field=FieldSpec.builder(TypeName.BOOLEAN, fieldName)
          .initializer("$L", entry.getValue());
      }
      else {
        field=FieldSpec.builder(String.class, fieldName)
          .initializer("$S", entry.getValue().toString());
      }

      field.addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
        .build();

      builder.addField(field.build());
    }

    JavaFile.builder(packageName, builder.build())
      .build()
      .writeTo(outputDir);
  }

  // inspired by http://stackoverflow.com/a/990492/115145

  private static String removeExtension(String s) {
    String result;

    int sepIndex=
      s.lastIndexOf(System.getProperty("file.separator"));

    if (sepIndex==-1) {
      result=s;
    }
    else {
      result=s.substring(sepIndex+1);
    }

    int extIndex=result.lastIndexOf(".");

    if (extIndex!=-1) {
      result=result.substring(0, extIndex);
    }

    return(result);
  }
}
