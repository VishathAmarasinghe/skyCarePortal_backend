import { BRAND_COLORS, SKYCARE_LOGO_URL } from "../constants.js";
import type { CarePlanForPdf, ClientForPdf, CompanyInfo, SupportScheduleRow } from "../types.js";

const C = BRAND_COLORS;

function isNonEmptyString(v: unknown): v is string {
  return typeof v === "string" && v.trim().length > 0;
}

function escapeHtml(s: unknown): string {
  return String(s ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function formatClientName(client: ClientForPdf): string {
  const firstName = client?.firstName ?? "";
  const lastName = client?.lastName ?? "";
  const full = `${firstName} ${lastName}`.trim();
  return full || client?.preferredName || "Client";
}

type GoalRow = { short: string; long: string };

function renderGoalsRows(carePlan: CarePlanForPdf): GoalRow[] {
  const longTerms = carePlan?.carePlanLongTermGoals ?? [];
  const rows: GoalRow[] = [];

  for (const lt of longTerms) {
    const longText = lt?.longTermGoal ?? "";
    const shortTerms = lt?.carePlanShortTermGoals ?? [];

    if (shortTerms.length === 0) {
      rows.push({ short: "", long: longText });
      continue;
    }

    shortTerms.forEach((st, idx) => {
      rows.push({
        short: st?.goalTitle ?? "",
        long: idx === 0 ? longText : "",
      });
    });
  }

  return rows;
}

function renderSection(title: string, htmlBody: string): string {
  return `
    <div class="section">
      <div class="section-title">${escapeHtml(title)}</div>
      <div class="section-body">${htmlBody}</div>
    </div>
  `;
}

function scheduleHasAnyValues(schedule: SupportScheduleRow[]): boolean {
  return schedule.some(
    (r) =>
      isNonEmptyString(r?.supportType) ||
      Number(r?.hours ?? 0) > 0 ||
      Number(r?.rate ?? 0) > 0
  );
}

function resolveLogoSrc(companyLogoDataUrl: string): string {
  if (isNonEmptyString(companyLogoDataUrl)) return companyLogoDataUrl;
  return SKYCARE_LOGO_URL;
}

export function renderSupportPlanHtml(params: {
  company: CompanyInfo;
  carePlan: CarePlanForPdf;
  client: ClientForPdf;
}): string {
  const { company, carePlan, client } = params;

  const companyName = company?.name ?? "Skycare Solutions";
  const logoSrc = resolveLogoSrc(company?.logoDataUrl ?? "");
  const clientName = formatClientName(client);

  const goalsRows = renderGoalsRows(carePlan);
  const supportsCore = carePlan?.supportsFundedCore ?? "";
  const supportsCapacity = carePlan?.supportsFundedCapacityBuilding ?? "";
  const supportsCapital = carePlan?.supportsFundedCapital ?? "";
  const schedule = carePlan?.supportSchedule ?? [];

  const blocks: string[] = [];

  blocks.push(
    renderSection(
      "Participant Goals",
      `
      <table class="table">
        <thead>
          <tr>
            <th style="width: 50%;">Short Term Goals</th>
            <th style="width: 50%;">Long Term Goals</th>
          </tr>
        </thead>
        <tbody>
          ${
            goalsRows.length > 0
              ? goalsRows
                  .map(
                    (r) => `
            <tr>
              <td>${escapeHtml(r.short)}</td>
              <td>${escapeHtml(r.long)}</td>
            </tr>
          `
                  )
                  .join("")
              : `<tr><td colspan="2" class="muted">No goals added.</td></tr>`
          }
        </tbody>
      </table>
    `
    )
  );

  if (isNonEmptyString(carePlan?.currentLivingSituation)) {
    blocks.push(
      renderSection(
        "Current Living Situation",
        `<div class="text-block">${escapeHtml(carePlan.currentLivingSituation)}</div>`
      )
    );
  }

  if (
    isNonEmptyString(supportsCore) ||
    isNonEmptyString(supportsCapacity) ||
    isNonEmptyString(supportsCapital)
  ) {
    blocks.push(
      renderSection(
        "Supports Funded In The Plan",
        `
        <table class="table">
          <thead>
            <tr>
              <th>Core Supports</th>
              <th>Capacity Building Supports</th>
              <th>Capital Supports</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>${escapeHtml(supportsCore)}</td>
              <td>${escapeHtml(supportsCapacity)}</td>
              <td>${escapeHtml(supportsCapital)}</td>
            </tr>
          </tbody>
        </table>
      `
      )
    );
  }

  if (Array.isArray(schedule) && schedule.length > 0 && scheduleHasAnyValues(schedule)) {
    blocks.push(
      renderSection(
        "Support Schedule",
        `
        <table class="table">
          <thead>
            <tr>
              <th style="width: 20%;">Day</th>
              <th style="width: 40%;">Support Type</th>
              <th style="width: 20%;" class="align-right">Hours</th>
              <th style="width: 20%;" class="align-right">Rate</th>
            </tr>
          </thead>
          <tbody>
            ${schedule
              .map(
                (r) => `
              <tr>
                <td>${escapeHtml(r?.day ?? "")}</td>
                <td>${escapeHtml(r?.supportType ?? "")}</td>
                <td class="align-right">${Number(r?.hours ?? 0).toFixed(2)}</td>
                <td class="align-right">${Number(r?.rate ?? 0).toFixed(2)}</td>
              </tr>
            `
              )
              .join("")}
          </tbody>
        </table>
      `
      )
    );
  }

  if (isNonEmptyString(carePlan?.riskManagement)) {
    blocks.push(
      renderSection(
        "Risk Management",
        `<div class="text-block">${escapeHtml(carePlan.riskManagement)}</div>`
      )
    );
  }

  if (isNonEmptyString(carePlan?.expectedOutcomes)) {
    blocks.push(
      renderSection(
        "Expected Outcomes",
        `<div class="text-block">${escapeHtml(carePlan.expectedOutcomes)}</div>`
      )
    );
  }

  return `<!doctype html>
  <html>
    <head>
      <meta charset="utf-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1" />
      <title>NDIS Support Plan</title>
      <link rel="preconnect" href="https://fonts.googleapis.com" />
      <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
      <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet" />
      <style>
        * { box-sizing: border-box; }
        body {
          font-family: "Poppins", Arial, Helvetica, sans-serif;
          color: ${C.textPrimary};
          margin: 0;
          padding: 0;
          font-size: 11px;
          line-height: 1.5;
          -webkit-print-color-adjust: exact;
          print-color-adjust: exact;
        }
        .header {
          margin: -28px -28px 0 -28px;
          background: ${C.headerBg};
          border-bottom: 2px solid ${C.primary};
          padding: 12px 28px 14px;
        }
        .header-inner {
          display: flex;
          align-items: center;
          justify-content: space-between;
          min-height: 36px;
        }
        .header-left { display: flex; align-items: center; gap: 10px; }
        .logo {
          width: 36px;
          height: 36px;
          display: flex;
          align-items: center;
          justify-content: center;
          overflow: hidden;
          flex-shrink: 0;
        }
        .logo img { width: 100%; height: 100%; object-fit: contain; }
        .company-name {
          font-size: 13px;
          font-weight: 600;
          color: ${C.primary};
          letter-spacing: 0.01em;
        }
        .report-title {
          font-size: 13px;
          font-weight: 700;
          color: ${C.primary};
          text-transform: uppercase;
          letter-spacing: 0.04em;
        }
        .page { padding: 16px 0 28px; }
        .participant-line {
          padding: 10px 0 12px;
          margin-bottom: 6px;
          font-size: 12px;
        }
        .participant-label {
          color: ${C.textMuted};
          font-weight: 500;
          margin-right: 6px;
        }
        .participant-name {
          font-weight: 700;
          color: ${C.textPrimary};
        }
        .section { margin-top: 18px; page-break-inside: avoid; }
        .section-title {
          font-size: 11px;
          font-weight: 700;
          margin-bottom: 8px;
          text-transform: uppercase;
          letter-spacing: 0.05em;
          color: ${C.primary};
        }
        .section-body { font-size: 11px; }
        .text-block {
          white-space: pre-wrap;
          line-height: 1.55;
          color: ${C.textSecondary};
          margin: 0;
          padding: 0;
        }
        .table {
          width: 100%;
          border-collapse: collapse;
          border: 1px solid ${C.textSecondary};
        }
        .table th,
        .table td {
          border: 1px solid ${C.textSecondary};
          padding: 8px 10px;
          vertical-align: top;
          white-space: pre-wrap;
        }
        .table th {
          text-align: left;
          font-size: 10px;
          font-weight: 600;
          color: ${C.textPrimary};
          text-transform: uppercase;
          letter-spacing: 0.03em;
        }
        .table td { font-size: 11px; color: ${C.textSecondary}; }
        .align-right { text-align: right; }
        .muted { color: ${C.textMuted}; font-style: italic; }
      </style>
    </head>
    <body>
      <div class="header">
        <div class="header-inner">
          <div class="header-left">
            <div class="logo">
              <img src="${escapeHtml(logoSrc)}" alt="Skycare Logo" />
            </div>
            <div class="company-name">${escapeHtml(companyName)}</div>
          </div>
          <div class="report-title">NDIS Support Plan</div>
        </div>
      </div>

      <div class="page">
        <div class="participant-line">
          <span class="participant-label">Participant:</span>
          <span class="participant-name">${escapeHtml(clientName)}</span>
        </div>

        ${blocks.join("")}
      </div>
    </body>
  </html>`;
}
