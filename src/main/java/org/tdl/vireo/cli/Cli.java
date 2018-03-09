package org.tdl.vireo.cli;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;
import org.tdl.vireo.model.repo.UserRepo;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.tamu.weaver.auth.model.Credentials;

/**
 * Activate the Vireo command line interface by passing the console argument to Maven
 * 
 * mvn clean spring-boot:run -Drun.arguments=console
 * 
 * NOTE: will enable allow submissions on institution
 * 
 * @author James Creel
 * @author Jeremy Huff
 *
 */
@Component
public class Cli implements CommandLineRunner {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FieldValueRepo fieldValueRepo;

    @Autowired
    private SubmissionRepo submissionRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private SubmissionStatusRepo submissionStatusRepo;

    @Override
    @SuppressWarnings("unchecked")
    public void run(String... arg0) throws Exception {
        boolean runConsole = false;
        for (String s : arg0) {
            if (s.equals("console")) {
                runConsole = true;
                break;
            }
        }

        Boolean running = runConsole ? true : false;

        if (running) {
            final String PROMPT = "\n" + (char) 27 + "[36mvireo>" + (char) 27 + "[37m ";

            Scanner reader = new Scanner(System.in); // Reading from System.in

            System.out.print(PROMPT);

            int itemsGenerated = 0;

            while (running && reader.hasNextLine()) {

                String n = reader.nextLine();

                n = n.trim();

                String[] commandTokens = n.split("\\s+");
                List<String> commandArgs = new ArrayList<String>();

                String command = null;
                int num = 0;

                if (commandTokens.length > 0)
                    command = commandTokens[0];
                if (commandTokens.length > 1) {
                    for (int i = 1; i < commandTokens.length; i++) {
                        commandArgs.add(commandTokens[i]);
                    }

                }

                switch (command) {
                case "exit":
                    System.out.println("\nGoodbye.");
                    running = false;
                    break;
                case "generate":

                    Organization org = organizationRepo.findAll().get(0);

                    if (!org.getAcceptsSubmissions()) {
                        org.setAcceptsSubmissions(true);
                        org = organizationRepo.save(org);
                    }

                    SubmissionStatus state = submissionStatusRepo.findAll().get(0);

                    if (commandArgs.size() > 0) {
                        try {
                            num = Integer.parseInt(commandArgs.get(0));
                        } catch (Exception e) {
                            System.err.println("unable to parse as a number of items: " + commandArgs.get(0));
                        }
                    }

                    Random random = new Random();

                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

                    for (int i = itemsGenerated; i < num + itemsGenerated; i++) {
                        User submitter = userRepo.create("bob" + (i + 1) + "@boring.bob", "bob", "boring", Role.ROLE_STUDENT);
                        Credentials credentials = new Credentials();
                        credentials.setFirstName("Bob");
                        credentials.setLastName("Boring");
                        credentials.setEmail("bob@boring.bob");
                        credentials.setRole(Role.ROLE_STUDENT.name());

                        Submission sub = submissionRepo.create(submitter, org, state, credentials);
                        for (SubmissionWorkflowStep step : sub.getSubmissionWorkflowSteps()) {
                            for (SubmissionFieldProfile fp : step.getAggregateFieldProfiles()) {
                                FieldPredicate pred = fp.getFieldPredicate();
                                FieldValue val;
                                switch (fp.getInputType().getName()) {
                                case "INPUT_FILE":
                                    break;
                                case "INPUT_CONTACT":
                                    val = fieldValueRepo.create(pred);
                                    val.setValue("test " + pred.getValue() + " " + i);
                                    val.setContacts(Arrays.asList(new String[] { "test" + pred.getValue() + i + "@mailinator.com" }));
                                    sub.addFieldValue(val);
                                    break;
                                case "INPUT_EMAIL":
                                    val = fieldValueRepo.create(pred);
                                    val.setValue("test" + pred.getValue() + i + "@mailinator.com");
                                    sub.addFieldValue(val);
                                    break;
                                case "INPUT_DATETIME":
                                    val = fieldValueRepo.create(pred);

                                    Calendar calendar = Calendar.getInstance();

                                    calendar.add(Calendar.YEAR, -random.nextInt(10));

                                    int rm = random.nextInt(10);
                                    if (random.nextInt(2) == 2) {
                                        rm = -rm;
                                    }

                                    calendar.add(Calendar.MONTH, rm);

                                    calendar.add(Calendar.DATE, random.nextInt(32 - calendar.get(Calendar.DAY_OF_MONTH)));

                                    val.setValue(format.format(calendar.getTime()));
                                    sub.addFieldValue(val);
                                    break;
                                default:
                                    val = fieldValueRepo.create(pred);
                                    val.setValue("test " + pred.getValue() + " " + i);
                                    sub.addFieldValue(val);
                                }
                            }
                        }
                        submissionRepo.saveAndFlush(sub);

                        System.out.print("\r" + (i - itemsGenerated) + " of " + num + " generated...");

                    }

                    System.out.println("\rGenerated " + num + " submissions.");
                    itemsGenerated += num;
                    break;

                case "":
                    break;

                default:
                    System.out.println("Unknown command " + n);
                }

                if (running)
                    System.out.print(PROMPT);

            }
            reader.close();

        }
    }
}
