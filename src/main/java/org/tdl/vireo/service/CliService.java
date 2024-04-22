package org.tdl.vireo.service;

import edu.tamu.weaver.auth.model.Credentials;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsException;
import org.tdl.vireo.model.CustomActionDefinition;
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
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;
import org.tdl.vireo.model.repo.UserRepo;

@Transactional
@Service
public class CliService {

    public static final SimpleDateFormat FORMAT_DAY = new SimpleDateFormat("yyyy-MM-dd");

    public static final SimpleDateFormat FORMAT_MONTH = new SimpleDateFormat("MMMM yyyy");

    public static final SimpleDateFormat EMAIL_DATE = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss_");

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
    private CustomActionDefinitionRepo customActionDefinitionRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void operateAccounts(boolean expansive, int generateTotal) {
        for (int i = 0; i < generateTotal; i++) {
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
    }

    public void operateAdminAccounts(boolean expansive, int generateTotal) {
        for (int i = 0; i < generateTotal; i++) {
            String enc_pwd = passwordEncoder.encode("password");
            User testacct = userRepo.create("admin" + (i + 1) + "@example.com", "", "admin" + (i + 1), enc_pwd, Role.ROLE_ADMIN);
            System.out.println("Creating account with email " + testacct.getEmail() + " with role ROLE_ADMIN");
            userRepo.saveAndFlush(testacct);
        }
    }

    public void operateGenerate(boolean expansive, int maxActionLogs, Random random, int idOffset, User helpfulHarry, int i) throws OrganizationDoesNotAcceptSubmissionsException {
        Calendar now = Calendar.getInstance();
        User submitter = userRepo.create("bob" + EMAIL_DATE.format(now.getTime()) + (idOffset + i + 1) + "@boring.bob", "bob", "boring " + (idOffset + i + 1), Role.ROLE_STUDENT);
        Credentials credentials = new Credentials();
        credentials.setFirstName("Bob");
        credentials.setLastName("Boring " + (idOffset + i + 1));
        credentials.setEmail("bob@boring.bob");
        credentials.setRole(Role.ROLE_STUDENT.name());

        Organization org = getOrganization(0);
        setAcceptSubmissions(org);

        List<SubmissionStatus> statuses = getAllSubmissionStatuses();
        SubmissionStatus state = statuses.get(0);

        // Status is chosen completely randomly for every option available when expansive is enabled.
        if (expansive) {
            state = statuses.get(random.nextInt(statuses.size()));
        }

        List<CustomActionDefinition> customActions = customActionDefinitionRepo.findAll();

        Submission sub = submissionRepo.create(submitter, org, state, credentials, customActions);

        sub.setSubmissionDate(getRandomDate());

        if (random.nextInt(10) < 3) {
            sub.setApproveAdvisorDate(getRandomDate());
            sub.setApproveAdvisor(random.nextInt(10) < 5);
        } else {
            sub.setApproveAdvisorDate(null);
            sub.setApproveAdvisor(false);
        }

        if (random.nextInt(10) < 3) {
            sub.setApproveApplicationDate(getRandomDate());
            sub.setApproveApplication(random.nextInt(10) < 5);
        } else {
            sub.setApproveApplicationDate(null);
            sub.setApproveApplication(false);
        }

        if (random.nextInt(10) < 3) {
            sub.setApproveEmbargoDate(getRandomDate());
            sub.setApproveEmbargo(random.nextInt(10) < 5);
        } else {
            sub.setApproveEmbargoDate(null);
            sub.setApproveEmbargo(false);
        }

        // 30% chance to be assigned to helpful harry.
        if (expansive && random.nextInt(10) < 3) {
            sub.setAssignee(helpfulHarry);
        }

        generateActionLogs(sub, submitter, expansive, maxActionLogs);

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
                    val = fieldValueRepo.create(pred);
                    val.setValue(FORMAT_MONTH.format(getRandomDegreeDate().getTime()));
                    sub.addFieldValue(val);
                    break;
                case "INPUT_DATE":
                    val = fieldValueRepo.create(pred);
                    val.setValue(FORMAT_DAY.format(getRandomDate().getTime()));
                    sub.addFieldValue(val);
                    break;
                default:
                    // Allow for a small number of unfilled field values.
                    if (random.nextInt(10) > 8) break;

                    val = fieldValueRepo.create(pred);
                    if (pred.getValue().equalsIgnoreCase("birth_year")) {
                        val.setValue(getRandomYearString(80));
                    } else {
                        val.setValue("test " + pred.getValue() + " " + i);
                    }
                    sub.addFieldValue(val);
                }
            }
        }

        submissionRepo.saveAndFlush(sub);
    }

    public Organization getOrganization(int index) {
        return organizationRepo.findAll().get(index);
    }

    public int countUsers() {
        return userRepo.findAll().toArray().length;
    }

    public void setAcceptSubmissions(Organization organization) {
        if (!organization.getAcceptsSubmissions()) {
            organization.setAcceptsSubmissions(true);
            organization = organizationRepo.save(organization);
        }
    }

    public List<SubmissionStatus> getAllSubmissionStatuses() {
        return submissionStatusRepo.findAll();
    }

    public User createHelpfulHarry(int offset, SimpleDateFormat formatter) {
        String dateString = formatter.format(Calendar.getInstance().getTime());
        return userRepo.create("harry" + dateString + offset + "@help.ful", "Harry", "Helpful " + offset, Role.ROLE_REVIEWER);
    }

    private Calendar getRandomDate() {
        Random random = new Random();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.YEAR, -random.nextInt(10));

        int rm = random.nextInt(10);
        if (random.nextInt(2) == 2) {
            rm = -rm;
        }

        date.set(Calendar.MONTH, rm);
        date.set(Calendar.DATE, random.nextInt(32 - date.get(Calendar.DAY_OF_MONTH)));
        return date;
    }

    private Calendar getRandomDegreeDate() {
        Random random = new Random();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.YEAR, -random.nextInt(10));

        int rm = random.nextInt(3);
        if (rm == 0) {
            rm = Calendar.MAY;
        } else if (rm == 1) {
            rm = Calendar.AUGUST;
        } else {
            rm = Calendar.DECEMBER;
        }

        date.set(Calendar.MONTH, rm);
        date.set(Calendar.DATE, random.nextInt(28) + 1);
        return date;
    }

    private String getRandomYearString(int max) {
        Random random = new Random();
        Calendar date = Calendar.getInstance();

        return "" + (date.get(Calendar.YEAR) - random.nextInt(max));
    }

    private void generateActionLogs(Submission sub, User submitter, boolean expansive, int maxActionLogs) {
        actionLogRepo.create(sub, submitter, Calendar.getInstance(), new String("Submission created."), false);

        // Only provide large data set when expansive parameter is provided.
        if (!expansive) {
            return;
        }

        int random = new Random().nextInt(10);

        // %20 chance to only have the created log.
        if (random < 2) {
            System.out.println("\rGenerating expansive submission without additional logs for submission " + sub.getId() + ".");
            return;
        }

        // %60 chance to have a small random amount of logs.
        int total = new Random().nextInt(20 < maxActionLogs ? 20 : maxActionLogs);
        boolean isPrivate = false;
        boolean bySubmitter = true;
        String percent = "[60%] ";

        // %20 chance to have a large random amount of logs.
        if (random > 7) {
            total = new Random().nextInt(maxActionLogs);
            percent = "[20%] ";
        }

        random = total;
        System.out.println("\rGenerating expansive submission action log with " + total + " additional logs for submission " + sub.getId() + ".");

        while (random-- > 0) {
            // Use ~%22 chance of private.
            isPrivate = new Random().nextInt(9) < 2 ? true : false;

            // %15 chance to not be by submitter.
            bySubmitter = new Random().nextInt(20) > 2;

            if (bySubmitter) {
                actionLogRepo.create(
                    sub,
                    submitter,
                    Calendar.getInstance(),
                    new String(percent + (random + 1) + " of " + total + (isPrivate ? " [private]" : "") + "."),
                    isPrivate
                );
            } else {
                actionLogRepo.create(
                    sub,
                    Calendar.getInstance(),
                    new String(percent + (random + 1) + " of " + total + (isPrivate ? " [private]" : "") + " [no submitter]."),
                    isPrivate
                );
            }
        }
    }
}
