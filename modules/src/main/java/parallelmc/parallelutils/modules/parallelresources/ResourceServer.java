package parallelmc.parallelutils.modules.parallelresources;

import com.sun.net.httpserver.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.ParallelUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.logging.Level;

public class ResourceServer implements Runnable{

	private HashMap<String, File> resourceMap = new HashMap<>();

	private HttpServer server;

	private final int port;
	private final boolean https;

	@Nullable
	private final File keystore;

	@Nullable
	private final String keystore_pass;

	public ResourceServer(int port, boolean https, @Nullable File keystore, @Nullable String keystore_pass) {
		this.port = port;
		this.https = https;
		this.keystore = keystore;
		this.keystore_pass = keystore_pass;
	}

	public ResourceServer() {
		this(8005, false, null, null);
	}

	public void destruct() {
		server.stop(0);

		server = null;
		resourceMap = null;
	}

	public boolean addResource(@NotNull String world, @NotNull File pack) {

		if (resourceMap.containsKey(world)) {
			return false;
		}

		resourceMap.put(world, pack);

		server.createContext("/" + pack.getName(), new ServerHandler(resourceMap));

		return true;
	}

	public void updateResource(@NotNull String world, @NotNull File pack) {
		resourceMap.put(world, pack);

		server.createContext("/" + pack.getName(), new ServerHandler(resourceMap));
	}

	@Override
	public void run() {
		ParallelUtils.log(Level.INFO, "Starting resources server...");
		try {
			if (https) {
				if (keystore == null) {
					throw new KeyStoreException("Keystore not provided!");
				}

				if (keystore_pass == null) {
					throw new KeyStoreException("Keystore pass not set!");
				}

				HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(port), 0);

				SSLContext sslContext = SSLContext.getInstance("TLS");

				char[] password = keystore_pass.toCharArray();
				KeyStore ks = KeyStore.getInstance("JKS");
				FileInputStream fis = new FileInputStream(keystore);
				ks.load(fis, password);

				// Set up the key manager factory
				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				kmf.init(ks, password);

				// Set up the trust manager factory
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				tmf.init(ks);

				// Set up the HTTPS context and parameters
				sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
				httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
					public void configure(HttpsParameters params) {
						try {
							// Initialise the SSL context
							SSLContext c = SSLContext.getDefault();
							SSLEngine engine = c.createSSLEngine();
							params.setNeedClientAuth(false);
							params.setCipherSuites(engine.getEnabledCipherSuites());
							params.setProtocols(engine.getEnabledProtocols());

							// Get the default parameters
							SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
							params.setSSLParameters(defaultSSLParameters);
						} catch (Exception ex) {
							ParallelUtils.log(Level.SEVERE, "Unable to create SSL port");
						}
					}
				});

			} else {
				server = HttpServer.create(new InetSocketAddress(port), 0);
			}
			server.setExecutor(null);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | KeyStoreException |
		         KeyManagementException e) {
			throw new RuntimeException(e);
		}
		ParallelUtils.log(Level.WARNING, "Resources Server Started");
	}

	static class ServerHandler implements HttpHandler {

		private final HashMap<String, File> resourceMap;

		public ServerHandler(@NotNull HashMap<String, File> resourceMap) {
			this.resourceMap = resourceMap;
		}


		@Override
		public void handle(HttpExchange t) throws IOException {
			String path = t.getHttpContext().getPath();

			File resource = resourceMap.get(path);

			if (resource == null) {
				ParallelUtils.log(Level.INFO, "Tried getting pack for " + path + ", but was null. Defaulting to base.");
				resource = resourceMap.get("base");
			}

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
