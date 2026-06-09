import { BRAND_COLORS } from "../constants.js";

export type RenderAgreementRequest = {
  html: string;
  documentTitle?: string;
  companyName?: string;
  generatedAtIso?: string;
};

export const renderAgreementHtml = ({
  html,
  documentTitle = "Service Agreement",
  companyName = "SkyCare Solutions",
}: RenderAgreementRequest): string => {
  return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>${escapeHtml(documentTitle)}</title>
  <style>
    @page { size: A4; margin: 20mm 18mm 24mm 18mm; }
    * { box-sizing: border-box; }
    body {
      font-family: Arial, Helvetica, sans-serif;
      font-size: 11pt;
      line-height: 1.55;
      color: ${BRAND_COLORS.textPrimary};
      margin: 0;
      padding: 0;
    }
    h1, h2, h3 { color: ${BRAND_COLORS.primary}; margin-top: 1.2em; margin-bottom: 0.5em; }
    h1 { font-size: 18pt; }
    h2 { font-size: 14pt; }
    h3 { font-size: 12pt; }
    p { margin: 0 0 0.75em; }
    ul, ol { margin: 0 0 0.75em 1.2em; padding: 0; }
    table {
      width: 100%;
      border-collapse: collapse;
      margin: 1em 0;
      table-layout: fixed;
      word-wrap: break-word;
    }
    thead { display: table-header-group; }
    th, td {
      border: 1px solid ${BRAND_COLORS.borderLight};
      padding: 8px 10px;
      vertical-align: top;
      text-align: left;
    }
    th {
      background-color: ${BRAND_COLORS.primary};
      color: #fff;
      font-weight: 600;
    }
    img {
      max-width: 100%;
      height: auto;
    }
    .page-break-before {
      page-break-before: always;
      break-before: page;
    }
    tr { page-break-inside: avoid; break-inside: avoid; }
    tbody tr { page-break-inside: avoid; break-inside: avoid; }
    .agreement-columns {
      display: flex;
      gap: 24px;
      margin: 1em 0;
      align-items: flex-start;
    }
    .agreement-column {
      flex: 1;
      min-width: 0;
    }
    .agreement-signature-section {
      width: 100%;
      border-collapse: collapse;
      border: 1px solid #000;
      margin: 1em 0;
      page-break-inside: avoid;
      break-inside: avoid;
    }
    .agreement-signature-section td {
      width: 50%;
      border: 1px solid #000;
      padding: 12px 14px;
      vertical-align: top;
    }
    .agreement-signature-line {
      min-height: 72px;
      display: flex;
      align-items: flex-end;
      padding-top: 8px;
    }
    .agreement-signature-line img,
    .agreement-signature-image {
      max-height: 80px;
      max-width: 240px;
      display: block;
    }
    .signature-block img {
      max-height: 80px;
      max-width: 240px;
    }
    a { color: ${BRAND_COLORS.primary}; }
  </style>
</head>
<body>
  ${html}
</body>
</html>`;
};

const escapeHtml = (value: string): string =>
  value
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
