# arch-checker
## Requirements Document

> **Course**: Object-Oriented Analysis and Design
> **Homework**: #2
> **Student**: 113598009 李俊威
> **Date**: 2026-03-12

---

## Change History

| Version | Date | Author | Description |
|---------|------|--------|-------------|
| 1.0 | 2026-03-03 | 李俊威 | Initial submission (HW1) — Problem Statement |
| 2.0 | 2026-03-12 | 李俊威 | Add System Features, Use Cases, NFR, Glossary (HW2) |

---

## 1. Problem Statement

隨著軟體系統規模不斷擴大，維護一致的架構風格成為開發團隊面臨的核心挑戰之一。架構風格是一組規範類別之間職責分配、依賴方向與互動方式的結構性約束，開發團隊通常會在專案初期選定特定的架構風格，作為整個程式碼庫的設計準則。然而，隨著程式碼持續增長，確認實際產出的程式碼是否真正符合預期的架構風格，往往只能依賴人工逐一審查，這是一個費時、主觀且難以規模化的過程。

這個問題在導入 AI 輔助程式碼生成的開發情境中尤為突出。當開發者使用大型語言模型生成程式碼時，產出物在功能上可能正確，但在結構上卻可能偏離預期的架構風格。若缺乏自動化的合規驗證工具，開發者只能在事後手動比對每個類別是否符合架構規範，大量的人工驗證時間在開發週期上變成了瓶頸。

現有工具（如 ArchUnit）雖然允許開發者將架構限制寫成測試規則，但使用門檻較高——開發者必須熟悉其底層 API，並為每一條規則撰寫繁瑣的樣板程式碼。目前市場上缺乏一種高階、以風格為中心的抽象工具，讓開發者能夠以簡潔的宣告方式定義「這個專案遵循某種架構風格」，並立即獲得具體、可操作的違規回饋。

**arch-checker** 是一套專為 Java 開發者、軟體架構師與研究人員設計的命令列工具，用於自動驗證 Java 專案的架構風格合規性。使用者以 YAML 格式定義架構風格規則集（Style Profile），所有架構約束完全由使用者自行宣告，工具本身不預設任何內建風格。系統底層透過 Java 原始碼的抽象語法樹（AST）解析，精確偵測結構性違規，並產出包含檔案路徑、行號與錯誤描述的完整報告。arch-checker 適用於本地開發、程式碼審查與持續整合三種情境。

**Development Language**: Java (JDK 17+)

---

## 2. System Features

| ID | Feature Name | Priority | Description |
|----|-------------|----------|-------------|
| SF-01 | Architecture Compliance Checking | High | Scan a Java project directory and detect violations based on a user-defined Style Profile |
| SF-02 | Style Profile Definition via YAML | High | User defines all architecture style rules in YAML format; no built-in styles are provided by the tool |
| SF-03 | Violation Report Generation | High | Output a report with file path, line number, rule ID, and description for each violation |
| SF-04 | Multiple Report Formats | Low | Support Console and JSON output formats |
| SF-05 | Violation Suppression | Medium | Mark specific violations as "known and accepted" so they are excluded from subsequent reports |

---

## 3. Use Case Model

### 3.1 Actors

| Actor | Type | Description |
|-------|------|-------------|
| Developer | Primary | Uses the tool to define architecture rules and check Java projects for compliance |
| CI System | Secondary | Automatically triggers compliance checks in a CI/CD pipeline; relies on exit codes |

### 3.2 Use Case Diagram

