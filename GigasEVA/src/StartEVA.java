import org.gigas.core.server.exception.MessageException;
import org.gigas.core.server.exception.ServerException;
import org.gigas.server.EVAServer;

public class StartEVA {

	public static void main(String[] args) throws MessageException, ServerException {
		Thread thread = new Thread(new EVAServer());
		thread.start();
	}

}
