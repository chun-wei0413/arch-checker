// Build midterm-team6.pptx for OOAD Midterm Review
// Project: arch-checker — A Fluent Architectural Style Compliance Tool for Java
const path = require("path");
process.chdir(__dirname);

const pptxgenjs = require(
  path.join("C:\\Users\\user\\AppData\\Roaming\\npm\\node_modules\\pptxgenjs")
);

const pres = new pptxgenjs();
pres.layout = "LAYOUT_WIDE"; // 13.333" x 7.5"
pres.author = "FrankLi";
pres.title = "arch-checker — OOAD 期中報告";
pres.subject = "OOAD Midterm Review";

// =============================================================
// Palette — Midnight Executive (navy + ice blue + gold accent)
// =============================================================
const NAVY      = "1E2761";
const NAVY_DARK = "0F1535";
const INK       = "1F2A44";
const ICE       = "CADCFC";
const ICE_SOFT  = "EAF1FF";
const GOLD      = "F4B400";
const TEAL      = "1C7293";
const CORAL     = "F96167";
const MUTED     = "5B6479";
const LIGHT     = "F4F6FB";
const WHITE     = "FFFFFF";
const RULE      = "D4DBE8";

// Image base paths
const HW2 = path.join("..", "hw2");
const HW3 = path.join("..", "hw3");
const HW4 = path.join("..", "hw4", "images");
const HW5 = path.join("..", "hw5", "images");

const SW = 13.333; // slide width (inches)
const SH = 7.5;    // slide height

let pageNo = 0;
const TOTAL_PAGES = 19; // 封面不計頁

// 字型：標題用 Cambria（提案），中文用 Microsoft JhengHei；內文 Calibri / Microsoft JhengHei
const F_TITLE_EN = "Cambria";
const F_BODY     = "Microsoft JhengHei";
const F_LABEL    = "Microsoft JhengHei";
const F_MONO     = "Consolas";

function chrome(slide, title, kicker) {
  pageNo += 1;
  slide.background = { color: WHITE };

  // 上方深藍橫條
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0, y: 0, w: SW, h: 0.85,
    fill: { color: NAVY }, line: { color: NAVY }
  });
  // 金色細條
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0, y: 0.85, w: SW, h: 0.05,
    fill: { color: GOLD }, line: { color: GOLD }
  });

  if (kicker) {
    slide.addText(kicker.toUpperCase(), {
      x: 0.55, y: 0.12, w: 8, h: 0.30,
      fontFace: F_LABEL, fontSize: 11, bold: true,
      color: ICE, charSpacing: 4, margin: 0
    });
  }
  slide.addText(title, {
    x: 0.55, y: 0.36, w: 11.5, h: 0.50,
    fontFace: F_BODY, fontSize: 24, bold: true,
    color: WHITE, margin: 0
  });

  // 頁尾
  slide.addShape(pres.shapes.LINE, {
    x: 0.55, y: SH - 0.42, w: SW - 1.1, h: 0,
    line: { color: RULE, width: 0.75 }
  });
  slide.addText("arch-checker  ·  OOAD 期中報告  ·  113598009 李俊威", {
    x: 0.55, y: SH - 0.36, w: 9, h: 0.30,
    fontFace: F_BODY, fontSize: 9, color: MUTED, margin: 0
  });
  slide.addText(`${pageNo} / ${TOTAL_PAGES}`, {
    x: SW - 1.55, y: SH - 0.36, w: 1.0, h: 0.30,
    fontFace: F_BODY, fontSize: 9, color: MUTED,
    align: "right", margin: 0
  });
}

function fitImage(maxW, maxH, origW, origH) {
  const r = Math.min(maxW / origW, maxH / origH);
  return { w: origW * r, h: origH * r };
}

// =============================================================
// Slide 1 — 封面
// =============================================================
{
  const s = pres.addSlide();
  s.background = { color: NAVY_DARK };
  for (let r = 0; r < 4; r++) {
    for (let c = 0; c < 6; c++) {
      s.addShape(pres.shapes.OVAL, {
        x: 10.6 + c * 0.32, y: 0.6 + r * 0.32, w: 0.06, h: 0.06,
        fill: { color: GOLD, transparency: 50 }, line: { color: GOLD, transparency: 50 }
      });
    }
  }
  s.addShape(pres.shapes.RECTANGLE, {
    x: 0.7, y: 2.5, w: 0.08, h: 2.6,
    fill: { color: GOLD }, line: { color: GOLD }
  });

  s.addText("OOAD 期中報告 · 第 6 組", {
    x: 1.0, y: 2.5, w: 11, h: 0.45,
    fontFace: F_BODY, fontSize: 14, bold: true,
    color: ICE, charSpacing: 6, margin: 0
  });

  s.addText("arch-checker", {
    x: 1.0, y: 2.95, w: 11, h: 1.1,
    fontFace: F_TITLE_EN, fontSize: 60, bold: true,
    color: WHITE, margin: 0
  });

  s.addText(
    "為 Java 設計的宣告式架構風格合規工具",
    {
      x: 1.0, y: 4.05, w: 11, h: 0.7,
      fontFace: F_BODY, fontSize: 22, italic: true,
      color: ICE, margin: 0
    }
  );

  s.addShape(pres.shapes.LINE, {
    x: 1.0, y: 5.0, w: 4.0, h: 0,
    line: { color: GOLD, width: 1.5 }
  });

  s.addText(
    [
      { text: "團隊成員", options: { bold: true, color: ICE, fontSize: 12, charSpacing: 4, breakLine: true } },
      { text: "113598009  李俊威 (Frank Li)", options: { color: WHITE, fontSize: 18 } },
    ],
    { x: 1.0, y: 5.2, w: 8, h: 1.0, fontFace: F_BODY, margin: 0 }
  );

  s.addText(
    "國立臺北科技大學  ·  物件導向分析與設計  ·  2026 春季學期",
    {
      x: 1.0, y: 6.7, w: 11, h: 0.4,
      fontFace: F_BODY, fontSize: 11, color: ICE, margin: 0
    }
  );
}

// =============================================================
// Slide 2 — Problem Statement
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "Problem Statement · 問題陳述", "01 · Requirement");

  // 左欄 — 痛點
  s.addShape(pres.shapes.RECTANGLE, {
    x: 0.55, y: 1.2, w: 5.6, h: 5.6,
    fill: { color: LIGHT }, line: { color: LIGHT }
  });
  s.addShape(pres.shapes.RECTANGLE, {
    x: 0.55, y: 1.2, w: 0.08, h: 5.6,
    fill: { color: CORAL }, line: { color: CORAL }
  });
  s.addText("痛點", {
    x: 0.85, y: 1.35, w: 5.0, h: 0.45,
    fontFace: F_BODY, fontSize: 20, bold: true, color: NAVY, margin: 0
  });
  s.addText(
    [
      { text: "AI 輔助生成程式碼大幅放大了人工程式碼審查的負擔", options: { bullet: true, breakLine: true } },
      { text: "架構漂移 (architectural drift) 成為開發週期新的瓶頸", options: { bullet: true, breakLine: true } },
      { text: "現有工具如 ArchUnit 需熟悉底層 API 並撰寫大量樣板程式", options: { bullet: true, breakLine: true } },
      { text: "缺乏「以風格 (style) 為中心」、可跨團隊重用的高階抽象", options: { bullet: true } },
    ],
    {
      x: 0.85, y: 1.95, w: 5.1, h: 4.7,
      fontFace: F_BODY, fontSize: 14, color: INK,
      paraSpaceAfter: 8, margin: 0
    }
  );

  // 右欄 — 解法
  s.addShape(pres.shapes.RECTANGLE, {
    x: 6.4, y: 1.2, w: 6.35, h: 5.6,
    fill: { color: ICE_SOFT }, line: { color: ICE_SOFT }
  });
  s.addShape(pres.shapes.RECTANGLE, {
    x: 6.4, y: 1.2, w: 0.08, h: 5.6,
    fill: { color: TEAL }, line: { color: TEAL }
  });
  s.addText("解決方案 — arch-checker", {
    x: 6.7, y: 1.35, w: 6.0, h: 0.45,
    fontFace: F_BODY, fontSize: 20, bold: true, color: NAVY, margin: 0
  });
  s.addText(
    [
      { text: "一套 Java CLI 工具，將專案自動驗證於以 YAML 宣告之 ", options: {} },
      { text: "Style Profile", options: { bold: true } },
      { text: "（涵蓋命名、依賴、繼承、套件等規則）", options: { breakLine: true } },
      { text: "", options: { breakLine: true } },
      { text: "底層以 JavaParser 進行 AST 分析", options: {} },
      { text: " → 精確偵測結構性違規，並輸出 file path、行號與描述。", options: { breakLine: true } },
      { text: "", options: { breakLine: true } },
      { text: "三種主要使用情境：", options: { bold: true } },
      { text: "本地開發、code review、CI/CD（POSIX exit code 0/1/2）。", options: {} },
    ],
    {
      x: 6.7, y: 1.95, w: 5.85, h: 4.7,
      fontFace: F_BODY, fontSize: 14, color: INK,
      paraSpaceAfter: 6, margin: 0
    }
  );
}

