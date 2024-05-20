/**
 *
 *  @author Szymkowiak Marek S28781
 *
 */

package zad1;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;


public class Tools {
    public static Options createOptionsFromYaml (String fileName) throws Exception {
        Yaml yaml = new Yaml();

        try {
            FileInputStream fis = new FileInputStream(fileName);

            Map<String, Object> data = yaml.load(fis);

            String host = (String) data.get("host");
            int port = (int) data.get("port");
            boolean concurMode = (boolean) data.get("concurMode");
            boolean showSendRes = (boolean) data.get("showSendRes");
            Map<String, List<String>> clientsMap = (Map<String, List<String>>) data.get("clientsMap");

            // create Options object
            Options options = new Options(host, port, concurMode, showSendRes, clientsMap);
//            System.out.println("created object from yaml file:");

            return options;
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("Error while creating Options object from yaml file");
        return null;
    }
}
