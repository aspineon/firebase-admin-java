/*
 * Copyright 2020 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.firebase.internal;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponseInterceptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal API for configuring outgoing HTTP requests. To be used with the
 * {@link ErrorHandlingHttpClient} class.
 */
public final class HttpRequestInfo {

  private final String method;
  private final GenericUrl url;
  private final HttpContent content;
  private final Map<String, String> headers = new HashMap<>();
  private HttpResponseInterceptor interceptor;

  private HttpRequestInfo(String method, String url, HttpContent content) {
    this.method = method;
    this.url = new GenericUrl(url);
    this.content = content;
  }

  public HttpRequestInfo addHeader(String name, String value) {
    this.headers.put(name, value);
    return this;
  }

  public HttpRequestInfo addAllHeaders(Map<String, String> headers) {
    this.headers.putAll(headers);
    return this;
  }

  public HttpRequestInfo setResponseInterceptor(HttpResponseInterceptor interceptor) {
    this.interceptor = interceptor;
    return this;
  }

  public static HttpRequestInfo buildGetRequest(String url) {
    return new HttpRequestInfo(HttpMethods.GET, url, null);
  }

  public static HttpRequestInfo buildDeleteRequest(String url) {
    return new HttpRequestInfo(HttpMethods.DELETE, url, null);
  }

  public static HttpRequestInfo buildPostRequest(String url, HttpContent content) {
    return new HttpRequestInfo(HttpMethods.POST, url, content);
  }

  HttpRequest newHttpRequest(HttpRequestFactory factory) throws IOException {
    HttpRequest request = factory.buildRequest(method, url, content);
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      request.getHeaders().set(entry.getKey(), entry.getValue());
    }
    request.setResponseInterceptor(interceptor);

    return request;
  }
}