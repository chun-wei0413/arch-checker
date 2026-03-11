# arch-checker 實作計畫

> 課程：Object-Oriented Analysis and Design
> 學生：113598009 李俊威
> 更新：含 HW1 + HW2 準備

---

## 1. 專案概述

**arch-checker** 是一個針對 Java 專案的命令列架構合規驗證工具。使用者以宣告式 API 定義「這個專案遵循哪種架構風格」，工具自動解析 Java 原始碼（AST），比對每個類別是否符合該風格的規則，並輸出含有檔案路徑、行號與說明的違規報告。

---

## 2. HW1 調整建議

### 2.1 簡化架構風格例子

原文 Problem Statement 舉了「事件驅動架構、角色導向架構（DCI）」，門檻過高。換成：

> ⚠️ **層級說明**：MVC 是 Architectural Pattern（架構模式）；Repository 是 Design Pattern（設計模式）。兩者都不在 Architectural Style 層級，不適合作為例子。應改用同屬 Architectural Style 的三個例子：

| 架構風格 | 層級 | 核心約束（一句話） |
|---------|------|----------------|
| **分層架構（Layered）** | Architectural Style ✅ | UI 層不能直接呼叫 DAO 層，依賴只能單向向下 |
| **六角形架構（Hexagonal）** | Architectural Style ✅ | Domain 不能依賴任何 Adapter；Adapter 不能互相依賴 |
| **Clean Architecture** | Architectural Style ✅ | 內圈（Entity/UseCase）不能依賴外圈（Framework/DB） |

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
- **What**：驗證 Java 專案的類別是否符合指定架構風格的規則
- **Where**：本地開發環境、Code Review 階段、CI/CD pipeline
- **When**：程式碼規模增大後、導入 AI 生成程式碼後、重構前
- **Why**：人工審查費時且難以規模化；現有工具（ArchUnit）使用門檻過高
- **How**：以 JavaParser 解析 AST，比對 Style Profile 中定義的規則，產出含行號的違規報告

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
| SF-01 | Architecture Compliance Checking | High | 掃描專案目錄，依指定 Style Profile 偵測違規 |
| SF-02 | Built-in Style Profiles | High | 內建 Layered、Hexagonal、Clean Architecture 三種 Architectural Style Profile |
| SF-03 | Custom Style Profile via YAML | Medium | 使用者以 YAML 檔案定義任意架構風格規則 |
| SF-04 | Violation Report Generation | High | 輸出含檔案路徑、行號、規則 ID、說明的報告 |
| SF-05 | Multiple Report Formats | Low | 支援 Console 與 JSON 兩種輸出格式 |
| SF-06 | Violation Suppression | Medium | 標記特定違規為「已知且接受」，下次不再回報 |

### 3.3 Use Case Diagram

**Actors:**
- **Developer**（主要）：使用工具檢查專案的開發者
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
│  │  UC-02: Load Style Profile             │   │  │  │
│  └────────────────────────────────────────┘   │  │  │
│                                               │  │  │
│  ┌──────────────────────────────────────────┐ │  │  │
│  │  UC-03: Initialize Project Config        │ │  │  │
│  │         (Interactive Wizard)             │ │  │  │
│  └──────────────────────────────────────────┘ │  │  │
│                                               │  │  │
│  ┌──────────────────────────────────────────┐ │  │  │
│  │  UC-04: Check with Fix Suggestions       │ │  │  │
│  │         (Interactive Mode)               │ │  │  │
│  └──────────────────────────────────────────┘ │  │  │
│                                               │  │  │
│  ┌──────────────────────────────────────────┐ │  │  │
│  │  UC-05: Define Custom Style Profile      │ │  │  │
│  └──────────────────────────────────────────┘ │  │  │
│                                               │  │  │
│  ┌──────────────────────────────────────────┐ │  │  │
│  │  UC-06: Suppress a Violation             │ │  │  │
│  └──────────────────────────────────────────┘ │  │  │
└──────────────────────────────────────────────────────┘
      ▲                                          ▲
  Developer                                  CI System
