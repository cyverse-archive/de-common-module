/**
 * 
 * Current efforts:
 * 
 * My intent is to create one or many autobeans which extends ServiceError, and all autobeans in this
 * package will derive from.
 * Then, a Category class will be created to supply the specific error string for each type of error
 * object.
 * 
 * Since the endpoints for disk resource operations are no longer split into two points per operation
 * (file|folder move|delete|rename), many of the specific error messages will no longer be used.
 * 
 * The logic for creating new exceptions and passing the new exception to ErrorHandler (in the old
 * DiskResourceServiceCallback failure method) will now be done in the ErrorHandler itself.
 * Now, when an failure happens in a callback, we objectify the error and send it to the error handler.
 * 
 * Another point to note is that some disk resource errors have "path" keys or "paths" keys in their
 * json. This is a consideration for
 * how the autobeans are created.
 * 
 * TODO Sharing errors, etc, needs to be implemented.
 * 
 * @author jstroot
 */
package org.iplantc.de.client.models.errors.diskResources;

