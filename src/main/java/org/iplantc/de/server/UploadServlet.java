package org.iplantc.de.server;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.iplantc.de.shared.services.MultiPartServiceWrapper;
import org.iplantc.de.shared.services.ServiceCallWrapper;

/**
 * A class to accept files from the client.
 *
 * This class extends the UploadAction class provided by the GWT Upload library. The executeAction method
 * must be overridden for custom behavior.
 *
 * @author sriram
 *
 */
@SuppressWarnings("nls")
public class UploadServlet extends UploadAction {
    private static final long serialVersionUID = 1L;

    /**
     * The logger for error and informational messages.
     */
    private static Logger LOG = Logger.getLogger(UploadServlet.class);

    public static final String USER_ID = "user";
    public static final String EMAIL = "email";

    protected String user;
    protected String email;
    protected JSONObject jsonErrors;
    protected JSONObject jsonInfo;

    /**
     * Used to resolve aliased service calls.
     */
    private ServiceCallResolver serviceResolver;

    /**
     * The default constructor.
     */
    public UploadServlet() {}

    /**
     * @param serviceResolver used to resolve aliased service calls.
     */
    public UploadServlet(ServiceCallResolver serviceResolver) {
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
        super.init();
        if (serviceResolver == null) {
            serviceResolver = ServiceCallResolver.getServiceCallResolver(getServletContext());
        }
    }

    /**
     * Performs the necessary operations for an upload action.
     *
     * @param request the HTTP request associated with the action.
     * @param fileItems the file associated with the action.
     * @return a string representing data in JSON format.
     * @throws UploadActionException if there is an issue invoking the dispatch to the servlet
     */
    @Override
    public String executeAction(HttpServletRequest request, List<FileItem> fileItems) {
        jsonErrors = new JSONObject();
        jsonInfo = new JSONObject();
        InputStream bodyFile;
        LOG.debug("Upload Action started.");
        long fileLength;
        String mimeType;

        user = getUserName(fileItems);
        email = getUserEmail(fileItems);
        jsonInfo.put(USER_ID, user);
        jsonInfo.put(EMAIL, email);

        for (FileItem item : fileItems) {
            String fileFieldName = item.getFieldName();

            if (item.getContentType() != null && (item.getSize() > 0)) {
                try {
                    fileLength = item.getSize();
                    mimeType = item.getContentType();
                    bodyFile = item.getInputStream();

                    jsonInfo.put(fileFieldName,
                            invokeService(request, item.getName(), bodyFile, fileLength, mimeType));
                } catch (IOException e) {
                    LOG.error("executeAction - Exception while getting file input stream.", e);
                    jsonErrors.put("error", e.getMessage());

                    return jsonErrors.toString();
                } catch (IRODSConfigurationException e) {
                    LOG.error("executeAction - Exception while getting users IRODS home directory.", e);
                    jsonErrors.put("error", e.getMessage());

                    return jsonErrors.toString();
                } catch (UploadActionException e) {
                    LOG.error(
                            "executeAction - Exception while getting uploading files to users home directory.",
                            e);
                    jsonErrors.put("error", e.getMessage());

                    return jsonErrors.toString();
                }

            } else {
                String contents = new String(item.get());
                jsonInfo.put(fileFieldName, contents);
            }
        }

        // remove files from session. this avoids duplicate submissions
        removeSessionFileItems(request, false);

        LOG.debug("executeAction - JSON returned: " + jsonErrors);
        return jsonErrors.toString();
    }

    private String getUserName(List<FileItem> fileItems) {
        String username = null;
        for (FileItem item : fileItems) {
            if (item.isFormField()) {
                String fieldName = item.getFieldName();
                byte[] contents = item.get();

                if (fieldName.equals(USER_ID)) {
                    username = new String(contents);
                    break;
                }
            }
        }
        return username;
    }

    private String getUserEmail(List<FileItem> fileItems) {
        String emailAddress = "";
        for (FileItem item : fileItems) {
            if (item.isFormField()) {
                String fieldName = item.getFieldName();
                if (fieldName.equals(EMAIL)) {
                    emailAddress = item.getString();
                    break;
                }
            }
        }
        return emailAddress;
    }

    /**
     * Handles the invocation of the file upload service.
     *
     * @param request current HTTP request
     * @param type the file type. It can be AUTO or CSVNAMELIST
     * @param filename the name of the file being uploaded
     * @param fileContents the content of the file
     * @param fileLength the length of the file being uploaded.
     * @param mimeType content mime type
     * @return a string representing data in JSON format.
     * @throws UploadActionException if there is an issue invoking the dispatch to the servlet
     * @throws IRODSConfigurationException if there is a problem with irods config.
     */
    private String invokeService(HttpServletRequest request, String filename, InputStream fileContents,
            long fileLength, String mimeType) throws UploadActionException, IRODSConfigurationException {
        String fileUrl = null;
        String userHome = getUserHomeDir(request);

        if (userHome == null) {
            throw new IRODSConfigurationException();
        }
        MultiPartServiceWrapper wrapper = createServiceWrapper(userHome, filename, fileLength, mimeType,
                fileContents);

        try { // call the RESTful service and get the results.
            DataApiServiceDispatcher dispatcher = new DataApiServiceDispatcher(serviceResolver);
            dispatcher.init(getServletConfig());
            dispatcher.setRequest(request);

            LOG.debug("invokeService - Making service call.");
            String response = dispatcher.getServiceData(wrapper);
            LOG.debug(response);

            fileUrl = extractUploadedUrl(response);
        } catch (Exception e) {
            LOG.error("unable to upload file", e); //$NON-NLS-1$

            UploadActionException uploadException = new UploadActionException(e.getMessage());
            uploadException.initCause(e);

            throw uploadException;
        }

        return fileUrl;
    }

    private String extractUploadedUrl(String json) {
        JSONObject jsonObj = JSONObject.fromObject(json);
        if (jsonObj != null) {
            JSONObject file = jsonObj.getJSONObject("file");
            if (file != null) {
                return file.getString("id");
            }
        }

        return null;
    }

    /**
     * Constructs and configures a multi-part service wrapper.
     *
     * @param path the folder identifier for where the file will be created
     * @param filename the name of the file being uploaded
     * @param fileContents the content of the file
     * @return an instance of a multi-part service wrapper.
     */
    private MultiPartServiceWrapper createServiceWrapper(String path, String filename, long fileLength,
            String mimeType, InputStream fileContents) {
        // address key that is resolved by the service dispatcher
        String addressKey = "org.iplantc.services.file-io.file-upload";

        MultiPartServiceWrapper wrapper = new MultiPartServiceWrapper(MultiPartServiceWrapper.Type.POST,
                addressKey);

        wrapper.addPart(new FileHTTPPart(fileContents, "file", filename, mimeType, fileLength)); //$NON-NLS-1$
        wrapper.addPart(path, "dest");

        return wrapper;
    }

    private String getUserHomeDir(HttpServletRequest request) {
        ServiceCallWrapper wrapper = new ServiceCallWrapper(
                "org.iplantc.services.de-data-mgmt.getuserhomedir");
        String homeDir = null;

        try {
            DataApiServiceDispatcher dispatcher = new DataApiServiceDispatcher(serviceResolver);
            dispatcher.init(getServletConfig());
            dispatcher.setRequest(request);
            homeDir = dispatcher.getServiceData(wrapper);
            LOG.debug("getUserHomeDir - Making service call.");
        } catch (Exception e) {
            LOG.error("getUserHomeDir - unable get users home dir", e);
        }
        return homeDir;
    }
}
