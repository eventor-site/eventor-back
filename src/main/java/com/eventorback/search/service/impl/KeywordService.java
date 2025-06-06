package com.eventorback.search.service.impl;

import java.util.List;

public interface KeywordService {

	List<String> getKeywords();

	void deleteKeyword(String keyword);

	void cleanUpOldTopKeywords();

	void updateTopKeywords();
}
