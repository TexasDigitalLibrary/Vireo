package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.Header;

import java.util.*;
import java.util.Map.Entry;

import org.tdl.vireo.model.RoleType;


public class Submit extends Controller {
 
    public static void VerifyPersonalInformation() {
        render("Submit/VerifyPersonalInformation.html");
    }       
}