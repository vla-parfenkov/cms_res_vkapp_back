package cmsrvkapp.config;

import org.json.JSONObject;

import java.util.Iterator;

public class Generator {
    public static void generate(JSONObject config, JSONObject template) {

        Iterator<String>  configIterator = config.keys();
        while (configIterator.hasNext()) {
            String configField = configIterator.next();
            String configFieldValue = template.get(configField).toString();
            if (configFieldValue.equals(BLANK_VALUE)) {
                template.put(configField, config.get(configField));
            } else {
                generate(config.getJSONObject(configField), template.getJSONObject(configField));
            }
        }

    }

    public static final String BLANK_VALUE = "~~!~~";
}
