package cmsrvkapp.generator;

import cmsrvkapp.config.Generator;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static cmsrvkapp.config.Generator.generate;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class GeneratorTest {

    @Test
    public void testGenerator() {
        JSONObject template = new JSONObject();
        JSONObject config = new JSONObject();

        try {
            template.put("1", Generator.BLANK_VALUE);
            config.put("1", "2");
            generate(config, template);

            assertEquals(template.get("1"), "2");

        } catch (JSONException ex) {
            fail();
        }
    }
}
