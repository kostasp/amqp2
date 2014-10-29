package org.springframework.integration.smsblaster.modules;


import com.google.common.io.Resources;
import com.google.gson.Gson;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;
import org.springframework.integration.smsblaster.messaging.SMS;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class Sybase  extends AbstractObjectMessageFactory {
	private static Map<String, String> headers;
    private static Gson gson;

    static {
		Map<String, String> h = new HashMap<String, String>();
		h.put("Content-Type", "application/x-www-form-urlencoded");
		headers = Collections.unmodifiableMap(h);
	}
	private CompiledTemplate template;
	
	public Sybase(List<String> args) {
		URL u = Resources.getResource("SybaseMO.mv");
		String content;
		try {
			content = Resources.toString(u, Charset.forName("UTF-8"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		template = TemplateCompiler.compileTemplate(content);
	}
	
	@Override
	public Map<String, String> useContentHeaders() {
		return headers;
	}
	
	@Override
	public Map<String, String> newSMS() {
		String rendered = (String)TemplateRuntime.execute(template, SMS.forMvel);
		Map<String, String> mo = new HashMap<String, String>();
		mo.put("XmlMsg", rendered);
		return mo;
	}

    @Override
	public boolean sendAsForm() {
		return true;
	}
}
