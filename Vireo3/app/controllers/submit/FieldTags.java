package controllers.submit;


import groovy.lang.Closure;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tdl.vireo.constant.FieldConfig;
import org.tdl.vireo.model.SettingsRepository;


import play.data.validation.Validation;
import play.exceptions.TagInternalException;
import play.exceptions.TemplateExecutionException;
import play.modules.spring.Spring;
import play.templates.FastTags;
import play.templates.JavaExtensions;
import play.templates.TagContext;
import play.templates.GroovyTemplate.ExecutableTemplate;

/**
 * These are a set of Play template tags that are used to easily access
 * information about the configuration of a particular field.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public class FieldTags extends FastTags {

	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);

	/**
	 * Conditional tag to check if at least one of the passed fields is enabled.
	 * You may pas either a list of fields, or just one field.
	 * 
	 * #{ifEnabled [STUDENT_FIRST_NAME,STUDENT_MIDDLE_NAME,STUDENT_LAST_NAME]}
	 *     ... do something if enabled ... 
	 * #{/if} 
	 * #{else}
	 *     ... do something else ...
	 * #{/else}
	 * 
	 */
	public static void _ifEnabled(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) {

		// Get the list of field arguments
		Object arg = args.get("arg");
		List fields;
		if (arg instanceof FieldConfig) {
			fields = new ArrayList();
			fields.add(arg);
		} else if (arg instanceof ArrayList) {		
			fields = (List) arg;
		} else {
			throw new TemplateExecutionException(template.template, fromLine, "Wrong parameter type, try #{ifEnabled [field1,field2] }", new TagInternalException("Wrong parameter type"));
		}

		// If enabled
		if (isEnabled(template, fromLine, fields.toArray())) {
			body.call();
			TagContext.parent().data.put("_executeNextElse", false);
		} else {
			TagContext.parent().data.put("_executeNextElse", true);
		}
	}
	
	/**
	 * Conditional tag to check if all of the passed fields are required. You
	 * may pas either a list of fields, or just one field.
	 * 
	 * #{ifRequired [STUDENT_FIRST_NAME,STUDENT_MIDDLE_NAME,STUDENT_LAST_NAME]}
	 *     ... do something if all required ... 
	 * #{/if}
	 * #{else}
	 *     ... do something else ...
	 * #{/else}
	 * 
	 */
	public static void _ifRequired(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) {

		// Get the list of field arguments
		Object arg = args.get("arg");
		List fields;
		if (arg instanceof FieldConfig) {
			fields = new ArrayList();
			fields.add(arg);
		} else if (arg instanceof ArrayList) {
			fields = (List) arg;
		} else {
			throw new TemplateExecutionException(template.template, fromLine, "Wrong parameter type, try #{ifRequired [field1,field2] }", new TagInternalException("Wrong parameter type"));

		}
		
		// If required
		if (isRequired(template, fromLine, fields.toArray())) {
			body.call();
			TagContext.parent().data.put("_executeNextElse", false);
		} else {
			TagContext.parent().data.put("_executeNextElse", true);
		}
	}


	/**
	 * Display the enabled class for this field, either "required", "optional",
	 * or "disabled".
	 * 
	 * #{fieldClass STUDENT_FIRST_NAME /}
	 * 
	 */
	public static void _fieldClass(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) {
		
		// Get the field
		Object arg = args.get("arg");
		FieldConfig field;
		if (arg instanceof FieldConfig)
			field = (FieldConfig) arg;
		else
			throw new TemplateExecutionException(template.template, fromLine, "Wrong parameter type, try #{fieldClass field/}", new TagInternalException("Wrong parameter type"));

		// Display the class
		if (isRequired(template, fromLine, field))
			out.print("required");
		else if (isEnabled(template, fromLine, field))
			out.print("optional");
		else
			out.print("disabled");
	}
	
	/**
	 * Display the label text of the field.
	 * 
	 * #{fieldLabel STUDENT_FIRST_NAME /}
	 * 
	 */
	public static void _fieldLabel(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) {
		
		// Get the field
		Object arg = args.get("arg");
		FieldConfig field;
		if (arg instanceof FieldConfig)
			field = (FieldConfig) arg;
		else
			throw new TemplateExecutionException(template.template, fromLine, "Wrong parameter type, try #{fieldLabel field/}", new TagInternalException("Wrong parameter type"));

		// Display the label
		out.print(settingRepo.getConfigValue(field.LABEL));
	}
	
	/**
	 * Display the help text of the field.
	 * 
	 * #{fieldHelp STUDENT_FIRST_NAME /}
	 * 
	 */
	public static void _fieldHelp(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) {
		
		// Get the field
		Object arg = args.get("arg");
		FieldConfig field;
		if (arg instanceof FieldConfig)
			field = (FieldConfig) arg;
		else
			throw new TemplateExecutionException(template.template, fromLine, "Wrong parameter type, try #{fieldHelp field/}", new TagInternalException("Wrong parameter type"));

		// Display the help text
		out.print(settingRepo.getConfigValue(field.HELP));
	}
	
	
	/**
	 * Override the default errorClass tag that comes with Play to use the
	 * "error" class instead of "hasError" class.
	 */
    public static void _errorClass(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) {
        if (args.get("arg") == null) {
            throw new TemplateExecutionException(template.template, fromLine, "Please specify the error key", new TagInternalException("Please specify the error key"));
        }
        if (Validation.hasError(args.get("arg").toString())) {
            out.print("error");
        }
    }
	
	
	
	/**
	 * Internal method to check that all the passed fields are set to be
	 * required.
	 * 
	 * @param fields
	 *            The fields to check.
	 * @return True if all fields are marked as required, otherwise false.
	 */
	protected static boolean isRequired(ExecutableTemplate template, int fromLine, Object ... fields) {
		
		for (Object obj : fields) {
			if (!(obj instanceof FieldConfig)) 
				throwTypeError(template, fromLine, fields);
						
			FieldConfig field = (FieldConfig) obj;
			if (!"required".equals(settingRepo.getConfigValue(field.ENABLED))) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Internal method to check that at least one of the passed fields is
	 * enabled.
	 * 
	 * @param fields
	 *            The fields to check.
	 * @return True if at least one field is enabled, otherwise fales.
	 */
	protected static boolean isEnabled(ExecutableTemplate template, int fromLine, Object ... fields) {
		
		for (Object obj : fields) {
			if (!(obj instanceof FieldConfig))
				throwTypeError(template, fromLine, fields);
			
			FieldConfig field = (FieldConfig) obj;
			if (!"disabled".equals(settingRepo.getConfigValue(field.ENABLED))) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Internal method to throw a type exception when one of the objects is not a FieldConfig.
	 * 
	 */
	private static void throwTypeError(ExecutableTemplate template, int fromLine, Object ... objects) {
		
		StringBuilder types = new StringBuilder("[");
		for (Object obj : objects) {
			
			if (types.length() != 1)
				types.append(",");
			
			if (obj == null) {
				types.append("null");
			} else {
				types.append(obj.getClass().getName());
			}
		}
		types.append("]");
		
		throw new TemplateExecutionException(template.template, fromLine, "Wrong parameter type, one or more of the arguments are not FieldConfig objects: "+types.toString(), new TagInternalException("Wrong parameter type"));
	}
	
}
