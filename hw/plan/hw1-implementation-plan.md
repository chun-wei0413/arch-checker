# arch-checker 實作計畫

> 課程：Object-Oriented Analysis and Design
> 學生：113598009 李俊威
> 更新：含 HW1 + HW2 準備

---

## 1. 專案概述

**arch-checker** 是一個針對 Java 專案的命令列架構合規驗證工具。使用者以 YAML 格式定義「這個專案遵循哪種架構風格」的規則集（Style Profile），工具自動解析 Java 原始碼（AST），比對每個類別是否符合該規則集，並輸出含有檔案路徑、行號與說明的違規報告。所有架構風格規則皆由使用者自行定義，工具本身不預設任何內建風格。

---

## 2. HW1 調整建議

### 2.1 架構風格例子說明

> ⚠️ **層級說明**：MVC 是 Architectural Pattern（架構模式）；Repository 是 Design Pattern（設計模式）。兩者都不在 Architectural Style 層級。本專案不內建任何特定架構風格，使用者可透過 YAML 自行定義任意風格，例如分層架構、六角形架構或 Clean Architecture 等。

### 2.2 HW1 自我檢查

| 要求項目 | 狀態 | 說明 |
|---------|------|------|
| 封面（專案名稱、作業編號、組員） | ✅ | arch-checker, HW#1, 113598009 李俊威 |
| Problem Statement 200–400 字 | ✅ | 估計約 350 字，需確認精確字數 |
| 回答 who / what / where / when / why / how | ⚠️ | 見下方補強 |
| 開發語言：OO 語言 | ✅ | Java (JDK 17+) |
| Measurement 時間格式 | ✅ | 115/03/03 15:00~18:00，3H |
| 限制 2 頁 | ✅ | 需確認排版後頁數 |

**Who/What/Where/When/Why/How 補強對應：**

- **Who**：Java 開發者、軟體架構師、導入 AI 程式碼生成的開發團隊
- **What**：驗證 Java 專案的類別是否符合使用者自定義架構風格規則
- **Where**：本地開發環境、Code Review 階段、CI/CD pipeline
- **When**：程式碼規模增大後、導入 AI 生成程式碼後、重構前
- **Why**：人工審查費時且難以規模化；現有工具（ArchUnit）使用門檻過高
- **How**：以 JavaParser 解析 AST，比對 YAML Style Profile 中定義的規則，產出含行號的違規報告

---

## 3. HW2 準備（Requirement Document）

> HW2 要求：1 人團隊需寫 **2–4 個 fully dressed use case**（其餘 brief format）；
> 每個 artifact 必須有**唯一識別碼**（traceable across homeworks）。
> 評分比重：Use Cases 45%、NFR 15%、System Features 10%、Glossary 10%。

### 3.1 Change History（格式）

| Version | Date | Author | Description |
|---------|------|--------|-------------|
| 1.0 | 2026-03-03 | 李俊威 | Initial submission (HW1) |
| 2.0 | TBD | 李俊威 | Add use cases, NFR, glossary (HW2) |

### 3.2 System Features（SF-xxx）

依 Larman04 Ch7.7，列出本專案**新開發**的系統功能：

| ID | 功能名稱 | 優先度 | 說明 |
|----|---------|--------|------|
| SF-01 | Architecture Compliance Checking | High | 掃描專案目錄，依使用者定義的 Style Profile 偵測違規 |
| SF-02 | Style Profile Definition via YAML | High | 使用者以 YAML 格式定義任意架構風格規則，工具不預設任何內建風格 |
| SF-03 | Violation Report Generation | High | 輸出含檔案路徑、行號、規則 ID、說明的報告 |
| SF-04 | Multiple Report Formats | Low | 支援 Console 與 JSON 兩種輸出格式 |
| SF-05 | Violation Suppression | Medium | 標記特定違規為「已知且接受」，下次不再回報 |

### 3.3 Use Case Diagram

**Actors:**
- **Developer**（主要）：使用工具定義規則、檢查專案的開發者
- **CI System**（次要）：自動觸發 check 的持續整合系統（reuses UC-01）

