/***
  Copyright (c) 2008-2012 CommonsWare, LLC
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

package com.commonsware.abj.interp;

import android.os.Bundle;

public class RhinoInterpreterTests extends InterpreterTestCase {
  protected String getInterpreterName() {
    return("com.commonsware.abj.interp.RhinoInterpreter");
  }

  public void testNoInput() {
    Bundle input=new Bundle();
    Bundle output=execServiceTest(input);

    assert (output.size() == 0);
  }

  public void testSimpleResult() {
    Bundle input=new Bundle();

    input.putString(InterpreterService.SCRIPT, "1+2");

    Bundle output=execServiceTest(input);

    assertNull(output.getString("error"));
    assert (output.size() == 2);
    assertEquals(output.getString("result"), "3");
  }

  public void testComplexResult() {
    Bundle input=new Bundle();

    input.putString(InterpreterService.SCRIPT,
                    "_result.putInt(\"foo\", 1+2);");

    Bundle output=execServiceTest(input);

    assertNull(output.getString("error"));
    assert (output.size() == 3);
    assertEquals(output.getInt("foo"), 3);
  }
}