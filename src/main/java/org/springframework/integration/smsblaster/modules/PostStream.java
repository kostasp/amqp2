package org.springframework.integration.smsblaster.modules;

import com.google.gson.Gson;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;
import org.springframework.integration.smsblaster.messaging.SMS;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostStream  extends AbstractObjectMessageFactory {
	private static Map<String, String> headers;
    private static Gson gson;

    static {
		Map<String, String> h = new HashMap<String, String>();
		h.put("Content-Type", "application/octet-stream");
		headers = Collections.unmodifiableMap(h);
	}
	private CompiledTemplate template;
	
	public PostStream(List<String> args) {
		template = TemplateCompiler.compileTemplate(args.get(0));
	}

	@Override
	public Map<String, String> useContentHeaders() {
		return headers;
	}
	
	@Override
	public Map<String, String> newSMS() {
		String rendered = (String)TemplateRuntime.execute(template, SMS.forMvel);
		Map<String, String> mo = new HashMap<String, String>();
		mo.put("Body", rendered);
		return mo;
	}


	@Override
	public boolean sendAsForm() {
		return false;
	}
}
