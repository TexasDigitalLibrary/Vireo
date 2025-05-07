package org.tdl.vireo.service;

import edu.tamu.weaver.auth.model.Credentials;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsException;
import org.tdl.vireo.model.Action;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.VocabularyWord;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;
import org.tdl.vireo.model.repo.DegreeRepo;
import org.tdl.vireo.model.repo.EmbargoRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStatusRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.VocabularyWordRepo;

@Transactional
@Service
public class CliService {

    public static final SimpleDateFormat FORMAT_DAY = new SimpleDateFormat("yyyy-MM-dd");

    public static final SimpleDateFormat FORMAT_MONTH = new SimpleDateFormat("MMMM yyyy");

    public static final SimpleDateFormat EMAIL_DATE = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss_");

    public static final String AT_ADDRESS = "@localhost.localdomain";

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FieldValueRepo fieldValueRepo;

    @Autowired
    private SubmissionRepo submissionRepo;

    @Autowired
    private LanguageRepo languageRepo;

    @Autowired
    private EmbargoRepo embargoRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private SubmissionStatusRepo submissionStatusRepo;

    @Autowired
    private ActionLogRepo actionLogRepo;

    @Autowired
    private CustomActionDefinitionRepo customActionDefinitionRepo;

    @Autowired
    private VocabularyWordRepo vocabularyWordRepo;

    @Autowired
    private DegreeRepo degreeRepo;

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void operateAccounts(boolean expansive, int generateTotal) {
        for (int i = 0; i < generateTotal; i++) {
            String enc_pwd = passwordEncoder.encode("password");
            User testacct = userRepo.create("student" + (i + 1) + AT_ADDRESS, "student" + (i + 1), "example", enc_pwd, Role.ROLE_STUDENT);
            System.out.println("Creating account with email " + testacct.getEmail() + " with role ROLE_STUDENT");
            userRepo.saveAndFlush(testacct);
            testacct = userRepo.create("reviewer" + (i + 1) + AT_ADDRESS, "reviewer" + (i + 1), "example", enc_pwd, Role.ROLE_REVIEWER);
            System.out.println("Creating account with email " + testacct.getEmail() + " with role ROLE_REVIEWER");
            userRepo.saveAndFlush(testacct);
            testacct = userRepo.create("manager" + (i + 1) + AT_ADDRESS, "", "manager" + (i + 1), enc_pwd, Role.ROLE_MANAGER);
            System.out.println("Creating account with email " + testacct.getEmail() + " with role ROLE_MANAGER");
            userRepo.saveAndFlush(testacct);
        }
    }

    public void operateAdminAccounts(boolean expansive, int generateTotal) {
        for (int i = 0; i < generateTotal; i++) {
            String enc_pwd = passwordEncoder.encode("password");
            User testacct = userRepo.create("admin" + (i + 1) + AT_ADDRESS, "", "admin" + (i + 1), enc_pwd, Role.ROLE_ADMIN);
            System.out.println("Creating account with email " + testacct.getEmail() + " with role ROLE_ADMIN");
            userRepo.saveAndFlush(testacct);
        }
    }

    public boolean hasSubmissionTypes() {
        return fieldValueRepo.getAllValuesByFieldPredicateValue("submission_type").size() > 0;
    }

