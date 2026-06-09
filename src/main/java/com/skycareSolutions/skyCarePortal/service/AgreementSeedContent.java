package com.skycareSolutions.skyCarePortal.service;

public final class AgreementSeedContent {

    public static final String SEED_CHANGE_NOTES_CONTRACTOR = "Seeded from contracts forms.txt (full)";
    public static final String SEED_CHANGE_NOTES_CASUAL = "Seeded from casual_contractor.txt (full)";
    public static final String SEED_CHANGE_NOTES_CLIENT = "Seeded from service_contract.txt (full)";
    public static final String RETIRED_WORKER_TEMPLATE_KEY = "worker_unspecified";
    private static final String LEGACY_SEED_CHANGE_NOTES = "Seeded from contracts forms.txt";

    private static final String SIGNATURE_SECTION = """
            <table class="agreement-signature-section" style="width:100%;border-collapse:collapse;border:1px solid #000;margin:16px 0;">
            <tbody>
            <tr>
            <td class="agreement-signature-column" style="width:50%;border:1px solid #000;padding:12px 14px;vertical-align:top;">
            <p style="margin:0 0 14px;"><strong><u>Employer / Organization</u></strong></p>
            <p style="margin:0 0 14px;">Name: {{org.directorName}}</p>
            <p style="margin:0 0 14px;">Date: {{signature.adminDate}}</p>
            <p style="margin:0 0 6px;">Signature:</p>
            <div class="agreement-signature-line">{{signature.admin}}</div>
            </td>
            <td class="agreement-signature-column" style="width:50%;border:1px solid #000;padding:12px 14px;vertical-align:top;">
            <p style="margin:0 0 14px;"><strong><u>Employee / Contractor</u></strong></p>
            <p style="margin:0 0 14px;">Name: {{worker.fullName}}</p>
            <p style="margin:0 0 14px;">Date: {{signature.recipientDate}}</p>
            <p style="margin:0 0 6px;">Signature:</p>
            <div class="agreement-signature-line">{{signature.recipient}}</div>
            </td>
            </tr>
            </tbody>
            </table>
            """;

    private AgreementSeedContent() {
    }

    public static boolean isLegacySeedChangeNotes(String changeNotes) {
        return LEGACY_SEED_CHANGE_NOTES.equals(changeNotes);
    }

    public static boolean isFullContractorContent(String html) {
        return html != null
                && html.contains("Service Agreement – Contractor")
                && html.contains("RISK ASSESSED ROLE");
    }

    public static boolean isFullCasualContent(String html) {
        return html != null
                && html.contains("Letter of Engagement – Casual Employees")
                && html.contains("Acceptance of Offer of Casual Employment");
    }

    public static boolean isFullClientContent(String html) {
        return html != null
                && html.contains("agreement-cover-page")
                && html.contains("4.1 Australian consumer law")
                && html.contains("Cancellation Policy");
    }

    public static boolean needsSeedContentRefresh(String templateKey, String changeNotes, String contentHtml) {
        if ("worker_contractor".equals(templateKey)) {
            if (isFullContractorContent(contentHtml)) {
                return false;
            }
            return isLegacySeedChangeNotes(changeNotes)
                    || changeNotes == null
                    || changeNotes.isBlank()
                    || SEED_CHANGE_NOTES_CONTRACTOR.equals(changeNotes)
                    || isGenericWorkerStub(contentHtml);
        }
        if ("worker_casual".equals(templateKey)) {
            if (isFullCasualContent(contentHtml)) {
                return false;
            }
            return isLegacySeedChangeNotes(changeNotes)
                    || changeNotes == null
                    || changeNotes.isBlank()
                    || SEED_CHANGE_NOTES_CASUAL.equals(changeNotes)
                    || isGenericWorkerStub(contentHtml);
        }
        if ("client_service_agreement".equals(templateKey)) {
            if (isFullClientContent(contentHtml)) {
                return false;
            }
            return isLegacySeedChangeNotes(changeNotes)
                    || "Initial client agreement seed".equals(changeNotes)
                    || changeNotes == null
                    || changeNotes.isBlank()
                    || SEED_CHANGE_NOTES_CLIENT.equals(changeNotes)
                    || isGenericClientStub(contentHtml);
        }
        return isLegacySeedChangeNotes(changeNotes);
    }