// =============================================================
// Slide 3 — Use Case Diagram
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "Use Case Diagram · 使用案例圖", "01 · Requirement");

  const dim = fitImage(7.6, 5.2, 1280, 830);
  s.addImage({
    path: path.join(HW2, "UseCaseDiagram.drawio.png"),
    x: 0.55, y: 1.15, w: dim.w, h: dim.h
  });

  const RX = 8.4, RY = 1.15;
  s.addText("狀態標記", {
    x: RX, y: RY, w: 4.4, h: 0.4,
    fontFace: F_BODY, fontSize: 16, bold: true, color: NAVY, margin: 0
  });

  s.addShape(pres.shapes.OVAL, { x: RX, y: RY + 0.55, w: 0.32, h: 0.32, fill: { color: GOLD }, line: { color: GOLD } });
  s.addText("M", { x: RX, y: RY + 0.55, w: 0.32, h: 0.32, fontFace: F_BODY, fontSize: 14, bold: true, color: NAVY, align: "center", valign: "middle", margin: 0 });
  s.addText("期中已實作 (Midterm)", { x: RX + 0.45, y: RY + 0.55, w: 4.0, h: 0.32, fontFace: F_BODY, fontSize: 12, color: INK, valign: "middle", margin: 0 });

  s.addShape(pres.shapes.OVAL, { x: RX, y: RY + 0.95, w: 0.32, h: 0.32, fill: { color: ICE }, line: { color: NAVY, width: 0.75 } });
  s.addText("F", { x: RX, y: RY + 0.95, w: 0.32, h: 0.32, fontFace: F_BODY, fontSize: 14, bold: true, color: NAVY, align: "center", valign: "middle", margin: 0 });
  s.addText("規劃於期末完成 (Final)", { x: RX + 0.45, y: RY + 0.95, w: 4.0, h: 0.32, fontFace: F_BODY, fontSize: 12, color: INK, valign: "middle", margin: 0 });

  const ucData = [
    [
      { text: "ID",   options: { bold: true, color: WHITE, fill: { color: NAVY }, align: "center" } },
      { text: "Use Case 名稱",   options: { bold: true, color: WHITE, fill: { color: NAVY } } },
      { text: "標記", options: { bold: true, color: WHITE, fill: { color: NAVY }, align: "center" } },
    ],
    ["UC-01", "Check Architecture Compliance", { text: "M", options: { align: "center", bold: true, color: NAVY, fill: { color: GOLD } } }],
    ["UC-02", "Define Style Profile",            { text: "F", options: { align: "center", bold: true, color: NAVY, fill: { color: ICE } } }],
    ["UC-03", "Check w/ Interactive Fix",        { text: "F", options: { align: "center", bold: true, color: NAVY, fill: { color: ICE } } }],
    ["UC-04", "Suppress a Violation",            { text: "M", options: { align: "center", bold: true, color: NAVY, fill: { color: GOLD } } }],
    ["UC-05", "Load Style Profile (subfunction)", { text: "M", options: { align: "center", bold: true, color: NAVY, fill: { color: GOLD } } }],
  ];
  s.addTable(ucData, {
    x: RX, y: RY + 1.45, w: 4.4, colW: [0.7, 2.7, 1.0],
    fontFace: F_BODY, fontSize: 11, color: INK,
    border: { pt: 0.5, color: RULE },
  });

  s.addText(
    "Actors：Developer（主要）·  CI System（次要，依賴 exit code 判斷結果）",
    {
      x: 0.55, y: 6.5, w: 12.2, h: 0.32,
      fontFace: F_BODY, fontSize: 11, italic: true, color: MUTED, margin: 0
    }
  );
}

// =============================================================
// Slide 4 — Significant UC: UC-01
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "Significant Use Case · UC-01 Check Architecture Compliance", "01 · Requirement");

  const headerRows = [
    [
      { text: "Scope",          options: { bold: true, fill: { color: ICE_SOFT } } },
      "arch-checker CLI",
      { text: "Level",          options: { bold: true, fill: { color: ICE_SOFT } } },
      "User Goal · Fully Dressed",
    ],
    [
      { text: "Primary Actor",  options: { bold: true, fill: { color: ICE_SOFT } } },
      "Developer",
      { text: "Secondary",      options: { bold: true, fill: { color: ICE_SOFT } } },
      "CI System（讀 exit code）",
    ],
  ];
  s.addTable(headerRows, {
    x: 0.55, y: 1.05, w: 12.2, colW: [1.4, 4.6, 1.4, 4.8],
    fontFace: F_BODY, fontSize: 11, color: INK,
    border: { pt: 0.5, color: RULE },
  });

  s.addText("Main Success Scenario · 主成功流程", {
    x: 0.55, y: 2.0, w: 6.2, h: 0.32,
    fontFace: F_BODY, fontSize: 14, bold: true, color: NAVY, margin: 0
  });
  s.addText(
    [
      { text: "Developer 對指定專案發出檢查請求，並選定 Style Profile", options: { bullet: { type: "number" }, breakLine: true } },
      { text: "系統確認專案目錄存在",                                       options: { bullet: { type: "number" }, breakLine: true } },
      { text: "系統讀取並解析 Style Profile YAML",                          options: { bullet: { type: "number" }, breakLine: true } },
      { text: "系統遞迴掃描所有 .java 檔案",                                options: { bullet: { type: "number" }, breakLine: true } },
      { text: "JavaParser 將每個檔案解析為 AST (CompilationUnit)",          options: { bullet: { type: "number" }, breakLine: true } },
      { text: "對每個檔案套用 Profile 中所有 ArchitectureConstraint",       options: { bullet: { type: "number" }, breakLine: true } },
      { text: "收集所有 Violation（檔案、行號、ruleId、訊息）",             options: { bullet: { type: "number" }, breakLine: true } },
      { text: "過濾已被 suppress 的 Violation",                             options: { bullet: { type: "number" }, breakLine: true } },
      { text: "以 Console 或 JSON 格式輸出 ViolationReport 與 summary",    options: { bullet: { type: "number" }, breakLine: true } },
      { text: "回傳 POSIX exit code（0 = 通過 / 1 = 有違規）",              options: { bullet: { type: "number" } } },
    ],
    { x: 0.55, y: 2.35, w: 6.5, h: 4.6, fontFace: F_BODY, fontSize: 11, color: INK, paraSpaceAfter: 2, margin: 0 }
  );

  s.addText("Extensions · 失敗與替代流程", {
    x: 7.15, y: 2.0, w: 5.6, h: 0.32,
    fontFace: F_BODY, fontSize: 14, bold: true, color: NAVY, margin: 0
  });
  s.addText(
    [
      { text: "2a · 專案目錄不存在 → exit 2",                       options: { bullet: true, breakLine: true } },
      { text: "3a · Profile 檔案不存在 → exit 2",                   options: { bullet: true, breakLine: true } },
      { text: "3b · YAML 語法錯誤 → 報錯並列出行號 → exit 2",       options: { bullet: true, breakLine: true } },
      { text: "3c · 規則型別未知 → 列出 validation 錯誤 → exit 2",  options: { bullet: true, breakLine: true } },
      { text: "4a · 目錄無 .java 檔 → 警告訊息 → exit 0",           options: { bullet: true, breakLine: true } },
      { text: "5a · 某檔語法錯誤無法解析 → 警告並跳過該檔",          options: { bullet: true, breakLine: true } },
      { text: "8a · 所有違規皆被 suppress → 報告全部通過 → exit 0",  options: { bullet: true } },
    ],
    { x: 7.15, y: 2.35, w: 5.6, h: 3.4, fontFace: F_BODY, fontSize: 11, color: INK, paraSpaceAfter: 4, margin: 0 }
  );

  s.addShape(pres.shapes.RECTANGLE, {
    x: 7.15, y: 5.85, w: 5.6, h: 1.05,
    fill: { color: ICE_SOFT }, line: { color: ICE_SOFT }
  });
  s.addText("Special Requirements · 非功能需求", {
    x: 7.3, y: 5.9, w: 5.4, h: 0.32,
    fontFace: F_BODY, fontSize: 12, bold: true, color: NAVY, margin: 0
  });
  s.addText(
    [
      { text: "NFR-01 Performance · 1,000 檔案需於 10 秒內完成", options: { breakLine: true } },
      { text: "NFR-02 Usability · 每則錯誤含原因 + 位置 + 建議修正", options: { breakLine: true } },
      { text: "NFR-05 CI Compatibility · 遵守 POSIX exit code 標準", options: {} },
    ],
    { x: 7.3, y: 6.18, w: 5.4, h: 0.7, fontFace: F_BODY, fontSize: 10, color: INK, margin: 0 }
  );
}

