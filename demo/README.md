# arch-checker · Demo

OOAD 期中報告（[`hw/Midterm/midterm-team6.pptx`](../hw/Midterm/midterm-team6.pptx)）所示
demo 場景的可重現範例。本資料夾包含一個刻意設計成「每條規則都各自違反 1 筆」的小型
Java 專案（`sample-project/`），以及對應的 Style Profile（`demo-profile.yaml`）。
搭配 README 即可端到端示範 arch-checker 的核心 use cases：

- **UC-01** Check Architecture Compliance（檢查、JSON 輸出）
- **UC-04** Suppress a Violation（標記「可接受」並讓下次檢查過濾）
- **UC-05** Load Style Profile（由 UC-01 include）

以及 arch-checker 內建的 4 條 GRASP Polymorphism 子類別 — `NamingRule`、
`DependencyRule`、`SupertypeRule`、`PackageRule`。

---

## 目錄

- [內容物](#內容物)
- [Style Profile 解說](#style-profile-解說)
- [前置作業：build arch-checker](#前置作業build-arch-checker)
- [Demo 場景對照（pptx Scenario A / B / C）](#demo-場景對照pptx-scenario-a--b--c)
  - [Scenario A · 成功流程](#scenario-a--成功流程uc-01-happy-path)
  - [Scenario B · 替代輸出（FEA-04 · `--json`）](#scenario-b--替代輸出fea-04---json)
  - [Scenario C · UC-04 Suppress + 重檢](#scenario-c--uc-04-suppress--重檢uc-01)
- [各條 Rule 的觸發示範](#各條-rule-的觸發示範)
  - [R-NAME-01 · NamingRule](#r-name-01--namingrule)
  - [R-DEP-01 · DependencyRule](#r-dep-01--dependencyrule)
  - [R-SUP-01 · SupertypeRule](#r-sup-01--supertyperule)
  - [R-PKG-01 · PackageRule](#r-pkg-01--packagerule)
  - [一次 suppress 全部 4 條規則](#一次-suppress-全部-4-條規則)
- [子指令參考](#子指令參考)
- [Exit code 對應 NFR-05](#exit-code-對應-nfr-05)

---

## 內容物

```
demo/
├── README.md                   ← 本檔
├── demo-profile.yaml           ← Style Profile（4 條規則：Naming/Dependency/Supertype/Package）
└── sample-project/             ← 被檢查的 Java 專案
    └── src/main/java/com/example/
        ├── service/
        │   ├── OrderService.java        ← 合規
        │   ├── PaymentService.java      ← 合規
        │   ├── UserManager.java         ← ❌ R-NAME-01
        │   └── AuditService.java        ← ❌ R-DEP-01
        ├── controller/
        │   ├── HomeController.java      ← 合規（extends BaseController）
        │   └── LegacyController.java    ← ❌ R-SUP-01
        └── support/
            └── BaseController.java      ← 合規（基底類別）
```

`sample-project` 不是 Maven 專案 — arch-checker 只需要遞迴尋找 `.java`，所以任意目錄都可作為輸入。
共 7 個 `.java` 檔，剛好觸發 4 條規則各 1 筆違規。

---

## Style Profile 解說

[`demo-profile.yaml`](./demo-profile.yaml) 定義 4 條規則，**對應 arch-checker 4 個
`ArchitectureConstraint` 子類別**（GRASP Polymorphism）：

| Rule ID    | Type / 子類別           | 規則內容                                                                          | 觸發違規之檔案                              |
|------------|------------------------|----------------------------------------------------------------------------------|---------------------------------------------|
| R-NAME-01  | `NamingRule`           | 所有 class 名稱須符合 regex `\w+(?:Service\|Controller\|Entity)`                  | `service/UserManager.java:7`                |
| R-DEP-01   | `DependencyRule`       | `com.example.service` 不得 import `com.example.controller`                       | `service/AuditService.java:2`               |
| R-SUP-01   | `SupertypeRule`        | `com.example.controller` 內每個 class 必須 extend `BaseController`               | `controller/LegacyController.java:5`        |
| R-PKG-01   | `PackageRule`          | 專案必須包含 `com.example.entity` 套件                                            | （專案層級違規，line 0）                     |

---

## 前置作業：build arch-checker

demo 需要先把 arch-checker 編譯打包到 `target/`。在 repository 根目錄執行：

```bash
mvn -B package -DskipTests \
    dependency:copy-dependencies \
    -DincludeScope=runtime \
    -DoutputDirectory=target/lib
```

成功後產生：

- `target/arch-checker.jar`
- `target/lib/*.jar`（執行期相依：JavaParser、SnakeYAML、picocli）

> Windows PowerShell 使用者：上述指令直接貼入 PowerShell / Git Bash 皆可。
> 後續所有 `java -cp` 指令的 classpath 在 Windows 用 `;`，POSIX shell 用 `:`。
> 本 README 的範例採 `;`，請依環境調整。

---

## Demo 場景對照（pptx Scenario A / B / C）

對應 pptx 第 6 頁「Demonstration · 展示流程」三張卡片。

### Scenario A · 成功流程（UC-01 happy path）

arch-checker 對自身 26 個 class 進行檢查，預期 0 violations、exit 0。

```bash
# repository 根目錄執行
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check src/main/java arch.yaml
echo $?
```

預期輸出：

```
--
Checked 26 file(s); 0 violation(s); 0 suppressed.
0
```

> Scenario A 使用 repository 根目錄的 `arch.yaml`（arch-checker 自身採用的 Style Profile），
> 與本資料夾 `demo-profile.yaml` 是兩份不同的設定檔。

---

### Scenario B · 替代輸出（FEA-04 · `--json`）

對 demo 專案執行檢查，改以 JSON 輸出。展示 **Reporter 多型** — 同一 service
透過 strategy 切換 Console / JSON。

```bash
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml --json
```

預期輸出（單行 JSON，下方手動排版以利閱讀）：

```json
{
  "checkedFiles": 7,
  "violationCount": 4,
  "suppressedCount": 0,
  "violations": [
    { "file": ".../service/UserManager.java",       "line": 7, "ruleId": "R-NAME-01", "message": "Class 'UserManager' violates naming pattern '\\w+(?:Service|Controller|Entity)'" },
    { "file": ".../service/AuditService.java",      "line": 2, "ruleId": "R-DEP-01",  "message": "Package 'com.example.service' must not depend on 'com.example.controller' (import: com.example.controller.HomeController)" },
    { "file": ".../controller/LegacyController.java","line": 5, "ruleId": "R-SUP-01", "message": "Class 'LegacyController' must extend/implement 'BaseController'" },
    { "file": ".../controller/HomeController.java", "line": 0, "ruleId": "R-PKG-01",  "message": "Required package pattern 'com.example.entity' not present in the project" }
  ]
}
```

`exit code = 1`（有違規）。

---

### Scenario C · UC-04 Suppress + 重檢（UC-01）

> Scenario C 是兩個 use cases 的串連：先檢查、再 suppress、再重檢。
> 此處示範針對最容易說明的 R-NAME-01；4 條規則各自的完整流程見[下一節](#各條-rule-的觸發示範)。

#### 第 1 步：第一次檢查

```bash
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml
```

預期輸出（4 筆違規）：

```
demo/sample-project/.../service/UserManager.java:7        [R-NAME-01] Class 'UserManager' violates naming pattern '\w+(?:Service|Controller|Entity)'
demo/sample-project/.../service/AuditService.java:2       [R-DEP-01]  Package 'com.example.service' must not depend on 'com.example.controller' (import: com.example.controller.HomeController)
demo/sample-project/.../controller/LegacyController.java:5 [R-SUP-01] Class 'LegacyController' must extend/implement 'BaseController'
demo/sample-project/.../controller/HomeController.java:0  [R-PKG-01]  Required package pattern 'com.example.entity' not present in the project
--
Checked 7 file(s); 4 violation(s); 0 suppressed.
```

`exit code = 1`。

#### 第 2 步：對 R-NAME-01 個案執行 UC-04

```bash
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml \
    R-NAME-01 \
    demo/sample-project/src/main/java/com/example/service/UserManager.java \
    7 \
    'legacy entry point - to be renamed in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml
```

預期輸出：

```
Suppressed: R-NAME-01 at demo/sample-project/src/main/java/com/example/service/UserManager.java:7
```

寫入 `demo/.arch-checker-suppress.yaml`：

```yaml
- constraintId: R-NAME-01
  filePath: demo/sample-project/src/main/java/com/example/service/UserManager.java
  lineNumber: 7
  reason: legacy entry point - to be renamed in v2
  timestamp: '2026-05-04T14:35:11.905073Z'
```

#### 第 3 步：重新檢查 — R-NAME-01 違規已被過濾

```bash
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml
```

預期輸出（剩 3 筆違規）：

```
demo/sample-project/.../service/AuditService.java:2       [R-DEP-01]  Package 'com.example.service' must not depend on 'com.example.controller' (import: com.example.controller.HomeController)
demo/sample-project/.../controller/LegacyController.java:5 [R-SUP-01] Class 'LegacyController' must extend/implement 'BaseController'
demo/sample-project/.../controller/HomeController.java:0  [R-PKG-01]  Required package pattern 'com.example.entity' not present in the project
--
Checked 7 file(s); 3 violation(s); 1 suppressed.
```

`exit code = 1`（剩餘違規仍存在）。對應 pptx 中 GRASP **Indirection / Protected Variation**：
suppression 的 YAML I/O 完全由 `YamlSuppressionRepository` 負責。

> 連續執行多次 demo 前，建議先 `rm demo/.arch-checker-suppress.yaml` 清掉示範用的
> suppression，否則檢查結果會有殘留過濾紀錄。

---

## 各條 Rule 的觸發示範

每條規則使用「先檢查（會出現該筆違規）→ suppress → 重檢（該筆不再出現）」三步驟示範。
為了讓每個段落獨立可重現，下面每個範例**都先 `rm` 掉 suppression 檔**重新開始。

### R-NAME-01 · NamingRule

> **規則**：所有 class 名稱必須以 `Service` / `Controller` / `Entity` 結尾。
> **違規來源**：`service/UserManager.java`（名稱不符 pattern）。

```bash
# (重置 suppression)
rm -f demo/.arch-checker-suppress.yaml

# (1) 檢查 — 應出現 R-NAME-01 違規於 line 7
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml | grep R-NAME-01
```

預期輸出：

```
demo/sample-project/.../service/UserManager.java:7 [R-NAME-01] Class 'UserManager' violates naming pattern '\w+(?:Service|Controller|Entity)'
```

```bash
# (2) 標記為「可接受」
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main suppress demo/demo-profile.yaml \
    R-NAME-01 \
    demo/sample-project/src/main/java/com/example/service/UserManager.java 7 \
    'legacy entry point' \
    --suppress-file demo/.arch-checker-suppress.yaml

# (3) 重檢 — 不應再出現 R-NAME-01 違規
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml | grep -c R-NAME-01
# 預期輸出：0
```

---

### R-DEP-01 · DependencyRule

> **規則**：`com.example.service` 不得 import `com.example.controller`（top-down 分層）。
> **違規來源**：`service/AuditService.java`（line 2 處 `import com.example.controller.HomeController;`）。

```bash
rm -f demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml | grep R-DEP-01
```

預期輸出：

```
demo/sample-project/.../service/AuditService.java:2 [R-DEP-01] Package 'com.example.service' must not depend on 'com.example.controller' (import: com.example.controller.HomeController)
```

```bash
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main suppress demo/demo-profile.yaml \
    R-DEP-01 \
    demo/sample-project/src/main/java/com/example/service/AuditService.java 2 \
    'temporary cross-layer wire-up; refactor in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml | grep -c R-DEP-01
# 預期輸出：0
```

---

### R-SUP-01 · SupertypeRule

> **規則**：`com.example.controller` 下每個 class 必須 extend `BaseController`。
> **違規來源**：`controller/LegacyController.java`（line 5 — 沒有 `extends BaseController`）。

```bash
rm -f demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml | grep R-SUP-01
```

預期輸出：

```
demo/sample-project/.../controller/LegacyController.java:5 [R-SUP-01] Class 'LegacyController' must extend/implement 'BaseController'
```

```bash
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main suppress demo/demo-profile.yaml \
    R-SUP-01 \
    demo/sample-project/src/main/java/com/example/controller/LegacyController.java 5 \
    'deprecated controller, kept for backwards compatibility' \
    --suppress-file demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml | grep -c R-SUP-01
# 預期輸出：0
```

> 對照：`controller/HomeController.java` 通過此規則 — 它在 `extends BaseController`（基底
> 類別放在 `com.example.support`，避免基底自我繼承的悖論）。

---

### R-PKG-01 · PackageRule

> **規則**：專案必須含 `com.example.entity` 套件（`sample-project` 沒有 → 違規）。
> **違規附掛點**：line `0`，掛在掃描順序中第一個 `.java` 檔（這裡是 `controller/HomeController.java`）。

```bash
rm -f demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml | grep R-PKG-01
```

預期輸出：

```
demo/sample-project/.../controller/HomeController.java:0 [R-PKG-01] Required package pattern 'com.example.entity' not present in the project
```

```bash
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main suppress demo/demo-profile.yaml \
    R-PKG-01 \
    demo/sample-project/src/main/java/com/example/controller/HomeController.java 0 \
    'entity package planned for v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml | grep -c R-PKG-01
# 預期輸出：0
```

> **小提醒**：PackageRule 的違規綁定到「掃描順序中第一個檔案」，確切是哪個檔案
> 取決於 file system 列舉順序。實務上 suppress 時依檢查輸出顯示的 `<file>:0`
> 抄上去即可。

---

### 一次 suppress 全部 4 條規則

把上面四段的 suppress 串成一次執行，最後的檢查應為 `0 violation(s); 4 suppressed; exit 0`：

```bash
rm -f demo/.arch-checker-suppress.yaml

# 4 次 suppress
java -cp 'target/arch-checker.jar;target/lib/*' com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml R-NAME-01 \
    demo/sample-project/src/main/java/com/example/service/UserManager.java 7 \
    'legacy entry point' \
    --suppress-file demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml R-DEP-01 \
    demo/sample-project/src/main/java/com/example/service/AuditService.java 2 \
    'temporary cross-layer wire-up' \
    --suppress-file demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml R-SUP-01 \
    demo/sample-project/src/main/java/com/example/controller/LegacyController.java 5 \
    'deprecated controller' \
    --suppress-file demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml R-PKG-01 \
    demo/sample-project/src/main/java/com/example/controller/HomeController.java 0 \
    'entity package planned for v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

# 最終檢查 — 全部通過
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml \
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
  check     Check architecture compliance of a Java project against a Style Profile.
  suppress  Mark a specific violation as 'known and accepted'.
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

---

## Exit code 對應 NFR-05

| code | 意義                                              |
|------|---------------------------------------------------|
| 0    | pass（無違規或全部已 suppress）                   |
| 1    | violations found                                  |
| 2    | error（profile 缺檔、YAML 語法錯誤等）            |

可直接在 GitHub Actions / Jenkins / GitLab CI 流水線使用。
