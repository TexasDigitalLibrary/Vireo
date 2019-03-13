/*[- */
    This is the MARC 21 export format. The leader, control fields and data fields
    were all set up originaly for Texas A&M University. Some variables can and
    should be customized for your institution.

    If adding additional control fields or data fields use the following format.	
    *********************************************************************************
    /*[- Control Field 040 -]*/
    [(${marcBuilder.addField("040", "  \u001FaTXA\u001FcTXA\u001E")})]
    *********************************************************************************

    Where the first parameter is the tag and the second is the text value.

    For repeated fields use the following format.
    *********************************************************************************
    /*[- Control Field 653 -]*/
    [# th:each="kw : ${KEYWORDS}"]
    [(${marcBuilder.addField("653", "  \u001Fa" + kw.getValue() + "\u001E")})]
    [/]
    *********************************************************************************

    The last line of the sample code should never be changed.

    Delimiters should be entered as "\u001F" without the quotes.
    Field Terminators should be entered as "\u001E" without the quotes.
    Record Terminators should be entered as "\u001D" without the quotes.

    For more information on MARC follow these links:
    http://www.loc.gov/marc/specifications/spechome.html
    http://www.loc.gov/marc/umb/um11to12.html

/* -]*/

[(${marcBuilder.setLeader("RLXXXnam a22BADXXKa 4500")})]

/*[- Control Field 006 -]*/
[(${marcBuilder.addField("006", "m    f   d        \u001E")})]

/*[- Control Field 007 -]*/
[(${marcBuilder.addField("007", "cr unu||||||||\u001E")})]

/*[- Control Field 008 -]*/
[(${marcBuilder.addField("008", "      s" + SUBMITTER_GRADUATION_YEAR + "    txu|||||obm   000 0|eng d\u001E")})]

/*[- Control Field 035 -]*/
/*[- [(${marcBuilder.addField("035", "  \u001Fa(TxCM)" + DEPOSIT_URL + "\u001E")})] -]*/

/*[- Control Field 040 -]*/
[(${marcBuilder.addField("040", "  \u001FaTXA\u001FcTXA\u001E")})]

/*[- Control Field 100 -]*/
[(${marcBuilder.addField("100", "1 \u001Fa" + SUBMITTER_FULL_NAME + ".\u001E")})]

/*[- Control Field 245 -]*/
[(${marcBuilder.addField("245", "1" + TITLE_IND2 + "\u001Fa" + TITLE + "\u001Fh[electronic resource] /\u001Fcby " + SUBMITTER_SHORT_NAME + ".\u001E")})]

/*[- Control Field 260 -]*/
[(${marcBuilder.addField("260", "  \u001Fa[College Station, Tex. :\u001Fb" + GRANTOR + ",\u001Fc" + SUBMITTER_GRADUATION_YEAR + "]\u001E")})]

/*[- Control Field 300 -]*/
[(${marcBuilder.addField("300", "  \u001Fa1 online resource.\u001E")})]

/*[- Control Field 500 -]*/
[(${marcBuilder.addField("500", '  \u001Fa"Major Subject: ' + MAJOR + '"\u001E')})]

/*[- Control Field 502 -]*/
[(${marcBuilder.addField("502", "  \u001Fa" + DEGREE_NAME + "\u001Fc" + GRANTOR + "\u001Fd" + SUBMITTER_GRADUATION_YEAR + "\u001Fo" + DEPOSIT_URL + "\u001E")})]

/*[- Control Field 504 -]*/
[(${marcBuilder.addField("504", "  \u001FaIncludes bibliographical references.\u001E")})]

/*[- Control Field 516 -]*/
[(${marcBuilder.addField("516", "  \u001FaText (" + PRIMARY_DOCUMENT_MIMETYPE + ")\u001E")})]

/*[- Control Field 520 -]*/
[(${marcBuilder.addField("520", "3 \u001Fa" + ABSTRACT + "       The electronic version of this dissertation is accessible from " + DEPOSIT_URL + "\u001E")})]

/*[- Control Field 588 -]*/
[(${marcBuilder.addField("588", "  \u001FaDescription from author supplied metadata(automated record created " + TIME + ").\u001E")})]

/*[- Control Field 650 -]*/
[(${marcBuilder.addField("650", " 4\u001FaMajor " + MAJOR + "\u001E")})]

/*[- Control Field 653 -]*/
[# th:each="kw : ${KEYWORDS}"]
[(${marcBuilder.addField("653", "  \u001Fa" + kw.getValue() + "\u001E")})]
[/]

/*[- Control Field 856 -]*/
[(${marcBuilder.addField("856", "40\u001Fu" + DEPOSIT_URL + "\u001FzConnect to the full text of this online resource.\u001E")})]


/*[- Output record. Do not modify -]*/
[(${marcBuilder.toString()})]