```
┌──────────────────────────────────────────────────────┐
│                    arch-checker                      │
│                                                      │
│  ┌────────────────────────────────────────────────┐  │
│  │  UC-01: Check Architecture Compliance  ◄───┐  │  │
│  └───────────────────┬────────────────────┘   │  │  │
│                      │ <<include>>             │  │  │
│  ┌───────────────────▼────────────────────┐   │  │  │
│  │  UC-05: Load Style Profile             │   │  │  │
│  │         (subfunction)                  │   │  │  │
│  └────────────────────────────────────────┘   │  │  │
│                                               │  │  │
│  ┌──────────────────────────────────────────┐ │  │  │
│  │  UC-02: Define Style Profile             │ │  │  │
│  └──────────────────────────────────────────┘ │  │  │
│                                               │  │  │
│  ┌──────────────────────────────────────────┐ │  │  │
│  │  UC-03: Check with Fix Suggestions       │─┘  │  │
│  │         (Interactive Mode)               │    │  │
│  └──────────────────────────────────────────┘    │  │
│                                                  │  │
│  ┌──────────────────────────────────────────┐    │  │
│  │  UC-04: Suppress a Violation             │    │  │
│  └──────────────────────────────────────────┘    │  │
└──────────────────────────────────────────────────────┘
      ▲                                          ▲
  Developer                                  CI System
```

**Use Case 豐富度說明：**

| UC | 觸發路徑 | 後置條件 | 互動性 | 格式 |
|----|---------|---------|--------|------|
| UC-01 | `check --profile <yaml> <path>` | ViolationReport 產出，exit code 0/1 | 單向 | Fully Dressed |
| UC-02 | `init-profile` + `validate-profile` | 有效 `.yaml` 寫入磁碟 | 單向 | Fully Dressed |
| UC-03 | `check --interactive --profile <yaml> <path>` | 每個 Violation 決策完成，suppression 寫入 | 多輪問答 | Fully Dressed |
| UC-04 | `suppress --id <id> --reason <text>` | Suppression 記錄持久化 | 單向 | Brief |
| UC-05 | UC-01 內部子流程 | Style Profile 載入至記憶體 | 無 | Brief（subfunction） |

### 3.4 Use Cases

#### ★ 策略：UC-01 + UC-02 + UC-03 寫 Fully Dressed；其餘寫 Brief

---

#### UC-01: Check Architecture Compliance（Fully Dressed）

