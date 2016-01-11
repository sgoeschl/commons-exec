package org.apache.commons.exec.launcher;

import org.apache.commons.exec.CommandLine;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class WinNTCommandLauncherTest {

    private static final String ANY_EXECUTABLE_NAME = "test.bat";
    private static final File ANY_WORKING_DIR = new File("/");
    private CommandLauncherStub commandLauncherStub = new CommandLauncherStub();
    private WinNTCommandLauncher winNTCommandLauncher = new WinNTCommandLauncher(commandLauncherStub);

    @Test
    public void testWinNTCommandLineWithoutWorkingDirectory() throws Exception {
        final CommandLine cmd = new CommandLine("test.bat");

        winNTCommandLauncher.exec(cmd, null, null);

        assertEquals(1, commandLauncherStub.cmd.toStrings().length);
        assertEquals(ANY_EXECUTABLE_NAME, commandLauncherStub.cmd.getExecutable());
    }

    @Test
    public void testWinNTCommandLineWithWorkingDirectory() throws Exception {
        final CommandLine cmd = new CommandLine("test.bat");

        winNTCommandLauncher.exec(cmd, null, ANY_WORKING_DIR);

        assertEquals(7, commandLauncherStub.cmd.toStrings().length);
        assertEquals("cmd", commandLauncherStub.cmd.getExecutable());
        assertEquals("/c", commandLauncherStub.cmd.getArguments()[0]);
        assertEquals("cd", commandLauncherStub.cmd.getArguments()[1]);
        assertEquals("/d", commandLauncherStub.cmd.getArguments()[2]);
        assertEquals(ANY_WORKING_DIR.getAbsolutePath(), commandLauncherStub.cmd.getArguments()[3]);
        assertEquals("&&", commandLauncherStub.cmd.getArguments()[4]);
        assertEquals(ANY_EXECUTABLE_NAME, commandLauncherStub.cmd.getArguments()[5]);
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