```
┌─────────────────────────────────────────────────────────┐
│                      arch-checker                       │
│                                                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │  UC-01: Check Architecture Compliance   ◄────┐  │   │
│  └──────────────────────┬──────────────────┘    │  │   │
│                         │ <<include>>            │  │   │
│  ┌──────────────────────▼──────────────────┐    │  │   │
│  │  UC-05: Load Style Profile              │    │  │   │
│  │         (subfunction)                   │    │  │   │
│  └─────────────────────────────────────────┘    │  │   │
│                                                 │  │   │
│  ┌─────────────────────────────────────────┐    │  │   │
│  │  UC-02: Define Style Profile            │    │  │   │
│  └─────────────────────────────────────────┘    │  │   │
│                                                 │  │   │
│  ┌─────────────────────────────────────────┐    │  │   │
│  │  UC-03: Check with Fix Suggestions      │────┘  │   │
│  │         (Interactive Mode)              │       │   │
│  └─────────────────────────────────────────┘       │   │
│                                                    │   │
│  ┌─────────────────────────────────────────┐       │   │
│  │  UC-04: Suppress a Violation            │       │   │
│  └─────────────────────────────────────────┘       │   │
└─────────────────────────────────────────────────────────┘
        ▲                                        ▲
    Developer                               CI System
```

### 3.3 Use Case Summary

| UC | Name | Level | Format |
|----|------|-------|--------|
| UC-01 | Check Architecture Compliance | User Goal | Fully Dressed |
| UC-02 | Define Style Profile | User Goal | Fully Dressed |
| UC-03 | Check Architecture Compliance with Interactive Fix Suggestions | User Goal | Fully Dressed |
| UC-04 | Suppress a Violation | User Goal | Brief |
| UC-05 | Load Style Profile | Subfunction | Brief |

---

### UC-01: Check Architecture Compliance（Fully Dressed）

| Use Case Section | Content |
|-----------------|---------|
| **Use Case** | UC-01 |
| **Use Case Name** | Check Architecture Compliance |
| **Scope** | arch-checker CLI |
| **Level** | User Goal |
| **Primary Actor** | Developer |
| **Stakeholders and Interests** | **Developer**: Wants an accurate and detailed violation report with file paths and line numbers to quickly identify non-compliant classes without time-consuming manual review.<br>**Team Lead / Architect**: Wants to ensure the entire codebase conforms to the team-defined architecture rules; zero violations mean architecture consistency is maintained.<br>**CI System** (secondary): Wants a reliable POSIX exit code (0/1/2) to determine pipeline pass/fail without parsing report content. |
| **Preconditions** | 1. `arch-checker` is installed and available on the system PATH<br>2. The target Java project directory exists and is readable<br>3. The Style Profile YAML file specified by `<yaml-path>` exists and is syntactically valid |
| **Success Guarantee** | The system produces a complete ViolationReport containing: number of files checked, list of violations found, and suppressedCount; and returns a POSIX exit code (0 = pass, 1 = violations found) |
| **Main Success Scenario** | 1. Developer executes `arch-checker check --profile <yaml-path> <project-path>`<br>2. System verifies that `project-path` directory exists<br>3. System reads and parses the Style Profile YAML file at `<yaml-path>`<br>4. System recursively scans all `.java` files under `project-path`<br>5. System parses each `.java` file into an AST (CompilationUnit) using JavaParser<br>6. System applies all ArchitectureConstraint rules in the Profile to each CompilationUnit<br>7. System collects all Violations; each record contains: file path, line number, rule ID, and description<br>8. System filters out Violations that have been previously suppressed<br>9. System outputs the ViolationReport in Console format (violation list + summary)<br>10. System returns exit code: 0 if no violations remain, 1 if violations exist |
| **Extensions** | **2a.** `project-path` does not exist: System outputs `Error: path not found <path>`, exit code 2, terminates<br>**3a.** `<yaml-path>` does not exist: System outputs `Error: profile file not found <path>`, exit code 2, terminates<br>**3b.** YAML is not syntactically valid: System outputs YAML parse error with line number, exit code 2, terminates<br>**3c.** YAML is syntactically valid but contains invalid rule definitions (e.g., unknown rule type): System outputs validation error list, exit code 2, terminates<br>**4a.** No `.java` files found under `project-path`: System outputs `Warning: No Java files found in <path>`, exit code 0, terminates<br>**5a.** A `.java` file cannot be parsed (syntax error): System logs `Warning: skipped <file> (parse error)`, skips that file, continues with remaining files<br>**8a.** All violations have been suppressed: Report displays `All checks passed (N violations suppressed)`, exit code 0 |
| **Special Requirements** | **NFR-01 (Performance)**: Scanning 1,000 `.java` files must complete within 10 seconds on a standard development machine<br>**NFR-02 (Usability)**: All error messages must include: error reason, location (file/line), and suggested corrective action<br>**NFR-05 (CI Compatibility)**: Exit codes follow POSIX convention; directly integrable with GitHub Actions / Jenkins |
| **Technology and Data Variations List** | **\*a.** `--profile` accepts any file system path to a YAML file; no built-in profile names are supported<br>**\*b.** Output format can be switched to JSON via `--format json`; default is Console text format<br>**4a.** Java file scanning uses recursive depth-first traversal |
| **Frequency of Occurrence** | CI environment: triggered on every push, potentially dozens of times per day; local development: several times per hour |
| **Miscellaneous** | Does the tool need to support multi-module Maven projects (multiple `src/main/java` subdirectories)?<br>Is incremental scanning needed for large projects (>10,000 `.java` files)? |