| Use Case Section | Content |
|-----------------|---------|
| **Use Case** | UC-01 |
| **Use Case Name** | Check Architecture Compliance |
| **Scope** | arch-checker CLI |
| **Level** | User Goal |
| **Primary Actor** | Developer |
| **Stakeholders and Interests** | **Developer**：想要準確、詳細的違規報告（含行號），快速找出哪些類別違反了自訂規則，避免費時人工審查。<br>**Team Lead / Architect**：想確保整個程式碼庫符合團隊定義的架構規範，零違規才能保持架構一致性。<br>**CI System**（次要 Actor）：想要可靠的 POSIX exit code（0/1/2）以判斷 pipeline 是否通過，不需解讀報告內文。 |
| **Preconditions** | 1. `arch-checker` 已安裝於系統 PATH<br>2. 目標 Java 專案目錄存在且可讀<br>3. `<yaml-path>` 指定的 Style Profile YAML 檔案存在且語法正確 |
| **Success Guarantee** | 系統產生完整的 ViolationReport，含：已檢查檔案數、發現違規清單、suppressedCount；並回傳 POSIX exit code（0 = pass，1 = violations found） |
| **Main Success Scenario** | 1. Developer 執行 `arch-checker check --profile <yaml-path> <project-path>`<br>2. 系統確認 `project-path` 目錄存在<br>3. 系統讀取並解析 `<yaml-path>` 指定的 Style Profile YAML 檔案<br>4. 系統遞迴掃描 `project-path` 下所有 `.java` 檔案<br>5. 系統以 JavaParser 將每個 `.java` 檔解析為 AST（CompilationUnit）<br>6. 系統對每個 CompilationUnit 套用 Profile 中所有 ArchitectureConstraint 規則<br>7. 系統收集所有 Violation，每筆包含：檔案路徑、行號、規則 ID、說明文字<br>8. 系統過濾已被 suppress 的違規項目<br>9. 系統依 Console 格式輸出 ViolationReport（含違規清單與 summary）<br>10. 系統回傳 exit code：無違規則 0，有違規則 1 |
| **Extensions** | **2a.** `project-path` 不存在：系統輸出 `Error: path not found <path>`，exit code 2，終止<br>**3a.** `<yaml-path>` 不存在：系統輸出 `Error: profile file not found <path>`，exit code 2，終止<br>**3b.** YAML 格式錯誤：系統輸出解析錯誤行號與訊息，exit code 2，終止<br>**3c.** YAML 語法正確但規則定義無效（如 rule type 不合法）：系統輸出 validation 錯誤清單，exit code 2，終止<br>**4a.** 目錄下無 `.java` 檔：系統輸出 `Warning: No Java files found in <path>`，exit code 0，終止<br>**5a.** 某 `.java` 檔語法錯誤無法解析：系統記錄 `Warning: skipped <file> (parse error)`，跳過，繼續處理其餘檔案<br>**8a.** 所有違規皆已被 suppress：報告顯示 `All checks passed (N violations suppressed)`，exit code 0 |
| **Special Requirements** | **NFR-01（Performance）**：掃描 1,000 個 `.java` 檔案必須在 10 秒內完成（一般開發機）<br>**NFR-02（Usability）**：所有錯誤訊息必須含：錯誤原因、位置、建議修正動作<br>**NFR-05（CI Compatibility）**：exit code 遵循 POSIX 標準，可直接整合 GitHub Actions |
| **Technology and Data Variations List** | **\*a.** `--profile` 接受任意 YAML 檔案路徑（無內建名稱模式）<br>**\*b.** 輸出格式可透過 `--format json` 切換為 JSON，預設為 Console 文字格式<br>**4a.** Java 檔案掃描採遞迴深度優先走訪 |
| **Frequency of Occurrence** | CI 環境：每次 push 觸發，可達每日數十次；本地開發：每小時數次 |
| **Miscellaneous** | 是否需支援多 module Maven 專案（多個 `src/main/java` 子目錄）？<br>大型專案（>10,000 個 `.java` 檔）是否需要增量掃描機制？ |

---

#### UC-02: Define Style Profile（Fully Dressed）

