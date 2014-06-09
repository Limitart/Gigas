import org.gigas.client.CEVAClient;
import org.gigas.core.exception.ClientException;
import org.gigas.core.exception.MessageException;

public class StartCEVA {

	public static void main(String[] args) throws MessageException, ClientException {
		Thread thread = new Thread(new CEVAClient());
		thread.start();
	}

}
