# arch-checker · Midterm Demo 完整流程

OOAD 期中報告現場 demo 用的逐步腳本。所有指令以 **macOS（zsh / bash）** 為主。

> Windows PowerShell 使用者請注意三點差異：
> - classpath 分隔符 `:` → 改成 `;`
> - 行接續 `\` → 改成反引號 `` ` ``
> - exit code 變數 `$?` → 改成 `$LASTEXITCODE`

> 對應投影片：`hw/Midterm/midterm-team6.pptx` 第 7~9 張（UC-01 / UC-04）。
> Use case 規格：見 `hw/hw2/Use Case Description.md`。

> **本次 demo 涵蓋 UC-01 的 5 個 extension（2a / 3a / 3c / 8a / 9a）**，超過
> guideline「one successful and several alternatives scenarios」的最低要求。

---

## 目錄

- [0. 前置作業（demo 前一天就準備好）](#0-前置作業demo-前一天就準備好)
- [1. Step 1 · UC-01 主成功流程（觸發 4 條規則）](#1-step-1--uc-01-主成功流程觸發-4-條規則)
- [2. Step 2 · UC-01 Extension 9a — `--json` 替代輸出](#2-step-2--uc-01-extension-9a----json-替代輸出)
- [3. Step 3 · UC-04 Suppress a Violation](#3-step-3--uc-04-suppress-a-violation)
- [4. Step 4 · UC-01 重檢（過濾 1 筆 suppressed）](#4-step-4--uc-01-重檢過濾-1-筆-suppressed)
- [5. Step 5 · UC-01 Extension 8a — 全數 suppress → exit 0](#5-step-5--uc-01-extension-8a--全數-suppress--exit-0)
- [6. Step 6 · UC-01 錯誤路徑 — Ext 2a / 3a / 3c（exit 2）](#6-step-6--uc-01-錯誤路徑--ext-2a--3a--3cexit-2)
- [7. 完整 use case 對照](#7-完整-use-case-對照)
- [8. Demo 前 checklist](#8-demo-前-checklist)
- [附：一鍵跑完整 demo](#附一鍵跑完整-demo備援用)

---

## 0. 前置作業（demo 前一天就準備好）

### 0.1 確認 Java + Maven 版本

```bash
java  -version    # 需 Java 17+
mvn   -version    # 需 Maven 3.8+
```

若沒裝，建議用 Homebrew：

```bash
brew install openjdk@17 maven
```

### 0.2 編譯 + 收齊執行期相依

在 repository 根目錄執行：

```bash
mvn -B package -DskipTests \
    dependency:copy-dependencies \
    -DincludeScope=runtime \
    -DoutputDirectory=target/lib
```

預期產生：

```
target/arch-checker.jar
target/lib/javaparser-core-*.jar
target/lib/snakeyaml-*.jar
target/lib/picocli-*.jar
```

### 0.3 清掉前次 demo 的殘留 suppression

```bash
rm -f demo/.arch-checker-suppress.yaml
```

### 0.4 設快捷別名（避免每次貼長 classpath）

```bash
alias ac="java -cp 'target/arch-checker.jar:target/lib/*' com.archchecker.cli.Main"
```

> 後續所有指令都用 `ac ...` 呼叫。注意 macOS classpath 使用 **冒號 `:`**。

---

## 1. Step 1 · UC-01 主成功流程（觸發 4 條規則）

**目的**：對 `demo/sample-project`（三層 payment 架構）執行檢查，同時觸發 4 個
`ComplianceRule` 子類別 — 對應投影片 18 的 **GRASP Polymorphism**。

> UC-01 的「主成功流程」指系統端到端走完 10 個步驟（讀 profile → 解析 AST →
> 套規則 → 過濾 suppressed → 輸出報告 → 回傳 exit code），**違規數不是判斷標準**。
> 這裡 4 筆違規正是 ViolationReport 的正確產出。

### 指令

```bash
java -cp 'target/arch-checker.jar:target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml
echo "exit=$?"
```

### 預期輸出

```
demo/sample-project/src/main/java/com/example/service/PaymentManager.java:12 [R-NAME-01] Class 'PaymentManager' violates naming pattern '(?!.*(?:Manager|Helper|Util|Handler)$).*'
demo/sample-project/src/main/java/com/example/service/ChargeService.java:2 [R-DEP-01] Package 'com.example.service' must not depend on 'com.example.controller' (import: com.example.controller.PaymentController)
demo/sample-project/src/main/java/com/example/domain/payment/CashPayment.java:12 [R-SUP-01] Class 'CashPayment' must extend/implement 'Payment'
demo/sample-project/src/main/java/com/example/controller/PaymentController.java:0 [R-PKG-01] Required package pattern 'com.example.repository' not present in the project
--
Checked 7 file(s); 4 violation(s); 0 suppressed.
exit=1
```

### 講解重點（4 條規則 → 4 個子類別 → 4 種真實 review concern）

| Rule | 子類別 | Review concern |
|---|---|---|
| R-NAME-01 | `NamingRule` | 「`Manager` 後綴不表達責任，改成 `PaymentReconciler` 之類 role-specific suffix」 |
| R-DEP-01 | `DependencyRule` | 「Service 反向依賴 Controller → 啟動迴圈、無法測試、web concern 滲透」 |
| R-SUP-01 | `SupertypeRule` | 「Concrete payment 沒繼承 `Payment` 基底 → Application 被迫寫 `if-else`」 |
| R-PKG-01 | `PackageRule` | 「典型分層專案應有 `repository` 套件存放 persistence 介面」 |

- exit code = 1 → 「有違規」，CI 會 fail build（對應 **NFR-05 CI Compatibility**）。
- 此處執行的就是投影片 13 的完整 SD 流程：`CheckCommand → ComplianceCheckService → CodeParser → ComplianceRule.validate(files) → Reporter`。

---

## 2. Step 2 · UC-01 Extension 9a — `--json` 替代輸出

**目的**：示範 UC-01 的 Extension 9a，同一份檢查結果改以 JSON 輸出。
背後是投影片 13 的 **Pure Fabrication / Low Coupling** — Reporter 多型，
Console / JSON 切換對 Service 透明。

### 指令（同時印到 console 與寫入根目錄 `violation-report.json`）

```bash
java -cp 'target/arch-checker.jar:target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml --json \
    | tee violation-report.json
```

- intellij 的 json 排版 -> Cmd + Option + L
> 若不需要 console 輸出，只想產檔，可改用 `> violation-report.json`。
> `tee` 會同時把 stdout 印到畫面並寫進檔案，demo 時可即時看到內容；
> 之後可用 `cat violation-report.json | python3 -m json.tool` 開排版過的版本給聽眾看。

### 預期輸出（單行 JSON，下方手動排版以利閱讀）

```json
{
  "checkedFiles": 7,
  "violationCount": 4,
  "suppressedCount": 0,
  "violations": [
    { "file": ".../service/PaymentManager.java",       "line": 12, "ruleId": "R-NAME-01", "message": "Class 'PaymentManager' violates naming pattern ..." },
    { "file": ".../service/ChargeService.java",        "line": 2,  "ruleId": "R-DEP-01",  "message": "Package 'com.example.service' must not depend on ..." },
    { "file": ".../domain/payment/CashPayment.java",   "line": 12, "ruleId": "R-SUP-01",  "message": "Class 'CashPayment' must extend/implement 'Payment'" },
    { "file": ".../controller/PaymentController.java", "line": 0,  "ruleId": "R-PKG-01",  "message": "Required package pattern 'com.example.repository' not present in the project" }
  ]
}
```

> 想看排版過的 JSON，可直接讀剛才產出的檔案：
>
> ```bash
> python3 -m json.tool violation-report.json
> ```
>
> 或一次完成「執行 → 寫檔 → 排版」：
>
> ```bash
> java -cp 'target/arch-checker.jar:target/lib/*' \
>     com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml --json \
>     | tee violation-report.json | python3 -m json.tool
> ```

### 講解重點

- 同一個 `ComplianceCheckService` 沒被改一行，只是注入不同 `Reporter` 實作。
- 對應投影片 14 的 GRASP 表 → `Reporter` 是 **Pure Fabrication**。
- CI 流水線可吃 JSON 結構化輸出（例：把違規送進 GitHub Annotation）。

---

## 3. Step 3 · UC-04 Suppress a Violation

**目的**：對 R-NAME-01 違規（`PaymentManager.java:12`）標記為「已知可接受」。
對應投影片 9 的 UC-04 Brief use case。

### 指令

```bash
java -cp 'target/arch-checker.jar:target/lib/*' com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml \
    R-NAME-01 \
    demo/sample-project/src/main/java/com/example/service/PaymentManager.java \
    12 \
    'legacy name; refactor in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml
```

### 預期輸出

```
Suppressed: R-NAME-01 at demo/sample-project/src/main/java/com/example/service/PaymentManager.java:12
```

### 預期寫入 `demo/.arch-checker-suppress.yaml`

```yaml
- constraintId: R-NAME-01
  filePath: demo/sample-project/src/main/java/com/example/service/PaymentManager.java
  lineNumber: 12
  reason: legacy name; refactor in v2
  timestamp: '2026-05-07T10:00:00.000000Z'
```

> `timestamp` 為實際執行時間，不需與此處字面值一致。

### 講解重點（投影片 15 GRASP 對照）

| Pattern | 對象 | 理由 |
|---|---|---|
| **Controller** | `:SuppressCommand` | 把 CLI 參數適配給 service |
| **Information Expert / Creator** | `:SuppressionService` | 驗證輸入，**create** Suppression aggregate |
| **Indirection / Protected Variation** | `:SuppressionStore` | YAML I/O 完全隔離在 adapter 層，未來換 SQLite 不影響 domain |

---

## 4. Step 4 · UC-01 重檢（過濾 1 筆 suppressed）

**目的**：示範 UC-04 與 UC-01 串接 — suppress 過的違規不再出現在報告中。

### 指令

```bash
java -cp 'target/arch-checker.jar:target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml
echo "exit=$?"
```

### 預期輸出

```
demo/sample-project/src/main/java/com/example/service/ChargeService.java:2 [R-DEP-01] Package 'com.example.service' must not depend on 'com.example.controller' (import: com.example.controller.PaymentController)
demo/sample-project/src/main/java/com/example/domain/payment/CashPayment.java:12 [R-SUP-01] Class 'CashPayment' must extend/implement 'Payment'
demo/sample-project/src/main/java/com/example/controller/PaymentController.java:0 [R-PKG-01] Required package pattern 'com.example.repository' not present in the project
--
Checked 7 file(s); 3 violation(s); 1 suppressed.
exit=1
```

### 講解重點

- 違規數從 4 降為 3，`suppressed=1`。
- R-NAME-01 那筆完全消失 → 對應 UC-01 主成功流程**第 8 步「過濾已 suppress 的 Violation」**。
- exit code 仍為 1（還有 3 筆未處理的違規）。

---

## 5. Step 5 · UC-01 Extension 8a — 全數 suppress → exit 0

**目的**：把剩下 3 筆違規各加上「真實的 review 理由」suppress 起來，
重檢應為 `0 violation(s); 4 suppressed; exit 0`。對應 pptx 第 8 張的
**Extension 8a「所有以上皆被 suppress → 報告指示通過」**。

> 這是 demo 收尾的「真正成功」結局 — exit 0 不是因為 codebase 沒問題，而是
> 因為團隊已經明確判斷哪些違規可接受並留下追蹤紀錄。

### 指令
#### 把剩下 3 筆違規分別 suppress（理由就是「常見 review 提醒」中的真實原因）
```bash
java -cp 'target/arch-checker.jar:target/lib/*' com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml R-DEP-01 \
    demo/sample-project/src/main/java/com/example/service/ChargeService.java 2 \
    'temporary cross-layer wire-up; refactor in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar:target/lib/*' com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml R-SUP-01 \
    demo/sample-project/src/main/java/com/example/domain/payment/CashPayment.java 12 \
    'cash payments out of scope for v1; will inherit Payment in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

java -cp 'target/arch-checker.jar:target/lib/*' com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml R-PKG-01 \
    demo/sample-project/src/main/java/com/example/controller/PaymentController.java 0 \
    'repository package planned for v2 once persistence is added' \
    --suppress-file demo/.arch-checker-suppress.yaml
```
```bash
java -cp 'target/arch-checker.jar:target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml
echo "exit=$?"
```

### 預期輸出（最後一個 check 指令）

```
--
Checked 7 file(s); 0 violation(s); 4 suppressed.
exit=0
```

### 講解重點

- **exit=0** → CI 會 pass，但 `4 suppressed` 留下完整 audit trail。
- 任何時候撤掉 `--suppress-file` 或刪掉 `demo/.arch-checker-suppress.yaml`，
  那 4 筆違規就會立刻重新出現 → 「可接受」是團隊明確的決策，不是悄悄藏起來。
- 對應 pptx 第 9 張 UC-04「項目重要性」段落 — UC-04 把「列管的妥協」變成
  團隊共識，每次 CI 都會驗證它仍然存在。

---

## 6. Step 6 · UC-01 錯誤路徑 — Ext 2a / 3a / 3c（exit 2）

**目的**：示範 fully dressed use case 的 robustness — 三種錯誤狀況各自
產出乾淨訊息與 `exit=2`，與「有違規 → exit 1」「成功 → exit 0」三段語意分明。

> 此功能由 `Main.java` 的 `IExecutionExceptionHandler` 統一處理：
> Callable 內拋出的 `IllegalArgumentException` / `RuntimeException` 都會被
> 攔截為 `Error: <msg>` + `exit 2`。

### 指令 + 預期輸出
#### Ext 2a — 專案目錄不存在
```bash
java -cp 'target/arch-checker.jar:target/lib/*' \
    com.archchecker.cli.Main check no-such-dir demo/demo-profile.yaml
echo "exit=$?"
```

```
Error: Project path is not a directory: no-such-dir
exit=2
```
#### Ext 3a — Profile 檔案不存在
```bash
java -cp 'target/arch-checker.jar:target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project no-such-profile.yaml
echo "exit=$?"
```

```
Error: Profile not found: no-such-profile.yaml
exit=2
```
#### Ext 3c — Profile 內含未知 rule type
```bash
cat > /tmp/bad-profile.yaml <<'EOF'
name: bad
rules:
  - type: bogus-rule-type
    id: R-XXX-01
EOF

java -cp 'target/arch-checker.jar:target/lib/*' \
    com.archchecker.cli.Main check demo/sample-project /tmp/bad-profile.yaml
echo "exit=$?"
```

```
Error: Unknown rule type: bogus-rule-type
exit=2
```

### 講解重點 — NFR-05 三段 exit code

| code | 意義 | 觸發條件 |
|---|---|---|
| `0` | pass | 無違規或全部 suppressed |
| `1` | violations found | 有未 suppress 的違規 |
| `2` | error | 目錄缺、profile 缺、YAML 語法錯誤、未知 rule type |

CI script 可用 `if [ $? -eq 2 ]` 區分「設定壞掉」與「程式碼真有違規」 —
前者該叫 platform team 修，後者該叫開發者修，兩者責任歸屬完全不同。

> **小提醒**：Ext 4a「目錄無 `.java`」目前實作是回 `Checked 0 file(s);
> 0 violation(s); 0 suppressed.` + `exit=0`，沒有印 warning。規格上是
> 「friendly warning」，現行行為是「graceful no-op」 — 老師若問起可如實回答
> 「graceful 回 0 比 noisy warning 對 CI 更友善，但完整 NFR 對齊留待 final」。

---

## 7. 完整 use case 對照

| Use Case / Extension | Demo Step | guideline 標記 |
|---|---|---|
| UC-01 Check Architecture Compliance · Main Success（10 步驟） | Step 1 | M ✅ |
| UC-01 Extension 9a（`--json`） | Step 2 | M ✅ |
| UC-01 第 8 步過濾 suppressed | Step 4 | M ✅ |
| UC-01 Extension 8a（全數 suppressed → exit 0） | Step 5 | M ✅ |
| UC-01 Extension 2a（目錄不存在 → exit 2） | Step 6 | M ✅ |
| UC-01 Extension 3a（Profile 不存在 → exit 2） | Step 6 | M ✅ |
| UC-01 Extension 3c（未知 rule type → exit 2） | Step 6 | M ✅ |
| UC-04 Suppress a Violation | Step 3, 5 | M ✅ |
| UC-05 Load Style Profile（被 UC-01 include） | 每個 Step 隱含 | M ✅ |

3 個 midterm use case 全部覆蓋；UC-01 同時涵蓋 main success + **5 個 Extension**
（2a / 3a / 3c / 8a / 9a），遠超 guideline「one successful and several alternatives
scenarios」的最低要求。

---

## 8. Demo 前 checklist

- [ ] **Step 0.2 build 完成**：`target/arch-checker.jar` 與 `target/lib/*.jar` 都存在
- [ ] **`mvn test` 21 pass**（最後一次驗證沒 regression）
- [ ] **Step 0.3 清掉殘留**：`demo/.arch-checker-suppress.yaml` 不存在
- [ ] **終端機字型 ≥ 16pt**（`Cmd + +`），後排能看清 ruleId 與行號
- [ ] **macOS 系統設定**：
  - [ ] 關閉「請勿打擾」以外的所有通知（避免 demo 跳出 Slack / Mail）
  - [ ] 隱藏 Dock：`System Settings → Desktop & Dock → Automatically hide`
  - [ ] 隱藏選單列：同上設定 → `Automatically hide and show the menu bar` → `Always`
  - [ ] 桌布改純色（避免私人桌布出現）
- [ ] **指令貼好在備援文字檔**（本檔即可），現場不要打字
- [ ] **6 張終端機截圖** 放進備援投影片，現場 demo 失敗時 fallback
- [ ] **計時器** 開好（guideline 第 10 頁明確要求）
- [ ] **轉接線**：MacBook 多半只有 USB-C，需自備 HDMI / VGA 轉接器
- [ ] **第二顯示器鏡像而非延伸**：`System Settings → Displays → Mirror Displays`，避免 demo 視窗跑到投影機外
- [ ] **斷網測試**：arch-checker 不需網路，但確認課堂環境終端機能順利啟動
- [ ] **印 2 份雙面黑白簡報** 給老師與助教（guideline 第 7 頁）

---

## 附：一鍵跑完整 demo（備援用）

若現場時間吃緊，下列 script 一次跑完 Step 1~6（Step 3/5 寫入時間戳會略有差異）：

```bash
#!/usr/bin/env bash
set +e   # 讓有違規的 step 也繼續往下跑
CP='target/arch-checker.jar:target/lib/*'

# === 0. 前置 ===
rm -f demo/.arch-checker-suppress.yaml

# === 1. sample-project 主成功（4 violations）===
echo "=== STEP 1 · UC-01 main success ==="
java -cp "$CP" com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml
echo "STEP 1 exit=$?"
echo

# === 2. Ext 9a · JSON 輸出（同時寫入根目錄 violation-report.json）===
echo "=== STEP 2 · Ext 9a --json ==="
java -cp "$CP" com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml --json \
    | tee violation-report.json
echo
echo "STEP 2 done (file: violation-report.json)"
echo

# === 3. UC-04 suppress R-NAME-01 ===
echo "=== STEP 3 · UC-04 suppress R-NAME-01 ==="
java -cp "$CP" com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml R-NAME-01 \
    demo/sample-project/src/main/java/com/example/service/PaymentManager.java 12 \
    'legacy name; refactor in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml
echo "STEP 3 done"
echo

# === 4. UC-01 重檢（3 violations, 1 suppressed）===
echo "=== STEP 4 · UC-01 re-check (3 / 1) ==="
java -cp "$CP" com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml
echo "STEP 4 exit=$?"
echo

# === 5. Ext 8a · 全數 suppress → exit 0 ===
echo "=== STEP 5 · Ext 8a · suppress remaining 3 ==="
java -cp "$CP" com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml R-DEP-01 \
    demo/sample-project/src/main/java/com/example/service/ChargeService.java 2 \
    'temporary cross-layer wire-up; refactor in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

java -cp "$CP" com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml R-SUP-01 \
    demo/sample-project/src/main/java/com/example/domain/payment/CashPayment.java 12 \
    'cash payments out of scope for v1; will inherit Payment in v2' \
    --suppress-file demo/.arch-checker-suppress.yaml

java -cp "$CP" com.archchecker.cli.Main suppress \
    demo/demo-profile.yaml R-PKG-01 \
    demo/sample-project/src/main/java/com/example/controller/PaymentController.java 0 \
    'repository package planned for v2 once persistence is added' \
    --suppress-file demo/.arch-checker-suppress.yaml

java -cp "$CP" com.archchecker.cli.Main check demo/sample-project demo/demo-profile.yaml \
    --suppress-file demo/.arch-checker-suppress.yaml
echo "STEP 5 exit=$?"
echo

# === 6. 錯誤路徑（Ext 2a / 3a / 3c ─ 都應 exit 2）===
echo "=== STEP 6a · Ext 2a · project not exist ==="
java -cp "$CP" com.archchecker.cli.Main check no-such-dir demo/demo-profile.yaml
echo "STEP 6a exit=$?"
echo

echo "=== STEP 6b · Ext 3a · profile not exist ==="
java -cp "$CP" com.archchecker.cli.Main check demo/sample-project no-such-profile.yaml
echo "STEP 6b exit=$?"
echo

echo "=== STEP 6c · Ext 3c · unknown rule type ==="
cat > /tmp/bad-profile.yaml <<'EOF'
name: bad
rules:
  - type: bogus-rule-type
    id: R-XXX-01
EOF
java -cp "$CP" com.archchecker.cli.Main check demo/sample-project /tmp/bad-profile.yaml
echo "STEP 6c exit=$?"
rm -f /tmp/bad-profile.yaml
```

存成 `demo-run.sh`，`chmod +x demo-run.sh && ./demo-run.sh` 即可。
