/*
 * Copyright 2009-2016 Nathan Freitas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.guardianproject.netcipher.proxy;

import android.content.Context;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.StringTokenizer;

public class TorServiceUtils {

    private final static String TAG = "TorUtils";
    // various console cmds
    public final static String SHELL_CMD_CHMOD = "chmod";
    public final static String SHELL_CMD_KILL = "kill -9";
    public final static String SHELL_CMD_RM = "rm";
    public final static String SHELL_CMD_PS = "ps";
    public final static String SHELL_CMD_PIDOF = "pidof";

    public final static String CHMOD_EXE_VALUE = "700";

    public static boolean isRootPossible()
    {

        StringBuilder log = new StringBuilder();

        try {

            // Check if Superuser.apk exists
            File fileSU = new File("/system/app/Superuser.apk");
            if (fileSU.exists())
                return true;

            fileSU = new File("/system/app/superuser.apk");
            if (fileSU.exists())
                return true;

            fileSU = new File("/system/bin/su");
            if (fileSU.exists())
            {
                String[] cmd = {
                    "su"
                };
                int exitCode = TorServiceUtils.doShellCommand(cmd, log, false, true);
                if (exitCode != 0)
                    return false;
                else
                    return true;
            }

            // Check for 'su' binary
            String[] cmd = {
                "which su"
            };
            int exitCode = TorServiceUtils.doShellCommand(cmd, log, false, true);

            if (exitCode == 0) {
                Log.d(TAG, "root exists, but not sure about permissions");
                return true;

            }

        } catch (IOException e) {
            // this means that there is no root to be had (normally) so we won't
            // log anything
            Log.e(TAG, "Error checking for root access", e);

        } catch (Exception e) {
            Log.e(TAG, "Error checking for root access", e);
            // this means that there is no root to be had (normally)
        }

        Log.e(TAG, "Could not acquire root permissions");

        return false;
    }

    public static int findProcessId(Context context) {
        String dataPath = context.getFilesDir().getParentFile().getParentFile().getAbsolutePath();
        String command = dataPath + "/" + OrbotHelper.ORBOT_PACKAGE_NAME + "/app_bin/tor";
        int procId = -1;

        try {
            procId = findProcessIdWithPidOf(command);

            if (procId == -1)
                procId = findProcessIdWithPS(command);
        } catch (Exception e) {
            try {
                procId = findProcessIdWithPS(command);
            } catch (Exception e2) {
                Log.e(TAG, "Unable to get proc id for command: " + URLEncoder.encode(command), e2);
            }
        }

        return procId;
    }

    // use 'pidof' command
    public static int findProcessIdWithPidOf(String command) throws Exception
    {

        int procId = -1;

        Runtime r = Runtime.getRuntime();

        Process procPs = null;

        String baseName = new File(command).getName();
        // fix contributed my mikos on 2010.12.10
        procPs = r.exec(new String[] {
                SHELL_CMD_PIDOF, baseName
        });
        // procPs = r.exec(SHELL_CMD_PIDOF);

        BufferedReader reader = new BufferedReader(new InputStreamReader(procPs.getInputStream()));
        String line = null;

        while ((line = reader.readLine()) != null)
        {

            try
            {
                // this line should just be the process id
                procId = Integer.parseInt(line.trim());
                break;
            } catch (NumberFormatException e)
            {
                Log.e("TorServiceUtils", "unable to parse process pid: " + line, e);
            }
        }

        return procId;

    }

    // use 'ps' command
    public static int findProcessIdWithPS(String command) throws Exception
    {

        int procId = -1;

        Runtime r = Runtime.getRuntime();

        Process procPs = null;

        procPs = r.exec(SHELL_CMD_PS);

        BufferedReader reader = new BufferedReader(new InputStreamReader(procPs.getInputStream()));
        String line = null;

        while ((line = reader.readLine()) != null)
        {
            if (line.indexOf(' ' + command) != -1)
            {

                StringTokenizer st = new StringTokenizer(line, " ");
                st.nextToken(); // proc owner

                procId = Integer.parseInt(st.nextToken().trim());

                break;
            }
        }

        return procId;

    }

    public static int doShellCommand(String[] cmds, StringBuilder log, boolean runAsRoot,
            boolean waitFor) throws Exception
    {

        Process proc = null;
        int exitCode = -1;

        if (runAsRoot)
            proc = Runtime.getRuntime().exec("su");
        else
            proc = Runtime.getRuntime().exec("sh");

        OutputStreamWriter out = new OutputStreamWriter(proc.getOutputStream());

        for (int i = 0; i < cmds.length; i++)
        {
            // TorService.logMessage("executing shell cmd: " + cmds[i] +
            // "; runAsRoot=" + runAsRoot + ";waitFor=" + waitFor);

            out.write(cmds[i]);
            out.write("\n");
        }

        out.flush();
        out.write("exit\n");
        out.flush();

        if (waitFor)
        {

            final char buf[] = new char[10];

            // Consume the "stdout"
            InputStreamReader reader = new InputStreamReader(proc.getInputStream());
            int read = 0;
            while ((read = reader.read(buf)) != -1) {
                if (log != null)
                    log.append(buf, 0, read);
            }

            // Consume the "stderr"
            reader = new InputStreamReader(proc.getErrorStream());
            read = 0;
            while ((read = reader.read(buf)) != -1) {
                if (log != null)
                    log.append(buf, 0, read);
            }

            exitCode = proc.waitFor();

        }

        return exitCode;

    }
}
