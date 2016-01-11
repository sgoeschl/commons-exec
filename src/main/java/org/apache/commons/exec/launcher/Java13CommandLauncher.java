/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.commons.exec.launcher;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.environment.EnvironmentUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * A command launcher for JDK/JRE 1.3 (and higher). Uses the built-in
 * Runtime.exec() command. An executable found in the working directory of
 * the newly started process takes precedence over the executable path.
 *
 * @version $Id$
 */
public class Java13CommandLauncher extends CommandLauncherImpl {

    /**
     * Constructor
     */
    public Java13CommandLauncher() {
    }

    /**
     * Launches the given command in a new process, in the given working
     * directory
     *
     * @param cmd the command line to execute as an array of strings
     * @param env the environment to set as an array of strings
     * @param workingDir the working directory where the command should run
     * @throws IOException probably forwarded from Runtime#exec
     */
    @Override
    public Process exec(final CommandLine cmd, final Map<String, String> env,
                        final File workingDir) throws IOException {

        final String[] envVars = EnvironmentUtils.toStrings(env);
        final String[] cmdArray = cmd.toStrings();

        if (!cmd.isFile()) {
            cmdArray[0] = findExecutable(cmd.getExecutable(), workingDir);
        }

        return Runtime.getRuntime().exec(cmdArray, envVars, workingDir);
    }

    private String findExecutable(final String executableName, File workingDir) {

        if (workingDir != null && !new File(executableName).isFile()) {
            final File executableFileInWorkingDir = new File(workingDir, executableName);
            if (executableFileInWorkingDir.exists() && executableFileInWorkingDir.isFile()) {
                return executableFileInWorkingDir.getAbsolutePath();
            }
        }

        return executableName;
    }
}
