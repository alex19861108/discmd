package com.fxiaoke.jason.service;

import com.google.common.collect.Maps;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by alex on 16/9/29.
 */
@Service
public class PaginationService {
  public String generatePagination(String baseUri, Map<String, String[]> parameterMap, int pageCnt, int pageNum) {
    String paginationElement = "<ul class=\"pagination pagination-sm no-margin pull-right\">";
    String fmt = "<li><a href=\"{0}\">{1}</a></li>";

    for (int idx = 1; idx <= pageCnt; idx++) {

      Map<String, String[]> overWriteMap = Maps.newHashMap();
      String[] idxArray = new String[]{String.valueOf(idx)};
      overWriteMap.put("pageNum", idxArray);
      String uri = generatePageMachineUri(baseUri, parameterMap, overWriteMap);

      if (idx == 1) {
        paginationElement += MessageFormat.format(fmt, uri, "首页");
      }
      if (idx == (pageNum - 1) ) {
        paginationElement += MessageFormat.format(fmt, uri, "上一页");
      }
      if (idx == (pageNum + 1) ) {
        paginationElement += MessageFormat.format(fmt, uri, "下一页");
      }
      if (idx == pageCnt) {
        paginationElement += MessageFormat.format(fmt, uri, "尾页");
      }
    }

    paginationElement += "</ul>";
    return paginationElement;
  }

  public String generatePageSize(int pageSize) {
    List<Integer> pageSelectors = new LinkedList<>();
    pageSelectors.add(10);
    pageSelectors.add(20);
    pageSelectors.add(50);
    pageSelectors.add(100);
    String pageElement = "<div class=\"pull-left\">每页显示: <select class=\"\">";
    for (Integer pn : pageSelectors) {
      if (pn == pageSize) {
        pageElement += "<option value=\"" + String.valueOf(pn) + "\" selected=\"selected\">" + String.valueOf(pn) + "</option>";
      } else {
        pageElement += "<option value=\"" + String.valueOf(pn) + "\">" + String.valueOf(pn) + "</option>";
      }
    }
    pageElement += "</select></div>";
    return pageElement;
  }

  public String generatePageMachineUri(String uri, Map<String, String[]> parameterMap, Map<String, String[]> overWriteMap) {

    // 参数合并
    Map<String, String[]> nMap = Maps.newHashMap();
    nMap.putAll(parameterMap);
    nMap.putAll(overWriteMap);
    String parameterUri = parameterMap2Uri(nMap);

    String pageMachineUri = uri + "?" + parameterUri;

    return pageMachineUri;
  }

  /**
   * 将parameterMap转换为url
   * @param map
   * @return
   */
  public String parameterMap2Uri(Map<String, String[]> map) {

    final List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

    for (Map.Entry entry : map.entrySet()) {
      String[] values = (String[]) entry.getValue();
      for (int i = 0; i < values.length; i++) {
        params.add(new BasicNameValuePair(entry.getKey().toString(), values[i].toString()));
      }
    }
    return URLEncodedUtils.format(params, "UTF-8");
  }
}
