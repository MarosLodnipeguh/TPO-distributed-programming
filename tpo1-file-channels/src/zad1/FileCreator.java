package zad1;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileCreator {

    public static void main (String[] args) throws IOException {

        FileChannel ch = FileChannel.open(Paths.get("src/TPO1dir/sub/f2.txt"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        Charset charset = Charset.forName("windows-1250");

        ch.write(charset.encode("f2 żżżżęęóóó"));

    }
}
