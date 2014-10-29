package org.springframework.integration.smsblaster.modules;


import com.google.gson.Gson;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;
import org.springframework.integration.smsblaster.messaging.SMS;
import org.springframework.integration.smsblaster.messaging.SMSFactoryUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GetQuery extends AbstractObjectMessageFactory {
    private static Map<String, String> headers;

    private static Gson gson;

    static {
        Map<String, String> h = new HashMap<String, String>();
        headers = Collections.unmodifiableMap(h);
    }

    private Map<String, CompiledTemplate> templates;

    public GetQuery(List<String> args) {
        templates = SMSFactoryUtils.createMVELTemplates(args);
    }

    @Override
    public Map<String, String> useContentHeaders() {
        return headers;
    }

    @Override
    public Map<String, String> newSMS() {
        Map<String, String> mo = new HashMap<String, String>();
        for (Map.Entry<String, CompiledTemplate> e : templates.entrySet()) {
            String rendered = (String) TemplateRuntime.execute(e.getValue(), SMS.forMvel);
            mo.put(e.getKey(), rendered);
        }
        return mo;
    }



    @Override
    public boolean sendAsForm() {
        return true;
    }
}