```

**Use Case 豐富度說明：**

| UC | 觸發路徑 | 後置條件 | 互動性 |
|----|---------|---------|--------|
| UC-01 | `check --profile <n> <path>` | ComplianceReport 產出，exit code 0/1 | 單向 |
| UC-03 | `init`（互動精靈） | `.arch-checker.yaml` 寫入磁碟 | **多輪問答** |
| UC-04 | `check --interactive <path>` | 每個 Violation：accept/skip/abort，Suppression 寫入 | **多輪問答** |
| UC-05 | `validate-profile <file>` | Profile 驗證通過或失敗回報 | 單向 |
| UC-06 | `suppress --id <id> --reason` | Suppression 記錄持久化 | 單向 |

UC-03 和 UC-04 具有明顯的多輪互動，SSD 不再是單向流，Contract 也有不同的前後置條件。

### 3.4 Use Cases

#### ★ 策略：UC-01 + UC-03 + UC-04 寫 Fully Dressed；其餘寫 Brief

- **UC-01**：核心批次流程，10 步 MSS，6 條 alternative flow
- **UC-03**：互動式精靈，多輪 prompt/response，後置條件為 config 檔產生
- **UC-04**：每個 Violation 都是一輪互動，後置條件依 Developer 選擇而異（accept/skip/abort）

---

#### UC-01: Check Architecture Compliance（Fully Dressed）

| 欄位 | 內容 |
|-----|------|
| **Use Case** | UC-01 |
| **Use Case Name** | Check Architecture Compliance |
| **Scope** | arch-checker CLI |
| **Level** | User Goal |
| **Primary Actor** | Developer |
| **Secondary Actor** | CI System（可代替 Developer 觸發） |
| **Stakeholders** | Developer（想要準確違規報告）；Team Lead（想要零違規的程式碼庫） |
| **Preconditions** | 1. arch-checker 已安裝於系統 PATH <br>2. 目標 Java 專案目錄存在 |
| **Postconditions** | 系統產生 ViolationReport 並回傳 exit code（0 = pass, 1 = violations found） |
| **Relates to** | SF-01, SF-02, SF-04 |

**Main Success Scenario：**

1. Developer 執行 `arch-checker check --profile <name> <project-path>`
2. 系統確認 `project-path` 目錄存在
3. 系統依 `<name>` 載入對應的 Style Profile（内建或自訂）
4. 系統遞迴掃描 `project-path` 下所有 `.java` 檔案
5. 系統以 JavaParser 將每個 `.java` 檔解析為 AST（CompilationUnit）
6. 系統對每個 CompilationUnit 套用 Profile 中所有 StyleRule
7. 系統收集所有違規（Violation），每筆含：檔案路徑、行號、規則 ID、說明
8. 系統過濾已被 suppress 的違規項目
9. 系統依 Console 格式輸出 ViolationReport
10. 系統回傳 exit code：無違規則 0，有違規則 1

**Alternative Flows：**

- **2a** 目錄不存在：系統輸出 `Error: path not found`，exit code 2，終止
- **3a** Profile 名稱不存在：系統列出可用 profile 名稱，exit code 2，終止
- **3b** 自訂 YAML profile 格式錯誤：系統輸出解析錯誤與行號，exit code 2，終止
- **4a** 目錄下無 `.java` 檔：系統輸出警告 `No Java files found`，exit code 0，終止
- **5a** 某 `.java` 檔無法解析（語法錯誤）：系統記錄警告，跳過該檔，繼續處理其餘檔案
- **8a** 所有違規皆已被 suppress：報告顯示 `All checks passed (N suppressed)`，exit code 0

---

#### UC-03: Define Custom Style Profile（Fully Dressed）

| 欄位 | 內容 |
|-----|------|
| **Use Case** | UC-03 |
| **Use Case Name** | Define Custom Style Profile |
| **Scope** | arch-checker CLI |
| **Level** | User Goal |
| **Primary Actor** | Developer |
| **Preconditions** | Developer 了解目標專案的架構規範 |
| **Postconditions** | 一個有效的 `.yaml` Profile 檔案可被 UC-01 載入使用 |
| **Relates to** | SF-03 |

**Main Success Scenario：**

1. Developer 執行 `arch-checker init-profile --name <name> --output <file.yaml>`
2. 系統產生含預設結構的空白 YAML 模板並寫入 `file.yaml`
3. Developer 編輯 `file.yaml`，依語法填入規則（rule type、package、constraint）
4. Developer 執行 `arch-checker validate-profile <file.yaml>` 驗證語法
5. 系統讀取並解析 YAML 檔
6. 系統驗證每條規則的型別（NamingRule / DependencyRule / PackageRule）與必填欄位
7. 系統驗證 package pattern 語法
8. 系統輸出 `Profile valid: <name>, <N> rules loaded`
9. Developer 在專案目錄加入 `.arch-checker.yaml` 指向此 Profile

**Alternative Flows：**

- **4a** Developer 直接執行 validate（略過 init-profile）：從步驟 5 開始，正常執行
- **5a** 檔案不存在：系統輸出 `Error: file not found`，終止
- **6a** YAML 語法錯誤：系統輸出錯誤行號與訊息，終止
- **6b** 未知的 rule type：系統輸出合法 rule type 清單，終止
- **7a** package pattern 含非法字元：系統說明允許的 pattern 語法，終止
- **8a** YAML 格式正確但 rules 清單為空：系統輸出警告 `Profile has no rules`，仍視為有效

---

---

#### UC-04: Check with Fix Suggestions - Interactive Mode（Fully Dressed）

| 欄位 | 內容 |
|-----|------|
| **Use Case** | UC-04 |
| **Use Case Name** | Check Architecture Compliance with Interactive Fix Suggestions |
| **Level** | User Goal |
| **Primary Actor** | Developer |
| **Preconditions** | 同 UC-01；Developer 有意願逐條決策每個違規 |
| **Postconditions** | 每條 Violation：(a) Suppression 寫入 config，或 (b) 被跳過，或 (c) 流程中止 |
| **Relates to** | SF-01, SF-06 |

**Main Success Scenario：**

1. Developer 執行 `arch-checker check --interactive --profile <name> <path>`
2. 系統完成 UC-01 Step 2–7（掃描、解析、套用規則、收集 Violation）
3. 系統逐條展示第一個 Violation，並顯示：
   - 違規位置（檔案路徑、行號）
   - 違反的 ArchitectureConstraint ID 與說明
   - RefactoringSuggestion（若存在）
4. 系統提示 `Accept suppression? [y/n/q]`
5. Developer 輸入 `y`：系統將此 Violation 的 constraint ID 寫入 `.arch-checker-suppress.yaml`
6. 系統繼續展示下一條 Violation（回到 Step 3）
7. 所有 Violation 處理完畢後，系統輸出最終 ComplianceReport（已扣除已接受的 suppression）
8. 系統回傳 exit code

**Alternative Flows：**

- **5a** Developer 輸入 `n`：此 Violation 保留在報告中，系統繼續下一條（Step 6）
- **5b** Developer 輸入 `q`：系統中止互動，輸出目前已處理的 Violation 之部分報告，exit code 1
- **3a** 此 Violation 有 RefactoringSuggestion：系統額外顯示建議步驟，再提示 `[y/n/q]`
- **3b** 無 Violation：系統直接輸出 `All checks passed`，不進入互動迴圈

---

#### UC-02: Load Style Profile（Brief）

`check` 或 `init` 時的子流程。依名稱載入內建 Profile（layered / hexagonal / clean）；否則視為 YAML 檔路徑，執行 UC-05 驗證後載入。

#### UC-05: Define Custom Style Profile（Brief，升格自前版 UC-03）

Developer 執行 `arch-checker init-profile` 產生 YAML 模板，編輯後以 `arch-checker validate-profile <file>` 驗證語法與規則型別。

#### UC-06: Suppress a Violation（Brief）

Developer 執行 `arch-checker suppress --id <id> --reason <text>`。系統將記錄寫入 `.arch-checker-suppress.yaml`，下次 UC-01 Step 8 過濾時生效。

---

### 3.5 Non-functional Requirements（NFR）

| ID | 類別 | 需求描述 |
|----|------|---------|
| NFR-01 | Performance | 掃描 1,000 個 `.java` 檔案的完整 check（UC-01）必須在 10 秒內完成（一般開發機） |
| NFR-02 | Usability | 所有錯誤訊息必須包含：錯誤原因、錯誤位置（檔案/行號）、建議修正動作 |
| NFR-03 | Portability | 工具需在 JDK 17+ 環境（Windows / macOS / Linux）上運行，不依賴特定 IDE |
| NFR-04 | Extensibility | 新增 StyleRule 子類別不需修改 `ArchChecker` 或 `StyleProfile`（Open/Closed Principle） |
| NFR-05 | CI Compatibility | exit code 慣例遵循 POSIX 標準（0 = success, 非 0 = failure），可直接整合 GitHub Actions |
| NFR-06 | Testability | 每個 `StyleRule` 子類別必須可在不需要完整 CLI 的情況下單獨進行 unit test |

### 3.6 Glossary

| 術語 | 定義 |
|-----|------|
| **Style Profile** | 定義一種架構風格的完整規則集合，以 YAML 格式描述或由內建設定提供 |
| **StyleRule** | Style Profile 中的單條約束規則，為 `NamingRule`、`DependencyRule`、`PackageRule` 的抽象父類別 |
| **Violation** | 一筆架構違規紀錄，包含檔案路徑、行號、規則 ID 與說明文字 |
| **ViolationReport** | 一次 check 執行的完整違規彙整結果 |
| **AST (Abstract Syntax Tree)** | Java 原始碼的抽象語法樹表示，由 JavaParser 解析產生，供 StyleRule 分析使用 |
| **CompilationUnit** | JavaParser 對單一 `.java` 檔案解析後的根節點物件 |
| **Suppression** | 開發者明確標記「此違規已知且接受」的機制，使其不出現在後續報告中 |
| **exit code** | CLI 程式結束時回傳給 shell 的整數碼（0 = pass，1 = violations found，2 = error） |
| **Layered Architecture** | 將系統分成 UI / Service / DAO 等層次，並限制層間依賴只能單向向下的架構風格 |
| **Hexagonal Architecture** | 又稱 Ports & Adapters，Domain 為核心，外部系統透過 Port 介面連接的架構風格 |
| **Clean Architecture** | 以同心圓表示依賴方向，Entity 在最內圈，Framework 在最外圈，依賴只能向內的架構風格 |

---

## 4. 核心 OO 設計

### 4.1 領域模型（Domain Model）

> 核心原則：領域概念應反映「架構規範」本身的業務邏輯，而非 JavaParser 的技術細節。

```
ArchitectureStyle
  - name: String
  - description: String
  1 ──< ArchitectureConstraint（抽象）
          - constraintId: String          ← e.g., "LAY-D01"
          - severity: Severity {HIGH, MEDIUM, LOW}
          ├── LayerDependencyConstraint
          │     - fromLayer: Layer
          │     - toLayer: Layer
          │     - direction: DependencyDirection {FORBIDDEN, REQUIRED}
          ├── NamingConventionConstraint
          │     - targetLayer: Layer
          │     - pattern: String          ← e.g., "*Controller"
          └── PackageOrganizationConstraint
                - requiredLayers: List<Layer>

