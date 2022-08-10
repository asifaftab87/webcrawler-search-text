package org.web.crawler.util;

import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web.crawler.stats.CrawlerStatistics;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class HtmlCrawler extends WebCrawler {
	
	Logger log = LoggerFactory.getLogger(HtmlCrawler.class);
	
	private final static Pattern EXCLUSIONS = Pattern.compile(".*(\\.(css|js|xml|gif|jpg|png|mp3|mp4|zip|gz|pdf))$");
	private CrawlerStatistics stats;

	public HtmlCrawler(CrawlerStatistics stats) {
		this.stats = stats;
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String urlString = url.getURL().toLowerCase();
		if (EXCLUSIONS.matcher(urlString).matches()) {
			return false;
		}

		if (urlString.startsWith("https://en.wikipedia.org/")) {
			return true;
		}

		return false;
	}

	@Override
	public void visit(Page page) {
		
		String url = page.getWebURL().getURL();
	    stats.incrementProcessedPageCount();

	    if (page.getParseData() instanceof HtmlParseData) {
	        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
	        String title = htmlParseData.getTitle();
	        String text = htmlParseData.getText();
	        String html = htmlParseData.getHtml();
	        Set<WebURL> links = htmlParseData.getOutgoingUrls();
	        stats.incrementTotalLinksCount(links.size());
	        log.info("title: "+title);
	        //System.out.println("html: "+html);
	        log.info("text: "+text);
	        // do something with collected data
	    }
	}

}
