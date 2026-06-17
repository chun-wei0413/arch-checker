# arch-checker · Demo

OOAD 期中報告（[`hw/Midterm/midterm-team6.pptx`](../hw/Midterm/midterm-team6.pptx)）
demo 場景的可重現範例。本資料夾含一個小型的**付款 (payment) 三層架構** Java 專案
（domain / service / controller），刻意設計成讓 4 條 rule 各觸發 1 筆違規，
且每筆違規都對應一個 **code review 常見的提醒**。

一條龍示範 arch-checker 的 5 個核心 use cases：

- **UC-01** Check Architecture Compliance（檢查 + JSON 輸出）
- **UC-02** Define Style Profile（`profile init` / `profile validate`）
- **UC-03** Check with Interactive Fix Suggestions（`fix`）
- **UC-04** Suppress a Violation（標記「可接受」並過濾）
- **UC-05** Load Style Profile（由 UC-01 載入 profile 時 include）

同時涵蓋 4 種 GRASP Polymorphism 子類別 — `NamingRule`、`DependencyRule`、
`SupertypeRule`、`PackageRule`。

---

## 內容物

```
demo/
├── README.md                ← 本檔
├── demo-profile.yaml        ← Style Profile（4 條規則）
└── sample-project/          ← 三層 Java 專案（payment 處理）
    └── src/main/java/com/example/
        ├── domain/
        │   ├── core/Payment.java                抽象基底（合規）
        │   └── payment/
        │       ├── CreditCardPayment.java       合規（extends Payment）
        │       └── CashPayment.java             ❌ R-SUP-01（未 extend Payment）
        ├── service/
        │   ├── PaymentService.java              合規
        │   ├── PaymentManager.java              ❌ R-NAME-01（Manager 後綴）
        │   └── ChargeService.java               ❌ R-DEP-01（import controller）
        └── controller/PaymentController.java     合規
```

共 **7 個 .java 檔，恰好觸發 4 條 rule 各 1 筆違規**（PackageRule 為 project-level，
因缺 `com.example.repository` 套件而觸發）。arch-checker 只遞迴尋找 `.java`，
任意目錄皆可作為輸入，毋須 Maven 專案。

---

## Style Profile

[`demo-profile.yaml`](./demo-profile.yaml) 的 4 條規則對應 4 個 `ComplianceRule`
子類別（GRASP Polymorphism）：

| Rule ID    | 子類別           | 規則內容                                          | 違規來源                          | Code review 常見提醒                                       |
|------------|------------------|---------------------------------------------------|-----------------------------------|------------------------------------------------------------|
| R-NAME-01  | `NamingRule`     | 禁止 `Manager`/`Helper`/`Util`/`Handler` 等模糊後綴 | `service/PaymentManager.java`     | Manager 不表達責任，建議改 `PaymentService` / `PaymentReconciler` |
| R-DEP-01   | `DependencyRule` | service 不得依賴 controller                        | `service/ChargeService.java`      | 反向依賴會循環依賴、且 service 不可測試                     |
| R-SUP-01   | `SupertypeRule`  | `domain.payment` 內每個 type 須 extend `Payment`   | `domain/payment/CashPayment.java` | 未繼承共同基底就無法被 `List<Payment>` 一視同仁處理        |
| R-PKG-01   | `PackageRule`    | 專案應有 `com.example.repository` 套件             | （project-level）                 | 分層專案應有 repository 套件存放 persistence 介面          |

> 基底 `Payment` 放在 `domain.core` 而非 `domain.payment`，否則 SupertypeRule
> 會抓到基底自己（「`Payment` 必須 extend `Payment`」）。

---

## 前置作業：build arch-checker

⚠️ **下列指令、以及本檔後續所有範例，都要在 repository 根目錄（`arch-checker/`）執行，
不是在 `demo/` 裡**。先 `cd` 回根目錄再開始（**build 一次即可**）：

```bash
cd ~/Coding/arch-checker
mvn -B package -DskipTests
chmod +x arch-checker
export PATH="$PWD:$PATH"
```

完成後所有範例都直接打 `arch-checker`，macOS 與 Windows 相同。`arch-checker`
（shell）與 `arch-checker.bat`（Windows）皆為 `java -jar target/arch-checker.jar`
的包裝，fat JAR 已內含所有執行期相依。

> ⚠️ **`export PATH` 只在當前終端機有效**。`arch-checker` 不是系統指令，而是專案
> 根目錄裡的 script，shell 只會在 `$PATH` 列出的目錄找它。**每次新開終端機**（或
> 出現 `command not found: arch-checker`）都要在 repository 根目錄重跑上面那行
> `export PATH="$PWD:$PATH"`；或改用相對路徑 `./arch-checker check ...`（須在根目錄、
> 名字前加 `./`）。想一勞永逸，把該行寫進 `~/.zshrc` 再 `source ~/.zshrc`。