Layer
  - name: String
  - packagePattern: String              ← e.g., "*.ui.*"

StyleProfile
  - profileName: String
  implements ──► ArchitectureStyle
  overrides layer packagePatterns for a specific project

Violation
  - location: SourceLocation            ← 技術細節封裝在此
  - severity: Severity
  * ──► 1 ArchitectureConstraint        ← 違反的是哪條「業務規則」
  0..1 ──► RefactoringSuggestion
             - description: String
             - suggestedAction: RefactoringAction {MOVE_CLASS, RENAME_CLASS, INTRODUCE_INTERFACE}

Suppression
  - constraintId: String
  - reason: String
  - isActive: Boolean

ComplianceReport
  - checkedAt: DateTime
  - profile: StyleProfile
  1 ──< Violation
  - summary: ComplianceSummary {passCount, failCount, suppressedCount}
```

**關鍵設計決策**：`Violation` 關聯到 `ArchitectureConstraint`（業務規則），而非只存行號。`RefactoringSuggestion` 的 `suggestedAction` 是枚舉，代表工具知道的重構策略，不是自由文字。這讓 `0..1` 關聯有明確的業務語意。

### 4.2 Package 結構

```
arch-checker/
├── domain/
│   ├── ArchitectureStyle
│   ├── ArchitectureConstraint（抽象）
│   │   ├── LayerDependencyConstraint
│   │   ├── NamingConventionConstraint
│   │   └── PackageOrganizationConstraint
│   ├── Layer
│   ├── StyleProfile
│   ├── Violation
│   ├── RefactoringSuggestion
│   ├── Suppression
│   └── ComplianceReport
├── checker/
│   ├── ConstraintChecker（介面，Strategy Pattern）
│   │   ├── LayerDependencyChecker
│   │   ├── NamingConventionChecker
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
    ├── InitCommand（UC-03 互動精靈）
    └── SuppressCommand