// =============================================================
// Slide 5 — Significant UC: UC-04
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "Significant Use Case · UC-04 Suppress a Violation", "01 · Requirement");

  const headerRows = [
    [
      { text: "Scope",         options: { bold: true, fill: { color: ICE_SOFT } } },
      "arch-checker CLI",
      { text: "Level",         options: { bold: true, fill: { color: ICE_SOFT } } },
      "User Goal · Brief",
    ],
    [
      { text: "Primary Actor", options: { bold: true, fill: { color: ICE_SOFT } } },
      "Developer",
      { text: "觸發時機",      options: { bold: true, fill: { color: ICE_SOFT } } },
      "將已知的違規標註為「可接受」",
    ],
  ];
  s.addTable(headerRows, {
    x: 0.55, y: 1.05, w: 12.2, colW: [1.4, 4.6, 1.4, 4.8],
    fontFace: F_BODY, fontSize: 11, color: INK,
    border: { pt: 0.5, color: RULE },
  });

  s.addText("Main Success Scenario · 主成功流程", {
    x: 0.55, y: 2.0, w: 6.2, h: 0.32,
    fontFace: F_BODY, fontSize: 14, bold: true, color: NAVY, margin: 0
  });
  s.addText(
    [
      { text: "Developer 發出 suppress(constraintId, filePath, lineNumber, reason)", options: { bullet: { type: "number" }, breakLine: true } },
      { text: "SuppressCommand 驗證輸入（不可為空、檔案需存在）",                       options: { bullet: { type: "number" }, breakLine: true } },
      { text: "SuppressionService 建立新的 Suppression aggregate",                    options: { bullet: { type: "number" }, breakLine: true } },
      { text: "SuppressionRepository 將其持久化至 .arch-checker-suppressions.yaml",   options: { bullet: { type: "number" }, breakLine: true } },
      { text: "系統回傳 suppressionId 與 timestamp，exit 0",                          options: { bullet: { type: "number" }, breakLine: true } },
      { text: "下次 UC-01 執行時，該 Violation 將自動被過濾",                          options: { bullet: { type: "number" } } },
    ],
    { x: 0.55, y: 2.35, w: 6.5, h: 3.0, fontFace: F_BODY, fontSize: 12, color: INK, paraSpaceAfter: 4, margin: 0 }
  );

  s.addShape(pres.shapes.RECTANGLE, {
    x: 0.55, y: 5.55, w: 6.5, h: 1.3,
    fill: { color: ICE_SOFT }, line: { color: ICE_SOFT }
  });
  s.addShape(pres.shapes.RECTANGLE, {
    x: 0.55, y: 5.55, w: 0.08, h: 1.3,
    fill: { color: GOLD }, line: { color: GOLD }
  });
  s.addText("為何重要", {
    x: 0.78, y: 5.6, w: 6.2, h: 0.30,
    fontFace: F_BODY, fontSize: 12, bold: true, color: NAVY, margin: 0
  });
  s.addText(
    "真實 codebase 必然存在被接受的例外。若無 suppress 機制，每次 CI 執行都會被舊有違規淹沒。UC-04 將「個別接受」的決策固化為團隊知識，跨次執行皆可繼承。",
    { x: 0.78, y: 5.9, w: 6.2, h: 0.95, fontFace: F_BODY, fontSize: 11, color: INK, margin: 0 }
  );

  s.addText("Extensions · 失敗流程", {
    x: 7.15, y: 2.0, w: 5.6, h: 0.32,
    fontFace: F_BODY, fontSize: 14, bold: true, color: NAVY, margin: 0
  });
  s.addText(
    [
      { text: "2a · constraintId 為空 → exit 2 (validation error)",         options: { bullet: true, breakLine: true } },
      { text: "2b · reason 為空 → exit 2（必須留下審計理由）",               options: { bullet: true, breakLine: true } },
      { text: "2c · 目標檔案不存在 → exit 2",                                options: { bullet: true, breakLine: true } },
      { text: "4a · Repository 寫入失敗 → exit 2（避免遺孤 Suppression）",   options: { bullet: true } },
    ],
    { x: 7.15, y: 2.35, w: 5.6, h: 2.5, fontFace: F_BODY, fontSize: 12, color: INK, paraSpaceAfter: 4, margin: 0 }
  );

  s.addText("GRASP 模式重點", {
    x: 7.15, y: 5.0, w: 5.6, h: 0.32,
    fontFace: F_BODY, fontSize: 14, bold: true, color: NAVY, margin: 0
  });
  s.addText(
    [
      { text: "Information Expert · SuppressionService 主導建立流程", options: { bullet: true, breakLine: true } },
      { text: "Creator · 由 Service 建立 Suppression aggregate",       options: { bullet: true, breakLine: true } },
      { text: "Indirection / Protected Variation · Repository 隱藏 YAML I/O", options: { bullet: true } },
    ],
    { x: 7.15, y: 5.35, w: 5.6, h: 1.7, fontFace: F_BODY, fontSize: 11, color: INK, paraSpaceAfter: 4, margin: 0 }
  );
}

// =============================================================
// Slide 6 — 展示流程
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "Demonstration · 展示流程", "02 · Demo");

  const cards = [
    {
      tag: "Scenario A · 成功流程",  color: GOLD,
      title: "UC-01 happy path — 對自身程式做檢查",
      lines: [
        "以 mvn 編譯與打包",
        "java …Main check src/main/java arch.yaml",
        "→ Checked 26 file(s); 0 violation(s); exit 0",
      ],
    },
    {
      tag: "Scenario B · 替代流程", color: TEAL,
      title: "UC-01 加上 --json (FEA-04)",
      lines: [
        "同一檢查改以機器可讀 JSON 輸出",
        "可直接給 CI 解析器 / dashboard 使用",
        "展示 Reporter 多型 (polymorphism)",
      ],
    },
    {
      tag: "Scenario C · 替代流程", color: CORAL,
      title: "UC-04 後再執行 UC-01（suppress 過濾）",
      lines: [
        "suppress NamingRule … --reason 'legacy'",
        "→ 系統持久化 suppressionId 與 timestamp",
        "再次 check 時，該違規顯示為 suppressed",
      ],
    },
  ];

  const cardW = 4.0, gap = 0.18;
  const startX = (SW - (3 * cardW + 2 * gap)) / 2;
  cards.forEach((c, i) => {
    const x = startX + i * (cardW + gap);
    const y = 1.5;
    s.addShape(pres.shapes.RECTANGLE, {
      x, y, w: cardW, h: 5.4,
      fill: { color: WHITE }, line: { color: RULE, width: 0.75 },
      shadow: { type: "outer", color: "000000", blur: 8, offset: 2, angle: 90, opacity: 0.08 }
    });
    s.addShape(pres.shapes.RECTANGLE, {
      x, y, w: cardW, h: 0.5,
      fill: { color: c.color }, line: { color: c.color }
    });
    s.addText(c.tag, {
      x: x + 0.15, y, w: cardW - 0.3, h: 0.5,
      fontFace: F_BODY, fontSize: 11, bold: true,
      color: NAVY_DARK, valign: "middle", charSpacing: 3, margin: 0
    });

    s.addText(c.title, {
      x: x + 0.25, y: y + 0.85, w: cardW - 0.5, h: 1.1,
      fontFace: F_BODY, fontSize: 16, bold: true, color: NAVY, margin: 0
    });

    s.addText(
      c.lines.map((ln, j) => ({
        text: ln,
        options: { bullet: true, breakLine: j < c.lines.length - 1 }
      })),
      { x: x + 0.25, y: y + 2.1, w: cardW - 0.5, h: 3.0,
        fontFace: F_BODY, fontSize: 12, color: INK, paraSpaceAfter: 6, margin: 0 }
    );
  });

  s.addText("依 guideline 要求 — Demonstration 安排於設計文件之前。",
    { x: 0.55, y: 6.55, w: 12.2, h: 0.32, fontFace: F_BODY, fontSize: 10, italic: true, color: MUTED, align: "center", margin: 0 });
}

