package org.datazup.webscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WebScraperJsoupExtractorImpl implements IWebScraperExtractor {

    private Map<String, Object> definition;

    public WebScraperJsoupExtractorImpl(Map<String, Object> definition) {
        this.definition = definition;
    }

    @Override
    public Map<String, Object> run(String inputData) {
        Map<String, Object> result = new HashMap<>();
        Document doc = Jsoup.parse(inputData);
        if (definition.containsKey("selectors")) {
            List<Map<String, Object>> selectors = (List<Map<String, Object>>) definition.get("selectors");

            for (Map<String, Object> selector : selectors) {

                Map<String, Object> r = process("selectors", selector, doc.getAllElements());
                result.putAll(r);
            }
        }
        return result;
    }

    private Map<String, Object> process(String parentType, Map<String, Object> selector, Elements elements) {
        Map<String, Object> result = new HashMap<>();

        String type = (String) selector.get("type");

        switch (type) {
            case "container":

                String key = (String) selector.get("key");
                List<Map<String, Object>> resList = handleContainer(selector, elements);

                result.put(key, resList);
                break;
            case "item":
                key = (String) selector.get("key");

                if (null != elements && elements.size() > 0) {
                    if (parentType.equalsIgnoreCase("object") || parentType.equalsIgnoreCase("container")) {
                        for (Element elem : elements) {
                            Map<String, Object> resMap = handleItem(selector, elem);
                            result.putAll(resMap);
                        }
                    } else {
                        Object o = null;
                        List l = new ArrayList();
                        for (Element elem : elements) {
                            Map<String, Object> resMap = handleItem(selector, elem);
                            l.add(resMap);
                        }
                        o = l;
                        result.put(key, o);
                    }
                }else{
                    result.put(key, null);
                }

                break;
            case "object":
                Map<String, Object> resMap = handleObject(selector, elements);
                result.putAll(resMap);
                break;
            default:
                throw new NotImplementedException();
        }


        return result;
    }

    /**
     * object can have CSS and Attr but it is not required
     * when CSS is not present we'll just use this as main property in the object
     * sample: { title: { } }
     * if there is css we'll use it to resolve other properties from
     * if there is CSS and Attr then we'll use this Attr as value and continue with other under "properties"
     * sample: { title:"value of title", properties: { ... other properties ...  }}
     *
     * @param selector
     * @param elements
     * @return
     */
    private Map<String, Object> handleObject(Map<String, Object> selector, Elements elements) {
        Map<String, Object> map = new HashMap<>();

        String key = (String) selector.get("key");
        String css = (String) selector.get("css");
        String attribute = null;

        Elements propElements = elements;
        if (null != css) {
            propElements = elements.select(css);
        }
        if (selector.containsKey("attr")) {
            attribute = (String) selector.get("attr");
        }
        if (null != attribute) {
            String val = resolveAttribute(attribute, propElements);// elements.attr(attribute);
            map.put(key, val);
        }

        List<Map<String, Object>> properties = (List<Map<String, Object>>) selector.get("properties");
        if (null != properties) {
            Elements elems = elements;
            if (null == attribute) {
                elems = propElements;
            }
            Map<String, Object> props = handleObjectProperties(properties, elems);
            if (null != css) {
                if (null==attribute){
                    map.put(key, props);
                }else {
                    map.put("properties", props);
                }
            } else {
                map.put(key, props);
            }
        }


        return map;
    }

    private String resolveAttribute(String attribute, Elements elements) {
        String value = null;
        switch (attribute) {
            case "text":
                value = elements.text();
                break;
            case "src":
                if (elements.hasAttr(attribute)) {
                    value = elements.attr(attribute);
                }

                break;
            case "html":
                value = elements.html();
                break;
            case "outerHtml":
                value = elements.outerHtml();
                break;
            default:
                // get by named attribute
                //if (elements.hasAttr(attribute)) {
                if (elements.size()==1){
                    value = elements.get(0).attr(attribute);
                }else {
                    value = elements.attr(attribute);
                }
                //}
                break;
        }

        return value;
    }

    private Map<String, Object> handleObjectProperties(List<Map<String, Object>> properties, Elements propElements) {
        Map<String, Object> map = new HashMap<>();

        for (Map<String, Object> prop : properties) {
            Map<String, Object> res = process("object", prop, propElements);
            map.putAll(res);
        }

        return map;
    }

    private Map<String, Object> handleItem(Map<String, Object> selector, Element inputData) {
        Map<String, Object> res = new HashMap<>();

        String key = (String) selector.get("key");
        String css = (String) selector.get("css");
        String attribute = "text";
        if (selector.containsKey("attr")) {
            attribute = (String) selector.get("attr");
        }
        Elements elements = inputData.select(css);

        String value = resolveAttribute(attribute, elements);

        res.put(key, value);

        return res;
    }


    private List<Map<String, Object>> handleContainer(Map<String, Object> selector, Elements inputData) {
        List<Map<String, Object>> res = new ArrayList<>();

        String css = (String) selector.get("css");
        Elements elements = inputData.select(css);

        if (selector.containsKey("items")) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) selector.get("items");

            for (Element element : elements) {
                Elements els = new Elements(element);

                Map<String, Object> objectMap = new HashMap<>();
                for (Map<String, Object> item : items) {
                    Map<String, Object> pr = process("container", item, els); //handleItem(item, element);
                    objectMap.putAll(pr);
                }
                res.add(objectMap);
            }

        } else {
            //TODO: should we return Object and be able to return HTML (e.g. toString()) in case where Items hot defined???
        }


        return res;
    }

}
