/*
 * Copyright (c) 2011. Michael S. Klishin and other contributors
 */

package clojurewerkz.urly;

import java.net.URI;
import java.net.URL;

public class UrlLike {
  private String protocol;
  private String userInfo;
  private String authority;
  private String host;
  private int port;

  private String path;
  private String query;
  private String fragment;


  private static final String SLASH = "/";
  private static final String BLANK_STRING = "";


  protected UrlLike(String scheme, String userInfo, String host, int port, String path, String query, String fragment) {
    this.protocol = scheme;
    this.userInfo = userInfo;
    this.host = host;
    this.port = port;
    this.path = path;
    this.query = query;
    this.fragment = fragment;
  }


  public String getAuthority() {
    return authority;
  }

  public String getFragment() {
    return fragment;
  }

  public String getRef() {
    return fragment;
  }

  public String getHost() {
    return host;
  }

  public String getPath() {
    return path;
  }

  public String getFile() {
    return path;
  }

  public int getPort() {
    return port;
  }

  public String getProtocol() {
    return protocol;
  }

  public String getScheme() {
    return protocol;
  }

  public String getQuery() {
    return query;
  }

  public String getUserInfo() {
    return userInfo;
  }


  public static UrlLike fromURI(URI uri) {
    return new UrlLike(normalizeProtocol(uri.getScheme()), uri.getUserInfo(), uri.getHost(), uri.getPort(), normalizePath(uri.getPath()), uri.getQuery(), uri.getFragment());
  }

  public static UrlLike fromURL(URL url) {
    return new UrlLike(normalizeProtocol(url.getProtocol()), url.getUserInfo(), url.getHost(), url.getPort(), normalizePath(url.getPath()), url.getQuery(), url.getRef());
  }

  private static String normalizeProtocol(String s) {
    if (s == null) {
      return null;
    } else {
      return s.toLowerCase();
    }
  }

  public static String normalizePath(String path) {
    if ((path == null) || SLASH.equals(path) || BLANK_STRING.equals(path)) {
      return BLANK_STRING;
    } else {
      return path.toLowerCase();
    }
  }
}
