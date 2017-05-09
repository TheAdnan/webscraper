# webscraper

Simple CSS selector JSON definition to extract data from HTML sites.
Underlying it uses Jsoup.

Sample

````

// get html using HTTP Client or load from String
String html = getHtml(...)

// get definition from Resource path or construct the Map<String,Object>();
Map<String,Object> def = getDefinition(path);

IWebScraperExtractor webExtractor = new WebScraperJsoupExtractorImpl();

// this will return extracted values as Map
Map<String,Object> m = webExtractor.run(html);

````

Sample definition extractor for landing page Wikipedia https://www.wikipedia.org/

````
{
  "selectors": [
    {
      "key":"langs",
      "type":"container",
      "css":".central-featured .central-featured-lang",
      "items":[
        {
          "key":"title",
          "type":"item",
          "css":".link-box strong",
          "attr":"text"
        },
        {
          "key":"url",
          "type":"item",
          "css":"a",
          "attr":"href"
        }
      ]
    },
    {
      "key":"otherProjects",
      "type":"container",
      "css":".other-projects .other-project",
      "items":[
        {
          "key":"title",
          "type":"item",
          "css":".other-project-title",
          "attr":"text"
        },
        {
          "key":"url",
          "type":"item",
          "css":"a",
          "attr":"href"
        }
      ]
    }
  ]
}

````

Please check Tests for other samples...