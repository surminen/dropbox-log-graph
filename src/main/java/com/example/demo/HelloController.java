package com.example.demo;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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

@RestController
public class HelloController {

	final String APP_KEY = "jr0tecrty7appm4";
	final String APP_SECRET = "ylhfjprpfvwec5s";

	DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
	DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0");

	DbxSessionStore csrfTokenStore;

	DbxWebAuth webAuth;

	String redirectUri = "https://192.168.99.100:8080/demo2";

	@RequestMapping("/demo")
	public ModelAndView index(HttpServletRequest request) {

		csrfTokenStore = new DbxStandardSessionStore(request.getSession(true), "dropbox-auth-csrf-token");
		// webAuth = new DbxWebAuth(config, appInfo,
		// "https://192.168.99.100:8080/demo2", csrfTokenStore);
		webAuth = new DbxWebAuth(config, appInfo);

		Builder builder = webAuth.newRequestBuilder();
		builder.withRedirectUri(redirectUri, csrfTokenStore);

		// String authorizeUrl = webAuth.start();
		String authorizeUrl = webAuth.authorize(builder.build());

		return new ModelAndView("redirect:" + authorizeUrl);
	}

	@RequestMapping("/demo2")
	public String usage(HttpServletRequest request, HttpServletResponse response, @RequestParam("code") String code)
			throws IOException, ListFolderErrorException, DbxException {

		DbxAuthFinish authFinish;

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

		String accessToken = authFinish.getAccessToken();

		DbxClientV2 client = new DbxClientV2(config, accessToken);

		// CreateFolderResult create = client.files().createFolderV2("/mytest");
		// return create.getMetadata().toString();

		StringBuilder sb = new StringBuilder("Result:\n");
		ListFolderResult listing = client.files().listFolderBuilder("/Life Log").start();
		for (Metadata child : listing.getEntries()) {
			sb.append(child.getName());
			sb.append("\n");
		}

		return sb.toString();

	}
}