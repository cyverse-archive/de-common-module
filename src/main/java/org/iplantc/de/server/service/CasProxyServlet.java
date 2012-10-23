package org.iplantc.de.server.service;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.iplantc.de.server.ServiceCallResolver;
import org.iplantc.de.server.UnresolvableServiceNameException;

/**
 * A secured servlet that forwards requests directly to other services.
 *
 * @author Dennis Roberts
 */
public class CasProxyServlet extends HttpServlet {

    /**
     * Used to resolve aliased service calls.
     */
    private ServiceCallResolver serviceResolver;

    /**
     * The default constructor.
     */
    public CasProxyServlet() {}

    /**
     * @param serviceResolver used to resolve aliased service calls.
     */
    public CasProxyServlet(ServiceCallResolver serviceResolver) {
        this.serviceResolver = serviceResolver;
    }

    /**
     * Initializes the servlet.
     *
     * @throws ServletException if the servlet can't be initialized.
     * @throws IllegalStateException if the service call resolver can't be found.
     */
    @Override
    public void init() throws ServletException {
        if (serviceResolver == null) {
            serviceResolver = ServiceCallResolver.getServiceCallResolver(getServletContext());
        }
    }

    /**
     * Forwards an HTTP GET request to a named service.
     *
     * @param req the HTTP servlet request.
     * @param res the HTTP servlet response.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        new ServiceCallResolutionWrapper() {
            @Override
            protected void forwardRequest(HttpClient client, String uri) throws IOException {
                HttpGet get = new HttpGet(uri);
                try {
                    copyHeaders(req, get);
                    copyResponse(client.execute(get), res);
                }
                finally {
                    get.releaseConnection();
                }
            }
        }.call(req, res);
    }

    /**
     * Copies an incoming response to an outgoing servlet response.
     *
     * @param source the incoming response.
     * @param dest the outgoing response.
     * @throws IOException if an I/O error occurs.
     */
    private void copyResponse(HttpResponse source, HttpServletResponse dest) throws IOException {
        dest.setStatus(source.getStatusLine().getStatusCode());
        copyHeaders(source, dest);
        IOUtils.copy(source.getEntity().getContent(), dest.getOutputStream());
    }

    /**
     * Copies all response headers from an incoming response to an outgoing servlet response.
     *
     * @param source the incoming response.
     * @param dest the outgoing response.
     */
    private void copyHeaders(HttpResponse source, HttpServletResponse dest) {
        for (Header header : source.getAllHeaders()) {
            dest.addHeader(header.getName(), header.getValue());
        }
    }

    /**
     * Copies all request headers from an incoming servlet request to an outgoing request.
     *
     * @param source the incoming request.
     * @param dest the outgoing request.
     */
    private void copyHeaders(HttpServletRequest source, HttpRequestBase dest) {
        Enumeration<String> names = source.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            Enumeration<String> values = source.getHeaders(name);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                dest.addHeader(name, value);
            }
        }
    }

    /**
     * Resolves an aliased service call.
     *
     * @param req the original HTTP servlet request.
     * @return the string representation of the URI to forward the request to.
     */
    private String resolveServiceCall(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            throw new NoServiceNameProvidedException();
        }
        String serviceName = pathInfo.replaceAll("^/", "");
        return serviceResolver.resolveAddress(serviceName);
    }

    /**
     * Wraps a service call resolution so that the servlet returns an appropriate response whenever a servlet can't be
     * resolved.
     */
    private abstract class ServiceCallResolutionWrapper {

        /**
         * Calls the service call resolution wrapper.
         *
         * @param req the incoming HTTP servlet request.
         * @param res the outgoing HTTP servlet response.
         * @throws IOException if an I/O error occurs.
         */
        public void call(HttpServletRequest req, HttpServletResponse res) throws IOException {
            String uri;
            try {
                uri = resolveServiceCall(req);
                String queryString = req.getQueryString();
                if (queryString != null) {
                    uri += "?" + queryString;
                }
            }
            catch (NoServiceNameProvidedException e) {
                sendErrorResponse(res, e.getMessage());
                return;
            }
            catch (UnresolvableServiceNameException e) {
                sendErrorResponse(res, e.getMessage());
                return;
            }
            forwardRequest(new DefaultHttpClient(), uri);
        }

        /**
         * Forwards the request to the named service.
         *
         * @param client the HTTP client to use for the request.
         * @param uri the URI to use to connect to the named service.
         * @throws IOException if an I/O error occurs.
         */
        protected abstract void forwardRequest(HttpClient client, String uri) throws IOException;

        /**
         * Sends a response indicating that a service call resolution error has occurred.
         *
         * @param res the outgoing HTTP servlet response.
         * @param msg the error detail message.
         * @throws IOException if an I/O error occurs.
         */
        private void sendErrorResponse(HttpServletResponse res, String msg) throws IOException {
            PrintStream out = new PrintStream(res.getOutputStream());
            try {
                res.setContentType("application/json");
                out.println(errorJson(msg));
            }
            finally {
                if (out != null) {
                    IOUtils.closeQuietly(out);
                }
            }
        }

        /**
         * Builds a string representation of a JSON object indicating that a service call resolution error has
         * occurred.
         *
         * @param msg the error detail message.
         * @return the string representation of the JSON object.
         */
        private String errorJson(String msg) {
            JSONObject json = new JSONObject();
            json.put("status", "failure");
            json.put("action", "PROXY_SERVICE_CALL");
            json.put("error_code", "ERR_BAD_OR_MISSING_FIELD");
            json.put("detail", msg);
            return json.toString(4);
        }
    }
}