| Use Case Section | Content |
|-----------------|---------|
| **Use Case** | UC-02 |
| **Use Case Name** | Define Style Profile |
| **Scope** | arch-checker CLI |
| **Level** | User Goal |
| **Primary Actor** | Developer |
| **Stakeholders and Interests** | **Developer**：想用最少工夫定義出可重複使用的架構規則，希望工具提供模板降低手寫 YAML 的門檻，並在儲存前即時得知格式是否正確。<br>**Team Lead**：想要團隊使用統一的 Profile 並能版本控制於 Git 儲存庫中，確保規則描述清楚且可維護。 |
| **Preconditions** | 1. `arch-checker` 已安裝於系統 PATH<br>2. Developer 了解目標專案的架構規範（知道哪些 package 屬於哪個層、哪些依賴是被禁止的） |
| **Success Guarantee** | 一個語法正確、至少含 1 條規則的 `.yaml` Profile 檔案存在於磁碟，且可被 UC-01 成功載入使用 |
| **Main Success Scenario** | 1. Developer 執行 `arch-checker init-profile --output <file.yaml>`<br>2. 系統產生含預設結構與欄位說明註解的空白 YAML 模板，寫入 `<file.yaml>`<br>3. Developer 以文字編輯器開啟 `<file.yaml>`，填入規則（rule type、package pattern、constraint 細節）<br>4. Developer 執行 `arch-checker validate-profile <file.yaml>` 驗證語法與規則定義<br>5. 系統讀取並解析 YAML 檔<br>6. 系統驗證每條規則的 type（`NamingRule` / `DependencyRule` / `PackageRule`）與必填欄位<br>7. 系統驗證每個 package pattern 的語法（只允許 `*`、`.`、英數字）<br>8. 系統輸出 `Profile valid: <N> rules loaded`<br>9. Developer 在執行 UC-01 時以 `--profile <file.yaml>` 指向此 Profile |
| **Extensions** | **4a.** Developer 跳過 `init-profile`，直接對已存在的 YAML 執行 validate：從 Step 5 開始，正常執行<br>**5a.** `<file.yaml>` 不存在：系統輸出 `Error: file not found <path>`，終止<br>**6a.** YAML 頂層結構錯誤（不是合法 YAML mapping）：系統輸出 `Invalid YAML structure` 與錯誤行號，終止<br>**6b.** 未知的 rule type：系統輸出 `Unknown rule type: <type>. Allowed: NamingRule, DependencyRule, PackageRule`，終止<br>**7a.** package pattern 含非法字元：系統輸出 `Invalid package pattern: <pattern>`，終止<br>**8a.** YAML 格式正確但 rules 清單為空：系統輸出 `Warning: Profile has no rules. Profile is valid but will produce no violations.`，exit code 0 |
| **Special Requirements** | **NFR-02（Usability）**：所有驗證錯誤訊息必須清楚指出 YAML 中的錯誤行號與修正建議<br>**NFR-04（Extensibility）**：新增 rule type 不需修改 `ProfileValidator` 核心邏輯（Open/Closed Principle） |
| **Technology and Data Variations List** | **\*a.** Profile 格式為 YAML（SnakeYAML 解析），採 UTF-8 編碼<br>**\*b.** Developer 可使用任意文字編輯器編輯，無需 IDE 外掛<br>**2a.** `--output` 若路徑目錄不存在，系統自動建立 |
| **Frequency of Occurrence** | 每個新專案通常執行一次；後續修改規則時重複執行 validate；相對低頻，以週或月為單位 |
| **Miscellaneous** | 是否提供 JSON Schema（供 IntelliJ / VS Code 自動補全提示）？<br>Profile 是否支援繼承（`extends: <other-yaml-path>`）以在既有規則上擴充？ |

---

#### UC-03: Check Architecture Compliance with Interactive Fix Suggestions（Fully Dressed）