    private static boolean isGenericClientStub(String html) {
        return html == null
                || html.isBlank()
                || (html.contains("Client Service Agreement") && !html.contains("agreement-cover-page"));
    }

    private static boolean isGenericWorkerStub(String html) {
        return html == null
                || html.isBlank()
                || (html.contains("Schedule – Position Description") && !html.contains("POSITION DESCRIPTION"));
    }

    public static String contractorAgreementHtml() {
        return """
                <div style="font-family: Arial, sans-serif; line-height: 1.55; color: #222;">
                  <p>{{today.date}}</p>
                  <p>Private and confidential</p>
                  <p><strong>{{worker.fullName}}</strong><br/>{{worker.address}}</p>
                  <h2 style="text-align:center;">Service Agreement – Contractor</h2>
                  <p>We are delighted to extend to you an offer of contractor employment for the position of {{worker.positionTitle}} with {{org.name}}. This offer is subject to the terms and conditions outlined in this letter.</p>
                  <p>Your employment will be on a contractor basis and will commence on {{worker.joinDate}}</p>
                  <ul>
                    <li>Your employment has no firm advance commitment in that the work will continue indefinitely with an agreed pattern of work. The following four factors are considered:
                      <ol>
                        <li>Our organisation extends work opportunities to you, and it remains your prerogative to accept or decline them.</li>
                        <li>Work assignments will be offered to you based on our organisational requirements.</li>
                        <li>Your employment status is that of a contractor.</li>
                        <li>You will be paid as a contractor or at a specific pay rate for contractor employees</li>
                      </ol>
                    </li>
                    <li>The duties relevant to this position are detailed in the attached job description. When you are scheduled for work, it is anticipated that you will carry out these responsibilities diligently, in addition to any supplementary tasks delegated by {{org.name}}. These additional assignments will be aligned with your skills, training, and professional background.</li>
                    <li>Your duties will primarily be carried out at {{org.name}}, the client's premises, or any other location as reasonably instructed by the employer.</li>
                  </ul>
                  <h3>1. Terms and conditions of employment</h3>
                  <p>Contractors/Employees are not allowed to engage in personal business or tasks with {{org.name}} clients and participants. Contractors/Employees must emphasise that such actions are strictly prohibited and contravene the terms of our agreement. Any unauthorised engagement with {{org.name}} clients and participants is not only a breach of contract but also poses significant risks to our organisation's reputation and integrity. As such, we will take decisive action to safeguard the interests of {{org.name}}. This could result in financial penalties aimed at recovering any losses or damages incurred by {{org.name}} as a consequence of Contractors'/employees' actions. Employees or subcontractors are not permitted to bring any individuals, including personal contacts or family members, to the work site or task location unless prior approval has been granted by an authorized representative of {{org.name}}</p>
                  <h3>2. Employment Schedule and Termination</h3>
                  <p>The Schedule attached to this letter of Engagement: the attached Schedule contains the {{org.name}} Position description statement and other information.</p>
                  <p>Your employment may be terminated at any time by providing you with notice to apply at the end of your current Engagement.</p>
                  <h3>3. Payment Terms</h3>
                  <p>As per our mutual agreement, payment will be issued on a fortnightly basis to the bank account provided by you. The agreed hourly rates will be applied, with adjustments possible based on location and specific tasks performed.</p>
                  <h3>4. Your obligations to the employer</h3>
                  <p>You will be required to:</p>
                  <ul>
                    <li>Perform all duties to the best of your ability at all times;</li>
                    <li>Comply with all NDIS and relevant legal requirements;</li>
                    <li>Follow our policies and procedures;</li>
                    <li>Use your best endeavours to promote and protect the interests of the employer; and</li>
                    <li>Follow all reasonable and lawful directions given to you by the employer, including complying with policies and procedures as amended from time to time. These policies and procedures are not incorporated into your contract of employment.</li>
                    <li>Communicate openly and honestly in a timely manner;</li>
                    <li>Treat the participant/nominated representative with courtesy and respect;</li>
                    <li>Listen to the participant/nominated representative's feedback and resolve problems quickly; and</li>
                    <li>Protect the participant's privacy and confidential information.</li>
                  </ul>
                  <h3>5. Confidentiality</h3>
                  <p>By accepting this letter of offer, you acknowledge and agree that you will not, during your employment or after that, except with the consent of {{org.name}} as required by law or in the performance of your duties, use or disclose confidential information relating to the employer's business, including but not limited to client lists, trade secrets, client details and pricing structures.</p>
                  <h3>6. Entire agreement</h3>
                  <ul>
                    <li>The terms and conditions referred to in this letter and the attached Schedule constitute all the terms and conditions of your employment and replace any prior understanding or agreement between you and the employer.</li>
                    <li>The terms and conditions referred to in this letter and the attached Schedule may only be varied by a written agreement signed by both you and {{org.name}}.</li>
                    <li>If you have any questions about the terms and conditions of employment, please don't hesitate to contact the Director, {{org.name}}.</li>
                  </ul>
                  <p>The Parties agree to the terms and conditions of this Service Agreement. The employer/organisation and contractor agree to the terms and conditions of this agreement by signing below</p>
                  %s
                  <h3>Schedule</h3>
                  <p>The following documents contain additional conditions of employment applicable to your Engagement with {{org.name}}:</p>
                  <ul>
                    <li>Position Description</li>
                    <li>Staff Handbook and Policy &amp; Procedures</li>
                    <li>Completed all relevant checks and training</li>
                  </ul>
                  <h3>POSITION DESCRIPTION</h3>
                  <table style="width:100%%; border-collapse:collapse;" border="1" cellpadding="8">
                    <tbody>
                      <tr><td style="width:30%%;"><strong>JOB TITLE:</strong></td><td>{{worker.positionTitle}}</td></tr>
                      <tr><td><strong>JOB DESCRIPTION:</strong></td><td>Support work includes activities that cater to people who need help to take care of themselves, including those with learning difficulties, physical disabilities, autism, brain injuries, mental health, aging and other complex needs. Some daily activities may include teaching a new skill, taking care of those in need or providing emotional support. The main goal of support work is to help enable people to live their own lives as independently as possible by helping with their day-to-day activities.</td></tr>
                      <tr><td><strong>RISK ASSESSED ROLE</strong></td><td>No</td></tr>
                      <tr><td><strong>TYPE OF EMPLOYMENT:</strong></td><td>Sub Contractor</td></tr>
                      <tr><td><strong>REPORT TO:</strong></td><td>{{org.directorName}}</td></tr>
                      <tr><td><strong>LOCATION:</strong></td><td>{{org.name}} and other-directed sites by the employer.</td></tr>
                      <tr><td><strong>MAIN DUTIES / RESPONSIBILITIES:</strong></td><td>
                        <ul>
                          <li>Providing clients with companionship, friendship and emotional support.</li>
                          <li>Assisting clients with recreational activities.</li>
                          <li>Providing support for clients' health needs, such as medical appointments.</li>
                          <li>Assisting clients with daily activities, such as household tasks, meal prep, grooming, shopping etc.</li>
                          <li>Effective Communication with clients (Talking, Listening and understanding their concerns.</li>
                          <li>Keeping a logbook and writing notes detailing daily interactions with the client and their progress (Shift Note).</li>
                          <li>Report {{org.name}} about any hazards, threats, or incidents during the time with the clients.</li>
                        </ul>
                      </td></tr>
                      <tr><td><strong>QUALIFICATIONS/ REQUIREMENTS:</strong></td><td>Certificate III or relevant support worker qualification</td></tr>
                      <tr><td><strong>EXPERIENCE:</strong></td><td>Sound background of Support Worker experience.</td></tr>
                      <tr><td><strong>SKILLS:</strong></td><td>Patience, Active Listening, Effective and clear communication, Collaboration, Attention to detail, Average computer skills, and Report writing.</td></tr>
                    </tbody>
                  </table>
                </div>
                """.formatted(SIGNATURE_SECTION);
    }

