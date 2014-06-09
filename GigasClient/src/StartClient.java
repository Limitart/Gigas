import org.gigas.core.client.BaseClient;
import org.gigas.core.client.channelInitializer.enumeration.ChannelInitializerEnum;
import org.gigas.core.exception.ClientException;
import org.gigas.core.exception.MessageException;

public class StartClient {
	public static void main(String[] args) {
		try {
			BaseClient instance = BaseClient.getInstance(ChannelInitializerEnum.GOOGLE_PROTOCOL_BUFFER);
			instance.startClient();
		} catch (ClientException | MessageException e) {
			e.printStackTrace();
		}
	}
}
