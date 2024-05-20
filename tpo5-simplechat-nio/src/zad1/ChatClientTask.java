/**
 *
 *  @author Szymkowiak Marek S28781
 *
 */

package zad1;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ChatClientTask extends FutureTask<ChatClient> {

    // constructor
    private ChatClientTask (ChatClient c, List<String> msgs, int wait) {
        super(()->{
            // login to the server
            c.login();
            if (wait != 0) Thread.sleep(wait);

            // send messages
            for (String msg : msgs) {
                c.send(msg);
                if (wait != 0) Thread.sleep(wait);
            }

            // logout from the server
            c.logout();
            if (wait != 0) Thread.sleep(wait);

            return c;
        });
    }

    public static ChatClientTask create (ChatClient c, List<String> msgs, int wait) {
        return new ChatClientTask(c, msgs, wait);
    }


    public ChatClient getClient () {
        try {
            return this.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
