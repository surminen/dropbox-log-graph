package com.example.demo;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.DbxWebAuth.BadRequestException;
import com.dropbox.core.DbxWebAuth.BadStateException;
import com.dropbox.core.DbxWebAuth.CsrfException;
import com.dropbox.core.DbxWebAuth.NotApprovedException;
import com.dropbox.core.DbxWebAuth.ProviderException;

@RestController
public class HelloController {

	final String APP_KEY = "pvbpqsiwyei1kxm";
	final String APP_SECRET = "4aqz9kyp7y7s6i1";

	DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
	DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0", Locale.getDefault().toString());

	DbxSessionStore csrfTokenStore;
	
	DbxWebAuth webAuth; 
	
	@RequestMapping("/demo")
	public ModelAndView index(HttpServletRequest request) {

		csrfTokenStore = new DbxStandardSessionStore(request.getSession(true),
				"dropbox-auth-csrf-token");
		webAuth = new DbxWebAuth(config, appInfo, "https://192.168.99.100:8080/demo2", csrfTokenStore);

		String authorizeUrl = webAuth.start();
		// authorizeUrl += "&redirect_uri=https://192.168.99.100:8080/demo2";

		return new ModelAndView("redirect:" + authorizeUrl);
	}

	@RequestMapping("/demo2")
	public String usage(HttpServletRequest request, @RequestParam("code") String code) throws DbxException,
			BadRequestException, BadStateException, CsrfException, NotApprovedException, ProviderException {

//		final DbxSessionStore csrfTokenStore = new DbxStandardSessionStore(request.getSession(true),
//				"dropbox-auth-csrf-token");
//		DbxWebAuth webAuth = new DbxWebAuth(config, appInfo, "https://192.168.99.100:8080/demo3", csrfTokenStore);

		// DbxAuthFinish authFinish = webAuth.finish(code);
		DbxAuthFinish authFinish = webAuth.finish(request.getParameterMap());
		String accessToken = authFinish.accessToken;

		return accessToken;
		
//		DbxClient client = new DbxClient(config, accessToken);
//		String account = client.getAccountInfo().displayName;

//		return account;
	}

	@RequestMapping("/demo3")
	public String usage2(HttpServletRequest request, @RequestParam("code") String code) throws DbxException,
			BadRequestException, BadStateException, CsrfException, NotApprovedException, ProviderException {

		return "here";
	}
}