# ArchUnit vs arch-checker · Side-by-side

同一條規則：

> **「`com.example.controller` 套件下的所有類別，名稱必須以 `Controller` 結尾」**

兩種寫法的對照，方便在投影片 / 報告做截圖比對。

## ArchUnit（Java + JUnit）

[`ArchUnitControllerNamingTest.java`](./ArchUnitControllerNamingTest.java)

```java
@AnalyzeClasses(packages = "com.example")
class ControllerNamingTest {

    @ArchTest
    static final ArchRule controllers_must_have_Controller_suffix =
            classes()
                    .that().resideInAPackage("..controller..")
                    .and().areNotInterfaces()
                    .should().haveSimpleNameEndingWith("Controller")
                    .because("統一 controller 命名規範");
}
```

**特徵**

- 規則寫在 `src/test/java/` 的 JUnit 測試類別
- 必須安裝 ArchUnit 依賴（`com.tngtech.archunit:archunit-junit5`）
- Method chaining：`classes() → that() → and() → should() → because()`
- 唯讀寫者：**懂 Java + JUnit + ArchUnit DSL** 的工程師
- 規則綁專案：跨專案重用須複製測試碼

## arch-checker（YAML）

[`arch-checker-controller-profile.yaml`](./arch-checker-controller-profile.yaml)

```yaml
name: controller-naming-style
rules:
  - id: R-CTRL-01
    type: naming
    description: Controller 類別必須以 Controller 結尾
    classNamePattern: ".*Controller"
```

```bash
java -jar arch-checker.jar check src/main/java arch-checker-controller-profile.yaml
```

**特徵**

- 規則寫在 **獨立 YAML 檔**，與業務程式碼解耦
- 不需要 JVM 以外的任何 Java 框架
- 5 行宣告式語法
- 唯讀寫者：**任何工程師**（架構師、團隊 lead、新人）皆可編輯
- Profile 是檔案：可 `git`、可在多個專案共享、可放於組織共用 repo

## 對照表

| 維度 | ArchUnit | arch-checker |
| --- | --- | --- |
| 規則表達形式 | Java 測試碼 | YAML 設定檔 |
| 必備依賴 | ArchUnit + JUnit 5 + Java | 僅需 JVM |
| 執行方式 | `mvn test`（綁進整個 test pipeline） | `java -jar arch-checker.jar check ...` |
| 編輯門檻 | 必須懂 Java + ArchUnit fluent API | 任何工程師看 YAML 即可改 |
| 跨專案共用 | 複製貼上測試碼 | 共享一份 YAML |
| 違規回報格式 | JUnit 失敗訊息 | Console 文字 / JSON |
| CI 整合方式 | 透過 `mvn test` 失敗讓 build 失敗 | POSIX exit code（0/1/2），CI 直接讀取 |

## 為什麼差別會放大

- **AI 輔助生成程式碼普及後**，每天要審查的 PR 數倍增。
- ArchUnit 的「規則綁在程式碼裡」意味著：每加一條規則 → 改 Java → push → 重跑測試 pipeline。
- arch-checker 的「規則是設定檔」意味著：每加一條規則 → 改 YAML → 即時生效，不必碰程式碼。
