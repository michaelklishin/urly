/*
 * Copyright (c) 2011. Michael S. Klishin and other contributors
 */

package clojurewerkz.urly;

import com.google.common.net.InternetDomainName;

import java.io.UnsupportedEncodingException;
import java.net.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class UrlLike {
  public static final String DEFAULT_PROTOCOL = "http";
  private static final String SLASH = "/";
  private static final String BLANK_STRING = "";
  public static final String COLON = ":";
  public static final String AT_SIGN = "@";

  private String protocol;
  private String userInfo;
  private String authority;
  private String host;
  private int port;

  private String path;
  private String query;
  private String fragment;
  private static final int DEFAULT_PORT = -1;
  private static final String DEFAULT_ENCODING = "UTF-8";


  protected UrlLike(String scheme, String userInfo, String host, String authority, int port, String path, String query, String fragment) {
    this.protocol = lowerCaseOrNull(scheme);
    this.userInfo = userInfo;
    this.host =  lowerCaseOrNull(host);
    this.authority = authority;
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

  public boolean hasFragment() {
    return (fragment != null);
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

  public boolean isDomainRoot() {
    return (this.path == SLASH);
  }

  public int getPort() {
    return port;
  }

  public boolean hasNonDefaultPort() {
    return ((port != DEFAULT_PORT) && (port != 0));
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

  public boolean hasQuery() {
    return (query != null);
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
    checkNotNull(idn);
    return fromInternetDomainName(idn);
  }

  public static UrlLike from(URI uri) {
    checkNotNull(uri);
    return fromURI(uri);
  }

  public static UrlLike from(URL url) {
    checkNotNull(url);
    return fromURL(url);
  }

  public static UrlLike from(UrlLike urly) {
    return urly;
  }

  public static UrlLike fromInternetDomainName(InternetDomainName idn) {
    checkNotNull(idn);
    return new UrlLike(DEFAULT_PROTOCOL, null, idn.name(), idn.name(), DEFAULT_PORT, SLASH, null, null);
  }

  public static UrlLike fromURI(URI uri) {
    checkNotNull(uri);
    return new UrlLike(lowerCaseOrNull(uri.getScheme()), uri.getUserInfo(), uri.getHost(), uri.getAuthority(), uri.getPort(), pathOrDefault(uri.getPath()), uri.getQuery(), uri.getFragment());
  }

  public static UrlLike fromURL(URL url) {
    checkNotNull(url);
    return new UrlLike(lowerCaseOrNull(url.getProtocol()), url.getUserInfo(), url.getHost(), url.getAuthority(), url.getPort(), pathOrDefault(url.getPath()), url.getQuery(), url.getRef());
  }

  public static UrlLike homepageOf(String hostname) {
    checkNotNull(hostname);
    return new UrlLike(DEFAULT_PROTOCOL, null, hostname, hostname, DEFAULT_PORT, SLASH, null, null);
  }

  public static UrlLike homepageOf(String hostname, String schema) {
    checkNotNull(hostname, "host cannot be null!");
    checkNotNull(schema, "schema cannot be null!");
    return new UrlLike(lowerCaseOrNull(schema), null, hostname, hostname, DEFAULT_PORT, SLASH, null, null);
  }


  public UrlLike mutateHost(String host) {
    checkNotNull(host);
    final String s = host.toLowerCase();
    return new UrlLike(this.protocol, this.userInfo, s, authorityFor(this.userInfo, s, this.port), this.port, this.path, this.query, this.fragment);
  }

  public UrlLike mutateHostname(String host) {
    return this.mutateHost(host);
  }

  public UrlLike withoutWww() {
    return this.mutateHost(host.replaceFirst("^www\\d*\\.", BLANK_STRING));
  }

  public UrlLike mutateProtocol(String protocol) {
    checkNotNull(protocol);
    return new UrlLike(protocol.toLowerCase(), this.userInfo, this.host, this.authority, this.port, this.path, this.query, this.fragment);
  }

  public UrlLike mutatePort(int port) {
    checkNotNull(port);
    return new UrlLike(this.protocol, this.userInfo, this.host, authorityFor(this.userInfo, this.host, port), port, this.path, this.query, this.fragment);
  }

  public UrlLike mutatePort(String port) {
    return this.mutatePort(Integer.valueOf(port));
  }

  public UrlLike mutateUserInfo(String info) {
    // note that user info may be null (when the intent is to clear it)
    // passwords are case-sensitive so don't lowercase user info. MK.
    return new UrlLike(this.protocol, info, this.host, this.authorityFor(info, this.host, this.port), this.port, this.path, this.query, this.fragment);
  }

  public UrlLike mutatePath(String path) {
    return new UrlLike(this.protocol, this.userInfo, this.host, this.authority, this.port, maybePrefixSlash(pathOrDefault(path)), this.query, this.fragment);
  }

  public UrlLike mutateQuery(String query) {
    return new UrlLike(this.protocol, this.userInfo, this.host, this.authority, this.port, this.path, query, this.fragment);
  }

  public UrlLike encodeQuery() throws UnsupportedEncodingException {
    return this.encodeQuery(DEFAULT_ENCODING);
  }

  public UrlLike encodeQuery(String encoding) throws UnsupportedEncodingException {
    checkNotNull(encoding);
    if(this.hasQuery()) {
      return this.mutateQuery(URLEncoder.encode(query, encoding));
    } else {
      return this;
    }
  }

  public UrlLike mutateFragment(String fragment) {
    // note that fragment may be null (when the intent is to clear it)
    return new UrlLike(this.protocol, this.userInfo, this.host, this.authority, this.port, this.path, this.query, fragment);
  }

  public UrlLike mutateQueryAndFragment(String query, String fragment) {
    return new UrlLike(this.protocol, this.userInfo, this.host, this.authority, this.port, this.path, query, fragment);
  }

  public UrlLike withoutQueryStringAndFragment() {
    return this.mutateQuery(null).mutateFragment(null);
  }

  public UrlLike withoutQuery() {
    return this.mutateQuery(null);
  }

  public UrlLike withoutQueryString() {
    return this.withoutQuery();
  }

  public UrlLike withoutFragment() {
    return this.mutateFragment(null);
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
    checkNotNull(s);
    if (s.startsWith(SLASH)) {
      return s;
    } else {
      return SLASH + s;
    }
  }

  private String authorityFor(String userInfo, String host, int port) {
    checkNotNull(host);
    StringBuilder newAuth = new StringBuilder();
    if (userInfo != null) {
      newAuth.append(userInfo).append(AT_SIGN);
    }
    newAuth.append(host.toLowerCase());
    if(port != DEFAULT_PORT) {
      newAuth.append(COLON).append(port);
    }
    return newAuth.toString();
  }
}
