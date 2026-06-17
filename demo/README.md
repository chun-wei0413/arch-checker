# arch-checker · Demo

OOAD 期中報告（[`hw/Midterm/midterm-team6.pptx`](../hw/Midterm/midterm-team6.pptx)）所示
demo 場景的可重現範例。本資料夾包含一個小型的**付款 (payment) 處理**三層
架構 Java 專案（domain / service / controller），刻意設計成讓 4 條 rule
各觸發 1 筆違規 — 而且每筆違規都對應一個 **code review 常見的提醒**，不是
為了製造違規而硬湊的 typo。

搭配 README 即可端到端示範 arch-checker 的核心 use cases：

- **UC-01** Check Architecture Compliance（檢查、JSON 輸出）
- **UC-02** Define Style Profile（`profile init` 產生樣板、`profile validate` 驗證）
- **UC-03** Check with Interactive Fix Suggestions（`fix` 指令逐筆修正）
- **UC-04** Suppress a Violation（標記「可接受」並讓下次檢查過濾）
- **UC-05** Load Style Profile（由 UC-01 include）

以及 arch-checker 的 4 種 GRASP Polymorphism 子類別 — `NamingRule`、
`DependencyRule`、`SupertypeRule`、`PackageRule`。

---

## 目錄

- [內容物](#內容物)
- [Style Profile 解說](#style-profile-解說)
- [前置作業：build arch-checker](#前置作業build-arch-checker)
- [Demo 場景對照（Scenario A–E）](#demo-場景對照scenario-ae)
- [各條 Rule 的觸發示範](#各條-rule-的觸發示範)
  - [R-NAME-01 · NamingRule（vague suffix）](#r-name-01--namingrulevague-suffix)
  - [R-DEP-01 · DependencyRule（service 依賴 controller）](#r-dep-01--dependencyruleservice-依賴-controller)
  - [R-SUP-01 · SupertypeRule（domain 多型基底）](#r-sup-01--supertyperuledomain-多型基底)
  - [R-PKG-01 · PackageRule（缺 repository 套件）](#r-pkg-01--packagerule缺-repository-套件)
  - [一次 suppress 全部 4 條規則](#一次-suppress-全部-4-條規則)
- [子指令參考](#子指令參考)
- [Exit code 對應 NFR-05](#exit-code-對應-nfr-05)

---

## 內容物

```
demo/
├── README.md                   ← 本檔
├── demo-profile.yaml           ← Style Profile（4 條規則）
└── sample-project/             ← 三層 Java 專案（payment 處理）
    └── src/main/java/com/example/
        ├── domain/
        │   ├── core/
        │   │   └── Payment.java                抽象基底（合規）
        │   └── payment/
        │       ├── CreditCardPayment.java      合規（extends Payment）
        │       └── CashPayment.java            ❌ R-SUP-01（沒 extend Payment）
        ├── service/
        │   ├── PaymentService.java             合規
        │   ├── PaymentManager.java             ❌ R-NAME-01（Manager 後綴）
        │   └── ChargeService.java              ❌ R-DEP-01（import controller）
        └── controller/
            └── PaymentController.java          合規
```

`sample-project` 不是 Maven 專案 — arch-checker 只需要遞迴尋找 `.java`，
所以任意目錄都可作為輸入。共 **7 個 .java 檔，恰好觸發 4 條 rule 各 1 筆違規**
（外加 PackageRule 是 project-level，沒有 `com.example.repository` 套件）。

---

## Style Profile 解說

[`demo-profile.yaml`](./demo-profile.yaml) 定義 4 條規則，**對應 arch-checker
4 個 `ComplianceRule` 子類別**（GRASP Polymorphism）：

| Rule ID    | Type / 子類別     | 規則內容                                                       | 違規來源                                | Code review 常見的提醒                                          |
|------------|------------------|---------------------------------------------------------------|-----------------------------------------|----------------------------------------------------------------|
| R-NAME-01  | `NamingRule`     | 禁止 `Manager` / `Helper` / `Util` / `Handler` 等模糊後綴        | `service/PaymentManager.java`           | 「Manager 不知道在做啥，建議改 PaymentService / PaymentReconciler」|
| R-DEP-01   | `DependencyRule` | service 不得依賴 controller                                      | `service/ChargeService.java`            | 「Controller 呼叫 Service，反過來會循環依賴 + service 不可測試」  |
| R-SUP-01   | `SupertypeRule`  | `domain.payment` 內每個 type 須 extend `Payment`                 | `domain/payment/CashPayment.java`       | 「沒繼承共同基底 = 沒辦法被 List<Payment> 一視同仁處理」         |
| R-PKG-01   | `PackageRule`    | 專案應有 `com.example.repository` 套件                          | （project-level）                       | 「typical 分層專案應有 repository 套件存放 persistence 介面」   |

---

## 前置作業：build arch-checker

在 repository 根目錄執行下列指令（**一次即可**）：

**macOS / Linux**

```bash
mvn -B package -DskipTests
chmod +x arch-checker
export PATH="$PWD:$PATH"
```

**Windows PowerShell**

```powershell
mvn -B package -DskipTests
$env:PATH = "$PWD;$env:PATH"
```

完成後，**後續所有範例指令都只要打 `arch-checker`**，macOS 與 Windows 完全相同，
不需要 `./` 或 `.\arch-checker.bat` 前綴。

> `arch-checker`（shell）和 `arch-checker.bat`（bat）是 `java -jar target/arch-checker.jar`
> 的一行包裝，fat JAR 已內含所有執行期相依，PATH 設完後系統會自動選正確的那支。

---

## Demo 場景對照（Scenario A–E）

### Scenario A · 成功流程（UC-01 happy path）

arch-checker 對自身 33 個 class 進行檢查，預期 0 violations、exit 0。

```bash
# repository 根目錄執行
arch-checker check src/main/java arch.yaml
echo $?
```

預期輸出：

```
--
Checked 32 file(s); 0 violation(s); 0 suppressed.
0
```

> Scenario A 是 arch-checker 對「自家」的檢查（dogfooding），使用 repository
> 根目錄的 `arch.yaml`，與本資料夾 `demo-profile.yaml` 是兩份不同的設定檔。

---

### Scenario B · 替代輸出（FEA-04 · `--json`）

對 sample-project 執行檢查，改以 JSON 輸出。展示 **Reporter 多型** — 同一
service 透過 strategy 切換 Console / JSON。

```bash
arch-checker check demo/sample-project demo/demo-profile.yaml --json
```

預期輸出（單行 JSON，下方手動排版以利閱讀）：

```json
{
  "checkedFiles": 7,
  "violationCount": 4,
  "suppressedCount": 0,
  "violations": [
    { "file": ".../service/PaymentManager.java",         "line": 12, "ruleId": "R-NAME-01", "message": "Class 'PaymentManager' violates naming pattern '(?!.*(?:Manager|Helper|Util|Handler)$).*'" },
    { "file": ".../service/ChargeService.java",          "line": 2,  "ruleId": "R-DEP-01",  "message": "Package 'com.example.service' must not depend on 'com.example.controller' (import: com.example.controller.PaymentController)" },
    { "file": ".../domain/payment/CashPayment.java",     "line": 12, "ruleId": "R-SUP-01",  "message": "Class 'CashPayment' must extend/implement 'Payment'" },
    { "file": ".../controller/PaymentController.java",   "line": 0,  "ruleId": "R-PKG-01",  "message": "Required package pattern 'com.example.repository' not present in the project" }
  ]
}
```

`exit code = 1`（有違規）。

---

### Scenario C · UC-04 Suppress + 重檢（UC-01）

> Scenario C 是兩個 use cases 的串連 — 先檢查、再 suppress、再重檢。
> 此處示範 R-NAME-01；4 條規則各自的完整流程見[下一節](#各條-rule-的觸發示範)。

#### 第 1 步：第一次檢查（4 筆違規）

```bash
arch-checker check demo/sample-project demo/demo-profile.yaml
```

預期輸出：

```
demo/sample-project/.../service/PaymentManager.java:12       [R-NAME-01] Class 'PaymentManager' violates naming pattern '(?!.*(?:Manager|Helper|Util|Handler)$).*'
demo/sample-project/.../service/ChargeService.java:2         [R-DEP-01]  Package 'com.example.service' must not depend on 'com.example.controller' (import: com.example.controller.PaymentController)
demo/sample-project/.../domain/payment/CashPayment.java:12   [R-SUP-01]  Class 'CashPayment' must extend/implement 'Payment'
demo/sample-project/.../controller/PaymentController.java:0  [R-PKG-01]  Required package pattern 'com.example.repository' not present in the project
--
Checked 7 file(s); 4 violation(s); 0 suppressed.
```

`exit code = 1`。

#### 第 2 步：對 R-NAME-01 個案執行 UC-04

```bash
arch-checker suppress \
    demo/demo-profile.yaml \
    R-NAME-01 \
    demo/sample-project/src/main/java/com/example/service/PaymentManager.java \
    12 \
    'legacy name; refactor in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml
```

預期輸出：

```
Suppressed: R-NAME-01 at demo/sample-project/src/main/java/com/example/service/PaymentManager.java:12
```

寫入 `demo/.arch-checker-suppress.yaml`：

```yaml
- constraintId: R-NAME-01
  filePath: demo/sample-project/src/main/java/com/example/service/PaymentManager.java
  lineNumber: 12
  reason: legacy name; refactor in v2
  timestamp: '2026-05-04T14:35:11.905073Z'
```

#### 第 3 步：重新檢查 — R-NAME-01 違規已被過濾

```bash
arch-checker check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml
```

預期輸出（剩 3 筆違規）：

```
demo/sample-project/.../service/ChargeService.java:2         [R-DEP-01]  ...
demo/sample-project/.../domain/payment/CashPayment.java:12   [R-SUP-01]  ...
demo/sample-project/.../controller/PaymentController.java:0  [R-PKG-01]  ...
--
Checked 7 file(s); 3 violation(s); 1 suppressed.
```

對應 pptx 中 GRASP **Indirection / Protected Variation**：suppress YAML I/O
完全由 `YamlSuppressionStore` 負責。

> 連續執行多次 demo 前，建議先 `rm demo/.arch-checker-suppress.yaml` 清掉
> 示範用的 suppression，否則檢查結果會有殘留過濾紀錄。

---

### Scenario D · UC-02 Define Style Profile（profile init + validate）

UC-02 讓開發者不需手寫 YAML，直接由工具產生包含 4 種規則型別的完整樣板；
再用 `profile validate` 確認語法無誤並顯示載入了哪些規則。

#### 步驟 1：產生樣板

```bash
arch-checker profile init demo/my-profile.yaml
```

預期輸出：

```
Profile template written to: demo\my-profile.yaml
```

產生的 `demo/my-profile.yaml` 已包含 `naming`、`dependency`、`supertype`、`package`
四種規則各一個範例。開發者可直接編輯成專案所需的實際規則。

#### 步驟 2：驗證既有 profile

```bash
arch-checker profile validate demo/demo-profile.yaml
```

預期輸出：

```
Profile 'demo-profile' is valid. Loaded 4 rule(s):
  - R-NAME-01 (NamingRule)
  - R-DEP-01 (DependencyRule)
  - R-SUP-01 (SupertypeRule)
  - R-PKG-01 (PackageRule)
```

`exit code = 0`。若 YAML 語法錯誤，會印 `Validation failed: <error>` 並以 exit 2 退出。

> `profile init` 對應 GRASP **Controller**（ProfileInitCommand）+ **Information Expert +
> Creator**（ProfileTemplateService 擁有樣板知識並負責建立檔案）。
> `profile validate` 的 GRASP：**Controller**（ProfileValidateCommand）+
> **Information Expert**（ProfileValidateService）+
> **Information Expert + Creator**（YamlProfileLoader 解析 YAML 並建立 StyleProfile）+
> **Low Coupling**（Service 依賴 ProfileLoader 介面，不直接耦合 YamlProfileLoader）。

---

### Scenario E · UC-03 Check with Interactive Fix Suggestions（fix）

`fix` 指令結合「逐條顯示修正建議」與「互動式 suppress」，讓開發者在終端機上逐筆
決定是否壓制違規：
- 按 `y` 後，系統會再詢問 **suppress 理由**（可直接 Enter 跳過，預設填入
  `Suppressed interactively`）；
- `n` 保留（本次不處理）；
- `q` 停止循環。

```bash
rm -f demo/.arch-checker-suppress.yaml

arch-checker fix \
    demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml
```

互動示範（`>` 代表使用者輸入）：

```
Violation 1/4:
  demo/sample-project/.../service/PaymentManager.java:12 [R-NAME-01] Class 'PaymentManager' violates naming pattern '(?!.*(?:Manager|Helper|Util|Handler)$).*'
  Suggestion: Rename 'PaymentManager' to match pattern '(?!.*(?:Manager|Helper|Util|Handler)$).*'
Suppress? [y/n/q]: > y
Reason (Enter to skip): > legacy name; will rename in v2

Violation 2/4:
  demo/sample-project/.../service/ChargeService.java:2 [R-DEP-01] Package 'com.example.service' must not depend on 'com.example.controller' (import: com.example.controller.PaymentController)
  Suggestion: Remove import 'com.example.controller.PaymentController'
Suppress? [y/n/q]: > y
Reason (Enter to skip): >

Violation 3/4:
  demo/sample-project/.../domain/payment/CashPayment.java:12 [R-SUP-01] Class 'CashPayment' must extend/implement 'Payment'
  Suggestion: Add 'extends Payment' or 'implements Payment' to class declaration
Suppress? [y/n/q]: > n

Violation 4/4:
  demo/sample-project/.../controller/PaymentController.java:0 [R-PKG-01] Required package pattern 'com.example.repository' not present in the project
  Suggestion: Create at least one class in a package matching 'com.example.repository'
Suppress? [y/n/q]: > q

--
Checked 7 file(s); 2 violation(s) remaining; 2 suppressed.
```

`exit code = 1`（仍有殘留違規）。

**互動決策說明：**

| 輸入 | 語意 | 後續效果 |
|------|------|----------|
| `y` | 同意壓制此條違規 | 再提示輸入理由（Enter 跳過 = 自動填 `Suppressed interactively`），寫入 suppression YAML |
| `n` | 保留（目前不處理）| 留在 remaining，下次 `fix` 或 `check` 仍會出現 |
| `q` | 離開循環 | 剩餘尚未問答的違規全部列為 remaining |

`Reason (Enter to skip):` 輸入的文字會寫入 suppression YAML 的 `reason` 欄位，
便於日後審計「為何當初壓制這筆違規」。空白 Enter 表示快速跳過，理由自動設為
`Suppressed interactively`。

若想全部壓制並填入原因，對所有違規回答 `y` 並輸入理由，最終輸出為
`0 violation(s) remaining; 4 suppressed; exit 0`。

> UC-03 GRASP：**Controller**（`FixCommand` 接收輸入、呈現違規、讀取 y/n/q 與理由）+
> **Pure Fabrication**（`SuppressionService` 封裝 loadAll → create Suppression → save
> 流程，與 `SuppressCommand` 共用，不對應任何 domain 概念）+
> **Information Expert**（`SuppressionStore` 掌握 suppression YAML 的讀寫）。

---

## 各條 Rule 的觸發示範

每條規則使用「先檢查（出現該筆違規）→ suppress（給出真實的 review 理由）→
重檢（該筆不再出現）」三步驟示範。為了讓每段獨立可重現，下面每段都先
`rm` 掉 suppression 檔重新開始。

### R-NAME-01 · NamingRule（vague suffix）

> **規則**：禁止 `Manager` / `Helper` / `Util` / `Handler` 等模糊後綴。
> **違規來源**：`service/PaymentManager.java`。
>
> **常見 review 提醒**：`Manager` 後綴不表達責任 — 是 refund? retry? settle?
> reconcile? 改成 `PaymentService`、`PaymentReconciler`、`PaymentRetryPolicy`
> 都比 `Manager` 精確。Suppress 理由通常是「這個 class 是 legacy 命名，新
> 功能用 role-specific suffix；舊 class 暫不重構」。

```bash
rm -f demo/.arch-checker-suppress.yaml

# (1) 檢查 — 出現 R-NAME-01 違規
arch-checker check demo/sample-project demo/demo-profile.yaml | grep R-NAME-01
```

預期輸出：

```
demo/sample-project/.../service/PaymentManager.java:12 [R-NAME-01] Class 'PaymentManager' violates naming pattern '(?!.*(?:Manager|Helper|Util|Handler)$).*'
```

```bash
# (2) 標記為「可接受」
arch-checker suppress demo/demo-profile.yaml \
    R-NAME-01 \
    demo/sample-project/src/main/java/com/example/service/PaymentManager.java 12 \
    'legacy name; refactor in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

# (3) 重檢 — 不應再出現 R-NAME-01 違規
arch-checker check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml | grep -c R-NAME-01
# 預期輸出：0
```

---

### R-DEP-01 · DependencyRule（service 依賴 controller）

> **規則**：service 套件不得依賴 controller 套件（top-down 分層 — Controller
> 呼叫 Service，反過來不行）。
> **違規來源**：`service/ChargeService.java:2`（`import com.example.controller.PaymentController;`）。
>
> **常見 review 提醒**：service 反向依賴 controller 會導致：
> 1. 啟動時迴圈依賴
> 2. service 沒有 controller 就不可測試
> 3. 把 web 層的 concern 滲透進 business logic
>
> Suppress 理由通常是「臨時 wire-up，待 v2 抽 interface 解耦」。

```bash
rm -f demo/.arch-checker-suppress.yaml

arch-checker check demo/sample-project demo/demo-profile.yaml | grep R-DEP-01
```

預期輸出：

```
demo/sample-project/.../service/ChargeService.java:2 [R-DEP-01] Package 'com.example.service' must not depend on 'com.example.controller' (import: com.example.controller.PaymentController)
```

```bash
arch-checker suppress demo/demo-profile.yaml \
    R-DEP-01 \
    demo/sample-project/src/main/java/com/example/service/ChargeService.java 2 \
    'temporary cross-layer wire-up; refactor in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

arch-checker check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml | grep -c R-DEP-01
# 預期輸出：0
```

---

### R-SUP-01 · SupertypeRule（domain 多型基底）

> **規則**：`com.example.domain.payment` 下每個 type 都必須 extend `Payment`，
> domain layer 各種付款方式共用同一個多型基底。
> **違規來源**：`domain/payment/CashPayment.java:12`（沒有 `extends Payment`）。
>
> **常見 review 提醒**：domain layer 多型 — 每個 concrete payment 都應該
> 是 `Payment` 的 subclass，這樣 Application service 才能寫成
> `charge(Payment p)`，靠 polymorphism 處理所有付款方式。沒繼承 `Payment`
> 表示 `CashPayment` 不能放進 `List<Payment>`、不能用 `instanceof Payment`
> 過濾、`charge(Payment)` 也吃不到 — 強迫 Application 寫一堆 `if-else` 分支。
> Suppress 理由通常是「v1 cash payment 用獨立流程處理，v2 統一抽到 Payment 基底」。
>
> **設計細節**：`Payment` 基底放在 `com.example.domain.core` 而非
> `com.example.domain.payment` — 否則 `targetPackage = com.example.domain.payment`
> 會把基底自己也抓出來「`Payment` 必須 extend `Payment`」。
> 這是**套件結構決定 SupertypeRule 是否會抓基底自身**的真實 edge case。

```bash
rm -f demo/.arch-checker-suppress.yaml

arch-checker check demo/sample-project demo/demo-profile.yaml | grep R-SUP-01
```

預期輸出：

```
demo/sample-project/.../domain/payment/CashPayment.java:12 [R-SUP-01] Class 'CashPayment' must extend/implement 'Payment'
```

```bash
arch-checker suppress demo/demo-profile.yaml \
    R-SUP-01 \
    demo/sample-project/src/main/java/com/example/domain/payment/CashPayment.java 12 \
    'cash payments are out of scope for v1; will inherit Payment in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

arch-checker check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml | grep -c R-SUP-01
# 預期輸出：0
```

> 對照：`CreditCardPayment` 通過此規則 — 它有 `extends Payment`。

---

### R-PKG-01 · PackageRule（缺 repository 套件）

> **規則**：典型分層專案應有 `com.example.repository` 套件，集中存放 persistence
> 介面（`PaymentRepository`、`UserRepository` 等）。
> **違規附掛點**：line `0`，掛在掃描順序中第一個 `.java` 檔（這裡是
> `controller/PaymentController.java`）。
>
> **常見 review 提醒**：團隊約定的「必有套件」不該被誤刪 / 漏建。
> PackageRule 用來防止結構偏移 — 例如新成員不知道專案約定，把 repository
> interface 跟 service 混在同一個套件下。
>
> Suppress 理由通常是「v1 還沒接資料庫，v2 加 persistence 時建立 repository
> 套件」或「我們是 in-memory CLI tool，不需要 repository」。

```bash
rm -f demo/.arch-checker-suppress.yaml

arch-checker check demo/sample-project demo/demo-profile.yaml | grep R-PKG-01
```

預期輸出：

```
demo/sample-project/.../controller/PaymentController.java:0 [R-PKG-01] Required package pattern 'com.example.repository' not present in the project
```

```bash
arch-checker suppress demo/demo-profile.yaml \
    R-PKG-01 \
    demo/sample-project/src/main/java/com/example/controller/PaymentController.java 0 \
    'repository package planned for v2 once persistence is added' \
    --suppress-file demo/.arch-checker-suppress.yaml

arch-checker check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml | grep -c R-PKG-01
# 預期輸出：0
```

> **小提醒**：PackageRule 的違規綁定到「掃描順序中第一個 .java 檔」，確切是
> 哪個檔案取決於 file system 列舉順序。實務上 suppress 時依檢查輸出顯示
> 的 `<file>:0` 抄上去即可。

---

### 一次 suppress 全部 4 條規則

把上面四段串成一次執行，最後檢查應為 `0 violation(s); 4 suppressed; exit 0`：

```bash
rm -f demo/.arch-checker-suppress.yaml

arch-checker suppress \
    demo/demo-profile.yaml R-NAME-01 \
    demo/sample-project/src/main/java/com/example/service/PaymentManager.java 12 \
    'legacy name; refactor in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

arch-checker suppress \
    demo/demo-profile.yaml R-DEP-01 \
    demo/sample-project/src/main/java/com/example/service/ChargeService.java 2 \
    'temporary cross-layer wire-up; refactor in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

arch-checker suppress \
    demo/demo-profile.yaml R-SUP-01 \
    demo/sample-project/src/main/java/com/example/domain/payment/CashPayment.java 12 \
    'cash payments are out of scope for v1; will inherit Payment in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

arch-checker suppress \
    demo/demo-profile.yaml R-PKG-01 \
    demo/sample-project/src/main/java/com/example/controller/PaymentController.java 0 \
    'repository package planned for v2 once persistence is added' \
    --suppress-file demo/.arch-checker-suppress.yaml

# 最終檢查 — 全部通過
arch-checker check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml
echo "exit=$?"
```

預期最後輸出：

```
--
Checked 7 file(s); 0 violation(s); 4 suppressed.
exit=0
```

---

## 子指令參考

```
Usage: arch-checker [-hV] [COMMAND]
Commands:
  check    Check architecture compliance of a Java project against a Style Profile.
  suppress Mark a specific violation as 'known and accepted'.
  fix      Check architecture compliance with interactive fix suggestions.
  profile  Manage Style Profile files.
```

`check` 參數：

| 位置 / 旗標            | 必填 | 說明                                                         |
|-------------------------|------|--------------------------------------------------------------|
| `<project-path>`        | 是   | 被檢查 Java 專案根目錄                                       |
| `<profile-path>`        | 是   | Style Profile YAML                                          |
| `-s, --suppress-file`   | 否   | suppression 檔案（預設 `.arch-checker-suppress.yaml`）       |
| `--json`                | 否   | 改以 JSON 格式輸出                                           |

`suppress` 參數（依序）：

```
suppress <profile-path> <constraint-id> <file-path> <line> <reason> [--suppress-file <path>]
```

`fix` 參數：

| 位置 / 旗標            | 必填 | 說明                                                           |
|-------------------------|------|----------------------------------------------------------------|
| `<project-path>`        | 是   | 被檢查 Java 專案根目錄                                         |
| `<profile-path>`        | 是   | Style Profile YAML                                            |
| `-s, --suppress-file`   | 否   | suppression 檔案（預設 `.arch-checker-suppress.yaml`）         |

互動期間可輸入 `y`（suppress）、`n`（跳過）、`q`（結束循環）。
輸入 `y` 後，系統會再提示 `Reason (Enter to skip):` — 輸入理由後 Enter 確認，
直接 Enter 則自動以 `Suppressed interactively` 作為理由寫入 suppression 檔。

`profile` 子指令群：

```
profile init     <output-path>   產生 YAML 樣板（含 4 種規則類型各一範例）
profile validate <profile-path>  驗證並顯示已載入的規則清單（exit 2 = 語法錯誤）
```

---

## Exit code 對應 NFR-05

| code | 意義                                              |
|------|---------------------------------------------------|
| 0    | pass（無違規或全部已 suppress）                   |
| 1    | violations found                                  |
| 2    | error（profile 缺檔、YAML 語法錯誤等）            |

可直接在 GitHub Actions / Jenkins / GitLab CI 流水線使用。