// =============================================================
// Slide 7 — Demo Snapshot · UC-01 成功
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "Demo Snapshot · UC-01 成功流程", "02 · Demo");

  s.addShape(pres.shapes.RECTANGLE, {
    x: 0.55, y: 1.15, w: 12.2, h: 5.3,
    fill: { color: NAVY_DARK }, line: { color: NAVY_DARK }
  });
  s.addShape(pres.shapes.RECTANGLE, {
    x: 0.55, y: 1.15, w: 12.2, h: 0.36,
    fill: { color: NAVY }, line: { color: NAVY }
  });
  s.addShape(pres.shapes.OVAL, { x: 0.7, y: 1.23, w: 0.2, h: 0.2, fill: { color: CORAL }, line: { color: CORAL } });
  s.addShape(pres.shapes.OVAL, { x: 0.95, y: 1.23, w: 0.2, h: 0.2, fill: { color: GOLD }, line: { color: GOLD } });
  s.addShape(pres.shapes.OVAL, { x: 1.20, y: 1.23, w: 0.2, h: 0.2, fill: { color: "70C77B" }, line: { color: "70C77B" } });
  s.addText("zsh — arch-checker", {
    x: 5.0, y: 1.15, w: 3.3, h: 0.36, fontFace: F_BODY, fontSize: 10, color: ICE,
    align: "center", valign: "middle", margin: 0
  });

  s.addText(
    [
      { text: "$ mvn -B package -DskipTests dependency:copy-dependencies \\\n      -DincludeScope=runtime -DoutputDirectory=target/lib", options: { color: GOLD, breakLine: true } },
      { text: "[INFO] BUILD SUCCESS", options: { color: "70C77B", breakLine: true } },
      { text: "", options: { breakLine: true } },
      { text: "$ java -cp 'target/arch-checker.jar;target/lib/*' \\\n      com.archchecker.cli.Main check src/main/java arch.yaml", options: { color: GOLD, breakLine: true } },
      { text: "-- Checked 26 file(s); 0 violation(s); 0 suppressed.", options: { color: WHITE, breakLine: true } },
      { text: "", options: { breakLine: true } },
      { text: "$ echo $?", options: { color: GOLD, breakLine: true } },
      { text: "0", options: { color: "70C77B", bold: true } },
    ],
    {
      x: 0.85, y: 1.7, w: 11.6, h: 4.5,
      fontFace: F_MONO, fontSize: 14, color: WHITE, margin: 0
    }
  );

  s.addText(
    "arch-checker 將自身 26 個 class 對 arch.yaml 檢查 — 0 violations，exit 0。\n" +
    "對應 UC-01 主流程（步驟 1-10）以及 NFR-05（POSIX exit code）。",
    {
      x: 0.55, y: 6.5, w: 12.2, h: 0.55,
      fontFace: F_BODY, fontSize: 10, italic: true, color: MUTED, margin: 0
    }
  );
}

// =============================================================
// Slide 8 — Demo Snapshot · UC-04 + 重檢
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "Demo Snapshot · UC-04 Suppress + 重檢 (UC-01)", "02 · Demo");

  s.addShape(pres.shapes.RECTANGLE, {
    x: 0.55, y: 1.15, w: 12.2, h: 5.3,
    fill: { color: NAVY_DARK }, line: { color: NAVY_DARK }
  });
  s.addShape(pres.shapes.RECTANGLE, {
    x: 0.55, y: 1.15, w: 12.2, h: 0.36,
    fill: { color: NAVY }, line: { color: NAVY }
  });
  s.addShape(pres.shapes.OVAL, { x: 0.7, y: 1.23, w: 0.2, h: 0.2, fill: { color: CORAL }, line: { color: CORAL } });
  s.addShape(pres.shapes.OVAL, { x: 0.95, y: 1.23, w: 0.2, h: 0.2, fill: { color: GOLD }, line: { color: GOLD } });
  s.addShape(pres.shapes.OVAL, { x: 1.20, y: 1.23, w: 0.2, h: 0.2, fill: { color: "70C77B" }, line: { color: "70C77B" } });
  s.addText("zsh — arch-checker", {
    x: 5.0, y: 1.15, w: 3.3, h: 0.36, fontFace: F_BODY, fontSize: 10, color: ICE,
    align: "center", valign: "middle", margin: 0
  });

  s.addText(
    [
      { text: "# 1. 第一次檢查 — 出現一筆 NamingRule 違規", options: { color: ICE, breakLine: true } },
      { text: "$ java …Main check sample-project demo.yaml", options: { color: GOLD, breakLine: true } },
      { text: "[VIOLATION] NamingRule  src/.../UserManager.java:7  Class must end with 'Service'", options: { color: CORAL, breakLine: true } },
      { text: "-- Checked 3 file(s); 1 violation(s); 0 suppressed.   exit 1", options: { color: WHITE, breakLine: true } },
      { text: "", options: { breakLine: true } },
      { text: "# 2. 將該已知個案 suppress 起來 (UC-04)", options: { color: ICE, breakLine: true } },
      { text: "$ java …Main suppress NamingRule .../UserManager.java 7 \\", options: { color: GOLD, breakLine: true } },
      { text: "         --reason '舊有進入點，預計 v2 重新命名'", options: { color: GOLD, breakLine: true } },
      { text: "Suppression saved · id=NamingRule@UserManager.java:7  ts=2026-04-29T10:42Z", options: { color: "70C77B", breakLine: true } },
      { text: "", options: { breakLine: true } },
      { text: "# 3. 重新執行 UC-01 — 違規被過濾", options: { color: ICE, breakLine: true } },
      { text: "$ java …Main check sample-project demo.yaml", options: { color: GOLD, breakLine: true } },
      { text: "-- Checked 3 file(s); 0 violation(s); 1 suppressed.   exit 0", options: { color: "70C77B", bold: true } },
    ],
    {
      x: 0.85, y: 1.62, w: 11.6, h: 4.7,
      fontFace: F_MONO, fontSize: 11, color: WHITE, paraSpaceAfter: 2, margin: 0
    }
  );

  s.addText(
    "對應 UC-04 主流程 + UC-01 步驟 8（過濾已 suppress 之違規）。展示 GRASP Indirection：Repository 獨佔 YAML I/O。",
    {
      x: 0.55, y: 6.5, w: 12.2, h: 0.45,
      fontFace: F_BODY, fontSize: 10, italic: true, color: MUTED, margin: 0
    }
  );
}

// =============================================================
// Slide 9 — Demo Snapshot · JSON / Help
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "Demo Snapshot · 替代輸出 (FEA-04)", "02 · Demo");

  s.addShape(pres.shapes.RECTANGLE, {
    x: 0.55, y: 1.15, w: 12.2, h: 5.3,
    fill: { color: NAVY_DARK }, line: { color: NAVY_DARK }
  });
  s.addShape(pres.shapes.RECTANGLE, {
    x: 0.55, y: 1.15, w: 12.2, h: 0.36,
    fill: { color: NAVY }, line: { color: NAVY }
  });
  s.addShape(pres.shapes.OVAL, { x: 0.7, y: 1.23, w: 0.2, h: 0.2, fill: { color: CORAL }, line: { color: CORAL } });
  s.addShape(pres.shapes.OVAL, { x: 0.95, y: 1.23, w: 0.2, h: 0.2, fill: { color: GOLD }, line: { color: GOLD } });
  s.addShape(pres.shapes.OVAL, { x: 1.20, y: 1.23, w: 0.2, h: 0.2, fill: { color: "70C77B" }, line: { color: "70C77B" } });
  s.addText("zsh — arch-checker", {
    x: 5.0, y: 1.15, w: 3.3, h: 0.36, fontFace: F_BODY, fontSize: 10, color: ICE,
    align: "center", valign: "middle", margin: 0
  });

  s.addText(
    [
      { text: "# 給 CI dashboard 使用的 JSON 輸出 (--json flag)", options: { color: ICE, breakLine: true } },
      { text: "$ java …Main check src/main/java arch.yaml --json", options: { color: GOLD, breakLine: true } },
      { text: '{"checkedFiles":26,"violationCount":0,"suppressedCount":0,"violations":[]}', options: { color: WHITE, breakLine: true } },
      { text: "", options: { breakLine: true } },
      { text: "# picocli 自動產生的 help — 可從 CLI 探索可用 use case", options: { color: ICE, breakLine: true } },
      { text: "$ java …Main --help", options: { color: GOLD, breakLine: true } },
      { text: "Usage: arch-checker [-hV] [COMMAND]", options: { color: WHITE, breakLine: true } },
      { text: "A Fluent Architectural Style Compliance Tool for Java.", options: { color: WHITE, breakLine: true } },
      { text: "  -h, --help     Show this help message and exit.", options: { color: WHITE, breakLine: true } },
      { text: "  -V, --version  Print version information and exit.", options: { color: WHITE, breakLine: true } },
      { text: "Commands:", options: { color: WHITE, breakLine: true } },
      { text: "  check     Check architecture compliance against a Style Profile.", options: { color: WHITE, breakLine: true } },
      { text: "  suppress  Mark a specific violation as 'known and accepted'.", options: { color: WHITE } },
    ],
    {
      x: 0.85, y: 1.62, w: 11.6, h: 4.7,
      fontFace: F_MONO, fontSize: 12, color: WHITE, paraSpaceAfter: 2, margin: 0
    }
  );

  s.addText(
    "FEA-04 多種報表格式 · Reporter polymorphism — 同一 Service 透過 strategy 產生 Console / JSON。",
    {
      x: 0.55, y: 6.5, w: 12.2, h: 0.45,
      fontFace: F_BODY, fontSize: 10, italic: true, color: MUTED, margin: 0
    }
  );
}

