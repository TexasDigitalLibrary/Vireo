package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        render("v1.html");
    }
    public static void submitIndex() {
        render("v2.html");
    }
    
    public static void submitLicense() {
        render("v3.html");
    }
    
    public static void submitDocInfo() {
        render("v4.html");
    }
    public static void submitFileUpload() {
        render("v5.html");
    }    
}