package org.tdl.vireo.cli;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.UserRepo;

import edu.tamu.framework.model.Credentials;
@Component
public class Cli implements CommandLineRunner {
	
	@Autowired
	SubmissionRepo submissionRepo;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	OrganizationRepo organizationRepo;
	
	@Autowired
	SubmissionStateRepo submissionStateRepo;
	
	@Autowired
	FieldValueRepo fieldValueRepo;
	
	
	@Override
	public void run(String... arg0) throws Exception {
		final String PROMPT = "\n"+(char)27 + "[36mvireo>"+(char)27 + "[37m ";
		
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		Boolean running = true;

		System.out.print(PROMPT);

		while(reader.hasNextLine() && running) {			
			
			String n = reader.nextLine();			
			
			int num = 10350;
			
			n=n.trim();
			
			switch (n) {
				case "exit":
					System.out.println("\nGoodbye.");
					running=false;
					break;
				
				case "generate items":
					
					Organization org = organizationRepo.findAll().get(0);
					SubmissionState state = submissionStateRepo.findAll().get(0);
					
					for(int i = 1; i <= num; i++) {
						User submitter = userRepo.create("bob" + i + "@boring.bob", "bob", "boring", AppRole.STUDENT);
						Credentials credentials = new Credentials();
						credentials.setFirstName("Bob");
						credentials.setLastName("Boring");
						credentials.setEmail("bob@boring.bob");
						credentials.setRole("bore");
						
						Submission sub = submissionRepo.create(submitter, org, state, credentials);
						for(SubmissionWorkflowStep step : sub.getSubmissionWorkflowSteps() ) {
							for(SubmissionFieldProfile fp : step.getAggregateFieldProfiles()) {
								FieldPredicate pred = fp.getFieldPredicate();
								if(! pred.getDocumentTypePredicate()) {
									FieldValue val = fieldValueRepo.create(pred);
									val.setValue("test value " + i);
									sub.addFieldValue(val);
								}
							}
							
						}
						submissionRepo.save(sub);
						System.out.print(".");
						
					}
					
					System.out.println("\nGenerated " + num + " submissions.");
					break;
					
				case "": 
					break;

				default:
					System.out.println("Unknown command " + n);
							
				}
			System.out.print(PROMPT);
		}
	}
}
