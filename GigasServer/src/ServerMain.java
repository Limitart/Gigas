import org.gigas.core.server.IServer;
import org.gigas.core.server.ProtoBufBasedServer;

/**
 * Main Class
 * 
 * @author hank
 * @since 2014-05-29 01:16:44
 */
public class ServerMain {

	public static void main(String[] args) {
		startServer(new ProtoBufBasedServer());
	}

	private static void startServer(IServer server) {
		Thread mainThread = new Thread(server);
		mainThread.setName("coreserver");
		mainThread.start();
	}
}
