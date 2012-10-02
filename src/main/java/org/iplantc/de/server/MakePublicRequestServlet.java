package org.iplantc.de.server;

import gwtupload.server.exceptions.UploadActionException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

public class MakePublicRequestServlet extends UploadServlet {

    /**
     * The logger for error and informational messages.
     */
    private static Logger LOG = Logger.getLogger(MakePublicRequestServlet.class);

    /**
     * {@inheritDoc}
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param serviceResolver used to resolve aliased service calls.
     */
    public MakePublicRequestServlet(ServiceCallResolver serviceResolver) {
        super(serviceResolver);
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
    public String executeAction(HttpServletRequest request, List<FileItem> fileItems)   {
        super.executeAction(request, fileItems);
        jsonInfo.put("success", "true"); //$NON-NLS-1$
        // remove files from session. this avoids duplicate submissions
        removeSessionFileItems(request, false);

        LOG.debug("MakePublicRequestServlet::executeAction - JSON returned: " + jsonInfo); //$NON-NLS-1$

        return jsonInfo.toString();
    }

}
