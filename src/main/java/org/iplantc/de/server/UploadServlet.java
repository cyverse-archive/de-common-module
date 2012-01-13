package org.iplantc.de.server;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
public class UploadServlet extends UploadAction {
    private static final long serialVersionUID = 1L;

    /**
     * The logger for error and informational messages.
     */
    private static Logger LOG = Logger.getLogger(UploadServlet.class);

    public static final String USER_ID = "user"; //$NON-NLS-1$
    public static final String EMAIL = "email"; //$NON-NLS-1$

    protected String user;
    protected String email;
    protected JSONObject jsonErrors;
    protected JSONObject jsonInfo;

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
        InputStream bodyFile = null;
        LOG.debug("Upload Action started."); //$NON-NLS-1$
        long fileLength = 0l;
        String mimeType = null;

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
                    LOG.error("FileUploadServlet::executeAction - Exception while getting file input stream:" //$NON-NLS-1$
                            + e.getMessage());
                    e.printStackTrace();
                    jsonErrors.put("error", e.getMessage()); //$NON-NLS-1$
                    return jsonErrors.toString();

                } catch (IRODSConfigurationException e) {
                    LOG.error("FileUploadServlet::executeAction - Exception while getting users IRODS home directory:" //$NON-NLS-1$
                            + e.getMessage());
                    e.printStackTrace();
                    jsonErrors.put("error", e.getMessage()); //$NON-NLS-1$
                    return jsonErrors.toString();
                } catch (UploadActionException e) {
                    LOG.error("FileUploadServlet::executeAction - Exception while getting uploading files to users home directory:" //$NON-NLS-1$
                            + e.getMessage());
                    e.printStackTrace();
                    jsonErrors.put("error", e.getMessage()); //$NON-NLS-1$
                    return jsonErrors.toString();
                }

            } else {
                String contents = new String(item.get());
                jsonInfo.put(fileFieldName, contents);
            }
        }

        // remove files from session. this avoids duplicate submissions
        removeSessionFileItems(request, false);

        LOG.debug("UploadServlet::executeAction - JSON returned: " + jsonErrors); //$NON-NLS-1$
        return jsonErrors.toString();
    }

    private String getUserName(List<FileItem> fileItems) {
        String user = null;
        for (FileItem item : fileItems) {
            if (item.isFormField()) {
                String fieldName = item.getFieldName();
                byte[] contents = item.get();

                if (fieldName.equals(USER_ID)) {
                    user = new String(contents);
                    break;
                }
            }
        }
        return user;
    }

    private String getUserEmail(List<FileItem> fileItems) {
        String email = ""; //$NON-NLS-1$
        for (FileItem item : fileItems) {
            if (item.isFormField()) {
                String fieldName = item.getFieldName();
                if (fieldName.equals(EMAIL)) {
                    email = item.getString();
                    break;
                }
            }
        }
        return email;
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
            DataApiServiceDispatcher dispatcher = new DataApiServiceDispatcher();
            dispatcher.init(getServletConfig());
            dispatcher.setRequest(request);
            fileUrl = extractUploadedUrl(dispatcher.getServiceData(wrapper));
            LOG.debug("UploadServlet::invokeService - Making service call."); //$NON-NLS-1$
        } catch (Exception e) {
            LOG.error("unable to upload file", e); //$NON-NLS-1$
            throw new UploadActionException(e.getMessage());
        }
        return fileUrl;
    }

    private String extractUploadedUrl(String json) {
        JSONObject jsonObj = JSONObject.fromObject(json);
        if (jsonObj != null) {
            return jsonObj.getString("id"); //$NON-NLS-1$
        } else {
            return null;
        }
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
        String addressKey = "org.iplantc.services.zoidberg.fileupload"; //$NON-NLS-1$

        MultiPartServiceWrapper wrapper = new MultiPartServiceWrapper(MultiPartServiceWrapper.Type.POST,
                addressKey);

        wrapper.addPart(new FileHTTPPart(fileContents, "file", filename, mimeType, fileLength)); //$NON-NLS-1$
        wrapper.addPart(path + "/" + filename, "dest"); //$NON-NLS-1$ //$NON-NLS-2$

        return wrapper;
    }

    private String getUserHomeDir(HttpServletRequest request) {
        ServiceCallWrapper wrapper = new ServiceCallWrapper(
                "org.iplantc.services.zoidberg.getuserhomedir"); //$NON-NLS-1$
        String homeDir = null;
        try {
            DataApiServiceDispatcher dispatcher = new DataApiServiceDispatcher();
            dispatcher.init(getServletConfig());
            dispatcher.setRequest(request);
            homeDir = dispatcher.getServiceData(wrapper);
            LOG.debug("UploadServlet::getUserHomeDir - Making service call."); //$NON-NLS-1$
        } catch (Exception e) {
            LOG.error("unable get users home dir", e); //$NON-NLS-1$
        }
        return homeDir;
    }
}
