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
Sample output (JSON):

````
{
  "langs": [
    {
      "title": "English",
      "url": "//en.wikipedia.org/"
    },
    {
      "title": "Español",
      "url": "//es.wikipedia.org/"
    },
    {
      "title": "日本語",
      "url": "//ja.wikipedia.org/"
    },
    {
      "title": "Deutsch",
      "url": "//de.wikipedia.org/"
    },
    {
      "title": "Русский",
      "url": "//ru.wikipedia.org/"
    },
    {
      "title": "Français",
      "url": "//fr.wikipedia.org/"
    },
    {
      "title": "Italiano",
      "url": "//it.wikipedia.org/"
    },
    {
      "title": "中文",
      "url": "//zh.wikipedia.org/"
    },
    {
      "title": "Português",
      "url": "//pt.wikipedia.org/"
    },
    {
      "title": "Polski",
      "url": "//pl.wikipedia.org/"
    }
  ],
  "otherProjects": [
    {
      "title": "Commons",
      "url": "//commons.wikimedia.org/"
    },
    {
      "title": "Wikivoyage",
      "url": "//www.wikivoyage.org/"
    },
    {
      "title": "Wiktionary",
      "url": "//www.wiktionary.org/"
    },
    {
      "title": "Wikibooks",
      "url": "//www.wikibooks.org/"
    },
    {
      "title": "Wikinews",
      "url": "//www.wikinews.org/"
    },
    {
      "title": "Wikidata",
      "url": "//www.wikidata.org/"
    },
    {
      "title": "Wikiversity",
      "url": "//www.wikiversity.org/"
    },
    {
      "title": "Wikiquote",
      "url": "//www.wikiquote.org/"
    },
    {
      "title": "MediaWiki",
      "url": "//www.mediawiki.org/"
    },
    {
      "title": "Wikisource",
      "url": "//www.wikisource.org/"
    },
    {
      "title": "Wikispecies",
      "url": "//species.wikimedia.org/"
    },
    {
      "title": "Meta-Wiki",
      "url": "//meta.wikimedia.org/"
    }
  ]
}

````


Please check Tests for other samples...