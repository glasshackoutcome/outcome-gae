package com.appspot.ghackoutcome;

import com.appspot.ghackoutcome.dao.Participant;
import com.appspot.ghackoutcome.dao.ParticipantMocker;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.mirror.model.*;
import com.google.common.collect.Lists;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Handles POST requests from index.jsp
 * 
 * @author Jenny Murphy - http://google.com/+JennyMurphy
 */
public class MainServlet extends HttpServlet {

	/**
	 * Private class to process batch request results.
	 * <p/>
	 * For more information, see
	 * https://code.google.com/p/google-api-java-client/wiki/Batch.
	 */
	private final class BatchCallback extends JsonBatchCallback<TimelineItem> {
		private int success = 0;
		private int failure = 0;

		@Override
		public void onSuccess(TimelineItem item, HttpHeaders headers)
				throws IOException {
			++success;
		}

		@Override
		public void onFailure(GoogleJsonError error, HttpHeaders headers)
				throws IOException {
			++failure;
			LOG.info("Failed to insert item: " + error.getMessage());
		}
	}

	private static final Logger LOG = Logger.getLogger(MainServlet.class
			.getSimpleName());
	public static final String CONTACT_ID = "com.google.glassware.contact.java-quick-start";
	public static final String CONTACT_NAME = "Java Quick Start";

	private static final String PAGINATED_HTML = "<article class='auto-paginate'>"
			+ "<h2 class='blue text-large'>Did you know...?</h2>"
			+ "<p>Cats are <em class='yellow'>solar-powered.</em> The time they spend napping in "
			+ "direct sunlight is necessary to regenerate their internal batteries. Cats that do not "
			+ "receive sufficient charge may exhibit the following symptoms: lethargy, "
			+ "irritability, and disdainful glares. Cats will reactivate on their own automatically "
			+ "after a complete charge cycle; it is recommended that they be left undisturbed during "
			+ "this process to maximize your enjoyment of your cat.</p><br/><p>"
			+ "For more cat maintenance tips, tap to view the website!</p>"
			+ "</article>";

