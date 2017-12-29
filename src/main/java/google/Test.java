package google;

import java.io.IOException;
import java.nio.file.*;

public class Test {
    public static void main(String[] args) throws NullPointerException {
    }
    public void wait_till_download_finish(String download_folder) throws NullPointerException {
        Path dir = FileSystems.getDefault().getPath(download_folder);
        Boolean still_downloading = false;
        do {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                still_downloading = false;
                for (Path file : stream) {
                    if (file.getFileName().toString().endsWith("crdownload")) {
                        still_downloading = true;
                        System.out.println("I see you. Still downloading?");
                        System.out.println(file.getFileName());
                        break;
                    }
                /*long last_mod = file.toFile().lastModified();
                long minutes_since = (last_mod - System.currentTimeMillis()) / 1000 / 60;
                if (minutes_since < -5) {
                    System.out.println("More than 5 minutes ago");
                }*/
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {

                }
            } catch (IOException | DirectoryIteratorException x) {
                // IOException can never be thrown by the iteration.
                // In this snippet, it can only be thrown by newDirectoryStream.
                System.err.println(x);
            }
        }
        while (still_downloading);

    }
}