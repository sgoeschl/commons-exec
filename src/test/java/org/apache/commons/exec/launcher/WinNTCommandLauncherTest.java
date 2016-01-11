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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.OS;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WinNTCommandLauncherTest {

    private static final String ANY_EXECUTABLE_NAME = "test.bat";
    private static final File ANY_WORKING_DIR = new File("/");

    private CommandLauncherStub commandLauncher = new CommandLauncherStub();
    private WinNTCommandLauncher winNTCommandLauncher = new WinNTCommandLauncher(commandLauncher);

    @Test
    public void testWinNTCommandLineWithoutWorkingDirectory() throws Exception {
        final CommandLine cmd = new CommandLine("test.bat");

        winNTCommandLauncher.exec(cmd, null, null);

        assertEquals(1, commandLauncher.cmd.toStrings().length);
        assertEquals(ANY_EXECUTABLE_NAME, commandLauncher.cmd.getExecutable());
    }

    @Test
    public void testWinNTCommandLineWithWorkingDirectory() throws Exception {
        final CommandLine cmd = new CommandLine("test.bat");

        winNTCommandLauncher.exec(cmd, null, ANY_WORKING_DIR);

        assertEquals(7, commandLauncher.cmd.toStrings().length);
        assertEquals("cmd", commandLauncher.cmd.getExecutable());
        assertEquals("/c", commandLauncher.cmd.getArguments()[0]);
        assertEquals("cd", commandLauncher.cmd.getArguments()[1]);
        assertEquals("/d", commandLauncher.cmd.getArguments()[2]);
        assertEquals(ANY_WORKING_DIR.getAbsolutePath(), commandLauncher.cmd.getArguments()[3]);
        assertEquals("&&", commandLauncher.cmd.getArguments()[4]);
        assertEquals(ANY_EXECUTABLE_NAME, commandLauncher.cmd.getArguments()[5]);
    }

    @Test
    public void testExecuteInternalCommandWithWorkingDirectory() throws Exception {
        if (OS.isFamilyWindows()) {
            final DefaultExecutor defaultExecutor = new DefaultExecutor();
            final CommandLine cmd = new CommandLine("dir");
            defaultExecutor.setLauncher(new WinNTCommandLauncher(new Java13CommandLauncher()));

            int exitValue = defaultExecutor.execute(cmd);

            assertEquals(0, exitValue);
        }
    }

    @Test
    public void testExecuteBatchFileWithoutWorkingDirectory() throws Exception {
        if (OS.isFamilyWindows()) {
            final DefaultExecutor defaultExecutor = new DefaultExecutor();
            final CommandLine cmd = new CommandLine("./src/test/scripts/test.bat");
            defaultExecutor.setLauncher(new WinNTCommandLauncher(new Java13CommandLauncher()));

            int exitValue = defaultExecutor.execute(cmd);

            assertEquals(0, exitValue);
        }
    }

    @Test
    public void testExecuteBatchFileWithWorkingDirectory() throws Exception {
        if (OS.isFamilyWindows()) {
            final DefaultExecutor defaultExecutor = new DefaultExecutor();
            final CommandLine cmd = new CommandLine("test.bat");
            defaultExecutor.setWorkingDirectory(new File("./src/test/scripts"));
            defaultExecutor.setLauncher(new WinNTCommandLauncher(new Java13CommandLauncher()));

            int exitValue = defaultExecutor.execute(cmd);

            assertEquals(0, exitValue);
        }
    }

    @Test
    public void testExecuteNonExistingBatchFile() throws Exception {
        if (OS.isFamilyWindows()) {
            final DefaultExecutor defaultExecutor = new DefaultExecutor();
            final CommandLine cmd = new CommandLine("./src/test/scripts/soes_not_exist.bat");
            defaultExecutor.setLauncher(new WinNTCommandLauncher(new Java13CommandLauncher()));

            int exitValue = defaultExecutor.execute(cmd);

            assertTrue(exitValue != 0);
        }
    }


    private static final class CommandLauncherStub implements CommandLauncher {

        CommandLine cmd;
        File workingDir;

        public Process exec(CommandLine cmd, Map<String, String> env) throws IOException {
            this.cmd = cmd;
            return null;
        }

        public Process exec(CommandLine cmd, Map<String, String> env, File workingDir) throws IOException {
            this.cmd = cmd;
            this.workingDir = workingDir;
            return null;
        }

        public boolean isFailure(int exitValue) {
            return false;
        }
    }
}