| Use Case Section | Content |
|-----------------|---------|
| **Use Case** | UC-03 |
| **Use Case Name** | Check Architecture Compliance with Interactive Fix Suggestions |
| **Scope** | arch-checker CLI |
| **Level** | User Goal |
| **Primary Actor** | Developer |
| **Stakeholders and Interests** | **Developer**：想要逐條審視每個違規並當場決定是否接受（suppress），希望系統提供可操作的修復建議，而不只是列出錯誤清單。<br>**Team Lead**：想要知道哪些違規是「已知且接受」的技術債，並有持久化紀錄可查，確保 suppression 不是無聲地被忽略。 |
| **Preconditions** | 1. 同 UC-01 前置條件<br>2. Developer 有意願逐條決策每個違規（非 CI 批次自動化情境）<br>3. Terminal 支援互動式輸入（非 pipe 重定向模式） |
| **Success Guarantee** | 對每條 Violation，結果三選一：(a) Suppression 寫入 `.arch-checker-suppress.yaml`，(b) 保留在報告中，(c) 流程中止；最終輸出 ComplianceReport 反映所有決策結果 |
| **Main Success Scenario** | 1. Developer 執行 `arch-checker check --interactive --profile <yaml-path> <project-path>`<br>2. 系統完成掃描流程（同 UC-01 Step 2–7）：掃描、解析 AST、套用規則、收集所有 Violation<br>3. 系統展示第一條 Violation，顯示：違規位置（檔案路徑、行號）、違反的 ArchitectureConstraint ID 與說明、RefactoringSuggestion（若存在）<br>4. 系統提示 `Accept suppression? [y/n/q]:`<br>5. Developer 輸入 `y`：系統將此 Violation 的 constraint ID 與時間戳記寫入 `.arch-checker-suppress.yaml`<br>6. 系統繼續展示下一條 Violation（回到 Step 3）<br>7. 所有 Violation 處理完畢後，系統輸出最終 ComplianceReport（已扣除接受的 suppressions）<br>8. 系統回傳 exit code（0 = 無未接受違規，1 = 有未接受違規） |
| **Extensions** | **3a.** 此 Violation 有 RefactoringSuggestion：系統在提示 `[y/n/q]` 前額外顯示建議步驟（如 `Suggestion: Move class to *.service.* package`）<br>**3b.** 掃描後無 Violation：系統直接輸出 `All checks passed`，不進入互動迴圈，exit code 0<br>**5a.** Developer 輸入 `n`：此 Violation 保留在最終報告中，系統繼續下一條（Step 6）<br>**5b.** Developer 輸入 `q`：系統中止互動迴圈，輸出已處理部分的 partial report，exit code 1<br>**5c.** Developer 輸入非法值（非 y/n/q）：系統輸出 `Invalid input. Please enter y, n, or q:`，重新等待輸入<br>**8a.** 所有 Violation 均被 suppress（全部輸入 y）：報告顯示 `All checks passed (N violations suppressed)`，exit code 0 |
| **Special Requirements** | **NFR-02（Usability）**：互動提示須清晰，使用者在不查文件的情況下也能理解 `[y/n/q]` 各選項含義<br>**NFR-06（Testability）**：互動流程必須可在不啟動完整 CLI 的情況下以 unit test 驗證（I/O 需可 mock） |
| **Technology and Data Variations List** | **\*a.** Suppression 記錄寫入 `.arch-checker-suppress.yaml`，含 constraint ID、reason（預設 `suppressed interactively`）、timestamp<br>**4a.** `[y/n/q]` 大小寫不敏感（`Y`、`Yes` 均視為接受）<br>**\*b.** 若 stdout 被 pipe 重定向（非 TTY），系統自動降級為非互動批次模式並輸出警告 |
| **Frequency of Occurrence** | 本地開發初次建立 suppression 清單時使用，通常每個新專案執行一次；後續違規出現時按需使用，相對低頻 |
| **Miscellaneous** | 是否需支援批次 suppress（一次接受全部違規）的選項 `--accept-all`？<br>Suppression 是否應有有效期限（避免長期堆積技術債）？ |

---

#### UC-04: Suppress a Violation（Brief）

Developer 執行 `arch-checker suppress --id <constraint-id> --reason <text>`。系統將抑制記錄（constraint ID、reason、timestamp）以 append 方式寫入 `.arch-checker-suppress.yaml`。下次 UC-01 Step 8 過濾時生效。

---

#### UC-03 vs UC-04 實際情境範例

**UC-03：互動式修復建議**

情境：第一次對專案跑合規檢查，想逐條審視每個違規。

```bash
$ arch-checker check --interactive --profile layered.yaml src/
```

工具掃完後，**逐條**問你怎麼處理每個違規：

```
[VIOLATION] src/web/UserController.java:42
  Rule: DEP-01 - web 層不能直接 import persistence 層
  Suggestion: 改透過 UserService 存取資料

Accept suppression? [y/n/q]:
```

- 輸入 `y`：這個違規是暫時技術債，先接受 → 寫入 `.arch-checker-suppress.yaml`，下次不再回報
- 輸入 `n`：這個要真的修 → 保留在報告中
- 輸入 `q`：不想繼續審了 → 中止互動，輸出已處理部分的報告

**UC-04：手動 suppress 單筆違規**

情境：你已經知道某個違規要 suppress，不想跑互動流程，直接下指令。

```bash
$ arch-checker suppress --id DEP-01 --reason "legacy code, scheduled for Q3 refactor"
```

直接把 `DEP-01` 這條規則寫進 suppress 清單，下次 UC-01 執行時自動濾掉。

| | UC-03 | UC-04 |
|---|---|---|
| 使用時機 | 第一次建立 suppress 清單，想逐條審視 | 已知哪條要 suppress，直接下指令 |
| 流程 | 互動式，一條一條問 | 單一指令，一次 suppress 一條 |
| 適合情境 | 本地開發初始設定 | CI 腳本、快速操作 |

---

#### UC-05: Load Style Profile（Brief，subfunction）

