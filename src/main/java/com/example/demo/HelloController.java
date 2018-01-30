package com.example.demo;

import java.util.Locale;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;

@RestController
public class HelloController {

	DbxWebAuthNoRedirect webAuth; 
	
    @RequestMapping("/demo")
    public ModelAndView index() {
    	
    	// Get your app key and secret from the Dropbox developers website.
        final String APP_KEY = "pvbpqsiwyei1kxm";
        final String APP_SECRET = "4aqz9kyp7y7s6i1";

        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        DbxRequestConfig config = new DbxRequestConfig(
            "JavaTutorial/1.0", Locale.getDefault().toString());
        webAuth = new DbxWebAuthNoRedirect(config, appInfo);
        
        String authorizeUrl = webAuth.start();
        
        authorizeUrl += "&redirect_uri=https://192.168.99.100:8080/demo2";
        
        return new ModelAndView("redirect:" + authorizeUrl);
    }

    @RequestMapping("/demo2")
    public String usage() {
    	
    	DbxAuthFinish authFinish = webAuth.finish(code);
    	String accessToken = authFinish.accessToken;
    	
    	return "And here we are, all Dropbox authenticated and authorized. :-)";
    }
    
}