```

### 4.3 Strategy Pattern：ConstraintChecker（無 if-else）

```java
// 介面
public interface ConstraintChecker {
    boolean supports(ArchitectureConstraint constraint);
    List<Violation> check(SourceFile file, ArchitectureConstraint constraint);
}

// 具體實作
public class LayerDependencyChecker  implements ConstraintChecker { ... }
public class NamingConventionChecker implements ConstraintChecker { ... }
public class PackageOrganizationChecker implements ConstraintChecker { ... }

// 協調者：透過 supports() dispatch，完全沒有 if-else / switch
public class ArchChecker {
    private final List<ConstraintChecker> checkers;  // 注入所有實作

    public ComplianceReport check(Project project, StyleProfile profile) {
        for (SourceFile file : project.sourceFiles()) {
            for (ArchitectureConstraint constraint : profile.constraints()) {
                checkers.stream()
                    .filter(c -> c.supports(constraint))
                    .forEach(c -> violations.addAll(c.check(file, constraint)));
            }
        }
    }
}
```

### 4.4 Visitor Pattern：AST 走訪

```java
// JavaParser 原生 Visitor API
public abstract class ConstraintViolationVisitor
        extends VoidVisitorAdapter<ViolationCollector> {
    // 子類別各自 override 需要的節點類型
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
    public LayerDependencyBuilder from(Layer layer)       { ... return this; }
    public LayerDependencyBuilder cannotDependOn(Layer l) { ... return this; }
    @Override protected LayerDependencyBuilder self()     { return this; }
    @Override public LayerDependencyConstraint build()    { ... }
}

