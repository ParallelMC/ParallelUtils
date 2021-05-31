package parallelmc.parallelutils.versionchecker;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.Version;

import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateChecker {

	private final OkHttpClient client;

	private static final Pattern TAG_PATTERN = Pattern.compile("(\"tag_name\":\\s?\"v\\d+\\.\\d+\\.\\d+\")");

	private static final String GITHUB_URL = "https://api.github.com/repos/ParallelMC/ParallelUtils/releases/latest";

	private final String token;

	public UpdateChecker(String token) {
		client = new OkHttpClient();
		this.token = token;
	}

	public Version getLatestVersion() {
		Request request = new Request.Builder()
				.url(GITHUB_URL)
				.addHeader("Authorization", "token " + token)
				.build();

		try (Response response = client.newCall(request).execute()) {
			ResponseBody body = response.body();
			if (body == null) {
				Parallelutils.log(Level.WARNING, "Could not get latest version. Body is null");
				return null;
			}

			String bodyString = body.string();

			Matcher matcher = TAG_PATTERN.matcher(bodyString);

			if (matcher.find()) {
				String sub = matcher.group(1).substring(13);

				int index = sub.indexOf("\"");

				String ver = sub.substring(0, index);

				Parallelutils.log(Level.INFO, ver);

				String[] split = ver.split("\\.");

				switch (split.length) {
					case 1:
						return new Version(Integer.parseInt(split[0]));
					case 2:
						return new Version(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
					case 3:
						String[] hotfix_flavor = split[2].split("-");
						if (hotfix_flavor.length == 1) {
							return new Version(Integer.parseInt(split[0]), Integer.parseInt(split[1]),
									Integer.parseInt(hotfix_flavor[0]));
						}
						return new Version(Integer.parseInt(split[0]), Integer.parseInt(split[1]),
								Integer.parseInt(hotfix_flavor[0]), hotfix_flavor[1]);
					default:
						Parallelutils.log(Level.WARNING, "Unable to parse version. Something broke!");
						return null;
				}
			} else {
				Parallelutils.log(Level.WARNING, "No match!!!!!");
				return null;
			}

		} catch (IOException e) {
			Parallelutils.log(Level.WARNING, "Could not get latest version");
			e.printStackTrace();
			return null;
		}
	}
}
