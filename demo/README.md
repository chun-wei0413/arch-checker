# arch-checker · Demo

OOAD 期中報告（[`hw/Midterm/midterm-team6.pptx`](../hw/Midterm/midterm-team6.pptx)）所示
demo 場景的可重現範例。本資料夾包含一個小型**購物系統**（Customer / Order /
Payment / Cart）作為被檢查專案，刻意設計成讓 4 條 rule 各觸發 1 筆違規 —
而且**每筆違規都對應一個真實的 DDD / 分層架構取捨**，不是為了製造違規而
硬湊的 typo。

搭配 README 即可端到端示範 arch-checker 的核心 use cases：

- **UC-01** Check Architecture Compliance（檢查、JSON 輸出）
- **UC-04** Suppress a Violation（標記「可接受」並讓下次檢查過濾）
- **UC-05** Load Style Profile（由 UC-01 include）

以及 arch-checker 的 4 種 GRASP Polymorphism 子類別 — `NamingRule`、
`DependencyRule`、`SupertypeRule`、`PackageRule`。

---

## 目錄

- [內容物](#內容物)
- [Style Profile 解說](#style-profile-解說)
- [前置作業：build arch-checker](#前置作業build-arch-checker)
- [Demo 場景對照（pptx Scenario A / B / C）](#demo-場景對照pptx-scenario-a--b--c)
- [各條 Rule 的觸發示範](#各條-rule-的觸發示範)
  - [R-NAME-01 · NamingRule（vague suffix）](#r-name-01--namingrulevague-suffix)
  - [R-DEP-01 · DependencyRule（cross-aggregate hard reference）](#r-dep-01--dependencyrulecross-aggregate-hard-reference)
  - [R-SUP-01 · SupertypeRule（base type self-extension）](#r-sup-01--supertyperulebase-type-self-extension)
  - [R-PKG-01 · PackageRule（DDD event package）](#r-pkg-01--packageruleddd-event-package)
  - [一次 suppress 全部 4 條規則](#一次-suppress-全部-4-條規則)
- [子指令參考](#子指令參考)
- [Exit code 對應 NFR-05](#exit-code-對應-nfr-05)

---

## 內容物

```
demo/
├── README.md                   ← 本檔
├── demo-profile.yaml           ← Style Profile（4 條規則）
└── sample-project/             ← 一個小型購物系統
    └── src/main/java/com/example/
        ├── customer/
        │   └── Customer.java               合規（Customer aggregate root）
        ├── order/
        │   ├── Order.java                  ❌ R-DEP-01（hard ref Customer）
        │   └── OrderService.java           合規
        ├── payment/
        │   └── Payment.java                合規
        ├── service/
        │   └── InventoryManager.java       ❌ R-NAME-01（vague Manager 後綴）
        ├── controller/
        │   ├── HomeController.java         合規（extends BaseController）
        │   └── CartController.java         ❌ R-SUP-01（沒繼承 BaseController）
        └── support/
            └── BaseController.java         合規（基底類別）
```

`sample-project` 不是 Maven 專案 — arch-checker 只需要遞迴尋找 `.java`，
所以任意目錄都可作為輸入。共 **8 個 .java 檔，恰好觸發 4 條 rule 各 1 筆違規**
（外加 PackageRule 是 project-level，沒有 `com.example.event` 套件）。

---

## Style Profile 解說

[`demo-profile.yaml`](./demo-profile.yaml) 定義 4 條規則，**對應 arch-checker
4 個 `ArchitectureConstraint` 子類別**（GRASP Polymorphism）：

| Rule ID    | Type / 子類別     | 規則內容                                                    | 違規來源                            | 真實架構故事                                                   |
|------------|------------------|------------------------------------------------------------|-------------------------------------|---------------------------------------------------------------|
| R-NAME-01  | `NamingRule`     | 禁止 `Manager` / `Helper` / `Util` / `Handler` 等 vague 後綴 | `service/InventoryManager.java`     | 通用 noun 後綴掩蓋責任；應改用 role-specific suffix             |
| R-DEP-01   | `DependencyRule` | `com.example.order` 不得 hard-reference `com.example.customer` | `order/Order.java`                  | DDD：跨 aggregate 應只持有 id，不持有 reference                |
| R-SUP-01   | `SupertypeRule`  | `com.example.controller` 內 type 須 extend `BaseController`  | `controller/CartController.java`    | 集中錯誤處理 / middleware；基底放外部套件避免自我繼承           |
| R-PKG-01   | `PackageRule`    | 專案應有 `com.example.event` 套件                            | （project-level）                   | DDD events 模式；本 v1 還沒導入 event-driven                  |

每條規則的 suppress 理由都不是「legacy code 留著不修」，而是**真正的 architectural
trade-off**：早期專案優先簡單性、向後相容、漸進式架構演進。

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
> 本 README 範例採 `;`，請依環境調整。

---

## Demo 場景對照（pptx Scenario A / B / C）

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

> Scenario A 是 arch-checker 對「自家」的檢查（dogfooding），使用 repository
> 根目錄的 `arch.yaml`，與本資料夾 `demo-profile.yaml` 是兩份不同的設定檔。

---

### Scenario B · 替代輸出（FEA-04 · `--json`）

對購物系統執行檢查，改以 JSON 輸出。展示 **Reporter 多型** — 同一 service
透過 strategy 切換 Console / JSON。

```bash
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml --json
```

預期輸出（單行 JSON，下方手動排版以利閱讀）：

```json
{
  "checkedFiles": 8,
  "violationCount": 4,
  "suppressedCount": 0,
  "violations": [
    { "file": ".../service/InventoryManager.java",   "line": 12, "ruleId": "R-NAME-01", "message": "Class 'InventoryManager' violates naming pattern '(?!.*(?:Manager|Helper|Util|Handler)$).*'" },
    { "file": ".../order/Order.java",                "line": 2,  "ruleId": "R-DEP-01",  "message": "Package 'com.example.order' must not depend on 'com.example.customer' (import: com.example.customer.Customer)" },
    { "file": ".../controller/CartController.java",  "line": 8,  "ruleId": "R-SUP-01",  "message": "Class 'CartController' must extend/implement 'BaseController'" },
    { "file": ".../controller/CartController.java",  "line": 0,  "ruleId": "R-PKG-01",  "message": "Required package pattern 'com.example.event' not present in the project" }
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
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml
```

預期輸出：

```
demo/sample-project/.../service/InventoryManager.java:12  [R-NAME-01] Class 'InventoryManager' violates naming pattern '(?!.*(?:Manager|Helper|Util|Handler)$).*'
demo/sample-project/.../order/Order.java:2                [R-DEP-01]  Package 'com.example.order' must not depend on 'com.example.customer' (import: com.example.customer.Customer)
demo/sample-project/.../controller/CartController.java:8  [R-SUP-01]  Class 'CartController' must extend/implement 'BaseController'
demo/sample-project/.../controller/CartController.java:0  [R-PKG-01]  Required package pattern 'com.example.event' not present in the project
--
Checked 8 file(s); 4 violation(s); 0 suppressed.
```

`exit code = 1`。

#### 第 2 步：對 R-NAME-01 個案執行 UC-04

```bash
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml \
    R-NAME-01 \
    demo/sample-project/src/main/java/com/example/service/InventoryManager.java \
    12 \
    'legacy name; refactor in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml
```

預期輸出：

```
Suppressed: R-NAME-01 at demo/sample-project/src/main/java/com/example/service/InventoryManager.java:12
```

寫入 `demo/.arch-checker-suppress.yaml`：

```yaml
- constraintId: R-NAME-01
  filePath: demo/sample-project/src/main/java/com/example/service/InventoryManager.java
  lineNumber: 12
  reason: legacy name; refactor in v2
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
demo/sample-project/.../order/Order.java:2                [R-DEP-01]  ...
demo/sample-project/.../controller/CartController.java:8  [R-SUP-01]  ...
demo/sample-project/.../controller/CartController.java:0  [R-PKG-01]  ...
--
Checked 8 file(s); 3 violation(s); 1 suppressed.
```

對應 pptx 中 GRASP **Indirection / Protected Variation**：suppress YAML I/O
完全由 `YamlSuppressionRepository` 負責。

> 連續執行多次 demo 前，建議先 `rm demo/.arch-checker-suppress.yaml` 清掉
> 示範用的 suppression，否則檢查結果會有殘留過濾紀錄。

---

## 各條 Rule 的觸發示範

每條規則使用「先檢查（出現該筆違規）→ suppress（給出真實架構理由）→ 重檢
（該筆不再出現）」三步驟示範。為了讓每段獨立可重現，下面每段都先
`rm` 掉 suppression 檔重新開始。

### R-NAME-01 · NamingRule（vague suffix）

> **規則**：禁止 `Manager` / `Helper` / `Util` / `Handler` 等通用 noun 後綴。
> **違規來源**：`service/InventoryManager.java`。
>
> **真實取捨**：`InventoryManager` 模糊地說「管 inventory」，但具體是查詢、
> 預留、配發？改成 `InventoryReservation`、`InventoryAllocator`、`InventoryService`
> 都比 `Manager` 精確。Suppress 理由通常是「legacy name；新功能用
> role-specific 命名，舊類別逐步重構」。

```bash
rm -f demo/.arch-checker-suppress.yaml

# (1) 檢查 — 出現 R-NAME-01 違規
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml | grep R-NAME-01
```

預期輸出：

```
demo/sample-project/.../service/InventoryManager.java:12 [R-NAME-01] Class 'InventoryManager' violates naming pattern '(?!.*(?:Manager|Helper|Util|Handler)$).*'
```

```bash
# (2) 標記為「可接受」
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main suppress demo/demo-profile.yaml \
    R-NAME-01 \
    demo/sample-project/src/main/java/com/example/service/InventoryManager.java 12 \
    'legacy name; refactor in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

# (3) 重檢 — 不應再出現 R-NAME-01 違規
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml | grep -c R-NAME-01
# 預期輸出：0
```

---

### R-DEP-01 · DependencyRule（cross-aggregate hard reference）

> **規則**：`com.example.order` 不得 hard-reference `com.example.customer`；
> 應改為持有 `customerId: long`，需要時再透過 `CustomerService` 查詢。
> **違規來源**：`order/Order.java:2`（`import com.example.customer.Customer;`）。
>
> **真實取捨**：DDD 主張 aggregate 之間用 ID 引用，避免跨 aggregate 的
> 載入連鎖、跨 transaction 維護、cascade delete 等問題。但**早期小型專案**
> 直接持有 reference 換取簡單性是常見的 pragmatic choice。Suppress 理由
> 通常是「v1 用直接引用，v2 流量增加後再導入 customerId-only 重構」。

```bash
rm -f demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml | grep R-DEP-01
```

預期輸出：

```
demo/sample-project/.../order/Order.java:2 [R-DEP-01] Package 'com.example.order' must not depend on 'com.example.customer' (import: com.example.customer.Customer)
```

```bash
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main suppress demo/demo-profile.yaml \
    R-DEP-01 \
    demo/sample-project/src/main/java/com/example/order/Order.java 2 \
    'early stage; will move to customerId in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml | grep -c R-DEP-01
# 預期輸出：0
```

---

### R-SUP-01 · SupertypeRule（base type self-extension）

> **規則**：`com.example.controller` 下每個 type 都必須 extend `BaseController`，
> 以繼承共同的錯誤處理 / logging / authentication middleware。
> **違規來源**：`controller/CartController.java:5`（沒有 `extends BaseController`）。
>
> **真實取捨**：presentation 層一致性。Suppress 理由通常是「deprecated
> endpoint 留著向後相容，故意不套新的 middleware」。
>
> **設計細節**：`BaseController` 放在 `com.example.support` 而非
> `com.example.controller` — 否則 `targetPackage = com.example.controller`
> 會把基底自己也抓出來「`BaseController` 必須 extend `BaseController`」。
> 這是**套件結構決定 SupertypeRule 是否會抓基底自身**的真實 edge case。

```bash
rm -f demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml | grep R-SUP-01
```

預期輸出：

```
demo/sample-project/.../controller/CartController.java:8 [R-SUP-01] Class 'CartController' must extend/implement 'BaseController'
```

```bash
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main suppress demo/demo-profile.yaml \
    R-SUP-01 \
    demo/sample-project/src/main/java/com/example/controller/CartController.java 8 \
    'deprecated endpoint kept for backwards compatibility' \
    --suppress-file demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml | grep -c R-SUP-01
# 預期輸出：0
```

> 對照：`HomeController` 通過此規則 — 它有 `extends BaseController`。

---

### R-PKG-01 · PackageRule（DDD event package）

> **規則**：DDD 風格的專案應有 `com.example.event` 套件，集中發佈 domain
> events（aggregate 狀態變化通知）。
> **違規附掛點**：line `0`，掛在掃描順序中第一個 `.java` 檔（這裡是
> `controller/CartController.java`）。
>
> **真實取捨**：規則表達「未來架構意圖」 — 期望最終導入 event-driven
> 整合。Suppress 理由通常是「v1 用直接呼叫，event sourcing 規劃在 v2」。
> PackageRule 也常用來防止「應該存在的核心套件被誤刪」。

```bash
rm -f demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml | grep R-PKG-01
```

預期輸出：

```
demo/sample-project/.../controller/CartController.java:0 [R-PKG-01] Required package pattern 'com.example.event' not present in the project
```

```bash
java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main suppress demo/demo-profile.yaml \
    R-PKG-01 \
    demo/sample-project/src/main/java/com/example/controller/CartController.java 0 \
    'event-driven integration planned for v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml \
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

java -cp 'target/arch-checker.jar;target/lib/*' com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml R-NAME-01 \
    demo/sample-project/src/main/java/com/example/service/InventoryManager.java 12 \
    'legacy name; refactor in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml R-DEP-01 \
    demo/sample-project/src/main/java/com/example/order/Order.java 2 \
    'early stage; will move to customerId in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml R-SUP-01 \
    demo/sample-project/src/main/java/com/example/controller/CartController.java 8 \
    'deprecated endpoint kept for backwards compatibility' \
    --suppress-file demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar;target/lib/*' com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml R-PKG-01 \
    demo/sample-project/src/main/java/com/example/controller/CartController.java 0 \
    'event-driven integration planned for v2' \
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
Checked 8 file(s); 0 violation(s); 4 suppressed.
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
