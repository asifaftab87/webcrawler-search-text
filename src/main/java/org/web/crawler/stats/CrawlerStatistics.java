package org.web.crawler.stats;

public class CrawlerStatistics {
	
    private int processedPageCount = 0;
    private int totalLinksCount = 0;
    
    public void incrementProcessedPageCount() {
        processedPageCount++;
    }
    
    public void incrementTotalLinksCount(int linksCount) {
        totalLinksCount += linksCount;
    }

	public int getProcessedPageCount() {
		return processedPageCount;
	}

	public void setProcessedPageCount(int processedPageCount) {
		this.processedPageCount = processedPageCount;
	}

	public int getTotalLinksCount() {
		return totalLinksCount;
	}

	public void setTotalLinksCount(int totalLinksCount) {
		this.totalLinksCount = totalLinksCount;
	}
    
}