    public static String casualAgreementHtml() {
        return """
                <div style="font-family: Arial, sans-serif; line-height: 1.55; color: #222;">
                  <p>{{today.date}}</p>
                  <p>Private and confidential</p>
                  <p><strong>{{worker.fullName}}</strong><br/>{{worker.address}}</p>
                  <h2 style="text-align:center;">Letter of Engagement – Casual Employees</h2>
                  <p>Dear {{worker.firstName}},</p>
                  <p>I am pleased to offer you casual employment in the position of {{worker.positionTitle}} with us at {{org.name}} on the terms and conditions set out in this letter.</p>
                  <h3>1. Position</h3>
                  <ul>
                    <li>Your employment will be on a casual basis under the Fair Work Act 2009, Section 15A, commencing on {{worker.joinDate}}</li>
                    <li>Your employment has no firm advance commitment in that the work will continue indefinitely with an agreed pattern of work. The following four factors are considered:
                      <ol>
                        <li>Our organisation may offer you work from time to time, and you are free to accept or decline those offers.</li>
                        <li>Work will be offered based on the organisation's operational needs.</li>
                        <li>Your employment status is casual.</li>
                        <li>You will be paid either a casual loading or a specified hourly rate applicable to casual employees.</li>
                      </ol>
                    </li>
                    <li>The duties of this role are in the attached position description. On each occasion that you work, you will be required to perform these duties, and any other duties {{org.name}} may assign to you, having regard to your skills, training and experience.</li>
                    <li>You will be required to perform your duties at {{org.name}}, sites or elsewhere as reasonably directed by the employer.</li>
                  </ul>
                  <h3>2. Terms and conditions of employment</h3>
                  <p>Unless more generous provisions are provided in this letter or the attached Schedule, the terms and conditions of your employment will be those set out in the SCHADS Award and applicable legislation, including but not limited to the National Employment Standards (NES) in the Fair Work Act 2009. Neither the Award nor any applicable legislation is incorporated into your contract of employment.</p>
                  <p>The Schedule attached to this letter of Engagement: the attached Schedule contains:</p>
                  <ul>
                    <li>The {{org.name}} Position Description Statement</li>
                    <li>Information to enable your access to the {{org.name}} Policy and Procedures Manual.</li>
                  </ul>
                  <p>The Schedule attached to this letter of Engagement includes a copy of the instructions for you to access the {{org.name}} Policy and Procedures Manual. Both of these documents contain further information about your conditions of employment.</p>
                  <p>Your employment may be terminated at any time by providing you with notice to apply at the end of your current Engagement.</p>
                  <p>The terms and conditions set out in the attached Schedule will also apply to your employment.</p>
                  <h3>3. Remuneration</h3>
                  <ul>
                    <li>You will be paid at the agreed hourly rate, including the applicable casual loading.</li>
                    <li>As mutually agreed, you will be paid weekly to the bank account nominated by you.</li>
                    <li>The employer will also make superannuation payments on your behalf under the Superannuation Guarantee (Administration) Act 1992.</li>
                    <li>Your remuneration will be reviewed annually and may be increased at the employer's discretion.</li>
                  </ul>
                  <h3>5. Your obligations to the employer</h3>
                  <p>You will be required to:</p>
                  <ul>
                    <li>Perform all duties to the best of your ability at all times;</li>
                    <li>Comply with all NDIS and legal requirements;</li>
                    <li>Follow our policies and procedures;</li>
                    <li>Use your best endeavours to promote and protect the interests of the employer; and</li>
                    <li>Follow all reasonable and lawful directions given to you by the employer, including complying with policies and procedures as amended from time to time. These policies and procedures are not incorporated into your contract of employment.</li>
                  </ul>
                  <h3>6. Confidentiality</h3>
                  <p>By accepting this letter of offer, you acknowledge and agree that you will not, during your employment or after that, except with the consent of {{org.name}} as required by law or in the performance of your duties, use or disclose confidential information relating to the employer's business, including but not limited to client lists, trade secrets, client details and pricing structures.</p>
                  <h3>7. Entire agreement</h3>
                  <ul>
                    <li>The terms and conditions referred to in this letter and the attached Schedule constitute all the terms and conditions of your employment and replace any prior understanding or agreement between you and the employer.</li>
                    <li>The terms and conditions referred to in this letter and the attached Schedule may only be varied by a written agreement signed by both you and {{org.name}}.</li>
                    <li>If you have any questions about the terms and conditions of employment, please don't hesitate to contact the Director, {{org.name}}.</li>
                  </ul>
                  <p>You may also seek information about the minimum terms and conditions of employment from the Fair Work Ombudsman. You can contact them on 13 13 94 or visit their website at www.fairwork.gov.au.</p>
                  <p>Yours sincerely,</p>
                  <p><strong>{{org.directorName}}</strong><br/>Business Manager</p>
                  %s
                  <h3>Acceptance of Offer of Casual Employment</h3>
                  <p>I, {{worker.fullName}}, have read and understood this letter and accept the offer of employment from {{org.name}} on the terms and conditions set out herein. I understand that each Engagement will constitute a separate contract of employment between us.</p>
                  <p>Signed:</p>
                  <div class="agreement-signature-line">{{signature.recipient}}</div>
                  <p>Print name: {{worker.fullName}}</p>
                  <p>Date: {{signature.recipientDate}}</p>
                  <h3>Schedule</h3>
                  <p>The following documents contain additional conditions of employment applicable to your Engagement with {{org.name}}.</p>
                  <ol>
                    <li>Position Description Statement</li>
                    <li>Staff handbook &amp; policy, and procedures</li>
                    <li>Completed all relevant checks and training</li>
                  </ol>
                  <h3>POSITION DESCRIPTION</h3>
                  <table style="width:100%%; border-collapse:collapse;" border="1" cellpadding="8">
                    <tbody>
                      <tr><td style="width:30%%;"><strong>JOB TITLE:</strong></td><td>{{worker.positionTitle}}</td></tr>
                      <tr><td><strong>JOB DESCRIPTION:</strong></td><td>Support work includes activities that cater to people who need help to take care of themselves, including those with learning difficulties, physical disabilities, autism, brain injuries, mental health, aging, and other complex needs. Some daily activities may include teaching a new skill, taking care of those in need or providing emotional support. The main goal of support work is to help enable people to live their own lives as independently as possible by helping with their day-to-day activities.</td></tr>
                      <tr><td><strong>TYPE OF EMPLOYMENT:</strong></td><td>Casual</td></tr>
                      <tr><td><strong>REPORT TO:</strong></td><td>{{org.directorName}}</td></tr>
                      <tr><td><strong>LOCATION:</strong></td><td>{{org.name}} and other-directed sites by the employer.</td></tr>
                      <tr><td><strong>MAIN DUTIES / RESPONSIBILITIES:</strong></td><td>
                        <ul>
                          <li>Providing clients with companionship, friendship, and emotional support.</li>
                          <li>Assisting clients with recreational activities.</li>
                          <li>Providing support for the client's health needs, such as medical appointments.</li>
                          <li>Assisting clients with daily activities, such as household tasks, meal prep, grooming, shopping, etc.</li>
                          <li>Effective Communication with clients (Talking, Listening and understanding their concerns.</li>
                          <li>Keeping a logbook and writing notes detailing daily interactions with the client and their progress (Shift Note).</li>
                          <li>Report {{org.name}} about any hazards, threats, or incidents during the time with the clients.</li>
                        </ul>
                      </td></tr>
                      <tr><td><strong>QUALIFICATIONS/ REQUIREMENTS:</strong></td><td>Certificate III or relevant support worker qualification</td></tr>
                      <tr><td><strong>EXPERIENCE:</strong></td><td>Sound background of Support Worker experience.</td></tr>
                      <tr><td><strong>SKILLS:</strong></td><td>Patience, Active Listening, Effective and clear communication, Collaboration, Attention to detail</td></tr>
                    </tbody>
                  </table>
                </div>
                """.formatted(SIGNATURE_SECTION);
    }