---

### UC-02: Define Style Profile（Fully Dressed）

| Use Case Section | Content |
|-----------------|---------|
| **Use Case** | UC-02 |
| **Use Case Name** | Define Style Profile |
| **Scope** | arch-checker CLI |
| **Level** | User Goal |
| **Primary Actor** | Developer |
| **Stakeholders and Interests** | **Developer**: Wants to define reusable architecture rules with minimal effort; wants a template to reduce YAML boilerplate and wants immediate feedback on format errors before saving.<br>**Team Lead**: Wants the team to share a single, version-controlled Profile in Git; wants rules to be clearly described and maintainable long-term. |
| **Preconditions** | 1. `arch-checker` is installed and available on the system PATH<br>2. Developer understands the target project's architecture conventions (knows which packages belong to which layer and which dependencies are forbidden) |
| **Success Guarantee** | A syntactically valid `.yaml` Profile file containing at least one rule exists on disk and can be successfully loaded by UC-01 |
| **Main Success Scenario** | 1. Developer executes `arch-checker init-profile --output <file.yaml>`<br>2. System generates a YAML template with default structure and inline comments explaining each field, and writes it to `<file.yaml>`<br>3. Developer opens `<file.yaml>` in a text editor and fills in rules (rule type, package patterns, constraint details)<br>4. Developer executes `arch-checker validate-profile <file.yaml>` to verify syntax and rule definitions<br>5. System reads and parses the YAML file<br>6. System validates each rule's `type` (`NamingRule` / `DependencyRule` / `PackageRule`) and all required fields<br>7. System validates the syntax of each package pattern (only alphanumeric, `*`, and `.` are allowed)<br>8. System outputs `Profile valid: <N> rules loaded`<br>9. Developer uses the profile in UC-01 via `--profile <file.yaml>` |
| **Extensions** | **4a.** Developer skips `init-profile` and runs `validate-profile` directly on an existing YAML file: execution begins at Step 5, proceeds normally<br>**5a.** `<file.yaml>` does not exist: System outputs `Error: file not found <path>`, terminates<br>**6a.** Top-level YAML structure is invalid (not a valid YAML mapping): System outputs `Invalid YAML structure` with error line number, terminates<br>**6b.** Unknown rule type encountered: System outputs `Unknown rule type: <type>. Allowed: NamingRule, DependencyRule, PackageRule`, terminates<br>**7a.** A package pattern contains illegal characters: System outputs `Invalid package pattern: <pattern>`, terminates<br>**8a.** YAML is valid but the rules list is empty: System outputs `Warning: Profile has no rules. Profile is valid but will produce no violations.`, exit code 0 |
| **Special Requirements** | **NFR-02 (Usability)**: All validation error messages must clearly indicate the YAML error line number and provide a corrective suggestion<br>**NFR-04 (Extensibility)**: Adding a new rule type must not require modifying the core `ProfileValidator` logic (Open/Closed Principle) |
| **Technology and Data Variations List** | **\*a.** Profile format is YAML (parsed by SnakeYAML), encoded in UTF-8<br>**\*b.** Developer may use any text editor (VS Code, IntelliJ, Vim, etc.); no IDE plugin is required<br>**2a.** If the directory specified by `--output` does not exist, the system creates it automatically |
| **Frequency of Occurrence** | Typically executed once per new project; repeated when architecture rules need to be updated; low frequency, on a weekly or monthly basis |
| **Miscellaneous** | Should the tool publish a JSON Schema to enable IntelliJ / VS Code autocomplete when editing profile YAML?<br>Should profiles support inheritance (`extends: <other-yaml-path>`) to extend existing rule sets? |

