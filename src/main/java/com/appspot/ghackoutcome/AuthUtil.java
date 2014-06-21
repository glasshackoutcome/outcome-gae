package com.appspot.ghackoutcome;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.appspot.ghackoutcome.dao.Participant;
import com.appspot.ghackoutcome.dao.ParticipantMocker;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;

/**
 * A collection of utility functions that simplify common authentication and
 * user identity tasks
 *
 * @author Jenny Murphy - http://google.com/+JennyMurphy
 */
public class AuthUtil {
  private static final Logger LOG = Logger.getLogger(AuthUtil.class.getSimpleName());
    public static List<Participant> participantsList;

    public static List<Participant> getParticipants(HttpServletRequest request) {
        if(participantsList == null) {
            participantsList = ParticipantMocker.getMockList();
        }
        // Add it to session of actual request to access it scoped in jsp
        HttpSession session = request.getSession();
        session.setAttribute("participantsListTest", participantsList);
        return participantsList;
    }

  public static DataStore<StoredCredential>  store;
  static {
	try {
      store = AppEngineDataStoreFactory.getDefaultInstance()
        .getDataStore(StoredCredential.DEFAULT_DATA_STORE_ID);
	} catch (IOException e) {
      LOG.log(Level.SEVERE, "Failed to obtain default credential store", e);
	}
  }
  public static final String GLASS_SCOPE = "https://www.googleapis.com/auth/glass.timeline "
      + "https://www.googleapis.com/auth/glass.location "
      + "https://www.googleapis.com/auth/userinfo.profile";

  /**
   * Creates and returns a new {@link com.google.api.client.auth.oauth2.AuthorizationCodeFlow} for this app.
   */
  public static AuthorizationCodeFlow newAuthorizationCodeFlow() throws IOException {
    URL resource = AuthUtil.class.getResource("/oauth.properties");
    File propertiesFile = new File("./src/main/resources/oauth.properties");
    try {
      propertiesFile = new File(resource.toURI());
      //LOG.info("Able to find oauth properties from file.");
    } catch (URISyntaxException e) {
      LOG.info(e.toString());
      LOG.info("Using default source path.");
    }
    FileInputStream authPropertiesStream = new FileInputStream(propertiesFile);

    Properties authProperties = new Properties();
    authProperties.load(authPropertiesStream);

    String clientId = authProperties.getProperty("client_id");
    String clientSecret = authProperties.getProperty("client_secret");

    return new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), new JacksonFactory(),
        clientId, clientSecret, Collections.singleton(GLASS_SCOPE)).setAccessType("offline")
        .setCredentialDataStore(store).build();
  }

  /**
   * Get the current user's ID from the session
   *
   * @return string user id or null if no one is logged in
   */
  public static String getUserId(HttpServletRequest request) {
    HttpSession session = request.getSession();
    return (String) session.getAttribute("userId");
  }

  public static void setUserId(HttpServletRequest request, String userId) {
    HttpSession session = request.getSession();
    session.setAttribute("userId", userId);
  }

  public static void clearUserId(HttpServletRequest request) throws IOException {
    // Delete the credential in the credential store
    String userId = getUserId(request);
    store.delete(userId);
    
    // Remove their ID from the local session
    request.getSession().removeAttribute("userId");
  }

  public static Credential getCredential(String userId) throws IOException {
    if (userId == null) {
      return null;
    } else {
      return AuthUtil.newAuthorizationCodeFlow().loadCredential(userId);
    }
  }

  public static Credential getCredential(HttpServletRequest req) throws IOException {
    return AuthUtil.newAuthorizationCodeFlow().loadCredential(getUserId(req));
  }

  public static List<String> getAllUserIds() {
	List<String> userIdList;
	try {
		userIdList = new ArrayList<String>( store.keySet() );
	} catch (IOException e) {
		LOG.log(Level.WARNING, "Failed to fetch user IDs", e);
		userIdList = Collections.emptyList();
	}
	return userIdList;
  }
}