    public void operateGenerate(boolean expansive, int maxActionLogs, Random random, long idOffset, User helpfulHarry, boolean hasSubmissionTypes, long i) throws OrganizationDoesNotAcceptSubmissionsException {
        Calendar now = Calendar.getInstance();
        User submitter = userRepo.create("bob" + EMAIL_DATE.format(now.getTime()) + (idOffset + i + 1) + "@boring.bob", "bob", "boring " + (idOffset + i + 1), Role.ROLE_STUDENT);
        Credentials credentials = new Credentials();
        credentials.setFirstName("Bob");
        credentials.setLastName("Boring " + (idOffset + i + 1));
        credentials.setEmail("bob@boring.bob");
        credentials.setRole(Role.ROLE_STUDENT.name());

        List<Organization> orgs = organizationRepo.findAll();
        List<SubmissionStatus> statuses = submissionStatusRepo.findAll();
        List<Embargo> embargoes = embargoRepo.findAll();
        List<Language> languages = languageRepo.findAll();
        List<Degree> degrees = degreeRepo.findAll();
        final List<VocabularyWord> collegesVW = new ArrayList<>();
        final List<VocabularyWord> programsVW = new ArrayList<>();
        final List<VocabularyWord> departmentsVW = new ArrayList<>();
        final List<VocabularyWord> schoolsVW = new ArrayList<>();
        final List<VocabularyWord> majorsVW = new ArrayList<>();
        final List<String> submissionTypeValues = new ArrayList<>();
        Organization org = orgs.get(getRandomNumber(organizationRepo.findAll().size()));
        SubmissionStatus state = statuses.get(0);
        setAcceptSubmissions(org);

        vocabularyWordRepo.findAll().forEach(vw -> {
            if (vw.getControlledVocabulary().getName().equalsIgnoreCase("colleges")) {
                collegesVW.add(vw);
            } else if (vw.getControlledVocabulary().getName().equalsIgnoreCase("departments")) {
                departmentsVW.add(vw);
            } else if (vw.getControlledVocabulary().getName().equalsIgnoreCase("majors")) {
                majorsVW.add(vw);
            } else if (vw.getControlledVocabulary().getName().equalsIgnoreCase("programs")) {
                programsVW.add(vw);
            } else if (vw.getControlledVocabulary().getName().equalsIgnoreCase("schools")) {
                schoolsVW.add(vw);
            }
        });

        if (hasSubmissionTypes) {
            fieldValueRepo.getAllValuesByFieldPredicateValue("submission_type").forEach((String value) -> {
                submissionTypeValues.add(value);
            });
        }

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
                    val.setContacts(Arrays.asList(new String[] { "test" + pred.getValue() + i + AT_ADDRESS }));
                    sub.addFieldValue(val);
                    break;
                case "INPUT_EMAIL":
                    val = fieldValueRepo.create(pred);
                    val.setValue("test" + pred.getValue() + i + AT_ADDRESS);
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
                    } else if (pred.getValue().equalsIgnoreCase("default_embargos") || pred.getValue().equalsIgnoreCase("proquest_embargos")) {
                        Embargo embargo = null;
                        if (embargoes.size() > 0) {
                            embargo = embargoes.get(getRandomNumber(embargoes.size()));
                        }

                        if (embargo == null) {
                            val.setValue("test " + pred.getValue() + " " + i);
                        } else {
                            val.setValue(embargo.getName());
                        }
                    } else if (pred.getValue().equalsIgnoreCase("dc.language.iso")) {
                        Language language = null;
                        if (languages.size() > 0) {
                            language = languages.get(getRandomNumber(languages.size()));
                        }

                        if (language == null) {
                            val.setValue("test " + pred.getValue() + " " + i);
                        } else {
                            val.setValue(language.getName());
                        }
                    } else if (pred.getValue().equalsIgnoreCase("thesis.degree.name")) {
                        Degree degree = null;
                        if (degrees.size() > 0) {
                            degree = degrees.get(getRandomNumber(degrees.size()));
                        }

                        if (degree == null) {
                            val.setValue("test " + pred.getValue() + " " + i);
                        } else {
                            val.setValue(degree.getName());
                        }
                    } else if (pred.getValue().equalsIgnoreCase("dc.subject") || pred.getValue().toLowerCase().startsWith("thesis.degree.")) {
                        VocabularyWord vw = null;
                        if (pred.getValue().equalsIgnoreCase("dc.subject") || pred.getValue().equalsIgnoreCase("thesis.degree.college")) {
                            if (collegesVW.size() > 0) {
                                vw = collegesVW.get(getRandomNumber(collegesVW.size()));
                            }
                        } else if (pred.getValue().equalsIgnoreCase("thesis.degree.school")) {
                            if (schoolsVW.size() > 0) {
                                vw = schoolsVW.get(getRandomNumber(schoolsVW.size()));
                            }
                        } else if (pred.getValue().equalsIgnoreCase("thesis.degree.program")) {
                            if (programsVW.size() > 0) {
                                vw = programsVW.get(getRandomNumber(programsVW.size()));
                            }
                        } else if (pred.getValue().equalsIgnoreCase("thesis.degree.department")) {
                            if (departmentsVW.size() > 0) {
                                vw = departmentsVW.get(getRandomNumber(departmentsVW.size()));
                            }
                        } else if (pred.getValue().equalsIgnoreCase("thesis.degree.major")) {
                            if (majorsVW.size() > 0) {
                                vw = majorsVW.get(getRandomNumber(majorsVW.size()));
                            }
                        }

                        if (vw == null) {
                            val.setValue("test " + pred.getValue() + " " + i);
                        } else {
                            val.setValue(vw.getName());
                        }
                    } else if (pred.getValue().equalsIgnoreCase("dc.contributor.advisor") || pred.getValue().equalsIgnoreCase("dc.contributor.committeeMember")) {
                        val = fieldValueRepo.create(pred);
                        val.setValue("test " + pred.getValue() + " " + i);
                        val.setContacts(Arrays.asList(new String[] { "test" + pred.getValue() + i + AT_ADDRESS }));
                        sub.addFieldValue(val);
                    } else if (pred.getValue().equalsIgnoreCase("submission_type")) {
                        String value = null;
                        if (submissionTypeValues.size() > 0) {
                            value = submissionTypeValues.get(getRandomNumber(submissionTypeValues.size()));
                        }

                        if (value == null) {
                            val.setValue("test " + pred.getValue() + " " + getRandomNumber(10));
                        } else {
                            val.setValue(value);
                        }
                    } else {
                        val.setValue("test " + pred.getValue() + " " + i);
                    }
                    sub.addFieldValue(val);
                }
            }
        }

        submissionRepo.saveAndFlush(sub);
    }

    public long countUsers() {
        return userRepo.count();
    }

    public void setAcceptSubmissions(Organization organization) {
        if (!organization.getAcceptsSubmissions()) {
            organization.setAcceptsSubmissions(true);
            organization = organizationRepo.save(organization);
        }
    }

    public User createHelpfulHarry(long offset, SimpleDateFormat formatter) {
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

    private int getRandomNumber(int max) {
        Random random = new Random();

        return random.nextInt(max);
    }

    private void generateActionLogs(Submission sub, User submitter, boolean expansive, int maxActionLogs) {
        actionLogRepo.create(Action.UNDETERMINED, sub, submitter, Calendar.getInstance(), new String("Submission created."), false);

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
                    Action.UNDETERMINED,
                    sub,
                    submitter,
                    Calendar.getInstance(),
                    new String(percent + (random + 1) + " of " + total + (isPrivate ? " [private]" : "") + "."),
                    isPrivate
                );
            } else {
                actionLogRepo.create(
                    Action.UNDETERMINED,
                    sub,
                    Calendar.getInstance(),
                    new String(percent + (random + 1) + " of " + total + (isPrivate ? " [private]" : "") + " [no submitter]."),
                    isPrivate
                );
            }
        }
    }
}
