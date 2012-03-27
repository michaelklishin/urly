/*
 * Copyright (c) 2011. Michael S. Klishin and other contributors
 */

package clojurewerkz.urly;

import com.google.common.net.InternetDomainName;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlLike {
  public static final String DEFAULT_PROTOCOL = "http";
  private static final String SLASH = "/";
  private static final String BLANK_STRING = "";

  private String protocol;
  private String userInfo;
  private String authority;
  private String host;
  private int port;

  private String path;
  private String query;
  private String fragment;
  private static final int DEFAULT_PORT = -1;


  protected UrlLike(String scheme, String userInfo, String host, int port, String path, String query, String fragment) {
    this.protocol = lowerCaseOrNull(scheme);
    this.userInfo = userInfo;
    this.host =  lowerCaseOrNull(host);
    this.port = port;
    this.path = pathOrDefault(path);
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

  public InternetDomainName getPublicSuffix() {
    if(this.host == null) {
      return null;
    }

    InternetDomainName idn = InternetDomainName.from(this.host);
    if ((idn != null) && (idn.hasPublicSuffix())) {
      return idn.publicSuffix();
    } else {
      return null;
    }      
  }

  public String getTld() {
    InternetDomainName idn = this.getPublicSuffix();
    if (idn != null) {
      return idn.name();
    } else {
      return null;
    }
  }

  public static UrlLike from(InternetDomainName idn) {
    return fromInternetDomainName(idn);
  }

  public static UrlLike from(URI uri) {
    return fromURI(uri);
  }

  public static UrlLike from(URL url) {
    return fromURL(url);
  }

  public static UrlLike from(UrlLike urly) {
    return urly;
  }

  public static UrlLike fromInternetDomainName(InternetDomainName idn) {
    return new UrlLike(DEFAULT_PROTOCOL, null, idn.name(), DEFAULT_PORT, SLASH, null, null);
  }

  public static UrlLike fromURI(URI uri) {
    return new UrlLike(lowerCaseOrNull(uri.getScheme()), uri.getUserInfo(), uri.getHost(), uri.getPort(), pathOrDefault(uri.getPath()), uri.getQuery(), uri.getFragment());
  }

  public static UrlLike fromURL(URL url) {
    return new UrlLike(lowerCaseOrNull(url.getProtocol()), url.getUserInfo(), url.getHost(), url.getPort(), pathOrDefault(url.getPath()), url.getQuery(), url.getRef());
  }

  public static UrlLike homepageOf(String hostname) {
    return new UrlLike(DEFAULT_PROTOCOL, null, hostname, DEFAULT_PORT, SLASH, null, null);
  }

  public static UrlLike homepageOf(String hostname, String schema) {
    return new UrlLike(lowerCaseOrNull(schema), null, hostname, DEFAULT_PORT, SLASH, null, null);
  }


  public UrlLike mutateHost(String host) {
    return new UrlLike(this.protocol, this.userInfo, host.toLowerCase(), this.port, this.path, this.query, this.fragment);
  }

  public UrlLike mutateHostname(String host) {
    return this.mutateHost(host);
  }

  public UrlLike mutateProtocol(String protocol) {
    return new UrlLike(protocol.toLowerCase(), this.userInfo, this.host, this.port, this.path, this.query, this.fragment);
  }

  public UrlLike mutatePort(int port) {
    return new UrlLike(this.protocol, this.userInfo, this.host, port, this.path, this.query, this.fragment);
  }

  public UrlLike mutatePort(String port) {
    return this.mutatePort(Integer.valueOf(port));
  }

  public UrlLike mutateUserInfo(String info) {
    // passwords are case-sensitive so don't lowercase user info. MK.
    return new UrlLike(this.protocol, info, this.host, this.port, this.path, this.query, this.fragment);
  }

  public UrlLike mutatePath(String path) {
    return new UrlLike(this.protocol, this.userInfo, this.host, this.port, maybePrefixSlash(pathOrDefault(path)), this.query, this.fragment);
  }

  public UrlLike mutateQuery(String query) {
    return new UrlLike(this.protocol, this.userInfo, this.host, this.port, this.path, query, this.fragment);
  }

  public UrlLike mutateFragment(String fragment) {
    return new UrlLike(this.protocol, this.userInfo, this.host, this.port, this.path, this.query, fragment);
  }

  public UrlLike withoutQueryStringAndFragment() {
    return this.mutateQuery(null).mutateFragment(null);
  }

  public UrlLike withoutLastPathSegment() {
    return this.mutatePath(this.path.replaceAll("[^/]+$", ""));
  }

  public URI toURI() throws URISyntaxException {
    return new URI(this.protocol, this.userInfo, this.host, this.port, this.path, this.query, this.fragment);
  }

  public URL toURL() throws URISyntaxException, MalformedURLException {
    return this.toURI().toURL();
  }

  @Override
  public String toString() {
    try {
      return this.toURI().toString();
    } catch (URISyntaxException e) {
      return "<malformed URI>";
    }
  }

  public static String eliminateDoubleProtocol(String uri, String preferredProtocol) {
    String lc = uri.toLowerCase();

    return lc.replaceFirst("^https?://https?://", preferredProtocol + "://");
  }

  public static String normalizeProtocol(String host) {
    return lowerCaseOrNull(host);
  }

  private static String lowerCaseOrNull(String host) {
    if (host == null) {
      return null;
    } else {
      return host.toLowerCase();
    }
  }

  public static String pathOrDefault(String path) {
    if ((path == null) || SLASH.equals(path) || BLANK_STRING.equals(path)) {
      return SLASH;
    } else {
      return path;
    }
  }

  public static String maybePrefixSlash(String s) {
    if (s.startsWith(SLASH)) {
      return s;
    } else {
      return SLASH + s;
    }
  }
}
