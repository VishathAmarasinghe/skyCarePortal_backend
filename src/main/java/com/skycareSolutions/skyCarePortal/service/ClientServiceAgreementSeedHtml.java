package com.skycareSolutions.skyCarePortal.service;

final class ClientServiceAgreementSeedHtml {

    private static final String SIGNATURE_SECTION = """
            <table class="agreement-signature-section" style="width:100%%;border-collapse:collapse;border:1px solid #000;margin:16px 0;">
            <tbody>
            <tr>
            <td class="agreement-signature-column" style="width:50%%;border:1px solid #000;padding:12px 14px;vertical-align:top;">
            <p style="margin:0 0 14px;"><strong><u>Provider / Organization</u></strong></p>
            <p style="margin:0 0 14px;">Name: {{org.directorName}}</p>
            <p style="margin:0 0 14px;">Date: {{signature.adminDate}}</p>
            <p style="margin:0 0 6px;">Signature:</p>
            <div class="agreement-signature-line">{{signature.admin}}</div>
            </td>
            <td class="agreement-signature-column" style="width:50%%;border:1px solid #000;padding:12px 14px;vertical-align:top;">
            <p style="margin:0 0 14px;"><strong><u>Participant / Participant's Representative</u></strong></p>
            <p style="margin:0 0 14px;">Name: {{client.fullName}}</p>
            <p style="margin:0 0 14px;">Date: {{signature.recipientDate}}</p>
            <p style="margin:0 0 6px;">Signature:</p>
            <div class="agreement-signature-line">{{signature.recipient}}</div>
            </td>
            </tr>
            </tbody>
            </table>
            """;

    private ClientServiceAgreementSeedHtml() {
    }

