package org.apache.commons.exec.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Some stuff from commons-io.
 */
public class IOUtils {

    /**
     * Represents the end-of-file (or stream).
     */
    public static final int EOF = -1;

    public static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ioe) {
            // ignore
        }
    }

    public static int copy(final InputStream input, final OutputStream output, final byte[] buffer)
          throws IOException {
        int count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static int copyAvailable(final InputStream input, final OutputStream output, final byte[] buffer)
          throws IOException {

        int count = 0;
        int available = input.available();

        if (available == 0) {
            return 0;
        }

        final int bufferLength = buffer.length;
        int bytesToRead = bufferLength;

        if (available > 0 && available < bufferLength) {
            bytesToRead = available;
        }

        int n = 0;

        while (bytesToRead > 0 && EOF != (n = input.read(buffer, 0, bytesToRead))) {
            output.write(buffer, 0, n);
            count += n;
            if (available > 0) {
                bytesToRead = Math.min(available - count, bufferLength);
            }
        }

        return (n != EOF ? 1 : EOF);
    }

    public static int copyNonBlocking(final InputStream input, final OutputStream output, final byte[] buffer)
          throws IOException {

        int n;

        while(EOF != (n = copyAvailable(input, output, buffer))) {

            try {
                Thread.sleep(50);
            }
            catch(InterruptedException e) {
                return copyAvailable(input, output, buffer);
            }
        }

        return n;
    }
}
