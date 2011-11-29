/*
 * Copyright (c) 2011. Michael S. Klishin and other contributors
 */

package clojurewerkz.urly;

import java.net.URI;

public class UrlLike {
  private String protocol;
  private String userInfo;
  private String authority;
  private String host;
  private int    port;

  private String path;
  private String query;
  private String fragment;


  protected UrlLike(String scheme, String userInfo, String host, int port, String path, String query, String fragment) {
    this.protocol = scheme;
    this.userInfo = userInfo;
    this.host     = host;
    this.port     = port;
    this.path     = path;
    this.query    = query;
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
    return new UrlLike(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
  }
}