    static String html() {
        return """
                <div class="agreement-document" style="font-family: Arial, sans-serif; line-height: 1.55; color: #222;">
                  <div class="agreement-cover-page" style="page-break-after: always; min-height: 90vh; display: flex; flex-direction: column; align-items: center; justify-content: center; text-align: center; padding: 48px 24px;">
                    <div style="margin-bottom: 32px;">{{org.logo}}</div>
                    <h1 style="font-size: 36px; font-weight: 700; letter-spacing: 0.02em; margin: 0;">Service Agreement</h1>
                  </div>

                  <div class="agreement-body" style="padding: 8px 4px;">
                    <p><strong>NOTE:</strong> A Service Agreement can be made between a participant and a provider, a participant's representative, and a provider. A participant's representative is someone close to the participant, such as a family member or friend, or manages the support funding under a participant's NDIS plan.</p>

                    <h3>1. Parties</h3>
                    <p>This Service Agreement is for <strong>{{client.fullName}}</strong>, a participant in the National Disability Insurance Scheme and is made between:</p>
                    <table style="width:100%%; border-collapse:collapse; margin: 12px 0;" border="1" cellpadding="8">
                      <tbody>
                        <tr><td style="width:32%%;"><strong>Participant</strong></td><td>{{client.fullName}} – {{client.ndisNumber}}</td></tr>
                        <tr><td><strong>Advocate / Participant's Representative</strong><br/><span style="font-size:12px;">(e.g. a family member or friend)</span></td><td>&nbsp;</td></tr>
                        <tr><td colspan="2" style="text-align:center;"><strong>and</strong></td></tr>
                        <tr><td><strong>Provider</strong></td><td>{{org.name}}</td></tr>
                        <tr><td><strong>STA Accommodation Address</strong></td><td>&nbsp;</td></tr>
                        <tr><td><strong>Phone number (if available)</strong></td><td>{{org.phone}}</td></tr>
                      </tbody>
                    </table>
                    <p>This Service Agreement will commence on <strong>{{agreement.commencementDate}}</strong> for the period <strong>{{carePlan.startDate}}</strong> to <strong>{{carePlan.endDate}}</strong>.</p>

                    <h3>2. The NDIS and this Service Agreement</h3>
                    <p>(a) This Agreement is made according to the rules and the goals of the National Disability Insurance Scheme (NDIS).</p>
                    <p>(b) A copy of the participant's NDIS plan is attached to this Service Agreement: ☐ Yes / ☐ No</p>
                    <p>(c) The participant and the service provider agree that this Agreement is in line with the main ideas of the NDIS. These ideas include having more choices, achieving your goals and participating in the community.</p>
                    <p>(d) The parties agree that this Service Agreement is made in the context of the NDIS, which is a scheme that aims to:</p>
                    <ul>
                      <li>support the independence and social and economic participation of people with disability</li>
                      <li>enable people with a disability to exercise choice and control in pursuing their goals and planning and delivering their supports.</li>
                    </ul>

                    <h3>3. Schedule of Supports – Short-Term Accommodation</h3>
                    <p>The Provider agrees to provide the required NDIS support as advised by the participant according to the NDIS plan. The Schedule of Supports will include the following information:</p>
                    <ol type="a">
                      <li>Provision method</li>
                      <li>The provision timeframe should be provided</li>
                      <li>Who will provide them</li>
                      <li>length of the provision</li>
                      <li>How much they will cost.</li>
                    </ol>
                    <p>Participant supports and costings are set out in the attached Schedule of Supports. All prices are GST inclusive (if applicable) and include the cost of providing the supports.</p>
                    <p>Additional expenses (i.e. things not included as part of a Participant's NDIS supports) are the responsibility of the participant or their nominee. They are not included in the cost of the supports. Examples include entrance fees, event tickets, meals, laundry products, food, creams etc.</p>

                    <h3>4. Responsibilities of the Provider</h3>
                    <p>The Provider agrees to:</p>
                    <ol type="a">
                      <li>Review the provision of support with the participant at least three monthly.</li>
                      <li>Create a Participant Emergency and Disaster Plan where we will consult and review your needs, risks and requirements to design a plan that keeps you safe during an emergency and disaster. To ensure that this plan is relevant and works for you, we will complete a trial of your emergency plan and seek your input to improve this plan. The Plan will be added as an appendix in this Agreement.</li>
                      <li>Test and adjust the emergency plan used to respond to an emergency or disaster.</li>
                      <li>Provide supports that meet the participant's needs at the participant's preferred times.</li>
                      <li>Provide supports that considers the participant's safety.</li>
                      <li>Communicate openly and honestly promptly.</li>
                      <li>Treat the participant with courtesy and respect.</li>
                      <li>Consult the participant on decisions about how supports are provided.</li>
                      <li>Ensure that there is no conflict of interest and inform the participant if there is any potential.</li>
                      <li>Provide an invoice to fix damages for the participant to pay.</li>
                      <li>Provide the supports that meets your needs at the preferred times.</li>
                      <li>Review the provision of support monthly.</li>
                      <li>Give the information about managing complaints or disagreements and details of the Provider's cancellation policy (if relevant).</li>
                      <li>Listen to the participant's feedback and resolve problems quickly.</li>
                      <li>Provide the participant with a minimum of 24 hours' notice if the Provider has to change a scheduled appointment to provide supports.</li>
                      <li>Keep personal information private.</li>
                      <li>Follow critical incident management procedures to ensure the safety of participants by providing information via the Participant Handbook.</li>
                      <li>Keep you safe and ensure the safety of others.</li>
                      <li>Never provide the participant with financial advice or information (see Participant Handbook for more details).</li>
                      <li>Review your support plan and service agreement when your circumstances change.</li>
                      <li>Provide a copy of any updated support or risk plan as soon as it is practicable.</li>
                      <li>Provide you with the opportunity to select your support worker/s via interviews or meetings. You have the final say on your workers.</li>
                      <li>Train staff to meet your needs and circumstances.</li>
                      <li>Give the participant the required notice if the Provider needs to end the Service Agreement (see 'Ending this Service Agreement' below for more information).</li>
                      <li>Protect the participant's privacy and confidential information, including the participant's data, health information and other personal details gathered during the intake process. We will ensure that your information remains private while delivering our services.</li>
                      <li>Provide support consistent with all relevant laws, including the National Disability Insurance Scheme Act 2013 and rules and the Australian Consumer Law; keep accurate records on the support provided to the participant.</li>
                      <li>Issue regular invoices and statements of the support delivered to the participant.</li>
                      <li>The Provider has policies and procedures that are built on human rights. Where allegations of abuse, neglect, violence, exploitation or discrimination are made (service provider employs a Zero Tolerance policy and procedure).</li>
                    </ol>

                    <h3>4.1 Australian consumer law</h3>
                    <p>{{org.name}} ensures that the participant is treated fairly. Our services are fit-for-purpose and match the description provided, per the Competition and Consumer Act 2010 (CCA). Support or replacement of services will be negotiated with the Participant. {{org.name}} will provide proof of financial transactions to the participant or their advocate, as requested. In the development of Service Agreements with the Participant, we do not:</p>
                    <ul>
                      <li>mislead or deceive participants (this includes providing false information or not enough information)</li>
                      <li>accept payment for goods or services if we are unsure of our ability to supply them to the participant</li>
                      <li>accept payment for goods or services that the participant has not agreed to purchase as part of their service agreement.</li>
                    </ul>
                    <p>{{org.name}} will not undertake unfair treatment or take advantage of the participant. Examples of this include:</p>
                    <ul>
                      <li>providing services or expending funds contrary to the participant's approved Plan</li>
                      <li>asking for or accepting any additional fees for providing a service</li>
                      <li>offering inducements or rewards that have no particular link to an NDIS Plan that could be perceived to encourage participants to take up or continue with your organisation or a particular service option</li>
                      <li>engaging in high-pressure sales tactics.</li>
                    </ul>

                    <h3>4.2 NDIS Code of Conduct</h3>
                    <p>All our actions are linked to the NDIS Code of Conduct, and we will always act with honesty, integrity and transparency. Our actions include the following:</p>
                    <ul>
                      <li>supplying truthful information about the capacity, qualifications, training and professional affiliations of our workforce, and we will never advise the participant of our ability to provide a specialised service when not legally able to do so</li>
                      <li>never making false claims about the efficacy of any of our supports, services or products</li>
                      <li>providing clear advice regarding the total costs of the service or support and what the cost covers</li>
                      <li>not making claims about the efficacy of treatments or supports that cannot be substantiated independently.</li>
                    </ul>

                    <h3>4.3 Critical Incidents</h3>
                    <p>All reportable incidents are managed as per our policy and procedure requirements. We will support and inform you of the actions taken and their results. We follow the NDIS (Incident Management and Reportable Incident) Rules 2018. See information in Participant Handbook and Easy Read documents in the appendix for more details.</p>

                    <h3>5. Responsibilities of the participant / participant's representative</h3>
                    <p>The participant / participant's representative agrees to:</p>
                    <ol type="a">
                      <li>Respect the rights of staff, ensuring their workplace is safe and healthy and free from harassment.</li>
                      <li>Abide by the terms of your Agreement with us.</li>
                      <li>Understand that your needs may change, and your services may need to meet your needs.</li>
                      <li>Accept responsibility for your actions and choices, even though some choices may involve risk.</li>
                      <li>Let us know if you have problems with the care and services you receive.</li>
                      <li>Give us enough information to develop, deliver and review your support plan.</li>
                      <li>Care for your health and well-being as much as you are able.</li>
                      <li>Provide us with information that will help us better meet your needs.</li>
                      <li>Give us a minimum of 24 hours' notice when you will not be home for your service.</li>
                      <li>Be aware that our staff are only authorised to perform the agreed number of hours and tasks outlined in your service agreement.</li>
                      <li>Participate in safety assessments of your home.</li>
                      <li>Ensure pets are controlled during service provision.</li>
                      <li>Provide a smoke-free working environment.</li>
                      <li>Pay the agreed amount for the services provided.</li>
                      <li>Tell us in writing (where able) and give us notice before the day you intend to stop receiving services from us.</li>
                      <li>To inform staff if you wish to opt out when asked.</li>
                      <li>Inform the Provider about how they wish the supports to be delivered to meet the participant's needs.</li>
                      <li>Treat the Provider with courtesy and respect.</li>
                      <li>Talk to the Provider if the participant has any concerns about the supports being provided.</li>
                      <li>Give the Provider a minimum of 24 hours' notice if the participant cannot make a scheduled appointment; if the notice is not provided by then, the Provider's cancellation policy will apply.</li>
                      <li>Give the Provider the required notice if the participant needs to end the Service Agreement (see 'Ending this Service Agreement' below for more information).</li>
                      <li>Let the Provider know immediately if the participant's NDIS plan is suspended or replaced by a new NDIS plan or if the participant stops participating in the NDIS.</li>
                    </ol>

                    <h3>6. Payments</h3>
                    <p>The Provider will seek payment for their provision of supports after satisfactory delivery is confirmed.</p>
                    <p>The participant's Plan Nominee manages the funding for the supports provided under this Service Agreement. After providing those supports, the Provider will send the participant's Nominee an invoice for those supports for the participant's Nominee to pay. The participant's Nominee will pay the invoice by EFT within seven working days.</p>
                    <p>The participant has nominated the Registered Plan Management Provider to manage the funding for NDIS supports provided under this Service Agreement. After providing those supports, the Provider will claim payment from the nominated plan manager.</p>
                    <p>A supply of supports under this Service Agreement is a supply of one or more reasonable and necessary supports specified in the statement of supports included, under subsection 33(2) of the National Disability Insurance Scheme Act 2013 (NDIS Act), in the participant's NDIS Plan currently in effect under section 37 of the NDIS Act.</p>

                    <h3>7. Changes to this Service Agreement</h3>
                    <p>If changes to the supports or their delivery are required, the parties agree to discuss and review this Service Agreement. The parties agree that any changes to this Service Agreement will be in writing, signed, and dated by the parties.</p>

                    <h3>8. Ending this Service Agreement</h3>
                    <p>Should either party wish to end this Service Agreement, they must give one month's notice.</p>
                    <p>The notice requirement will be waived if either party seriously breaches this Service Agreement.</p>

                    <h3>9. Feedback, complaints and disputes</h3>
                    <p>If the participant wishes to give the provider feedback, the participant can talk to {{org.name}} on {{org.email}}, {{org.phone}}.</p>
                    <p>The participant can also make an anonymous complaint by completing the Anonymous Complaints and Feedback Form or phoning our Complaints Manager.</p>
                    <p>If the participant is not happy with the provision of supports and wishes to make a complaint, the participant can talk to our Complaints Manager.</p>
                    <p>If the participant is not satisfied with the handling of the complaint or does not want to talk to our complaints manager regarding their feedback or complaint, they can contact the National Disability Insurance Scheme at any time throughout the process by calling 1800 035 544, visiting one of their offices in person, or visiting ndis.gov.au for further information.</p>

                    <h3>10. Goods and Services Tax (GST)</h3>
                    <p>For GST legislation, the Parties confirm that:</p>
                    <ol type="a">
                      <li>A supply of supports under this Service Agreement is a supply of one or more of the reasonable and necessary supports specified in the statement included, under subsection 33(2) of the National Disability Insurance Scheme Act 2013 (NDIS Act), in the participant's NDIS plan currently in effect under section 37 of the NDIS Act.</li>
                      <li>The participant's NDIS plan is expected to remain in effect during the period the supports are provided.</li>
                      <li>The participant, {{client.fullName}}, will immediately notify the Provider if a new plan replaces the participant's NDIS Plan or stops being a participant in the NDIS.</li>
                    </ol>

                    <h3>11. Access to Records</h3>
                    <p>My file can be accessed by an NDIS Registered Auditor for audit purposes only: ☐ Yes ☐ No</p>
                    <p>I agree that the following people can be provided access to my records. Please tick below the people you wish to have permission to access your records:</p>
                    <p>☐ Support Coordinator &nbsp; ☐ Plan Manager &nbsp; ☐ Family Member &nbsp; ☐ Other practitioners &nbsp; ☐ Other</p>

                    <h3>12. Information Storage</h3>
                    <p>The NDIS Commission may collect personal information about and from you, your representative or a third party. Use forms, online portals and other electronic or paper correspondence to collect this information. The NDIS Commission, or we as service providers, may collect information directly. The NDIS Commission may obtain personal information from other Commonwealth agencies, State or Territory government bodies, or other organisations. From time to time, the NDIS Commission may receive personal information from members of the public without it being requested.</p>
                    <p>The NDIS Commission and we, as service providers, will not ask you for any personal information we do not need. The Privacy Act requires that we collect information for a reasonably necessary purpose for or related to a function or activity of the NDIS Commission.</p>
                    <p>When the NDIS Commission collects personal information, we are required by the Privacy Act to notify you of several matters. These include the purposes for collecting the information, whether the collection is required or authorised by law and any person or body to whom we usually disclose the information. The NDIS Commission generally provides this notification by having Privacy Notices on our paper-based forms and online portals.</p>

                    <h3>13. Contact details</h3>
                    <table style="width:100%%; border-collapse:collapse; margin: 12px 0;" border="1" cellpadding="8">
                      <thead>
                        <tr><th class="agreement-section-th" colspan="2">Participant Contact details</th></tr>
                      </thead>
                      <tbody>
                        <tr><td style="width:32%%;"><strong>Mobile</strong></td><td>{{client.phone}}</td></tr>
                        <tr><td><strong>Email</strong></td><td>{{client.email}}</td></tr>
                        <tr><td><strong>Address</strong></td><td>{{client.address}}</td></tr>
                        <tr><td><strong>Alternative contact person / advocate</strong></td><td>&nbsp;</td></tr>
                      </tbody>
                    </table>
                    <table style="width:100%%; border-collapse:collapse; margin: 12px 0;" border="1" cellpadding="8">
                      <thead>
                        <tr><th class="agreement-section-th" colspan="2">Provider Contact Details</th></tr>
                      </thead>
                      <tbody>
                        <tr><td style="width:32%%;"><strong>Contact name</strong></td><td>{{org.directorName}}</td></tr>
                        <tr><td><strong>Phone [B/H]</strong></td><td>{{org.phone}}</td></tr>
                        <tr><td><strong>Email</strong></td><td>{{org.email}}</td></tr>
                        <tr><td><strong>Address</strong></td><td>{{org.address}}</td></tr>
                      </tbody>
                    </table>

                    <h3>14. Participant's copy of the service agreement</h3>
                    <p>The participant confirms they have been offered a copy of this Service Agreement once completed: ☐ Yes ☐ No</p>
                    <p>The participant advised that they DO NOT wish to receive a copy of this Service Agreement: ☐ Yes ☐ No</p>
                    <p>If the above answer is yes, the reason why the participant does not want a copy of the Service Agreement:</p>
                    <p style="min-height:48px; border-bottom:1px solid #ccc;">&nbsp;</p>

                    <h3>Agreement signatures</h3>
                    <p>The parties agree to the terms and conditions of this Service Agreement.</p>
                    <p>This Agreement has been explained verbally: ☐ Yes ☐ No</p>
                    %s

                    <div style="page-break-before: always; margin-top: 24px;">
                      <h3>SCHEDULE OF SUPPORTS</h3>
                      <h4>Short Term Accommodation</h4>
                      <table style="width:100%%; border-collapse:collapse; margin: 12px 0;" border="1" cellpadding="8">
                        <tbody>
                          <tr><td style="width:40%%;"><strong>Goal-linked STA</strong></td><td>&nbsp;</td></tr>
                          <tr><td><strong>Number of days currently accessed</strong></td><td>&nbsp; days</td></tr>
                          <tr><td><strong>Days included in the Agreement</strong><br/><span style="font-size:12px;">Maximum of 28 days annually and 14 continuous days</span></td><td>&nbsp; days</td></tr>
                          <tr><td><strong>Total days after this STA</strong></td><td>&nbsp; days</td></tr>
                          <tr><td><strong>The participant is not using the STA for holidays or a housing crisis</strong></td><td>☐ Yes</td></tr>
                        </tbody>
                      </table>
                      <h4>Medical Information</h4>
                      <p>Provision of medical history: ☐ Yes ☐ No &nbsp; Information in the support plan: ☐ Yes ☐ No</p>
                      <p>Allergies: ☐ Yes ☐ No &nbsp; Identify allergy: __________________ &nbsp; Allergy Plan provided: ☐ Yes ☐ No</p>
                      <p>Additional health information:</p>
                      <p style="min-height:40px; border-bottom:1px solid #ccc;">&nbsp;</p>
                      <p><em>Note: Schedule of Fees to include personal care, accommodation, activities and food</em></p>
                      <table style="width:100%%; border-collapse:collapse; margin: 12px 0;" border="1" cellpadding="8">
                        <thead>
                          <tr>
                            <th>Support</th>
                            <th>Description of support</th>
                            <th>Price and payment information</th>
                            <th>How will the support be provided</th>
                            <th>Total Cost</th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
                          <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
                          <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
                        </tbody>
                      </table>
                      {{carePlan.supportScheduleTable}}

                      <h3 style="margin-top: 24px;">Cancellation Policy</h3>
                      <p>This cancellation policy applies to the agreement between {{org.name}} and the undersigned NDIS participant. It outlines the conditions under which services scheduled under this agreement may be cancelled or rescheduled.</p>
                      <p><strong>Notice Period:</strong> To cancel or reschedule a service appointment without incurring a fee, the participant, their plan manager, or nominee must provide {{org.name}} with at least 14 days' notice.</p>
                      <p><strong>Late Cancellation Fee:</strong> If the required notice is not provided, a late cancellation fee may be charged. The fee will not exceed the cost of the scheduled service and will be in accordance with the NDIS price guide and limits.</p>
                      <p><strong>Exceptions:</strong> Exceptions to this policy may be made at the discretion of {{org.name}} in cases of emergency or sudden illness. Documentation or evidence may be requested to waive the late cancellation fee.</p>
                      <p><strong>Cancellation by Service Provider:</strong> If {{org.name}} needs to cancel a scheduled service, we will provide the participant with as much notice as possible and make efforts to reschedule at a convenient time. No fees will be charged to the participant for cancellations initiated by {{org.name}}.</p>
                      <p><strong>Communication:</strong> All cancellations or rescheduling requests must be communicated to {{org.name}} by phone or email.</p>
                      <p><strong>Review:</strong> This policy will be reviewed periodically and may be updated. Participants will be notified of any changes to the cancellation policy.</p>
                    </div>
                  </div>
                </div>
                """.formatted(SIGNATURE_SECTION);
    }
}
