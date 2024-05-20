/**
 *
 *  @author Szymkowiak Marek S28781
 *
 */

package zad1;

import java.util.List;
import java.util.concurrent.FutureTask;

public class ClientTask extends FutureTask<String> {

    private ClientTask(Client client, List<String> reqs, boolean showSendRes) {
        super(()->{

            // connect to server
            client.connect();

            // send login request with id
            client.send("login " + client.getId());

            // send requests from list
            for (String req : reqs) {
                String resp = client.send(req);
                if (showSendRes) System.out.println(resp);
            }

            // send get log
            return client.send("bye and log transfer");
        });
    }

    public static ClientTask create(Client c, List<String> reqs, boolean showSendRes){
        return new ClientTask(c, reqs, showSendRes);
    }
}