// =============================================================
// Slide 10 — Domain Model
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "Domain Model · 領域模型", "03 · Analysis");

  const dim = fitImage(8.4, 5.5, 1170, 810);
  s.addImage({ path: path.join(HW3, "Domain class diagram.drawio.png"),
    x: 0.55, y: 1.15, w: dim.w, h: dim.h });

  const RX = 9.2;
  s.addText("12 個概念 · 4 條繼承 · 10 條關聯", {
    x: RX, y: 1.15, w: 3.6, h: 0.4,
    fontFace: F_BODY, fontSize: 13, bold: true, color: NAVY, margin: 0
  });
  s.addText(
    [
      { text: "ComplianceCheck — 整體流程的 aggregate", options: { bullet: true, breakLine: true } },
      { text: "StyleProfile — 持有 1..* 個 ArchitectureConstraint", options: { bullet: true, breakLine: true } },
      { text: "ArchitectureConstraint (abstract) → 4 個具體規則", options: { bullet: true, breakLine: true } },
      { text: "Project · 持有 1..* 個 File", options: { bullet: true, breakLine: true } },
      { text: "Violation — 包含 file、行號、訊息、ruleId", options: { bullet: true, breakLine: true } },
      { text: "Suppression — 標的某 Constraint，下次執行被忽略", options: { bullet: true } },
    ],
    {
      x: RX, y: 1.6, w: 3.6, h: 5.2,
      fontFace: F_BODY, fontSize: 11, color: INK,
      paraSpaceAfter: 6, margin: 0
    }
  );
}

// =============================================================
// Slide 11 — Logical Architecture
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "Logical Architecture · Package Diagram", "04 · Design");

  const dim = fitImage(7.6, 5.6, 540, 760);
  s.addImage({ path: path.join(HW4, "3.1-logical-architecture.png"),
    x: 0.55, y: 1.1, w: dim.w, h: dim.h });

  const RX = 6.6;
  s.addText("各層職責（由上而下）", {
    x: RX, y: 1.15, w: 6.4, h: 0.4,
    fontFace: F_BODY, fontSize: 16, bold: true, color: NAVY, margin: 0
  });
  const layers = [
    { name: "Presentation · cli", color: NAVY,
      desc: "picocli @Command 進入點：CheckCommand、SuppressCommand" },
    { name: "Application", color: TEAL,
      desc: "use case services 與技術介面（ports）：CodeParser、ProfileRepository、SuppressionRepository、Reporter" },
    { name: "Domain", color: GOLD,
      desc: "4 個 sub-package — constraint、profile、codebase、compliance — 純業務模型 + 多型 validate(files)" },
    { name: "Infrastructure", color: CORAL,
      desc: "實作 ports 的 adapters：JavaParserAdapter、YamlProfileRepository、ConsoleReporter / JsonReporter、YamlSuppressionRepository" },
  ];
  let y = 1.7;
  layers.forEach(L => {
    s.addShape(pres.shapes.RECTANGLE, {
      x: RX, y, w: 0.18, h: 1.05,
      fill: { color: L.color }, line: { color: L.color }
    });
    s.addText(L.name, {
      x: RX + 0.3, y, w: 6.2, h: 0.32,
      fontFace: F_BODY, fontSize: 13, bold: true, color: NAVY, margin: 0
    });
    s.addText(L.desc, {
      x: RX + 0.3, y: y + 0.32, w: 6.2, h: 0.7,
      fontFace: F_BODY, fontSize: 11, color: INK, margin: 0
    });
    y += 1.18;
  });
}

// =============================================================
// Slide 12 — UC-01 SSD
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "UC-01 · System Sequence Diagram", "04 · Design — Use-Case Realization");

  const dim = fitImage(9.0, 5.5, 920, 660);
  s.addImage({ path: path.join(HW4, "3.2.1-uc01-ssd.png"),
    x: 0.55, y: 1.15, w: dim.w, h: dim.h });

  const RX = 10.0;
  s.addText("System Event", { x: RX, y: 1.15, w: 3.0, h: 0.32, fontFace: F_BODY, fontSize: 13, bold: true, color: NAVY, margin: 0 });
  s.addText("runCheck(projectPath, profilePath)", {
    x: RX, y: 1.5, w: 3.0, h: 0.4,
    fontFace: F_MONO, fontSize: 11, color: NAVY_DARK, fill: { color: ICE_SOFT }, margin: 4
  });
  s.addText("回傳值", { x: RX, y: 2.05, w: 3.0, h: 0.32, fontFace: F_BODY, fontSize: 13, bold: true, color: NAVY, margin: 0 });
  s.addText(
    [
      { text: "violationReport（console output）", options: { bullet: true, breakLine: true } },
      { text: "exitCode  ∈ {0, 1, 2}", options: { bullet: true } },
    ],
    { x: RX, y: 2.4, w: 3.0, h: 1.6, fontFace: F_BODY, fontSize: 11, color: INK, paraSpaceAfter: 4, margin: 0 }
  );

  s.addText("解讀", { x: RX, y: 4.0, w: 3.0, h: 0.32, fontFace: F_BODY, fontSize: 13, bold: true, color: NAVY, margin: 0 });
  s.addText(
    "Developer → arch-checker · 一道 CLI 指令對應 System 黑盒的單一訊息。CI 讀 exit code，工程師讀 report。",
    { x: RX, y: 4.35, w: 3.0, h: 2.5, fontFace: F_BODY, fontSize: 11, color: INK, margin: 0 }
  );
}

// =============================================================
// Slide 13 — UC-01 SD with GRASP
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "UC-01 · Sequence Diagram with GRASP", "04 · Design — Use-Case Realization");

  const dim = fitImage(12.5, 3.85, 1700, 1180);
  s.addImage({ path: path.join(HW4, "3.2.1-uc01-sd.png"),
    x: (SW - dim.w) / 2, y: 1.0, w: dim.w, h: dim.h });

  const tableY = 1.0 + dim.h + 0.15;
  const data = [
    [
      { text: "GRASP Pattern", options: { bold: true, color: WHITE, fill: { color: NAVY }, align: "center" } },
      { text: "套用對象",     options: { bold: true, color: WHITE, fill: { color: NAVY }, align: "center" } },
      { text: "責任",         options: { bold: true, color: WHITE, fill: { color: NAVY }, align: "center" } },
    ],
    ["Controller",          ":CheckCommand",             "接收 system event 後委派給 Service（不含業務邏輯）"],
    ["Information Expert",  ":ComplianceCheckService",   "主導檢查流程，整合 Profile + Files + Constraints"],
    ["Polymorphism",        "ArchitectureConstraint",    "validate(files) 由 Naming/Dependency/Supertype/PackageRule 各自實作"],
    ["Indirection / PV",    ":Adapter / :Repository",    "JavaParser、YAML 等技術隔離於 ports（CodeParser、ProfileRepository …）"],
    ["Pure Fabrication",    ":Reporter",                 "Console / JSON 格式化從 domain 解耦（同時達成 Low Coupling）"],
  ];
  s.addTable(data, {
    x: 0.55, y: tableY, w: 12.2, colW: [2.2, 2.8, 7.2],
    fontFace: F_BODY, fontSize: 9, color: INK,
    border: { pt: 0.5, color: RULE },
  });
}

