package com.petfinder.rest.domain;

import java.util.List;

import com.petfinder.domain.Advertisement;

public class SearchResults 
{
	private List<Advertisement> advertisements;
	private int allResultsCount;
	
	public SearchResults(List<Advertisement> advertisements, int allResultsCount)
	{
		this.advertisements = advertisements;
		this.allResultsCount = allResultsCount;
	}

	public List<Advertisement> getAdvertisements() {
		return advertisements;
	}

	public void setAdvertisements(List<Advertisement> advertisements) {
		this.advertisements = advertisements;
	}

	public int getAllResultsCount() {
		return allResultsCount;
	}

	public void setAllResults(int allResultsCount) {
		this.allResultsCount = allResultsCount;
	}
}
