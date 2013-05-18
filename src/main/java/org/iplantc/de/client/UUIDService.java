package org.iplantc.de.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("uuidService")
public interface UUIDService extends RemoteService {

    List<String> getUUIDs(int num);
}
