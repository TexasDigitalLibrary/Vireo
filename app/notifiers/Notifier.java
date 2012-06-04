package notifiers;

import org.apache.commons.mail.EmailAttachment;
import org.tdl.vireo.model.Person;
import play.*;
import play.mvc.*;
import java.util.*;
import play.mvc.Mailer;

public class Notifier extends Mailer{
	
	public static void review(Person person) {
		setContentType("text/html");
		setSubject("Welcome %s", person.getFullName());
		addRecipient(person.getEmail());
		setFrom("Admin <noreply@tdl.org>");
		send(person);
	}

	public static void lostPassword(Person person) {
		setFrom("Admin <noreply@tdl.org>");
		setSubject("Your password has been reset");
		addRecipient(person.getEmail());
		send(person, "newpassword");
	}
	
}
