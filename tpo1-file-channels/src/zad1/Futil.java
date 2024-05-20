package zad1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Futil {
    public static void processDir (String dirName, String resultFileName) {

        Path dirPath = Paths.get(dirName);
        Path resultPath = Paths.get(resultFileName);

        if (!Files.exists(dirPath)) {
            throw new RuntimeException("Directory does not exist: " + dirName);
        }

        try {
            FileChannel resultChannel = FileChannel.open(resultPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            MyFileVisitor myFileVisitor = new MyFileVisitor(resultChannel);
            Files.walkFileTree(dirPath, myFileVisitor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void processFile(Path sourcePath, FileChannel targetChannel) {
        try {

            FileChannel inputChannel = FileChannel.open(sourcePath, StandardOpenOption.READ);
            Charset outputCharset = Charset.forName("UTF-8");
            Charset inputCharset = Charset.forName("windows-1250");

            ByteBuffer buffer = ByteBuffer.allocate(256);

            int bytesRead;
            while ((bytesRead = inputChannel.read(buffer)) != -1) {
                buffer.flip();

                CharBuffer charBuffer = inputCharset.decode(buffer);
                ByteBuffer outputBuffer = outputCharset.encode(charBuffer);

                targetChannel.write(outputBuffer);

                buffer.clear(); // Prepare buffer for next read

                if (bytesRead < buffer.capacity()) {
                    break; // Exit loop if no more bytes to read
                }
            }


            System.out.println("File copied successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class MyFileVisitor implements FileVisitor<Path> {

//        private final Path resultPath;
        private final FileChannel resultChannel;

        public MyFileVisitor(FileChannel resultChannel) {
            this.resultChannel = resultChannel;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            processFile(file, resultChannel);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            System.out.println("Failed to visit file: " + file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }


    //String dirName = "src/TPO1dir"; // for testing purposes

}