> 連續 demo 前先 `rm -f demo/.arch-checker-suppress.yaml` 清掉示範用的 suppression。

---

## UC-01 · Check Architecture Compliance

**(a) 對 arch-checker 自身檢查（dogfooding，happy path）** — 0 violations、exit 0：

```bash
arch-checker check src/main/java arch.yaml
```

```
--
Checked 32 file(s); 0 violation(s); 0 suppressed.
```

**(b) 檢查 sample-project** — 4 筆違規、exit 1。載入 profile 即 UC-05：

```bash
arch-checker check demo/sample-project demo/demo-profile.yaml
```

```
demo/sample-project/src/main/java/com/example/service/PaymentManager.java:12 [R-NAME-01] Class 'PaymentManager' violates naming pattern '(?!.*(?:Manager|Helper|Util|Handler)$).*'
demo/sample-project/src/main/java/com/example/service/ChargeService.java:2 [R-DEP-01] Package 'com.example.service' must not depend on 'com.example.controller' (import: com.example.controller.PaymentController)
demo/sample-project/src/main/java/com/example/domain/payment/CashPayment.java:12 [R-SUP-01] Class 'CashPayment' must extend/implement 'Payment'
demo/sample-project/src/main/java/com/example/controller/PaymentController.java:0 [R-PKG-01] Required package pattern 'com.example.repository' not present in the project
--
Checked 7 file(s); 4 violation(s); 0 suppressed.
```

**(c) JSON 輸出（FEA-04）** — 展示 Reporter 多型（Console / JSON strategy 切換），exit 1。
重導向把報告寫成 `demo/sample-project-report.json`，在 IntelliJ 開啟後按
**Option + Command + L** 即可把單行 JSON 重新排版（reformat）成易讀格式：

```bash
arch-checker check demo/sample-project demo/demo-profile.yaml --json > demo/sample-project-report.json
```

```json
{"checkedFiles":7,"violationCount":4,"suppressedCount":0,"violations":[
  {"file":".../service/PaymentManager.java","line":12,"ruleId":"R-NAME-01","message":"Class 'PaymentManager' violates naming pattern '...'"},
  {"file":".../service/ChargeService.java","line":2,"ruleId":"R-DEP-01","message":"Package 'com.example.service' must not depend on 'com.example.controller' ..."},
  {"file":".../domain/payment/CashPayment.java","line":12,"ruleId":"R-SUP-01","message":"Class 'CashPayment' must extend/implement 'Payment'"},
  {"file":".../controller/PaymentController.java","line":0,"ruleId":"R-PKG-01","message":"Required package pattern 'com.example.repository' not present in the project"}
]}
```

> 實際輸出為單行 JSON；上方為手動排版以利閱讀。

---

## UC-02 · Define Style Profile

由工具產生含 4 種規則型別的樣板，再驗證語法並列出載入的規則。

```bash
arch-checker profile init demo/my-profile.yaml
```

```
Profile template written to: demo/my-profile.yaml
```

```bash
arch-checker profile validate demo/demo-profile.yaml
```

```
Profile 'demo-profile' is valid. Loaded 4 rule(s):
  - R-NAME-01 (NamingRule)
  - R-DEP-01 (DependencyRule)
  - R-SUP-01 (SupertypeRule)
  - R-PKG-01 (PackageRule)
```

exit 0；YAML 語法錯誤或檔案不存在則印 `Validation failed: <error>` 並 exit 2。

> GRASP：`profile init` = **Controller**（ProfileInitCommand）+ **Information Expert + Creator**（ProfileTemplateService）；
> `profile validate` = **Controller** + **Information Expert**（ProfileValidateService / YamlProfileLoader）+
> **Low Coupling**（Service 依賴 ProfileLoader 介面）。

---

## UC-03 · Check with Interactive Fix Suggestions

`fix` 逐筆顯示修正建議並互動式 suppress：`y` 壓制（再問理由，Enter 跳過＝`Suppressed interactively`）、
`n` 保留、`q` 結束循環。

```bash
rm -f demo/.arch-checker-suppress.yaml
arch-checker fix demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml
```

互動示範（`>` 代表使用者輸入）：