---

### UC-03: Check Architecture Compliance with Interactive Fix Suggestions（Fully Dressed）

| Use Case Section | Content |
|-----------------|---------|
| **Use Case** | UC-03 |
| **Use Case Name** | Check Architecture Compliance with Interactive Fix Suggestions |
| **Scope** | arch-checker CLI |
| **Level** | User Goal |
| **Primary Actor** | Developer |
| **Stakeholders and Interests** | **Developer**: Wants to review each violation one by one and decide whether to suppress it on the spot; wants actionable fix suggestions rather than just a list of errors.<br>**Team Lead**: Wants to know which violations are "known and accepted" technical debt, with a persistent and auditable suppression record rather than silent workarounds. |
| **Preconditions** | 1. Same preconditions as UC-01<br>2. Developer intends to decide on each violation individually (not a CI batch automation context)<br>3. Terminal supports interactive input (not a pipe-redirected non-TTY mode) |
| **Success Guarantee** | For each Violation, one of three outcomes applies: (a) a Suppression record is written to `.arch-checker-suppress.yaml`, (b) the Violation is retained in the report, or (c) the process is aborted; the final ComplianceReport reflects all decisions made |
| **Main Success Scenario** | 1. Developer executes `arch-checker check --interactive --profile <yaml-path> <project-path>`<br>2. System completes the scan (same as UC-01 Steps 2–7): traverses files, parses AST, applies rules, collects all Violations<br>3. System displays the first Violation, showing: file path and line number, violated ArchitectureConstraint ID and description, and RefactoringSuggestion (if one exists)<br>4. System prompts `Accept suppression? [y/n/q]:`<br>5. Developer enters `y`: System writes the Violation's constraint ID and a timestamp to `.arch-checker-suppress.yaml`<br>6. System displays the next Violation (returns to Step 3)<br>7. After all Violations have been processed, System outputs the final ComplianceReport (with accepted suppressions excluded)<br>8. System returns exit code (0 = no unaccepted violations remain, 1 = unaccepted violations exist) |
| **Extensions** | **3a.** The current Violation has a RefactoringSuggestion: System displays the suggested action (e.g., `Suggestion: Move class to *.service.* package`) before showing the `[y/n/q]` prompt<br>**3b.** No violations were found after scanning: System outputs `All checks passed` immediately without entering the interactive loop, exit code 0<br>**5a.** Developer enters `n`: The Violation is retained in the final report; System continues to the next Violation (Step 6)<br>**5b.** Developer enters `q`: System aborts the interactive loop, outputs a partial report of decisions made so far, exit code 1<br>**5c.** Developer enters an invalid value (not y/n/q): System outputs `Invalid input. Please enter y, n, or q:` and waits for input again<br>**8a.** All Violations were suppressed (Developer entered `y` for all): Report displays `All checks passed (N violations suppressed)`, exit code 0 |
| **Special Requirements** | **NFR-02 (Usability)**: The interactive prompt must be self-explanatory; Developers should understand the meaning of `[y/n/q]` without consulting documentation<br>**NFR-06 (Testability)**: The interactive flow must be verifiable by unit tests without launching the full CLI (I/O must be mockable) |
| **Technology and Data Variations List** | **\*a.** Suppression records are written to `.arch-checker-suppress.yaml` and include: constraint ID, reason (default: `suppressed interactively`), and timestamp<br>**4a.** `[y/n/q]` input is case-insensitive (`Y` and `Yes` are treated as accepted)<br>**\*b.** If stdout is pipe-redirected (non-TTY), the system automatically falls back to non-interactive batch mode and outputs a warning |
| **Frequency of Occurrence** | Used when initially building the suppression list for a new project, typically once per project; used on-demand when new violations appear; low frequency overall |
| **Miscellaneous** | Should a `--accept-all` option be provided to suppress all violations in one batch?<br>Should suppressions have an expiration date to prevent indefinite accumulation of technical debt? |