// 使用範例（Fluent API）
ArchitectureConstraint c = new LayerDependencyBuilder()
    .withId("LAY-D01")
    .withSeverity(Severity.HIGH)
    .from(UI_LAYER)
    .cannotDependOn(DAO_LAYER)
    .build();
```

→ DCD 展示 `ConstraintBuilder<SELF>` → `LayerDependencyBuilder` / `NamingConventionBuilder` 的泛型繼承，這正是「透過繼承與介面實現 Fluent API」的具體圖面。

### 4.6 Strategy Pattern：ReportFormatter

```java
public interface ReportFormatter {
    String format(ComplianceReport report);
}

public class ConsoleFormatter implements ReportFormatter { ... }
public class JsonFormatter    implements ReportFormatter { ... }
```

### 4.5 三種內建 Style Profile

> 三種均屬 **Architectural Style** 層級，可透過靜態分析套件依賴方向來驗證。

#### Layered Architecture（分層架構）

依賴方向：UI → Service → DAO，只能單向向下。

| 規則 ID | 說明 |
|--------|------|
| LAY-D01 | `*.ui.*` 不能直接 import `*.dao.*` 或 `*.repository.*`（跨層） |
| LAY-D02 | `*.service.*` 不能 import `*.ui.*`（下層不依賴上層） |
| LAY-D03 | `*.dao.*` 不能 import `*.service.*` 或 `*.ui.*` |

#### Hexagonal Architecture（六角形架構 / Ports & Adapters）

Domain 在核心，Adapter 在外圍，依賴方向只能由外向內。

| 規則 ID | 說明 |
|--------|------|
| HEX-D01 | `*.domain.*` 不能 import `*.adapter.*`（核心不知道外部） |
| HEX-D02 | `*.adapter.*` 不能互相 import（各 Adapter 獨立） |
| HEX-P01 | `*.domain.*` 只能透過 Port 介面（`*Port`）對外溝通 |

#### Clean Architecture

圈層由內而外：Entity → UseCase → Interface Adapter → Framework。依賴只能向內。

| 規則 ID | 說明 |
|--------|------|
| CLN-D01 | `*.entity.*` 不能 import 任何其他圈層 |
| CLN-D02 | `*.usecase.*` 不能 import `*.adapter.*` 或 `*.framework.*` |
| CLN-D03 | `*.framework.*`（最外圈）可以 import 任何圈層 |

---

## 5. 實作步驟（對應課程 HW 進度）

| Phase | 內容 | 對應 HW |
|-------|------|---------|
| Phase 1 | 建立 Maven 專案、`StyleRule` 抽象層級、`StyleProfile` Builder | HW2 |
| Phase 2 | `JavaSourceParser`（JavaParser AST 解析）、`ArchChecker` 主流程、Console 報告 | HW3 |
| Phase 3 | `ProfileLoader`（YAML）、`ProfileValidator`、`SuppressConfig` | HW4 |
| Phase 4 | picocli CLI 介面、JSON 輸出、內建三種 Profile | HW5 |
| Phase 5 | JUnit 5 單元測試（每個 Rule 獨立可測） | HW5 |

---

## 6. Use Case 豐富度的誠實評估

| 評估維度 | 評分 | 說明 |
|---------|------|------|
| Use Case 數量（1 人組） | ✅ | 2 fully dressed + 3 brief，符合規定 |
| Fully Dressed UC 大小 | ⚠️ | UC-01 有 10 步 MSS + 6 alternative flow，屬中等大小；若老師認為偏小，UC-04（suppress）可升為 fully dressed |
| SSD 可繪性 | ✅ | UC-01 Step 4–8 可畫出有意義的 SSD（系統內部有多個物件互動） |
| Domain Model 豐富度 | ✅✅ | **這是強項**：StyleRule 繼承層級 + Violation + ViolationReport + ProfileLoader 等物件豐富 |
| OO Design（DCD）豐富度 | ✅✅ | **這是強項**：多型、Strategy、Builder 三種設計模式可充分展示 |

**結論**：對 1 人組而言，HW2 可以通過，但建議在 UC-01 的 Fully Dressed 格式中把 alternative flow 寫得夠詳細，以補足 use case 數量偏少的問題。

---

## 7. 技術選型

| 項目 | 選擇 | 理由 |
|-----|------|------|
| 語言 | Java 17 | 課程規定 OO 語言；LTS 版本穩定 |
| AST 解析 | JavaParser 3.x | 純 Java，API 直覺，無需 JDK tools |
| CLI 框架 | picocli | 輕量，annotation-driven，與 Java 17 相容 |
| Profile 格式 | YAML（SnakeYAML） | 可讀性高，易於手工編輯 |
| 測試框架 | JUnit 5 | 課程要求 unit testing |
| 建置工具 | Maven | 依賴管理方便 |
