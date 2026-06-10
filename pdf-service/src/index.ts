import express, { type Request, type Response } from "express";
import bodyParser from "body-parser";
import puppeteer from "puppeteer";
import { SKYCARE_LOGO_URL, BRAND_COLORS } from "./constants.js";
import { renderSupportPlanHtml } from "./templates/supportPlanTemplate.js";
import { renderAgreementHtml } from "./templates/agreementTemplate.js";
import type { RenderNdisSupportPlanRequest, RenderAgreementRequest } from "./types.js";

const app = express();
app.use(bodyParser.json({ limit: "5mb" }));

const DEFAULT_COMPANY_NAME = "Skycare Solutions";
let cachedLogoDataUrl = "";

const resolveLogoDataUrl = async (): Promise<string> => {
  try {
    const response = await fetch(SKYCARE_LOGO_URL);
    if (!response.ok) return SKYCARE_LOGO_URL;
    const bytes = Buffer.from(await response.arrayBuffer());
    return `data:image/png;base64,${bytes.toString("base64")}`;
  } catch {
    return SKYCARE_LOGO_URL;
  }
};

app.get("/health", (_req: Request, res: Response) => res.status(200).send("ok"));

app.post(
  "/render/ndis-support-plan",
  async (req: Request<unknown, unknown, RenderNdisSupportPlanRequest>, res: Response) => {
    const body = req.body;

    if (!body?.carePlan || !body?.client) {
      return res.status(400).json({ message: "carePlan and client are required" });
    }

    let browser: puppeteer.Browser | undefined;
    try {
      const generatedAtIso = body.generatedAtIso ?? new Date().toISOString();
      const html = renderSupportPlanHtml({
        company: {
          name: body.company?.name ?? DEFAULT_COMPANY_NAME,
          logoDataUrl: body.company?.logoDataUrl ?? cachedLogoDataUrl,
        },
        carePlan: body.carePlan,
        client: body.client,
      });

      browser = await puppeteer.launch({
        args: ["--no-sandbox", "--disable-setuid-sandbox"],
      });

      const page = await browser.newPage();
      await page.setContent(html, { waitUntil: "load" });

      const footerTemplate = `
        <div style="width: 100%; font-family: Poppins, Arial, sans-serif; font-size: 8px; padding: 10px 28px 0; color: ${BRAND_COLORS.textMuted}; display: flex; justify-content: space-between; border-top: 1px solid ${BRAND_COLORS.borderLight};">
          <div style="color: ${BRAND_COLORS.primary}; font-weight: 600;">${DEFAULT_COMPANY_NAME}</div>
          <div>System Generated: ${new Date(generatedAtIso).toLocaleString()}</div>
          <div><span class="pageNumber"></span> / <span class="totalPages"></span></div>
        </div>
      `;

      const pdf = await page.pdf({
        format: "A4",
        printBackground: true,
        margin: { top: "28px", right: "28px", bottom: "52px", left: "28px" },
        displayHeaderFooter: true,
        headerTemplate: `<div></div>`,
        footerTemplate,
      });

      const pdfBuffer = Buffer.from(pdf);
      res.setHeader("Content-Type", "application/pdf");
      res.setHeader("Content-Length", String(pdfBuffer.byteLength));
      return res.status(200).send(pdfBuffer);
    } catch (e) {
      return res.status(500).json({ message: String((e as Error)?.message ?? e) });
    } finally {
      if (browser) {
        await browser.close();
      }
    }
  }
);

app.post(
  "/render/agreement",
  async (req: Request<unknown, unknown, RenderAgreementRequest>, res: Response) => {
    const body = req.body;
    if (!body?.html) {
      return res.status(400).json({ message: "html is required" });
    }

    let browser: puppeteer.Browser | undefined;
    try {
      const generatedAtIso = body.generatedAtIso ?? new Date().toISOString();
      const companyName = body.companyName ?? DEFAULT_COMPANY_NAME;
      const html = renderAgreementHtml({
        html: body.html,
        documentTitle: body.documentTitle,
        companyName,
      });

      browser = await puppeteer.launch({
        args: ["--no-sandbox", "--disable-setuid-sandbox"],
      });

      const page = await browser.newPage();
      await page.setContent(html, { waitUntil: "load", timeout: 30000 });

      const footerTemplate = `
        <div style="width: 100%; font-family: Arial, sans-serif; font-size: 8px; padding: 8px 24px 0; color: ${BRAND_COLORS.textMuted}; display: flex; justify-content: space-between; border-top: 1px solid ${BRAND_COLORS.borderLight};">
          <div style="color: ${BRAND_COLORS.primary}; font-weight: 600;">${companyName}</div>
          <div>Generated: ${new Date(generatedAtIso).toLocaleString()}</div>
          <div><span class="pageNumber"></span> / <span class="totalPages"></span></div>
        </div>
      `;

      const pdf = await page.pdf({
        format: "A4",
        printBackground: true,
        margin: { top: "20mm", right: "18mm", bottom: "22mm", left: "18mm" },
        displayHeaderFooter: true,
        headerTemplate: `<div></div>`,
        footerTemplate,
      });

      const pdfBuffer = Buffer.from(pdf);
      res.setHeader("Content-Type", "application/pdf");
      res.setHeader("Content-Length", String(pdfBuffer.byteLength));
      return res.status(200).send(pdfBuffer);
    } catch (e) {
      return res.status(500).json({ message: String((e as Error)?.message ?? e) });
    } finally {
      if (browser) {
        await browser.close();
      }
    }
  }
);

const port = Number(process.env.PORT ?? 3001);

resolveLogoDataUrl().then((logo) => {
  cachedLogoDataUrl = logo;
  app.listen(port, () => {
    // eslint-disable-next-line no-console
    console.log(`pdf-service listening on ${port}`);
  });
});
