# arch-checker · Demo

OOAD 期中報告（[`hw/Midterm/midterm-team6.pptx`](../hw/Midterm/midterm-team6.pptx)）所示三個 demo
場景的可重現範例。本資料夾包含一個刻意違規的小型 Java 專案
（`sample-project/`）以及對應的 Style Profile（`demo-profile.yaml`），用來驗證
arch-checker 的核心 use cases：UC-01（Check Architecture Compliance）、
UC-04（Suppress a Violation）、UC-05（Load Style Profile，由 UC-01 include）。

---

## 內容物

```
demo/
├── README.md                ← 本檔
├── demo-profile.yaml        ← Style Profile（NamingRule + PackageRule + DependencyRule）
└── sample-project/          ← 被檢查的 Java 專案
    └── src/main/java/com/example/service/
        ├── OrderService.java     ← 合規
        ├── PaymentService.java   ← 合規
        └── UserManager.java      ← 違反 NamingRule R-NAME-01
```

`sample-project` 完全不是 Maven 專案，arch-checker 只需要遞迴尋找 `.java` 檔案，
所以任意目錄都可作為輸入。

---

## Style Profile 解說

[`demo-profile.yaml`](./demo-profile.yaml) 定義三條規則：

| ID         | Type        | 規則內容                                                                |
|------------|-------------|-------------------------------------------------------------------------|
| R-NAME-01  | naming      | 所有 class 名稱必須符合 regex `\w+Service`（即以 `Service` 結尾）        |
| R-PKG-01   | package     | 專案中必須至少有一個 class 落在 `com.example.service` 套件下             |
| R-DEP-01   | dependency  | `com.example.service` 不得 import `com.example.controller`              |

`UserManager` 故意命名違反 R-NAME-01；其餘兩條規則在 `sample-project` 中皆通過，
作為「無違規」基線示範。

---

## 前置作業：build arch-checker

demo 需要先把 arch-checker 編譯打包到 `target/`。在 repository 根目錄執行：

```bash
mvn -B package -DskipTests \
    dependency:copy-dependencies \
    -DincludeScope=runtime \
    -DoutputDirectory=target/lib
```

成功後會產生：

- `target/arch-checker.jar`
- `target/lib/*.jar`（執行期相依：JavaParser、SnakeYAML、picocli）

> Windows PowerShell 使用者：上述指令直接貼入 PowerShell / Git Bash 皆可。
> 後續所有 `java -cp` 指令的 classpath 在 Windows 用 `;`，POSIX shell 用 `:`。
> 本 README 的範例採 `;`，請依環境調整。

---

## Demo 場景對照

對應 pptx 第 6 頁「Demonstration · 展示流程」三張卡片。

### Scenario A · 成功流程（UC-01 happy path）

arch-checker 對自身 26 個 class 進行檢查，預期 0 violations、exit 0。

```bash
# 在 repository 根目錄執行
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

> Scenario A 使用的是 repository 根目錄的 `arch.yaml`，那是 arch-checker
> 自身採用的 Style Profile。它與本資料夾 `demo-profile.yaml` 是兩份不同的設定檔。

---

### Scenario B · 替代輸出（FEA-04 · `--json`）

對 demo 專案執行檢查並改以 JSON 輸出。展示 Reporter 多型 — 同一 service
透過 strategy 切換 Console / JSON。

```bash
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml --json
```

預期輸出（單行 JSON，下方為了易讀手動排版）：

```json
{
  "checkedFiles": 3,
  "violationCount": 1,
  "suppressedCount": 0,
  "violations": [
    {
      "file": "demo/sample-project/src/main/java/com/example/service/UserManager.java",
      "line": 7,
      "ruleId": "R-NAME-01",
      "message": "Class 'UserManager' violates naming pattern '\\w+Service'"
    }
  ]
}
```

`exit code = 1`（有違規）。

---

### Scenario C · UC-04 Suppress + 重檢（UC-01）

> Scenario C 是兩個 use cases 的串連，也是 demo 的主軸：先檢查、再 suppress、再重檢。

#### 第 1 步：第一次檢查（會出現一筆 NamingRule 違規）

```bash
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml
```

預期輸出：

```
demo/sample-project/src/main/java/com/example/service/UserManager.java:7 [R-NAME-01] Class 'UserManager' violates naming pattern '\w+Service'
--
Checked 3 file(s); 1 violation(s); 0 suppressed.
```

`exit code = 1`。

#### 第 2 步：對該已知個案執行 UC-04（suppress）

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

此命令會在 `demo/.arch-checker-suppress.yaml` 寫入一筆紀錄：

```yaml
- constraintId: R-NAME-01
  filePath: demo/sample-project/src/main/java/com/example/service/UserManager.java
  lineNumber: 7
  reason: legacy entry point - to be renamed in v2
  timestamp: '2026-05-04T14:35:11.905073Z'
```

#### 第 3 步：重新檢查 — 該違規已被過濾

```bash
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml
```

預期輸出：

```
--
Checked 3 file(s); 0 violation(s); 1 suppressed.
```

`exit code = 0` — 對應 pptx 中 GRASP **Indirection / Protected Variation**：
suppress 紀錄的 YAML I/O 完全由 `YamlSuppressionRepository` 負責，
ComplianceCheckService 只透過 port 介面取用。

> 連續執行多次 demo 前，建議先 `rm demo/.arch-checker-suppress.yaml`
> 把示範用的 suppression 清掉，否則第 1 步看不到違規（已被前次 suppress 過濾）。

---

## 子指令參考

```
Usage: arch-checker [-hV] [COMMAND]
Commands:
  check     Check architecture compliance of a Java project against a Style Profile.
  suppress  Mark a specific violation as 'known and accepted'.
```

`check` 參數：

| 位置 / 旗標            | 必填 | 說明                                      |
|-------------------------|------|-------------------------------------------|
| `<project-path>`        | 是   | 被檢查 Java 專案根目錄                    |
| `<profile-path>`        | 是   | Style Profile YAML                       |
| `-s, --suppress-file`   | 否   | suppression 檔案（預設 `.arch-checker-suppress.yaml`） |
| `--json`                | 否   | 改以 JSON 格式輸出                       |

`suppress` 參數（依序）：

```
suppress <profile-path> <constraint-id> <file-path> <line> <reason> [--suppress-file <path>]
```

---

## Exit code 對應 NFR-05

| code | 意義                                      |
|------|-------------------------------------------|
| 0    | pass（無違規或全部已 suppress）           |
| 1    | violations found                          |
| 2    | error（profile 缺檔、YAML 語法錯誤等）    |

可直接在 GitHub Actions / Jenkins / GitLab CI 流水線使用。
