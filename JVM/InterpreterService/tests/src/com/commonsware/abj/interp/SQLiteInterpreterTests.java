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
import java.util.ArrayList;

public class SQLiteInterpreterTests extends InterpreterTestCase {
  protected String getInterpreterName() {
    return("com.commonsware.abj.interp.SQLiteInterpreter");
  }
  
  public void testNoInput() {
    Bundle input=new Bundle();
    Bundle results=execServiceTest(input);
    
    assertNotNull(results);
    assert(results.size()==0);
  }
  
  public void testSingleColumnResult() {
    Bundle input=new Bundle();

    input.putString(InterpreterService.SCRIPT, "SELECT 1+2 AS result;");
    
    Bundle output=execServiceTest(input);
    
    assert(output.size()==2);
    assertEquals(output.getString("result"), "3");
  }
  
  public void testSeveralColumnResult() {
    Bundle input=new Bundle();

    input.putString(InterpreterService.SCRIPT, "SELECT 1+2 AS result, 'foo' AS other_result, 3*8 AS third_result;");
    
    Bundle output=execServiceTest(input);
    
    assert(output.size()==4);
    
    assertEquals(output.getString("result"), "3");
    assertEquals(output.getString("other_result"), "foo");
    assertEquals(output.getString("third_result"), "24");
  }
  
  public void testNoColumnResult() {
    Bundle input=new Bundle();

    input.putString(InterpreterService.SCRIPT, ";");
    
    Bundle output=execServiceTest(input);
    
    assert(output.size()==1);
  }
}