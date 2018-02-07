package com.example.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.DbxWebAuth.Request.Builder;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

@Controller
public class GreetingController {

	final String APP_KEY = "jr0tecrty7appm4";
	final String APP_SECRET = "ylhfjprpfvwec5s";

	DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
	DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0");

	DbxSessionStore csrfTokenStore;

	DbxWebAuth webAuth;

	String accessToken;

	String redirectUri = "https://192.168.99.100:8080/demo2";

	@RequestMapping("/graph")
	public String greeting(HttpServletRequest request) {

		// Create the request objects
		csrfTokenStore = new DbxStandardSessionStore(request.getSession(true), "dropbox-auth-csrf-token");
		webAuth = new DbxWebAuth(config, appInfo);
		Builder builder = DbxWebAuth.newRequestBuilder();

		// Get the dropbox authorization URL
		// Builder builder = webAuth.newRequestBuilder();
		builder.withRedirectUri(redirectUri, csrfTokenStore);
		String authorizeUrl = webAuth.authorize(builder.build());

		return "redirect:" + authorizeUrl;
	}

	@RequestMapping("/demo2")
	public String usage(HttpServletRequest request, HttpServletResponse response, @RequestParam("code") String code,
			Model model) throws IOException, ListFolderErrorException, DbxException {

		// Add the username to the model
		model.addAttribute("name", "Bob");

		DbxAuthFinish authFinish;

		if (accessToken == null) {

			try {
				authFinish = webAuth.finishFromRedirect(redirectUri, csrfTokenStore, request.getParameterMap());

			} catch (

			DbxWebAuth.BadRequestException ex) {
				// log("On /dropbox-auth-finish: Bad request: " + ex.getMessage());
				response.sendError(400);
				return ex.getMessage();
			} catch (DbxWebAuth.BadStateException ex) {
				// Send them back to the start of the auth flow.
				response.sendRedirect("http://my-server.com/dropbox-auth-start");
				return ex.getMessage();
			} catch (DbxWebAuth.CsrfException ex) {
				// log("On /dropbox-auth-finish: CSRF mismatch: " + ex.getMessage());
				response.sendError(403, "Forbidden.");
				return ex.getMessage();
			} catch (DbxWebAuth.NotApprovedException ex) {
				// log("User rejected: " + ex.getMessage());
				return ex.getMessage();
			} catch (DbxWebAuth.ProviderException ex) {
				// log("On /dropbox-auth-finish: Auth failed: " + ex.getMessage());
				response.sendError(503, "Error communicating with Dropbox.");
				return ex.getMessage();
			} catch (DbxException ex) {
				// log("On /dropbox-auth-finish: Error getting token: " + ex.getMessage());
				response.sendError(503, "Error communicating with Dropbox.");
				return ex.getMessage();
			}

			accessToken = authFinish.getAccessToken();
		}

		// Get all files in the specified folder
		DbxClientV2 client = new DbxClientV2(config, accessToken);
		ListFolderResult listing = client.files().listFolderBuilder("/Life Log").start();

		// Add the file data to the model
		model.addAttribute("data", listing.getEntries());

		List<String> dates = new ArrayList<>();
		List<String> titles = new ArrayList<>();
		for (Metadata item : listing.getEntries()) {
			if (item.getName().endsWith(".gpx")) {
				// String date = item.getName().split("\\xA7")[0];
				String title = item.getName().split("\\xA7")[2];

				String date = item.getName().substring(0, 8);
				String date2 = date.substring(0, 4) + "-";
				date2 += date.substring(4, 6) + "-";
				date2 += date.substring(6, 8);

				dates.add(date2);
				titles.add(title);
				
				// Also get file contents here
			}
			else
			{
				// Get coodinates out of the gpx file
			}
		}
		model.addAttribute("dates", dates);
		model.addAttribute("titles", titles);

		// return the template to display;
		return "graphView";

	}
}
