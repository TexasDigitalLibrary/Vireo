package org.tdl.vireo.cli;

import edu.tamu.weaver.auth.model.Credentials;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;
import org.tdl.vireo.model.repo.UserRepo;

/**
 * Activate the Vireo command line interface by passing the console argument to Maven
 *
 * mvn clean spring-boot:run -Drun.arguments=console
 * 
 * NOTE: will enable allow submissions on institution
 * 
 * @author James Creel
 * @author Jeremy Huff
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

    @Autowired
    private ActionLogRepo actionLogRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
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
            boolean expansive = false;

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
                case "expansive":
                    expansive = !expansive;
                    System.out.println("\nSet expansive = " + (expansive ? "true" : "false") + ".");
                    break;

                case "accounts":
                    int acct = 0;
                    if (commandArgs.size() > 0) {
                        try {
                            acct = Integer.parseInt(commandArgs.get(0));
                        } catch (Exception e) {
                            System.err.println("unable to parse as a number of items: " + commandArgs.get(0));
                        }
                    }
                    for (int i = 0; i < acct; i++) {
                        String enc_pwd = passwordEncoder.encode("password");
                        User testacct = userRepo.create("student" + (i + 1) + "@example.com", "student" + (i + 1), "example", enc_pwd, Role.ROLE_STUDENT);
                        System.out.println("Creating account with email " + testacct.getEmail() + " with role ROLE_STUDENT");
                        userRepo.saveAndFlush(testacct);
                        testacct = userRepo.create("reviewer" + (i + 1) + "@example.com", "reviewer" + (i + 1), "example", enc_pwd, Role.ROLE_REVIEWER);
                        System.out.println("Creating account with email " + testacct.getEmail() + " with role ROLE_REVIEWER");
                        userRepo.saveAndFlush(testacct);
                        testacct = userRepo.create("manager" + (i + 1) + "@example.com", "", "manager" + (i + 1), enc_pwd, Role.ROLE_MANAGER);
                        System.out.println("Creating account with email " + testacct.getEmail() + " with role ROLE_MANAGER");
                        userRepo.saveAndFlush(testacct);
                    }
                    break;

                case "admin_accounts":
                    int admin_acct = 0;
                    if (commandArgs.size() > 0) {
                        try {
                            admin_acct = Integer.parseInt(commandArgs.get(0));
                        } catch (Exception e) {
                            System.err.println("unable to parse as a number of items: " + commandArgs.get(0));
                        }
                    }
                    for (int i = 0; i < admin_acct; i++) {
                        String enc_pwd = passwordEncoder.encode("password");
                        User testacct = userRepo.create("admin" + (i + 1) + "@example.com", "", "admin" + (i + 1), enc_pwd, Role.ROLE_ADMIN);
                        System.out.println("Creating account with email " + testacct.getEmail() + " with role ROLE_ADMIN");
                        userRepo.saveAndFlush(testacct);
                    }
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

                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                    SimpleDateFormat emailDate = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss_");

                    int idOffset = userRepo.findAll().toArray().length;

                    for (int i = itemsGenerated; i < num + itemsGenerated; i++) {
                        Calendar now = Calendar.getInstance();
                        User submitter = userRepo.create("bob" + emailDate.format(now.getTime()) + (idOffset + i + 1) + "@boring.bob", "bob", "boring", Role.ROLE_STUDENT);
                        Credentials credentials = new Credentials();
                        credentials.setFirstName("Bob");
                        credentials.setLastName("Boring");
                        credentials.setEmail("bob@boring.bob");
                        credentials.setRole(Role.ROLE_STUDENT.name());

                        Submission sub = submissionRepo.create(submitter, org, state, credentials);

                        sub.setSubmissionDate(getRandomDate());
                        sub.setApproveAdvisorDate(getRandomDate());
                        sub.setApproveApplicationDate(getRandomDate());
                        sub.setApproveEmbargoDate(getRandomDate());

                        generateActionLogs(sub, submitter, expansive);

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
                                case "INPUT_DEGREEDATE":
                                case "INPUT_DATETIME":
                                    val = fieldValueRepo.create(pred);
                                    Calendar calendar = getRandomDate();
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

    private Calendar getRandomDate() {
        Random random = new Random();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.YEAR, -random.nextInt(10));
        int rm = random.nextInt(10);
        if (random.nextInt(2) == 2) {
            rm = -rm;
        }
        date.add(Calendar.MONTH, rm);
        date.add(Calendar.DATE, random.nextInt(32 - date.get(Calendar.DAY_OF_MONTH)));
        return date;
    }

    private void generateActionLogs(Submission sub, User submitter, boolean expansive) {
        actionLogRepo.create(sub, submitter, Calendar.getInstance(), new String("Submission created."), false);

        // Only provide large data set when expansive parameter is provided.
        if (!expansive) {
            return;
        }

        int random = new Random().nextInt(10);

        // %20 chance to only have the created log.
        if (random < 2) {
            return;
        }

        // %60 chance to have a small random amount of logs.
        int total = new Random().nextInt(20) + 1;
        boolean isPrivate = false;
        boolean bySubmitter = true;
        String percent = "[60%] ";

        // %20 chance to have a large random amount of logs.
        if (random > 7) {
            total = new Random().nextInt(500) + 1;
            percent = "[20%] ";
        }

        random = total;

        if (total > 1) {
            System.out.println("\rGenerating expansive submission action log with " + total + " additional logs for submission " + sub.getId() + ".");
        }

        while (--random > 0) {
            // Use ~%22 chance of private.
            isPrivate = new Random().nextInt(9) < 2 ? true : false;

            // %15 chance to not be by submitter.
            bySubmitter = new Random().nextInt(20) > 2;

            if (bySubmitter) {
                actionLogRepo.create(
                    sub,
                    submitter,
                    Calendar.getInstance(),
                    new String(percent + random + " of " + total + (isPrivate ? " [private]" : "") + "."),
                    isPrivate
                );
            } else {
                actionLogRepo.create(
                    sub,
                    Calendar.getInstance(),
                    new String(percent + random + " of " + total + (isPrivate ? " [private]" : "") + " [no submitter]."),
                    isPrivate
                );
            }
        }
    }

}
