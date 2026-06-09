export type CompanyInfo = {
  name: string;
  logoDataUrl?: string;
};

export type ShortTermGoal = {
  goalTitle?: string;
};

export type LongTermGoal = {
  longTermGoal?: string;
  carePlanShortTermGoals?: ShortTermGoal[];
};

export type SupportScheduleRow = {
  day?: string;
  supportType?: string;
  hours?: number;
  rate?: number;
};

export type CarePlanForPdf = {
  careplanID?: string;
  title?: string;
  startDate?: string;
  endDate?: string;
  carePlanLongTermGoals?: LongTermGoal[];

  currentLivingSituation?: string;
  supportsFundedCore?: string;
  supportsFundedCapacityBuilding?: string;
  supportsFundedCapital?: string;
  supportSchedule?: SupportScheduleRow[];
  riskManagement?: string;
  expectedOutcomes?: string;
};

export type ClientForPdf = {
  firstName?: string;
  lastName?: string;
  preferredName?: string;
};

export type RenderNdisSupportPlanRequest = {
  company?: CompanyInfo;
  carePlan: CarePlanForPdf;
  client: ClientForPdf;
  generatedAtIso?: string;
};

export type RenderAgreementRequest = {
  html: string;
  documentTitle?: string;
  companyName?: string;
  generatedAtIso?: string;
};