---

### UC-04: Suppress a Violation（Brief）

Developer executes `arch-checker suppress --id <constraint-id> --reason <text>`. System appends a suppression record (constraint ID, reason, timestamp) to `.arch-checker-suppress.yaml`. The suppression takes effect at UC-01 Step 8 on the next check run.

---

### UC-05: Load Style Profile（Brief — Subfunction）

An `<<include>>` subfunction of UC-01 and UC-03. System reads the YAML file specified by `--profile <yaml-path>`, parses it via `ProfileLoader`, and validates rule definitions via `ProfileValidator`. On success, the profile is loaded into memory for use by the constraint checkers.

---

## 4. Non-Functional Requirements

| ID | Category | Requirement |
|----|----------|-------------|
| NFR-01 | Performance | A full compliance check (UC-01) scanning 1,000 `.java` files must complete within 10 seconds on a standard development machine |
| NFR-02 | Usability | All error messages must include: error reason, location (file path / line number), and suggested corrective action |
| NFR-03 | Portability | The tool must run on JDK 17+ environments (Windows / macOS / Linux) without depending on any specific IDE |
| NFR-04 | Extensibility | Adding a new `ConstraintChecker` subclass must not require modifying `ArchChecker` or `StyleProfile` (Open/Closed Principle) |
| NFR-05 | CI Compatibility | Exit codes follow POSIX convention (0 = success, non-zero = failure); directly integrable with GitHub Actions and Jenkins |
| NFR-06 | Testability | Each `ConstraintChecker` subclass must be independently unit-testable without requiring the full CLI to be running |

---

## 5. Glossary

| Term | Definition |
|------|------------|
| **Style Profile** | A YAML file authored by the user that defines a complete set of architecture constraint rules for a specific project; the tool provides no built-in profiles |
| **ArchitectureConstraint** | A single constraint rule within a Style Profile; the abstract parent of `NamingRule`, `DependencyRule`, and `PackageRule` |
| **Violation** | A single architecture violation record containing: file path, line number, rule ID, and description text |
| **ViolationReport** | The complete output of a single check execution, aggregating all violations and a summary |
| **AST (Abstract Syntax Tree)** | The tree representation of a Java source file's structure, produced by JavaParser and used by ConstraintCheckers for analysis |
| **CompilationUnit** | The root node object produced by JavaParser when parsing a single `.java` file |
| **Suppression** | A developer-declared record marking a specific violation as "known and accepted", causing it to be excluded from future reports |
| **exit code** | The integer value returned by the CLI to the shell upon completion (0 = pass, 1 = violations found, 2 = error) |
| **Package Pattern** | A wildcard string (e.g., `*.ui.*`) used in rule definitions to match Java package names; only alphanumeric characters, `*`, and `.` are permitted |

---

## 6. Measurement

| Student ID | Name | HW | Date | Duration |
|-----------|------|----|------|----------|
| 113598009 | 李俊威 | HW#2 | 115/03/12 | TBD |
