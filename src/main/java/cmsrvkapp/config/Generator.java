package cmsrvkapp.config;

import org.json.JSONObject;

import java.util.Iterator;

public class Generator {
    public static void generate(JSONObject config, JSONObject template) {

        Iterator<String>  configIterator = config.keys();
        while (configIterator.hasNext()) {
            String configField = configIterator.next();
            if (template.get(configField) == "~~!~~") {
                template.put(configField, config.get(configField));
            } else {
                generate(config.getJSONObject(configField), template.getJSONObject(configField));
            }
        }

    }

    public static final String BLANK_VALUE = "~~!~~";
}