UC-01 / UC-03 的 `<<include>>` 子流程。系統讀取 `--profile <yaml-path>` 指定的 YAML 檔，透過 `ProfileLoader` 解析並以 `ProfileValidator` 驗證規則定義，成功後載入至記憶體供 checker 使用。

---

### 3.5 Non-functional Requirements（NFR）

| ID | 類別 | 需求描述 |
|----|------|---------|
| NFR-01 | Performance | 掃描 1,000 個 `.java` 檔案的完整 check（UC-01）必須在 10 秒內完成（一般開發機） |
| NFR-02 | Usability | 所有錯誤訊息必須包含：錯誤原因、錯誤位置（檔案/行號）、建議修正動作 |
| NFR-03 | Portability | 工具需在 JDK 17+ 環境（Windows / macOS / Linux）上運行，不依賴特定 IDE |
| NFR-04 | Extensibility | 新增 ConstraintChecker 子類別不需修改 `ArchChecker` 或 `StyleProfile`（Open/Closed Principle） |
| NFR-05 | CI Compatibility | exit code 慣例遵循 POSIX 標準（0 = success, 非 0 = failure），可直接整合 GitHub Actions |
| NFR-06 | Testability | 每個 `ConstraintChecker` 子類別必須可在不需要完整 CLI 的情況下單獨進行 unit test |

### 3.6 Glossary

| 術語 | 定義 |
|-----|------|
| **Style Profile** | 使用者以 YAML 格式定義的一份架構規則集合，描述目標專案的架構約束 |
| **ArchitectureConstraint** | Style Profile 中的單條約束規則，為 `NamingRule`、`DependencyRule`、`SupertypeRule`、`PackageRule` 的抽象父類別 |
| **SupertypeRule** | 一種 ArchitectureConstraint，要求特定 package 內的 class 必須 implement 指定 interface 或 extend 指定 class；對應實作為 `SupertypeConstraint` 與 `SupertypeChecker`；以 simple name 比對，不進行 fully qualified name resolution |
| **Violation** | 一筆架構違規紀錄，包含檔案路徑、行號、規則 ID 與說明文字 |
| **ViolationReport** | 一次 check 執行的完整違規彙整結果 |
| **AST (Abstract Syntax Tree)** | Java 原始碼的抽象語法樹表示，由 JavaParser 解析產生，供 ConstraintChecker 分析使用 |
| **CompilationUnit** | JavaParser 對單一 `.java` 檔案解析後的根節點物件 |
| **Suppression** | 開發者明確標記「此違規已知且接受」的機制，使其不出現在後續報告中 |
| **exit code** | CLI 程式結束時回傳給 shell 的整數碼（0 = pass，1 = violations found，2 = error） |

---

## 4. 核心 OO 設計

### 4.1 領域模型（Domain Model）

> 核心原則：領域概念應反映「架構規範」本身的業務邏輯，而非 JavaParser 的技術細節。

```
ArchitectureConstraint（抽象）
  - constraintId: String          ← e.g., "D01"
  - description: String
  - severity: Severity {HIGH, MEDIUM, LOW}
  ├── LayerDependencyConstraint
  │     - fromPackagePattern: String   ← e.g., "*.ui.*"
  │     - toPackagePattern: String     ← e.g., "*.dao.*"
  │     - direction: DependencyDirection {FORBIDDEN, REQUIRED}
  ├── NamingConventionConstraint
  │     - targetPackagePattern: String
  │     - classNamePattern: String     ← e.g., "*Controller"
  ├── SupertypeConstraint
  │     - targetPackagePattern: String
  │     - mustImplement: String        ← interface simple name，null 代表不檢查
  │     - mustExtend: String           ← class simple name，null 代表不檢查
  └── PackageOrganizationConstraint
        - requiredPackagePatterns: List<String>

StyleProfile
  - profileName: String
  - description: String
  1 ──< ArchitectureConstraint

Violation
  - location: SourceLocation            ← 技術細節封裝在此
  - severity: Severity
  * ──► 1 ArchitectureConstraint        ← 違反的是哪條業務規則
  0..1 ──► RefactoringSuggestion
             - description: String
             - suggestedAction: RefactoringAction {MOVE_CLASS, RENAME_CLASS, INTRODUCE_INTERFACE}

Suppression
  - constraintId: String
  - reason: String
  - suppressedAt: DateTime

ComplianceReport
  - checkedAt: DateTime
  - profile: StyleProfile
  1 ──< Violation
  - summary: ComplianceSummary {passCount, failCount, suppressedCount}
```

