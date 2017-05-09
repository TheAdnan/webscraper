package org.datazup.webscraper;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;


public class WebScrapperJsoupExtractorTest {

    public static String getJsonFromObject(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static Map<String,Object> getMapFromJson(String jsonString){
        ObjectMapper mapper = new ObjectMapper();
        // mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        // false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.findAndRegisterModules();
        try {
            Object o = mapper.readValue(jsonString, Map.class);
            return (Map<String, Object>) o;
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String loadDefinitionFromResources(String path){
        InputStream inputStream = WebScrapperJsoupExtractorTest.class.getClassLoader().getResourceAsStream(path);
        String result = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));

        return result;
    }

    private Map<String,Object> getDefinition(String path){

        String jsonDef = loadDefinitionFromResources(path);
        Map<String,Object> def = getMapFromJson(jsonDef);

        return def;
    }

    private String getHtmlFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String html = doc.outerHtml();

        return html;
    }

    Map<String,Object> runTest(String path, String url) throws IOException {
        Map<String,Object> def = getDefinition(path);

        WebScraperJsoupExtractorImpl impl = new WebScraperJsoupExtractorImpl(def);

        String html = getHtmlFromUrl(url);

        Map<String,Object> m = impl.run(html);

        return m;
    }

    @Test
    public void mainContainerExtractionTest() throws IOException {
        String path = "autopazar_def.json";
        String url = "http://autopazar.ba/community/search/?tag=reno+megane";

        Map m = runTest(path, url);
        Assert.assertNotNull(m);

        String jsonM = getJsonFromObject(m);

        System.out.println(jsonM);
    }

    @Test
    public void singlePageExtractionTest() throws IOException {
        String url = "http://autopazar.ba/vozila/29983/reno-15dci-svi-tipovi-pumpa-delphi-170e-garancija";
        String path = "autopazar_single_def.json";

        Map m = runTest(path, url);

        // {"title":"Reno 1.5dci svi tipovi Pumpa delphi 170e garancija","properties":{"details":{"title":"Reno 1.5dci svi tipovi Pumpa delphi 170e garancija","price":"170 KM"}}}

        Assert.assertTrue(m.size()==2);
        Assert.assertTrue(m.containsKey("title"));
        Assert.assertTrue(m.containsKey("properties"));
        Map m1 = (Map) m.get("properties");
        Assert.assertNotNull(m1);
        Assert.assertTrue(m1.containsKey("details"));
        Map details = (Map) m1.get("details");
        Assert.assertNotNull(details);
        Assert.assertTrue(details.containsKey("title"));
        Assert.assertTrue(details.containsKey("price"));

        String jsonM = getJsonFromObject(m);

        System.out.println(jsonM);
    }

    @Test
    public void multipleContainerSelectoreTest() throws IOException {

        String url = "https://www.wikipedia.org/";
        String path = "wikipedia_main_def.json";

        Map m = runTest(path, url);

        String jsonM = getJsonFromObject(m);

        System.out.println(jsonM);
    }

    @Test
    public void multipleContainerObjectSelectorTest() throws IOException {
        String url = "https://en.wikipedia.org/wiki/Fred_Thompson";
        String path = "wikipedia_single_def.json";

        Map m = runTest(path, url);

        String jsonM = getJsonFromObject(m);

        System.out.println(jsonM);

    }

}