    public static String workerAgreementHtml(String employmentTypeLabel, String employmentBasisText) {
        return """
                <div style="font-family: Arial, sans-serif; line-height: 1.5; color: #222;">
                  <p style="text-align:right;">{{today.date}}</p>
                  <p>Private and confidential</p>
                  <p><strong>{{worker.fullName}}</strong><br/>{{worker.address}}</p>
                  <h2>Service Agreement – %s</h2>
                  <p style="text-align:center;">{{org.logo}}</p>
                  <p>We are delighted to extend to you an offer of %s employment for the position of {{worker.positionTitle}} with {{org.name}}. This offer is subject to the terms and conditions outlined in this letter.</p>
                  <p>Your employment will be on a %s basis and will commence on {{agreement.commencementDate}}.</p>
                  <ul>
                    <li>Your employment has no firm advance commitment in that the work will continue indefinitely with an agreed pattern of work.</li>
                    <li>The duties relevant to this position are detailed in the attached job description.</li>
                    <li>Your duties will primarily be carried out at {{org.name}}, the client's premises, or any other location as reasonably instructed by the employer.</li>
                  </ul>
                  <h3>1. Terms and conditions of employment</h3>
                  <p>Contractors/Employees are not allowed to engage in personal business or tasks with {{org.name}} clients and participants without prior approval.</p>
                  <h3>2. Employment Schedule and Termination</h3>
                  <p>The Schedule attached to this letter contains the position description statement and other information.</p>
                  <h3>3. Payment Terms</h3>
                  <p>Payment will be issued on a fortnightly basis to the bank account provided by you.</p>
                  <h3>4. Your obligations to the employer</h3>
                  <ul>
                    <li>Perform all duties to the best of your ability at all times;</li>
                    <li>Comply with all NDIS and relevant legal requirements;</li>
                    <li>Follow our policies and procedures;</li>
                    <li>Protect the participant's privacy and confidential information.</li>
                  </ul>
                  <h3>5. Confidentiality</h3>
                  <p>By accepting this letter of offer, you agree not to disclose confidential information relating to the employer's business.</p>
                  <h3>6. Entire agreement</h3>
                  <p>The terms referred to in this letter and the attached Schedule constitute all the terms and conditions of your employment.</p>
                  <p>The Parties agree to the terms and conditions of this Service Agreement by signing below.</p>
                  %s
                  <h3>Schedule – Position Description</h3>
                  <table style="width:100%%; border-collapse:collapse;" border="1" cellpadding="8">
                    <thead>
                      <tr style="background-color:#055476; color:#fff;">
                        <th style="width:30%%;">Field</th>
                        <th>Details</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr><td><strong>Job Title</strong></td><td>{{worker.positionTitle}}</td></tr>
                      <tr><td><strong>Employee Name</strong></td><td>{{worker.fullName}}</td></tr>
                      <tr><td><strong>Type of Employment</strong></td><td>%s</td></tr>
                      <tr><td><strong>Care Giver Type</strong></td><td>{{worker.careGiverType}}</td></tr>
                      <tr><td><strong>Report To</strong></td><td>{{org.directorName}}</td></tr>
                      <tr><td><strong>Location</strong></td><td>{{org.name}} and other directed sites by the employer</td></tr>
                      <tr><td><strong>Main Duties</strong></td><td>Providing clients with companionship and emotional support; assisting with daily activities, household tasks, meal prep, grooming, shopping; effective communication; keeping shift notes; reporting hazards and incidents.</td></tr>
                      <tr><td><strong>Qualifications</strong></td><td>Certificate III or relevant support worker qualification</td></tr>
                      <tr><td><strong>Experience</strong></td><td>Sound background of Support Worker experience</td></tr>
                      <tr><td><strong>Skills</strong></td><td>Patience, active listening, effective communication, collaboration, attention to detail, report writing</td></tr>
                    </tbody>
                  </table>
                </div>
                """.formatted(employmentTypeLabel, employmentBasisText, employmentBasisText, SIGNATURE_SECTION, employmentTypeLabel);
    }

    public static String clientServiceAgreementHtml() {
        return ClientServiceAgreementSeedHtml.html();
    }
}
