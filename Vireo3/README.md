# Vireo Electronic Thesis and Dissertation Management System #

## About Vireo  ##

Vireo is a turnkey Electronic Thesis and Dissertation (ETD) Management System
addressing all steps of the ETD process, from submission to publication to 
preservation. Vireo provides students the ability to submit their digital
theses and dissertations via a simple online interface. Graduate offices can
use Vireo to manage the ETD submission and approval process. 

The project is organized by the [Texas Digital Library](https://www.tdl.org/)
in collaboration with [Texas A&M University](http://www.tamu.edu/),
[University of Illinois at Urbana-Champaign](http://illinois.edu/), and
[Massachusetts Institute of Technology](http://web.mit.edu/).

For more information on Vireo, visit the 
[Github Project page for Vireo](https://github.com/TexasDigitalLibrary/Vireo)
OR
[Github Project wiki for Vireo](https://github.com/TexasDigitalLibrary/Vireo/wiki)
OR
[Github page for Vireo](http://texasdigitallibrary.github.io/Vireo/)

## Vireo 3.0 - What's New ##

Vireo 3.0 is major feature upgrade to Vireo. It builds upon the past success of 
2.0 and the Java Play Framework to enable new features. Here are some highlights

**New Fields:**
- ORCID
- ProQuest Embargoes

**New Features:**
- Revised Needs Corrections workflow: Vireo 3 includes a more intuitive student workflow for submitting corrected manuscripts, including better validations to minimize student errors.
- Email: Vireo 3 adds the ability to set up rules for sending automated emails to configurable groups of stakeholders.
- Reporting and Exports: Vireo 3 allows administrative users to create custom, exportable Excel reports by using saved filters and columns. It also includes the addition of the Action Log to a file export package.
- Multiple submissions: Vireo 3 overhauls the functionality for allowing multiple submissions by a single student. Specifically, it adds more sophisticated validations to prevent students from creating a new submission for the same degree, while allowing legitimate multiple submissions (for different degrees) by the same student.
- Embargoes: Vireo 3 adds a separate, optional embargo period for submissions going to ProQuest.
- Custom Action Checklist: In Vireo 3, administrative users have the ability to filter an ETD list by individual Custom Actions. Additionally, Vireo 3 can be configured to display certain Checklist items in the student view.

**New Settings:**
- Email "From" and "ReplyTo" are now in administrative settings instead of application.conf
- Deposit locations can now be configured to have a timeout (defaulted to 60 seconds, used to be 10 seconds) for the SWORD client when depositing large items into DSpace
- Email Workflow Rules
- Administrative Groups
- Separate "Default" and "ProQuest" embargoes

**Other New Features:**
- An ADA-compliant student submission interface
- Several bug fixes

## Building and Deploying Vireo ##

Refer to [the wiki pages](https://github.com/TexasDigitalLibrary/Vireo/wiki) 
for instructions on deploying Vireo from scratch or updating a previous release. 

## License and Copyright ##

Copyright (c) 2015, Texas Digital Library
All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions 
are met:

- Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

- Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation 
  and/or other materials provided with the distribution.

- Neither the name of the Texas Digital Library nor the names of its
  contributors may be used to endorse or promote products derived from this
  software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
