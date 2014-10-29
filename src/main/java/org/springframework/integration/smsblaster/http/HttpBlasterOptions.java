package org.springframework.integration.smsblaster.http;

import org.kohsuke.args4j.Option;
import org.springframework.integration.smsblaster.GenericOptions;

import java.io.File;
import java.net.URL;
import java.util.List;

public class HttpBlasterOptions extends GenericOptions {

	

	@Option(name = "-u", aliases = {"--urls"}, usage = "urls to hit", required = true, multiValued = true)
    public List<URL> urls;

	@Option(name = "-m", aliases = {"--module"}, usage = "messaging module: GetQuery, PostForm, PostStream, PostJson", required = true)
    public String module="GetQuery";
	
	@Option(name = "-b", aliases = {"--body"}, usage = "request entity body. Sets PostStream module", required = false)
    public File body;
	

}
