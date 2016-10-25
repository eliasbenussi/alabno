package alabno.wserver;

public class Main {

	public static void main(String[] args) {
		
		System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
		
		PropertiesLoader properties_loader = new PropertiesLoader();
		
		int port = properties_loader.getPort();
		
		if (port == -1)
		{
			System.out.println("Error when reading properties");
			System.exit(1);
		}
		
		System.out.println("Starting WebSocket server on port " + port);
		
		AutoMarkerWSServer the_server = new AutoMarkerWSServer(port);
		the_server.start();
		
	}

}
