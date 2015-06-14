/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.gcm.cmd;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class GCM {
  @SuppressWarnings("static-access")
  public static void main(String[] args) {
    Option helpOpt=new Option("h", "help", false, "print this message");
    Option apiKeyOpt=
        OptionBuilder.withArgName("key").hasArg().isRequired()
                     .withDescription("GCM API key")
                     .withLongOpt("apiKey").create('a');
    Option deviceOpt=
        OptionBuilder.withArgName("regId").hasArg().isRequired()
                     .withDescription("device to send to")
                     .withLongOpt("device").create('d');
    Option dataOpt=
        OptionBuilder.withArgName("key=value").hasArgs(2)
                     .withDescription("datum to send")
                     .withValueSeparator().withLongOpt("data")
                     .create('D');

    Options options=new Options();

    options.addOption(apiKeyOpt);
    options.addOption(deviceOpt);
    options.addOption(dataOpt);
    options.addOption(helpOpt);

    CommandLineParser parser=new PosixParser();

    try {
      CommandLine line=parser.parse(options, args);

      if (line.hasOption('h') || !line.hasOption('a')
          || !line.hasOption('d')) {
        HelpFormatter formatter=new HelpFormatter();
        formatter.printHelp("gcm", options, true);
      }
      else {
        sendMessage(line.getOptionValue('a'),
                    Arrays.asList(line.getOptionValues('d')),
                    line.getOptionProperties("data"));
      }
    }
    catch (org.apache.commons.cli.MissingOptionException moe) {
      System.err.println("Invalid command syntax");
      HelpFormatter formatter=new HelpFormatter();
      formatter.printHelp("gcm", options, true);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void sendMessage(String apiKey, List<String> devices,
                                  Properties data) throws Exception {
    Sender sender=new Sender(apiKey);
    Message.Builder builder=new Message.Builder();

    for (Object o : data.keySet()) {
      String key=o.toString();

      builder.addData(key, data.getProperty(key));
    }

    MulticastResult mcResult=sender.send(builder.build(), devices, 5);

    for (int i=0; i < mcResult.getTotal(); i++) {
      Result result=mcResult.getResults().get(i);

      if (result.getMessageId() != null) {
        String canonicalRegId=result.getCanonicalRegistrationId();

        if (canonicalRegId != null) {
          System.err.println(String.format("%s canonical ID = %s",
                                           devices.get(i),
                                           canonicalRegId));
        }
        else {
          System.out.println(String.format("%s success", devices.get(i)));
        }
      }
      else {
        String error=result.getErrorCodeName();

        if (Constants.ERROR_NOT_REGISTERED.equals(error)) {
          System.err.println(String.format("%s is unregistered",
                                           devices.get(i)));
        }
        else if (error != null) {
          System.err.println(String.format("%s error = %s",
                                           devices.get(i), error));
        }
      }
    }
  }
}