// =============================================================
// Slide 14 — UC-04 SSD + SD with GRASP
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "UC-04 · SSD + Sequence Diagram with GRASP", "04 · Design — Use-Case Realization");

  const ssdDim = fitImage(5.4, 4.0, 920, 520);
  s.addImage({ path: path.join(HW4, "3.2.3-uc04-ssd.png"),
    x: 0.55, y: 1.1, w: ssdDim.w, h: ssdDim.h });
  s.addText("System Sequence Diagram", {
    x: 0.55, y: 1.1 + ssdDim.h + 0.05, w: ssdDim.w, h: 0.3,
    fontFace: F_BODY, fontSize: 10, italic: true, color: MUTED, align: "center", margin: 0
  });

  const sdDim = fitImage(7.0, 4.4, 1320, 1100);
  s.addImage({ path: path.join(HW4, "3.2.3-uc04-sd.png"),
    x: 6.2, y: 1.1, w: sdDim.w, h: sdDim.h });
  s.addText("Sequence Diagram", {
    x: 6.2, y: 1.1 + sdDim.h + 0.05, w: sdDim.w, h: 0.3,
    fontFace: F_BODY, fontSize: 10, italic: true, color: MUTED, align: "center", margin: 0
  });

  const data = [
    [
      { text: "Pattern",  options: { bold: true, color: WHITE, fill: { color: NAVY }, align: "center" } },
      { text: "套用對象", options: { bold: true, color: WHITE, fill: { color: NAVY }, align: "center" } },
      { text: "理由",     options: { bold: true, color: WHITE, fill: { color: NAVY }, align: "center" } },
    ],
    ["Controller",                    ":SuppressCommand",        "將 CLI 參數適配為 service 呼叫"],
    ["Information Expert / Creator",  ":SuppressionService",     "驗證輸入並 «create» Suppression aggregate"],
    ["Indirection / PV",              ":SuppressionRepository",  "持久化至 .arch-checker-suppressions.yaml；YAML I/O 全部隱藏"],
  ];
  s.addTable(data, {
    x: 0.55, y: 5.85, w: 12.2, colW: [2.6, 3.6, 6.0],
    fontFace: F_BODY, fontSize: 10, color: INK,
    border: { pt: 0.5, color: RULE },
  });
}

// =============================================================
// Slide 15 — Design Class Diagram + 與 Domain Model 的差異
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "Design Class Diagram · Domain Layer（DCD vs Domain Model）", "04 · Design");

  const dim = fitImage(8.6, 5.6, 1100, 700);
  s.addImage({ path: path.join(HW4, "3.3-design-class-diagram.png"),
    x: 0.55, y: 1.1, w: dim.w, h: dim.h });

  const RX = 9.4;
  s.addText("DCD 與 Domain Model 的差異", {
    x: RX, y: 1.1, w: 3.7, h: 0.32,
    fontFace: F_BODY, fontSize: 14, bold: true, color: NAVY, margin: 0
  });
  s.addText(
    [
      { text: "12 個概念維持不變 — 名稱、角色、multiplicity 全部沿用", options: { bullet: true, breakLine: true } },
      { text: "DCD 額外加入 navigability、method 簽章、abstract / interface 等 stereotype", options: { bullet: true, breakLine: true } },
      { text: "ArchitectureConstraint 升級為 ", options: {} },
      { text: "abstract", options: { italic: true } },
      { text: "，提供多型 validate(files)", options: { breakLine: true } },
      { text: "DCD 範圍限縮於 ", options: {} },
      { text: "Domain Layer", options: { bold: true } },
      { text: " — Application/Infrastructure 已於 §3.1 package diagram 呈現", options: {} },
    ],
    {
      x: RX, y: 1.45, w: 3.7, h: 3.2,
      fontFace: F_BODY, fontSize: 11, color: INK,
      paraSpaceAfter: 6, margin: 0
    }
  );

  s.addShape(pres.shapes.RECTANGLE, {
    x: RX, y: 4.85, w: 3.7, h: 1.95,
    fill: { color: ICE_SOFT }, line: { color: ICE_SOFT }
  });
  s.addText("為何 DCD 要分層繪製？", {
    x: RX + 0.15, y: 4.95, w: 3.4, h: 0.32,
    fontFace: F_BODY, fontSize: 12, bold: true, color: NAVY, margin: 0
  });
  s.addText(
    "Larman Ch.16 — DCD 應分層呈現。Domain DCD 聚焦業務概念；Application DCD 描述 services 與 ports；Infrastructure DCD 描述 adapters。將不同層級混在同一張會嚴重損及可讀性。",
    { x: RX + 0.15, y: 5.25, w: 3.4, h: 1.55,
      fontFace: F_BODY, fontSize: 10, color: INK, margin: 0 }
  );
}

// =============================================================
// Slide 16 — Implementation Class Diagram
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "Implementation Class Diagram (HW5)", "05 · Implementation");

  const dim = fitImage(9.0, 5.6, 600, 660);
  s.addImage({ path: path.join(HW5, "5.1-implementation-class-diagram.png"),
    x: 0.55, y: 1.1, w: dim.w, h: dim.h });

  const RX = 9.8;
  s.addText("Δ Implementation vs DCD", {
    x: RX, y: 1.1, w: 3.4, h: 0.32,
    fontFace: F_BODY, fontSize: 14, bold: true, color: NAVY, margin: 0
  });
  const data = [
    [
      { text: "範圍",  options: { bold: true, color: WHITE, fill: { color: NAVY } } },
      { text: "+",     options: { bold: true, color: WHITE, fill: { color: NAVY }, align: "center" } },
      { text: "−",     options: { bold: true, color: WHITE, fill: { color: NAVY }, align: "center" } },
      { text: "~",     options: { bold: true, color: WHITE, fill: { color: NAVY }, align: "center" } },
    ],
    [{ text: "Domain · class", options: {} }, { text: "0", options: { align: "center" } },  { text: "0", options: { align: "center" } },  { text: "0", options: { align: "center" } }],
    [{ text: "Domain · method", options: {} },{ text: "23", options: { align: "center", bold: true, color: TEAL } }, { text: "1", options: { align: "center", color: CORAL } },  { text: "0", options: { align: "center" } }],
    [{ text: "Non-domain · class（HW5 新增）", options: {} },{ text: "14", options: { align: "center", bold: true, color: TEAL } }, { text: "0", options: { align: "center" } }, { text: "0", options: { align: "center" } }],
    [{ text: "Non-domain · method", options: {} },{ text: "29", options: { align: "center", bold: true, color: TEAL } },  { text: "0", options: { align: "center" } },  { text: "0", options: { align: "center" } }],
  ];
  s.addTable(data, {
    x: RX, y: 1.5, w: 3.4, colW: [1.8, 0.55, 0.5, 0.55],
    fontFace: F_BODY, fontSize: 10, color: INK,
    border: { pt: 0.5, color: RULE },
  });

  s.addText(
    [
      { text: "Domain class set 完全不變 — 設計通過實作驗證。", options: { bullet: true, breakLine: true } },
      { text: "新增多為 reporter 所需之 accessor / value getter。", options: { bullet: true, breakLine: true } },
      { text: "唯一移除：", options: { bullet: true } },
      { text: "ViolationReport.getExitCode()", options: { italic: true } },
      { text: " 改由 ComplianceCheck 持有（單一資料來源原則）。", options: { breakLine: true } },
      { text: "0 個 method 簽章被修改。", options: { bullet: true } },
    ],
    {
      x: RX, y: 3.7, w: 3.4, h: 3.0,
      fontFace: F_BODY, fontSize: 10, color: INK, paraSpaceAfter: 4, margin: 0
    }
  );
}

