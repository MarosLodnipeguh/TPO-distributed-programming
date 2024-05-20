package zad1;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Windows1250FileWriter {
    public static void main(String[] args) {
        String filePath = "src/TPO1dir/sub/example.txt";

        try {
            // Create FileOutputStream with Windows-1250 encoding
            FileOutputStream fos = new FileOutputStream(filePath);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "Windows-1250");
            BufferedWriter writer = new BufferedWriter(osw);

            // Write text to the file
            writer.write("Český text v kódování Windows-1250");

            // Close writers to flush and release resources
            writer.close();
            osw.close();
            fos.close();

            System.out.println("File created and text written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
