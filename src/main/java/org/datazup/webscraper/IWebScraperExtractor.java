package org.datazup.webscraper;

import java.util.Map;

/**
 * Created by ninel on 5/7/17.
 */
public interface IWebScraperExtractor {
    Map<String, Object> run(String inputData);
}
