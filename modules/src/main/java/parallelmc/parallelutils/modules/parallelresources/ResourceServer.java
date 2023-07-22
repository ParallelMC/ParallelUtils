package parallelmc.parallelutils.modules.parallelresources;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import parallelmc.parallelutils.ParallelUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.Level;

public class ResourceServer implements Runnable{

	private HashMap<String, File> resourceMap = new HashMap<>();

	private HttpServer server;

	private final int port;

	public ResourceServer(int port) {
		this.port = port;
	}

	public ResourceServer() {
		this(8005);
	}

	public void destruct() {
		server.stop(0);

		server = null;
		resourceMap = null;
	}

	public boolean addResource(String world, File pack) {

		if (resourceMap.containsKey(world)) {
			return false;
		}

		resourceMap.put(world, pack);

		server.createContext("/" + pack.getName(), new ServerHandler(resourceMap));

		return true;
	}

	public void updateResource(String world, File pack) {
		resourceMap.put(world, pack);

		server.createContext("/" + pack.getName(), new ServerHandler(resourceMap));
	}

	@Override
	public void run() {
		ParallelUtils.log(Level.INFO, "Starting resources server...");
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			server.setExecutor(null);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ParallelUtils.log(Level.WARNING, "Resources Server Started");
	}

	static class ServerHandler implements HttpHandler {

		private final HashMap<String, File> resourceMap;

		public ServerHandler(HashMap<String, File> resourceMap) {
			this.resourceMap = resourceMap;
		}


		@Override
		public void handle(HttpExchange t) throws IOException {
			String path = t.getHttpContext().getPath();

			File resource = resourceMap.get(path);
			Path pathObj = resource.toPath();

			long resourceLen = Files.size(pathObj);

			t.sendResponseHeaders(200, resourceLen);

			try (OutputStream os = t.getResponseBody()) {
				Files.copy(pathObj, os);
				os.flush();
			}
		}
	}
}