**關鍵設計決策**：`Violation` 關聯到 `ArchitectureConstraint`（業務規則），而非只存行號。`StyleProfile` 不繼承任何預設風格，所有規則完全由 YAML 定義。

### 4.2 Package 結構

```
arch-checker/
├── domain/
│   ├── ArchitectureConstraint（抽象）
│   │   ├── LayerDependencyConstraint
│   │   ├── NamingConventionConstraint
│   │   ├── SupertypeConstraint
│   │   └── PackageOrganizationConstraint
│   ├── StyleProfile
│   ├── Violation
│   ├── RefactoringSuggestion
│   ├── Suppression
│   └── ComplianceReport
├── checker/
│   ├── ConstraintChecker（介面，Strategy Pattern）
│   │   ├── LayerDependencyChecker
│   │   ├── NamingConventionChecker
│   │   ├── SupertypeChecker
│   │   └── PackageOrganizationChecker
│   └── ArchChecker（協調者）
├── ast/
│   ├── SourceFileParser      ← JavaParser 封裝（技術層）
│   └── ConstraintViolationVisitor（Visitor Pattern）
├── profile/
│   ├── ProfileLoader
│   ├── ProfileValidator
│   └── ConstraintBuilder（泛型 Builder）
│       ├── LayerDependencyBuilder
│       └── NamingConventionBuilder
├── report/
│   ├── ReportFormatter（介面，Strategy Pattern）
│   ├── ConsoleFormatter
│   └── JsonFormatter
└── cli/
    ├── CheckCommand
    ├── InitProfileCommand
    ├── ValidateProfileCommand
    └── SuppressCommand
```

### 4.3 Strategy Pattern：ConstraintChecker（無 if-else）

```java
// 介面
public interface ConstraintChecker {
    boolean supports(ArchitectureConstraint constraint);
    List<Violation> check(SourceFile file, ArchitectureConstraint constraint);
}

// 具體實作（檔案層級）
public class LayerDependencyChecker     implements ConstraintChecker { ... }
public class NamingConventionChecker    implements ConstraintChecker { ... }
public class SupertypeChecker           implements ConstraintChecker { ... }
public class PackageOrganizationChecker implements ConstraintChecker { ... }  // 專案層級

// 協調者：透過 supports() dispatch，完全沒有 if-else / switch
// PackageOrganizationConstraint 需在所有檔案掃完後才能檢查，分兩階段執行
public class ArchChecker {
    private final List<ConstraintChecker> checkers;

    public ComplianceReport check(Project project, StyleProfile profile) {
        // 第一階段：逐檔套用檔案層級規則
        for (SourceFile file : project.sourceFiles()) {
            for (ArchitectureConstraint constraint : profile.fileLevelConstraints()) {
                checkers.stream()
                    .filter(c -> c.supports(constraint))
                    .forEach(c -> violations.addAll(c.check(file, constraint)));
            }
        }
        // 第二階段：整個專案掃完後套用專案層級規則（PackageOrganizationConstraint）
        Set<String> allPackages = collectAllPackages(project);
        profile.projectLevelConstraints()
            .forEach(c -> violations.addAll(packageChecker.checkProject(allPackages, c)));
    }
}
```

### 4.4 Visitor Pattern：AST 走訪

```java
public abstract class ConstraintViolationVisitor
        extends VoidVisitorAdapter<ViolationCollector> {
}

public class DependencyViolationVisitor extends ConstraintViolationVisitor {
    @Override
    public void visit(ImportDeclaration n, ViolationCollector collector) {
        // 檢查 import 是否違反 LayerDependencyConstraint
    }
}

public class NamingViolationVisitor extends ConstraintViolationVisitor {
    @Override
    public void visit(ClassOrInterfaceDeclaration n, ViolationCollector collector) {
        // 檢查類別名稱是否符合 NamingConventionConstraint
    }
}
```

