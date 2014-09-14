package controllers.submit;

import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import org.apache.commons.lang.StringUtils;
import org.tdl.vireo.model.Submission;
import play.Logger;
import play.Play;
import play.data.validation.Validation;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * This validator is used to report any errors with PDF submission files (primary documents only).
 * Currently it reports any non-embedded fonts. This is typically a ProQuest requirement (and of
 * interest to those requiring an archival quality PDF).
 *
 * To enable this validator add the following to your application.conf:
 *   pdf.validate = true
 *   pdf.validate.help = optional instructions to users on how to remedy PDF errors
 *
 * Also, the iText PDF library must be included in your dependencies.yml:
 *   - com.itextpdf -> itextpdf 5.1.3
 *
 * @author william_mckinney@harvard.edu
 *
 */
public final class PdfValidator {

    private PdfValidator() { }

    /**
     *
     * Perform PDF validation and add any errors to the validation object and action log.
     *
     * @param sub Submission to validate
     * @param validation Play validation object
     */
    public static void doValidation(Submission sub, Validation validation) {

        try {

            Set<String> notEmbedded = PdfValidator.listFonts(sub.getPrimaryDocument().getFile().getAbsolutePath());

            StringBuilder sb = new StringBuilder();
            if (notEmbedded.size() > 0) {

                sb.append("One or more fonts used in your PDF are missing: ");
                sb.append(StringUtils.join(notEmbedded, ","));
                sub.logAction("PRIMARY FILE has PDF errors: " + sb.toString());

                // add any help message
                String helpMsg = Play.configuration.getProperty("pdf.validate.help", "false");
                if (null != helpMsg && helpMsg.length() > 0) {
                    sb.append(helpMsg);
                }

                validation.addError("nonEmbeddedFontsFound", sb.toString());

                // delete the document
                sub.getPrimaryDocument().delete();

            }

        } catch (IOException ioe) {
            Logger.error("Error trying to check for embedded fonts in PDF file: " + ioe.getMessage());
        }

    }

    /**
     * Creates a set containing information about the not-embedded fonts within the src PDF file.
     * @param src the path to a PDF file
     * @throws IOException
     */
    public static Set<String> listFonts(String src) throws IOException {
        Set<String> set = new TreeSet<String>();
        PdfReader reader = new PdfReader(src);
        PdfDictionary resources;
        for (int k = 1; k <= reader.getNumberOfPages(); ++k) {
            resources = reader.getPageN(k).getAsDict(PdfName.RESOURCES);
            processResource(set, resources);
        }
        reader.close();
        return set;
    }

    /**
     * Finds out if the font is an embedded subset font
     * @param name name
     * @return true if the name denotes an embedded subset font
     */
    private static boolean isEmbeddedSubset(String name) {

        return name != null && name.length() > 8 && name.charAt(7) == '+';

    }

    /**
     *
     * @param font PDF fon
     * @param set set with the font names
     */
    private static void processFont(PdfDictionary font, Set<String> set) {
        String name = font.getAsName(PdfName.BASEFONT).toString();
        if(isEmbeddedSubset(name))
            return;

        PdfDictionary desc = font.getAsDict(PdfName.FONTDESCRIPTOR);

        //nofontdescriptor
        if (desc == null) {
            PdfArray descendant = font.getAsArray(PdfName.DESCENDANTFONTS);

            if (descendant == null) {
                set.add(name.substring(1));
            }
            else {
                for (int i = 0; i < descendant.size(); i++) {
                    PdfDictionary dic = descendant.getAsDict(i);
                    processFont(dic, set);
                }
            }
        }
        /**
         * (Type 1) embedded
         */
        else if (desc.get(PdfName.FONTFILE) != null);
        /**
         * (TrueType) embedded
         */
        else if (desc.get(PdfName.FONTFILE2) != null);
        /**
         * " (" + font.getAsName(PdfName.SUBTYPE).toString().substring(1) + ") embedded"
         */
        else if (desc.get(PdfName.FONTFILE3) != null);
        else {
            set.add(name.substring(1));
        }
    }

    /**
     * Extracts the names of the not-embedded fonts from page or XObject resources.
     * @param set the set with the font names
     * @param resource the resources dictionary
     */
    public static void processResource(Set<String> set, PdfDictionary resource) {
        if (resource == null)
            return;
        PdfDictionary xobjects = resource.getAsDict(PdfName.XOBJECT);
        if (xobjects != null) {
            for (PdfName key : xobjects.getKeys()) {
                processResource(set, xobjects.getAsDict(key));
            }
        }
        PdfDictionary fonts = resource.getAsDict(PdfName.FONT);
        if (fonts == null)
            return;
        PdfDictionary font;
        for (PdfName key : fonts.getKeys()) {
            font = fonts.getAsDict(key);
            processFont(font, set);
        }
    }

}