package org.web.crawler.rest.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web.crawler.stats.CrawlerStatistics;
import org.web.crawler.util.HtmlCrawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

@RestController
public class CrawlerController {

	private final int NUMBER_OF_LEVEL_URL_INSIDE_PAGE = 0;
	
	public void crawl(int level, String url, List<String> visited) {
		if(level<=NUMBER_OF_LEVEL_URL_INSIDE_PAGE) {
			Document doc = request(url, visited);
			if(doc != null) {
				for(Element link: doc.select("a[href]")) {
					String nextLink = link.absUrl("href");
					if(visited.contains(nextLink) == false) {
						crawl(++level, nextLink, visited); 
					}
				}
			}
		}
	}
	
	@GetMapping("/crawl")
	public Map<String, String> search(@RequestParam String url, @RequestParam String keyword){
		Map<String, String> map = new HashMap<>();
		map.put(url, keyword);
		crawl(1, url, new ArrayList<>());
		return map;
	}
	private Document request(String url, List<String> v) {
		try {
			Connection con = Jsoup.connect(url);
			Document doc = con.get();
			if(con.response().statusCode() == 200) {
				System.out.println("Link: "+url);
				System.out.println("Title: "+doc.title());
				v.add(url);
				return doc;
			}
			return null;
		} catch(Exception e) {
			return null;
		}
	}
	
	@GetMapping("/crawl2")
	public Map<String, String> crawl2(@RequestParam String url, @RequestParam String keyword){
		
		File crawlStorage = new File("src/test/resources/crawler4j");
		CrawlConfig crawlConfig = new CrawlConfig();
		crawlConfig.setCrawlStorageFolder(crawlStorage.getAbsolutePath());
		crawlConfig.setIncludeBinaryContentInCrawling(true);
		crawlConfig.setMaxDepthOfCrawling(1);	//set depth
		crawlConfig.setMaxPagesToFetch(100);	//set max pages
		crawlConfig.setMaxOutgoingLinksToFollow(0);	//limit the number of outgoing links followed off each page
		int numCrawlers = 12;

		PageFetcher pageFetcher = new PageFetcher(crawlConfig);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer= new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller;
		try {
			controller = new CrawlController(crawlConfig, pageFetcher, robotstxtServer);
			//controller.addSeed("https://www.baeldung.com/");
			controller.addSeed(url);
			CrawlerStatistics stats = new CrawlerStatistics();
			CrawlController.WebCrawlerFactory<HtmlCrawler> factory = () -> new HtmlCrawler(stats);
			controller.start(factory, numCrawlers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Map<String, String> map = new HashMap<>();
		map.put(url, keyword);
		crawl(1, url, new ArrayList<>());
		return map;
	}
	
}