// =============================================================
// Slide 17 — Source code · GRASP Polymorphism
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "原始碼節錄 · GRASP Polymorphism", "05 · Implementation");

  s.addShape(pres.shapes.RECTANGLE, { x: 0.55, y: 1.1, w: 6.2, h: 5.3, fill: { color: NAVY_DARK }, line: { color: NAVY_DARK } });
  s.addText("domain.constraint.ArchitectureConstraint", {
    x: 0.65, y: 1.15, w: 6.0, h: 0.3,
    fontFace: F_MONO, fontSize: 10, color: ICE, margin: 0
  });
  s.addText(
    [
      { text: "public abstract class ArchitectureConstraint {",                                options: { color: WHITE, breakLine: true } },
      { text: "    protected final String id;",                                                options: { color: WHITE, breakLine: true } },
      { text: "    protected final String description;",                                       options: { color: WHITE, breakLine: true } },
      { text: "",                                                                              options: { breakLine: true } },
      { text: "    protected ArchitectureConstraint(String id, String description) {",        options: { color: WHITE, breakLine: true } },
      { text: "        this.id = id; this.description = description;",                         options: { color: WHITE, breakLine: true } },
      { text: "    }",                                                                          options: { color: WHITE, breakLine: true } },
      { text: "",                                                                              options: { breakLine: true } },
      { text: "    public String getId()          { return id; }",                              options: { color: WHITE, breakLine: true } },
      { text: "    public String getDescription() { return description; }",                     options: { color: WHITE, breakLine: true } },
      { text: "",                                                                              options: { breakLine: true } },
      { text: "    public abstract List<Violation> validate(List<File> files);",                options: { color: GOLD, bold: true, breakLine: true } },
      { text: "",                                                                              options: { breakLine: true } },
      { text: "    protected static boolean packageMatches(",                                  options: { color: WHITE, breakLine: true } },
      { text: "            String packageName, String pattern) {",                             options: { color: WHITE, breakLine: true } },
      { text: "        String regex = pattern.replace(\".\", \"\\\\.\")",                      options: { color: WHITE, breakLine: true } },
      { text: "                .replace(\"**\", \"<<DS>>\")",                                  options: { color: WHITE, breakLine: true } },
      { text: "                .replace(\"*\", \"[^.]*\")",                                    options: { color: WHITE, breakLine: true } },
      { text: "                .replace(\"<<DS>>\", \".*\");",                                 options: { color: WHITE, breakLine: true } },
      { text: "        return packageName.matches(regex);",                                    options: { color: WHITE, breakLine: true } },
      { text: "    }",                                                                          options: { color: WHITE, breakLine: true } },
      { text: "}",                                                                              options: { color: WHITE } },
    ],
    {
      x: 0.65, y: 1.5, w: 6.0, h: 4.85,
      fontFace: F_MONO, fontSize: 10, color: WHITE, margin: 0
    }
  );

  s.addShape(pres.shapes.RECTANGLE, { x: 6.95, y: 1.1, w: 5.8, h: 5.3, fill: { color: NAVY_DARK }, line: { color: NAVY_DARK } });
  s.addText("domain.constraint.NamingRule", {
    x: 7.05, y: 1.15, w: 5.6, h: 0.3,
    fontFace: F_MONO, fontSize: 10, color: ICE, margin: 0
  });
  s.addText(
    [
      { text: "public class NamingRule extends ArchitectureConstraint {",                  options: { color: WHITE, breakLine: true } },
      { text: "    private final String classNamePattern;",                                  options: { color: WHITE, breakLine: true } },
      { text: "",                                                                            options: { breakLine: true } },
      { text: "    public NamingRule(String id, String description,",                       options: { color: WHITE, breakLine: true } },
      { text: "                      String classNamePattern) {",                           options: { color: WHITE, breakLine: true } },
      { text: "        super(id, description);",                                             options: { color: WHITE, breakLine: true } },
      { text: "        this.classNamePattern = classNamePattern;",                           options: { color: WHITE, breakLine: true } },
      { text: "    }",                                                                        options: { color: WHITE, breakLine: true } },
      { text: "",                                                                            options: { breakLine: true } },
      { text: "    @Override",                                                               options: { color: GOLD, bold: true, breakLine: true } },
      { text: "    public List<Violation> validate(List<File> files) {",                    options: { color: GOLD, bold: true, breakLine: true } },
      { text: "        List<Violation> vs = new ArrayList<>();",                             options: { color: WHITE, breakLine: true } },
      { text: "        for (File f : files) {",                                              options: { color: WHITE, breakLine: true } },
      { text: "            String name = stripExt(f.getFilePath());",                        options: { color: WHITE, breakLine: true } },
      { text: "            if (!name.matches(classNamePattern.replace(\"*\",\".*\")))",     options: { color: WHITE, breakLine: true } },
      { text: "                vs.add(new Violation(f, 1, this,",                            options: { color: WHITE, breakLine: true } },
      { text: "                    \"Class \" + name + \" violates \" + classNamePattern));", options: { color: WHITE, breakLine: true } },
      { text: "        }",                                                                    options: { color: WHITE, breakLine: true } },
      { text: "        return vs;",                                                           options: { color: WHITE, breakLine: true } },
      { text: "    }",                                                                        options: { color: WHITE, breakLine: true } },
      { text: "    public String getClassNamePattern() { return classNamePattern; }",       options: { color: WHITE, breakLine: true } },
      { text: "}",                                                                            options: { color: WHITE } },
    ],
    {
      x: 7.05, y: 1.5, w: 5.6, h: 4.85,
      fontFace: F_MONO, fontSize: 10, color: WHITE, margin: 0
    }
  );

  s.addText("Polymorphism — Service 走訪 List<ArchitectureConstraint> 並呼叫 validate(files)，每個子類掌管自己的演算法。",
    { x: 0.55, y: 6.55, w: 12.2, h: 0.32,
      fontFace: F_BODY, fontSize: 10, italic: true, color: MUTED, margin: 0 });
}

// =============================================================
// Slide 18 — 單元測試 · mvn test
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "Unit Testing · mvn -B clean test", "06 · Testing");

  s.addShape(pres.shapes.RECTANGLE, { x: 0.55, y: 1.15, w: 12.2, h: 1.5, fill: { color: ICE_SOFT }, line: { color: ICE_SOFT } });
  const stats = [
    { v: "8",   l: "test 類別" },
    { v: "21",  l: "@Test 數" },
    { v: "0",   l: "失敗" },
    { v: "0",   l: "錯誤" },
    { v: "418", l: "test LOC" },
  ];
  const colW = 12.2 / stats.length;
  stats.forEach((s_, i) => {
    s.addText(s_.v, {
      x: 0.55 + i * colW, y: 1.2, w: colW, h: 0.8,
      fontFace: F_TITLE_EN, fontSize: 44, bold: true, color: NAVY, align: "center", margin: 0
    });
    s.addText(s_.l, {
      x: 0.55 + i * colW, y: 2.0, w: colW, h: 0.55,
      fontFace: F_BODY, fontSize: 12, color: MUTED, align: "center", charSpacing: 2, margin: 0
    });
  });

  s.addShape(pres.shapes.RECTANGLE, { x: 0.55, y: 2.85, w: 12.2, h: 3.6, fill: { color: NAVY_DARK }, line: { color: NAVY_DARK } });
  s.addShape(pres.shapes.RECTANGLE, { x: 0.55, y: 2.85, w: 12.2, h: 0.34, fill: { color: NAVY }, line: { color: NAVY } });
  s.addText("mvn — JUnit 5", { x: 5.0, y: 2.85, w: 3.3, h: 0.34, fontFace: F_BODY, fontSize: 10, color: ICE, align: "center", valign: "middle", margin: 0 });

  s.addText(
    [
      { text: "$ mvn -B clean test",                                                   options: { color: GOLD, breakLine: true } },
      { text: "[INFO] -------------------------------------------------------",        options: { color: ICE, breakLine: true } },
      { text: "[INFO]  T E S T S",                                                      options: { color: ICE, breakLine: true } },
      { text: "[INFO] -------------------------------------------------------",        options: { color: ICE, breakLine: true } },
      { text: "[INFO] Running com.archchecker.application.ComplianceCheckServiceTest", options: { color: WHITE, breakLine: true } },
      { text: "[INFO] Running com.archchecker.application.SuppressionServiceTest",     options: { color: WHITE, breakLine: true } },
      { text: "[INFO] Running com.archchecker.domain.constraint.NamingRuleTest",       options: { color: WHITE, breakLine: true } },
      { text: "[INFO] Running com.archchecker.domain.constraint.DependencyRuleTest",   options: { color: WHITE, breakLine: true } },
      { text: "[INFO] Running com.archchecker.domain.constraint.SupertypeRuleTest",    options: { color: WHITE, breakLine: true } },
      { text: "[INFO] Running com.archchecker.domain.constraint.PackageRuleTest",      options: { color: WHITE, breakLine: true } },
      { text: "[INFO] Running com.archchecker.infrastructure.profile.YamlProfileRepositoryTest", options: { color: WHITE, breakLine: true } },
      { text: "[INFO] Running com.archchecker.infrastructure.suppression.YamlSuppressionRepositoryTest", options: { color: WHITE, breakLine: true } },
      { text: "[INFO] Tests run: 21, Failures: 0, Errors: 0, Skipped: 0",               options: { color: "70C77B", bold: true, breakLine: true } },
      { text: "[INFO] BUILD SUCCESS",                                                    options: { color: "70C77B", bold: true } },
    ],
    {
      x: 0.85, y: 3.25, w: 11.6, h: 3.1,
      fontFace: F_MONO, fontSize: 11, color: WHITE, margin: 0
    }
  );

  s.addText("全部測試通過。Domain 規則皆獨立於 Application / Infrastructure 進行驗證。",
    { x: 0.55, y: 6.55, w: 12.2, h: 0.32,
      fontFace: F_BODY, fontSize: 10, italic: true, color: MUTED, margin: 0 });
}

