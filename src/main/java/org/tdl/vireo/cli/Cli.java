package org.tdl.vireo.cli;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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
		boolean runConsole = false;
		for(String s : arg0) {
			if(s.equals("console")) {
					runConsole = true;
					break;
			}
		}
		
		if(!runConsole) return;
		
		final String PROMPT = "\n"+(char)27 + "[36mvireo>"+(char)27 + "[37m ";
		
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		Boolean running = true;

		
		
		System.out.print(PROMPT);
		
		int itemsGenerated = 0;

		while(reader.hasNextLine() && running) {			
			
			String n = reader.nextLine();			
			
			n=n.trim();
			
			String[] commandTokens = n.split("\\s+");
			
			String command = null;
			String argument = null;
			int num = 0;
			
			if(commandTokens.length > 0)
				command = commandTokens[0];
			if(commandTokens.length > 1) {
				argument = commandTokens[1];
				try {
					num = Integer.parseInt(argument);
				} catch (Exception e) {
					System.err.println("unable to parse int " + argument);
				}
			}
					
			
			switch (command) {
				case "exit":
					System.out.println("\nGoodbye.");
					running=false;
					break;
				
				case "generate":
					
					Organization org = organizationRepo.findAll().get(0);
					SubmissionState state = submissionStateRepo.findAll().get(0);
					
					for(int i = itemsGenerated; i < num + itemsGenerated; i++) {
						User submitter = userRepo.create("bob" + (i+1) + "@boring.bob", "bob", "boring", AppRole.STUDENT);
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
						submissionRepo.saveAndFlush(sub);
						
						System.out.print("\r" + (i-itemsGenerated) + " of " + num + " generated...");
						
					}
					
					System.out.println("\rGenerated " + num + " submissions.");
					itemsGenerated += num;
					break;
					
				case "": 
					break;

				default:
					System.out.println("Unknown command " + n);
							
				}
			
			if(running) 
				System.out.print(PROMPT);
			
		}
		reader.close();
	}
}
