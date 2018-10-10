package cmsrvkapp.generator;

import cmsrvkapp.config.Generator;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;

public class GeneratorResourceTest {

    @Test
    public void testGenerator() throws JSONException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("testTemplate.json");

        String templateText = new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));

        JSONObject template = new JSONObject(templateText);

        is = Thread.currentThread().getContextClassLoader().getResourceAsStream("testConfig.json");

        String configText = new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));

        JSONObject config = new JSONObject(configText);

        is = Thread.currentThread().getContextClassLoader().getResourceAsStream("template.json");

        String originalText = new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));

        JSONObject original = new JSONObject(originalText);

        Generator.generate(config, template);

        assertEquals(original.toString(), template.toString());
    }
}
