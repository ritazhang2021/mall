package com.rita.mall.search.service;


import com.rita.mall.search.vo.SearchParam;
import com.rita.mall.search.vo.SearchResult;

public interface SearchService {
    SearchResult getSearchResult(SearchParam searchParam);
}