→ 多型體現在 Visitor 的 `visit()` overloading，不同節點類型觸發不同檢查邏輯。

### 4.5 Fluent API 透過泛型繼承（DCD 可展示的繼承層級）

```java
// 抽象 Builder，SELF 型別參數實現 Fluent API 的連鎖呼叫
public abstract class ConstraintBuilder<SELF extends ConstraintBuilder<SELF>> {
    protected String id;
    protected Severity severity = Severity.HIGH;

    public SELF withId(String id)         { this.id = id; return self(); }
    public SELF withSeverity(Severity s)  { this.severity = s; return self(); }
    protected abstract SELF self();
    public abstract ArchitectureConstraint build();
}

// 具體 Builder：繼承並補充領域特定方法
public class LayerDependencyBuilder
        extends ConstraintBuilder<LayerDependencyBuilder> {
    public LayerDependencyBuilder from(String packagePattern) { ... return this; }
    public LayerDependencyBuilder cannotDependOn(String packagePattern) { ... return this; }
    @Override protected LayerDependencyBuilder self() { return this; }
    @Override public LayerDependencyConstraint build() { ... }
}

// 使用範例（Fluent API）
ArchitectureConstraint c = new LayerDependencyBuilder()
    .withId("D01")
    .withSeverity(Severity.HIGH)
    .from("*.ui.*")
    .cannotDependOn("*.dao.*")
    .build();
```

### 4.6 Strategy Pattern：ReportFormatter

```java
public interface ReportFormatter {
    String format(ComplianceReport report);
}

public class ConsoleFormatter implements ReportFormatter { ... }
public class JsonFormatter    implements ReportFormatter { ... }
```

---

## 5. 實作步驟（對應課程 HW 進度）

| Phase | 內容 | 對應 HW |
|-------|------|---------|
| Phase 1 | 建立 Maven 專案、`ArchitectureConstraint` 抽象層級、`ConstraintBuilder` Fluent API | HW2 |
| Phase 2 | `SourceFileParser`（JavaParser AST 解析）、`ArchChecker` 主流程、Console 報告 | HW3 |
| Phase 3 | `ProfileLoader`（YAML）、`ProfileValidator`、`SuppressConfig` | HW4 |
| Phase 4 | picocli CLI 介面、JSON 輸出 | HW5 |
| Phase 5 | JUnit 5 單元測試（每個 ConstraintChecker 獨立可測） | HW5 |

---

## 6. Use Case 豐富度的誠實評估

| 評估維度 | 評分 | 說明 |
|---------|------|------|
| Use Case 數量（1 人組） | ✅ | 3 fully dressed + 2 brief，符合規定 |
| Fully Dressed UC 大小 | ✅ | UC-01 有 10 步 MSS + 7 條 alternative flow；UC-03 有多輪互動 |
| SSD 可繪性 | ✅ | UC-01 Step 3–8 可畫出有意義的 SSD（ProfileLoader、ArchChecker、ReportFormatter 多物件互動） |
| Domain Model 豐富度 | ✅✅ | **強項**：ArchitectureConstraint 繼承層級 + Violation + ComplianceReport + Suppression 物件豐富 |
| OO Design（DCD）豐富度 | ✅✅ | **強項**：多型、Strategy（ConstraintChecker / ReportFormatter）、Visitor、Builder 四種設計模式 |

---

## 7. 技術選型

| 項目 | 選擇 | 理由 |
|-----|------|------|
| 語言 | Java 17 | 課程規定 OO 語言；LTS 版本穩定 |
| AST 解析 | JavaParser 3.x | 純 Java，API 直覺，無需 JDK tools |
| CLI 框架 | picocli | 輕量，annotation-driven，與 Java 17 相容 |
| Profile 格式 | YAML（SnakeYAML） | 可讀性高，易於手工編輯，無內建預設 |
| 測試框架 | JUnit 5 | 課程要求 unit testing |
| 建置工具 | Maven | 依賴管理方便 |
