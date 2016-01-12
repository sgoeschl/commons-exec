/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.exec.issues;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeoutException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.OS;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @see <a href="https://issues.apache.org/jira/browse/EXEC-62">EXEC-62</a>
 */
public class Exec62Test
{
    private File outputFile;

    @Before
    public void setUp() throws Exception {
        outputFile = File.createTempFile("foo", ".log");
    }

    @After
    public void tearDown() throws Exception {
        outputFile.delete();
    }

    @Test (expected = TimeoutException.class, timeout = 10000)
    public void testMe() throws Exception {
        if(OS.isFamilyUnix()) {
            execute ("exec-62");
        }
    }

    private void execute (String scriptName) throws Exception {
        ExecuteWatchdog watchdog = new ExecuteWatchdog(4000);
        CommandLine commandLine = new CommandLine("/bin/sh");
        File testScript = TestUtil.resolveScriptForOS("./src/test/scripts/issues/" + scriptName);

        commandLine.addArgument(testScript.getAbsolutePath());

        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValues(null); // ignore exit values
        executor.setWatchdog(watchdog);

        FileOutputStream fos = new FileOutputStream(outputFile);
        PumpStreamHandler streamHandler = new PumpStreamHandler(fos);
        executor.setStreamHandler(streamHandler);
        executor.execute(commandLine);

        if (watchdog.killedProcess()) {
            System.out.println(outputFile.length());
            throw new TimeoutException(String.format("Transcode process was killed on timeout %1$s ms, command line %2$s", 4000, commandLine.toString()));
        }
    }
}