```
Violation 1/4:
  .../service/PaymentManager.java:12 [R-NAME-01] Class 'PaymentManager' violates naming pattern '...'
  Suggestion: Rename 'PaymentManager' to match pattern '...'
Suppress? [y/n/q]: > y
Reason (Enter to skip): > legacy name; will rename in v2

Violation 2/4:
  .../service/ChargeService.java:2 [R-DEP-01] ...
  Suggestion: Remove import 'com.example.controller.PaymentController'
Suppress? [y/n/q]: > y
Reason (Enter to skip): >

Violation 3/4:
  .../domain/payment/CashPayment.java:12 [R-SUP-01] ...
  Suggestion: Add 'extends Payment' or 'implements Payment' to class declaration
Suppress? [y/n/q]: > n

Violation 4/4:
  .../controller/PaymentController.java:0 [R-PKG-01] ...
  Suggestion: Create at least one class in a package matching 'com.example.repository'
Suppress? [y/n/q]: > q

--
Checked 7 file(s); 2 violation(s) remaining; 2 suppressed.
```

exit 1（仍有殘留違規）。若全部回答 `y`，最終為 `0 violation(s) remaining; 4 suppressed`、exit 0。

> GRASP：**Controller**（FixCommand）+ **Pure Fabrication**（SuppressionService 封裝 load→create→save，與 SuppressCommand 共用）+
> **Information Expert**（SuppressionStore 掌握 suppression YAML 讀寫）。

---

## UC-04 · Suppress a Violation

把違規標記為「known and accepted」，下次檢查即過濾。流程：檢查 → suppress → 重檢。

```bash
rm -f demo/.arch-checker-suppress.yaml

# 對 R-NAME-01 個案執行 suppress（參數依序：profile / rule-id / file / line / reason）
arch-checker suppress demo/demo-profile.yaml R-NAME-01 \
    demo/sample-project/src/main/java/com/example/service/PaymentManager.java 12 \
    'legacy name; refactor in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml
```

```
Suppressed: R-NAME-01 at demo/sample-project/src/main/java/com/example/service/PaymentManager.java:12
```

寫入 `demo/.arch-checker-suppress.yaml`（`timestamp` 為當下時間）：

```yaml
- constraintId: R-NAME-01
  filePath: demo/sample-project/src/main/java/com/example/service/PaymentManager.java
  lineNumber: 12
  reason: legacy name; refactor in v2
  timestamp: '2026-06-18T00:00:00Z'
```

重檢 — R-NAME-01 已被過濾，剩 3 筆、exit 1：

```bash
arch-checker check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml
```

```
--
Checked 7 file(s); 3 violation(s); 1 suppressed.
```

其餘三條規則的 suppress 參數（rule-id / file / line）：

| Rule      | file-path                                                                  | line | 建議 reason                                              |
|-----------|----------------------------------------------------------------------------|------|----------------------------------------------------------|
| R-DEP-01  | `.../service/ChargeService.java`                                           | 2    | `temporary cross-layer wire-up; refactor in v2`          |
| R-SUP-01  | `.../domain/payment/CashPayment.java`                                      | 12   | `cash payments are out of scope for v1; inherit in v2`   |
| R-PKG-01  | `.../controller/PaymentController.java`                                    | 0    | `repository package planned for v2`                      |

> PackageRule 違規綁在掃描順序中第一個 `.java` 檔（`<file>:0`），依檢查輸出抄上去即可。

把 4 條全部 suppress 後重檢，得 `0 violation(s); 4 suppressed`、exit 0。

> GRASP **Indirection / Protected Variation**：suppress YAML I/O 全由 `YamlSuppressionStore` 負責。

---

## 子指令參考

```
Usage: arch-checker [-hV] [COMMAND]
  check     Check architecture compliance of a Java project against a Style Profile.
  suppress  Mark a specific violation as 'known and accepted'.
  fix       Check architecture compliance with interactive fix suggestions.
  profile   Manage Style Profile files (init / validate).
```

| 指令                                                              | 必填參數                                   | 選填                                              |
|-------------------------------------------------------------------|--------------------------------------------|---------------------------------------------------|
| `check <project-path> <profile-path>`                             | 專案根目錄、Style Profile YAML             | `-s, --suppress-file <path>`、`--json`            |
| `fix <project-path> <profile-path>`                               | 同上                                       | `-s, --suppress-file <path>`                      |
| `suppress <profile-path> <constraint-id> <file-path> <line> <reason>` | 依序 5 個位置參數                       | `--suppress-file <path>`                          |
| `profile init <output-path>`                                      | 輸出路徑                                   | —                                                 |
| `profile validate <profile-path>`                                 | Style Profile YAML                         | —                                                 |

`--suppress-file` 預設為 `.arch-checker-suppress.yaml`。

## Exit code（NFR-05）

| code | 意義                                   |
|------|----------------------------------------|
| 0    | pass（無違規或全部已 suppress）        |
| 1    | violations found                       |
| 2    | error（profile 缺檔、YAML 語法錯誤等） |

可直接用於 GitHub Actions / Jenkins / GitLab CI 流水線。
