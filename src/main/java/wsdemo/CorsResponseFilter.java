package wsdemo;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

@Provider
public class CorsResponseFilter implements ContainerResponseFilter {

  public static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
  public final static int MAX_AGE = 42 * 60 * 60;
  public final static String DEFAULT_ALLOWED_HEADERS = "origin,accept,content-type";

  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
    final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
    headers.add("Access-Control-Allow-Origin", "*");
    headers.add("Access-Control-Allow-Headers", getRequestedHeaders(responseContext));
    headers.add("Access-Control-Allow-Credentials", "true");
    headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS);
    headers.add("Access-Control-Max-Age", MAX_AGE);
    headers.add("x-responded-by", "cors-response-filter");
  }

  String getRequestedHeaders(ContainerResponseContext responseContext) {
    List<Object> headers = responseContext.getHeaders().get("Access-Control-Request-Headers");
    return createHeaderList(headers);
  }

  String createHeaderList(List<Object> headers) {
    if (headers == null || headers.isEmpty()) {
      return DEFAULT_ALLOWED_HEADERS;
    }
    StringBuilder retVal = new StringBuilder();
    for (int i = 0; i < headers.size(); i++) {
      String header = (String) headers.get(i);
      retVal.append(header);
      if (i != headers.size() - 1) {
        retVal.append(',');
      }
    }
    return retVal.toString();
  }

}
