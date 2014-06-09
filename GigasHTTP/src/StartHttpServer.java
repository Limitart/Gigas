import org.gigas.server.HttpServer;

public class StartHttpServer {

	public static void main(String[] args) {
		new Thread(new HttpServer()).start();
	}

}
