/**
 *
 *  @author Szymkowiak Marek S28781
 *
 */

package zad1;


import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClientTask_BAD implements Runnable {

    private Client c;
    private List<String> reqs;
    private boolean showSendRes;
    private String clog;
    public ClientTask_BAD (Client c, List<String> reqs, boolean showSendRes) {
        this.c = c;
        this.reqs = reqs;
        this.showSendRes = showSendRes;
        this.clog = "";
    }

    public static ClientTask_BAD create(Client c, List<String> reqs, boolean showSendRes) {
        return new ClientTask_BAD(c, reqs, showSendRes);
    }

    @Override
    public void run() {

//        System.out.println("ClientTask execute " + c.getId());

        // connect to server
        c.connect();

        // send login request with id
        String loginResponse = c.send("login " + c.getId());
        if (showSendRes) System.out.println(loginResponse);

        // send requests from list
        for (String req : reqs) {
            String requestResponse = c.send(req);
            if (showSendRes) System.out.println(requestResponse);
        }

        // send get log
        clog = c.send("bye and log transfer");
        if (showSendRes) System.out.println(clog);

    }


    public String get () throws InterruptedException, ExecutionException {
        return clog;
    }
}