	/**
	 * Do stuff when buttons on index.jsp are clicked
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException {

		String userId = AuthUtil.getUserId(req);
		Credential credential = AuthUtil.newAuthorizationCodeFlow()
				.loadCredential(userId);
		String message = "";

		if (req.getParameter("operation").equals("insertSubscription")) {

			// subscribe (only works deployed to production)
			try {
				MirrorClient.insertSubscription(credential,
						WebUtil.buildUrl(req, "/notify"), userId,
						req.getParameter("collection"));
				message = "Application is now subscribed to updates.";
			} catch (GoogleJsonResponseException e) {
				LOG.warning("Could not subscribe "
						+ WebUtil.buildUrl(req, "/notify") + " because "
						+ e.getDetails().toPrettyString());
				message = "Failed to subscribe. Check your log for details";
			}

		} else if (req.getParameter("operation").equals("deleteSubscription")) {

			// subscribe (only works deployed to production)
			MirrorClient.deleteSubscription(credential,
					req.getParameter("subscriptionId"));

			message = "Application has been unsubscribed.";

		} else if (req.getParameter("operation").equals("insertParticipant")) {
			LOG.fine("Inserting Participant Timeline Item");
			List<Participant> allParticipants = ParticipantMocker.getMockList();

			for (Participant p : allParticipants) {
				if (req.getParameter("pid").equals(
						p.getFirstName() + "-" + p.getLastName())) {
					makeBundle(credential, req.getParameter("pid"), p);
				}
			}

			message = "A participant timeline item has been inserted.";

		} else if (req.getParameter("operation").equals("insertItem")) {
			LOG.fine("Inserting Timeline Item");
			TimelineItem timelineItem = new TimelineItem();

			if (req.getParameter("message") != null) {
				timelineItem.setText(req.getParameter("message"));
			}

			// Triggers an audible tone when the timeline item is received
			timelineItem.setNotification(new NotificationConfig()
					.setLevel("DEFAULT"));

			if (req.getParameter("imageUrl") != null) {
				// Attach an image, if we have one
				URL url = new URL(req.getParameter("imageUrl"));
				String contentType = req.getParameter("contentType");
				MirrorClient.insertTimelineItem(credential, timelineItem,
						contentType, url.openStream());
			} else {
				MirrorClient.insertTimelineItem(credential, timelineItem);
			}

			message = "A timeline item has been inserted.";

		} else if (req.getParameter("operation").equals("insertPaginatedItem")) {
			LOG.fine("Inserting Timeline Item");
			TimelineItem timelineItem = new TimelineItem();
			timelineItem.setHtml(PAGINATED_HTML);

			List<MenuItem> menuItemList = new ArrayList<MenuItem>();
			menuItemList.add(new MenuItem().setAction("OPEN_URI").setPayload(
					"https://www.google.com/search?q=cat+maintenance+tips"));
			timelineItem.setMenuItems(menuItemList);

			// Triggers an audible tone when the timeline item is received
			timelineItem.setNotification(new NotificationConfig()
					.setLevel("DEFAULT"));

			MirrorClient.insertTimelineItem(credential, timelineItem);

			message = "A timeline item has been inserted.";

		} else if (req.getParameter("operation").equals("insertItemWithAction")) {
			LOG.fine("Inserting Timeline Item");
			TimelineItem timelineItem = new TimelineItem();
			timelineItem.setText("Tell me what you had for lunch :)");

			List<MenuItem> menuItemList = new ArrayList<MenuItem>();
			// Built in actions
			menuItemList.add(new MenuItem().setAction("REPLY"));
			menuItemList.add(new MenuItem().setAction("READ_ALOUD"));

			// And custom actions
			List<MenuValue> menuValues = new ArrayList<MenuValue>();
			menuValues.add(new MenuValue().setIconUrl(
					WebUtil.buildUrl(req, "/static/images/drill.png"))
					.setDisplayName("Drill In"));
			menuItemList.add(new MenuItem().setValues(menuValues)
					.setId("drill").setAction("CUSTOM"));

			timelineItem.setMenuItems(menuItemList);
			timelineItem.setNotification(new NotificationConfig()
					.setLevel("DEFAULT"));

			MirrorClient.insertTimelineItem(credential, timelineItem);

			message = "A timeline item with actions has been inserted.";

		} else if (req.getParameter("operation").equals("insertContact")) {
			if (req.getParameter("iconUrl") == null
					|| req.getParameter("name") == null) {
				message = "Must specify iconUrl and name to insert contact";
			} else {
				// Insert a contact
				LOG.fine("Inserting contact Item");
				Contact contact = new Contact();
				contact.setId(req.getParameter("id"));
				contact.setDisplayName(req.getParameter("name"));
				contact.setImageUrls(Lists.newArrayList(req
						.getParameter("iconUrl")));
				contact.setAcceptCommands(Lists.newArrayList(new Command()
						.setType("TAKE_A_NOTE")));
				MirrorClient.insertContact(credential, contact);

				message = "Inserted contact: " + req.getParameter("name");
			}

		} else if (req.getParameter("operation").equals("deleteContact")) {

			// Insert a contact
			LOG.fine("Deleting contact Item");
			MirrorClient.deleteContact(credential, req.getParameter("id"));

			message = "Contact has been deleted.";

		} else if (req.getParameter("operation").equals("insertItemAllUsers")) {
			if (req.getServerName().contains(
					"glass-java-starter-demo.appspot.com")) {
				message = "This function is disabled on the demo instance.";
			}

			// Insert a contact
			List<String> users = AuthUtil.getAllUserIds();
			LOG.info("found " + users.size() + " users");
			if (users.size() > 10) {
				// We wouldn't want you to run out of quota on your first day!
				message = "Total user count is " + users.size()
						+ ". Aborting broadcast " + "to save your quota.";
			} else {
				TimelineItem allUsersItem = new TimelineItem();
				allUsersItem.setText("Hello Everyone!");

				BatchRequest batch = MirrorClient.getMirror(null).batch();
				BatchCallback callback = new BatchCallback();

				// TODO: add a picture of a cat
				for (String user : users) {
					Credential userCredential = AuthUtil.getCredential(user);
					MirrorClient.getMirror(userCredential).timeline()
							.insert(allUsersItem).queue(batch, callback);
				}

				batch.execute();
				message = "Successfully sent cards to " + callback.success
						+ " users (" + callback.failure + " failed).";
			}

		} else if (req.getParameter("operation").equals("deleteTimelineItem")) {

			// Delete a timeline item
			LOG.fine("Deleting Timeline Item");
			MirrorClient.deleteTimelineItem(credential,
					req.getParameter("itemId"));

			message = "Timeline Item has been deleted.";

		} else {
			String operation = req.getParameter("operation");
			LOG.warning("Unknown operation specified " + operation);
			message = "I don't know how to do that";
		}
		WebUtil.setFlash(req, message);
		res.sendRedirect(WebUtil.buildUrl(req, "/"));
	}

	public void makeBundle(Credential credential, String pid, Participant p) throws IOException{


        LOG.fine("Inserting Participant Timeline Items Bundle");
        
    	List<TimelineItem> timelineList = new ArrayList<TimelineItem>();
    	
    	TimelineItem cover = new TimelineItem();
    	TimelineItem info = new TimelineItem();
    	TimelineItem method1 = new TimelineItem();
    	TimelineItem method2 = new TimelineItem();
    	TimelineItem method3 = new TimelineItem();
    	TimelineItem safety = new TimelineItem();

    	cover.setBundleId(pid);
    	info.setBundleId(pid);
    	method1.setBundleId(pid);
    	method2.setBundleId(pid);
    	method3.setBundleId(pid);
    	safety.setBundleId(pid);

    	cover.setIsBundleCover(true);

        // Triggers an audible tone when the timeline item is received
        cover.setNotification(new NotificationConfig().setLevel("DEFAULT"));
        

        Map<String, String> methodOne = new HashMap<String, String>();
        methodOne.put("methodText", p.getMethodOne());
        methodOne.put("lastCompleteDate", "never");

        Map<String, String> methodTwo = new HashMap<String, String>();
        methodTwo.put("methodText", p.getMethodTwo());
        methodTwo.put("lastCompleteDate", "never");
        
        Map<String, String> methodThree = new HashMap<String, String>();
        methodThree.put("methodText", p.getMethodThree());
        methodThree.put("lastCompleteDate", "never");
        
    	cover.setHtml(CardUtil.getCardTemplate("participant_cover.html", p.getMap()));
    	info.setHtml(CardUtil.getCardTemplate("participant_card.html", p.getMap()));
    	method1.setHtml(CardUtil.getCardTemplate("participant_method.html", methodOne));
    	method2.setHtml(CardUtil.getCardTemplate("participant_method.html", methodTwo));
    	method3.setHtml(CardUtil.getCardTemplate("participant_method.html", methodThree));
    	safety.setHtml(CardUtil.getCardTemplate("participant_alerts.html", p.getMap()));

        MirrorClient.insertTimelineItem(credential, cover);
        MirrorClient.insertTimelineItem(credential, info);
        MirrorClient.insertTimelineItem(credential, method1);
        MirrorClient.insertTimelineItem(credential, method2);
        MirrorClient.insertTimelineItem(credential, method3);
        MirrorClient.insertTimelineItem(credential, safety);

    }
}