// =============================================================
// Slide 19 — 重要測試碼 · NamingRuleTest
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "重要測試碼 · NamingRuleTest", "06 · Testing");

  s.addShape(pres.shapes.RECTANGLE, { x: 0.55, y: 1.1, w: 12.2, h: 5.3, fill: { color: NAVY_DARK }, line: { color: NAVY_DARK } });
  s.addText("src/test/java/com/archchecker/domain/constraint/NamingRuleTest.java", {
    x: 0.65, y: 1.13, w: 12.0, h: 0.3,
    fontFace: F_MONO, fontSize: 10, color: ICE, margin: 0
  });

  s.addText(
    [
      { text: "class NamingRuleTest {",                                                                                      options: { color: WHITE, breakLine: true } },
      { text: "",                                                                                                            options: { breakLine: true } },
      { text: "    @Test void classMatchingPattern_producesNoViolation() {",                                                  options: { color: GOLD, bold: true, breakLine: true } },
      { text: "        NamingRule r = new NamingRule(\"NR-1\", \"Service must end with Service\", \"*Service\");",          options: { color: WHITE, breakLine: true } },
      { text: "        File f = stub(\"src/.../UserService.java\", \"package x;\\nclass UserService {}\");",                  options: { color: WHITE, breakLine: true } },
      { text: "        assertThat(r.validate(List.of(f))).isEmpty();",                                                        options: { color: WHITE, breakLine: true } },
      { text: "    }",                                                                                                       options: { color: WHITE, breakLine: true } },
      { text: "",                                                                                                            options: { breakLine: true } },
      { text: "    @Test void classNotMatchingPattern_reportsViolationWithLine() {",                                          options: { color: GOLD, bold: true, breakLine: true } },
      { text: "        NamingRule r = new NamingRule(\"NR-1\", \"Service must end with Service\", \"*Service\");",          options: { color: WHITE, breakLine: true } },
      { text: "        File f = stub(\"src/.../UserManager.java\", \"package x;\\nclass UserManager {}\");",                  options: { color: WHITE, breakLine: true } },
      { text: "",                                                                                                            options: { breakLine: true } },
      { text: "        List<Violation> vs = r.validate(List.of(f));",                                                         options: { color: WHITE, breakLine: true } },
      { text: "",                                                                                                            options: { breakLine: true } },
      { text: "        assertThat(vs).hasSize(1);",                                                                           options: { color: WHITE, breakLine: true } },
      { text: "        Violation v = vs.get(0);",                                                                             options: { color: WHITE, breakLine: true } },
      { text: "        assertThat(v.getConstraint().getId()).isEqualTo(\"NR-1\");",                                          options: { color: WHITE, breakLine: true } },
      { text: "        assertThat(v.getMessage()).contains(\"UserManager\").contains(\"*Service\");",                        options: { color: WHITE, breakLine: true } },
      { text: "    }",                                                                                                       options: { color: WHITE, breakLine: true } },
      { text: "",                                                                                                            options: { breakLine: true } },
      { text: "    @Test void multipleFiles_onlyOffendersAreReported() { /* … */ }",                                         options: { color: GOLD, bold: true, breakLine: true } },
      { text: "}",                                                                                                            options: { color: WHITE } },
    ],
    {
      x: 0.85, y: 1.55, w: 12.0, h: 4.85,
      fontFace: F_MONO, fontSize: 11, color: WHITE, margin: 0
    }
  );

  s.addText("測試僅針對單一 domain 規則，無 JavaParser 也無 I/O — 失敗時可立即定位該規則的演算法。",
    { x: 0.55, y: 6.55, w: 12.2, h: 0.32,
      fontFace: F_BODY, fontSize: 10, italic: true, color: MUTED, margin: 0 });
}

// =============================================================
// Slide 20 — Project Information + Team Member Efforts
// =============================================================
{
  const s = pres.addSlide();
  chrome(s, "Project Information & Team Effort", "07 · Metrics");

  s.addText("LOC of code（全新專案，單人團隊）", {
    x: 0.55, y: 1.1, w: 8.3, h: 0.4,
    fontFace: F_BODY, fontSize: 14, bold: true, color: NAVY, margin: 0
  });
  const loc = [
    [
      { text: "Project information",       options: { bold: true, color: WHITE, fill: { color: NAVY } } },
      { text: "Total",   options: { bold: true, color: WHITE, fill: { color: NAVY }, align: "center" } },
      { text: "Domain (M)", options: { bold: true, color: WHITE, fill: { color: NAVY }, align: "center" } },
      { text: "UI (VC)",    options: { bold: true, color: WHITE, fill: { color: NAVY }, align: "center" } },
    ],
    ["LOC of production code",                  { text: "827", options: { align: "center" } }, { text: "447", options: { align: "center" } }, { text: "92",  options: { align: "center" } }],
    ["# classes of production code",            { text: "26",  options: { align: "center" } }, { text: "12",  options: { align: "center" } }, { text: "3",   options: { align: "center" } }],
    ["# methods of production code",            { text: "65",  options: { align: "center" } }, { text: "44",  options: { align: "center" } }, { text: "3",   options: { align: "center" } }],
    ["# unit tests (testXxx · @Test)",          { text: "21",  options: { align: "center" } }, { text: "10",  options: { align: "center" } }, { text: "0",   options: { align: "center" } }],
    ["LOC of test code",                         { text: "418", options: { align: "center" } }, { text: "159", options: { align: "center" } }, { text: "0",   options: { align: "center" } }],
  ];
  s.addTable(loc, {
    x: 0.55, y: 1.55, w: 8.3, colW: [3.5, 1.4, 1.7, 1.7],
    fontFace: F_BODY, fontSize: 11, color: INK,
    border: { pt: 0.5, color: RULE },
  });

  s.addText(
    "註：Total 含 Domain (Model) + UI (View/Controller — cli) + Application services + Infrastructure adapters。\n" +
    "Application + Infrastructure 為 Total − Domain − UI 之餘額。",
    { x: 0.55, y: 4.5, w: 8.3, h: 0.7,
      fontFace: F_BODY, fontSize: 10, italic: true, color: MUTED, margin: 0 });

  s.addText("Team Member Efforts（小時）", {
    x: 9.1, y: 1.1, w: 3.7, h: 0.4,
    fontFace: F_BODY, fontSize: 14, bold: true, color: NAVY, margin: 0
  });
  const eff = [
    [
      { text: "",           options: { bold: true, color: WHITE, fill: { color: NAVY } } },
      { text: "專案",       options: { bold: true, color: WHITE, fill: { color: NAVY }, align: "center" } },
      { text: "作業文件",   options: { bold: true, color: WHITE, fill: { color: NAVY }, align: "center" } },
    ],
    ["李俊威 (113598009)",                              { text: "12.5", options: { align: "center" } }, { text: "15.0", options: { align: "center" } }],
    [{ text: "Total",                                  options: { bold: true } }, { text: "12.5", options: { align: "center", bold: true } }, { text: "15.0", options: { align: "center", bold: true } }],
  ];
  s.addTable(eff, {
    x: 9.1, y: 1.55, w: 3.7, colW: [1.5, 1.1, 1.1],
    fontFace: F_BODY, fontSize: 11, color: INK,
    border: { pt: 0.5, color: RULE },
  });

  s.addText("各次作業工時", {
    x: 9.1, y: 3.4, w: 3.7, h: 0.32,
    fontFace: F_BODY, fontSize: 12, bold: true, color: NAVY, margin: 0
  });
  s.addText(
    [
      { text: "HW1 · 03/03 · 3.0 h",  options: { bullet: true, breakLine: true } },
      { text: "HW2 · 03/12 · 6.0 h",  options: { bullet: true, breakLine: true } },
      { text: "HW3 · 04/01 · 6.0 h",  options: { bullet: true, breakLine: true } },
      { text: "HW4 · 04/17 + 04/22 · 8.0 h", options: { bullet: true, breakLine: true } },
      { text: "HW5 · 04/29 · 4.5 h",  options: { bullet: true, breakLine: true } },
      { text: "Σ Total · 27.5 h",     options: { bullet: true, bold: true } },
    ],
    { x: 9.1, y: 3.75, w: 3.7, h: 2.6,
      fontFace: F_BODY, fontSize: 11, color: INK, paraSpaceAfter: 4, margin: 0 }
  );

  s.addShape(pres.shapes.RECTANGLE, {
    x: 0.55, y: 6.25, w: 12.2, h: 0.55,
    fill: { color: NAVY }, line: { color: NAVY }
  });
  s.addText("敬請指教 · Q & A",
    { x: 0.55, y: 6.25, w: 12.2, h: 0.55,
      fontFace: F_BODY, fontSize: 18, bold: true, color: WHITE, align: "center", valign: "middle", charSpacing: 4, margin: 0 });
}

// 寫檔
pres.writeFile({ fileName: "midterm-team6.pptx" }).then(fn => {
  console.log("Wrote " + fn);
}).catch(err => {
  console.error("Failed:", err);
  process.exitCode = 1;
});
