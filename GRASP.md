# GRASP 速查筆記

> 為對應作業 hw5 中各 Sequence Diagram 上的 «by X» 標註而整理的簡要說明。

## 1. 在 slides/ 中可以查到 GRASP 的位置

| 檔案 | 章節 | 內容 |
| --- | --- | --- |
| `slides/chapter17-18.pdf` | Larman Ch 17 | GRASP 9 個原則的完整介紹（核心章節） |
| `slides/chapter17-18.pdf` | Larman Ch 18 | 用 NextGen POS 範例示範把責任指派到正確類別 |
| `slides/chapter22-26.pdf` | Larman Ch 25 | 進階補充：Polymorphism、Pure Fabrication、Indirection、Protected Variations |

> 備註：Ch 17 著重在前 5 個基本原則（Information Expert / Creator / Controller / Low Coupling / High Cohesion），Ch 25 補完剩下 4 個。

## 2. GRASP 是什麼？

**GRASP = General Responsibility Assignment Software Patterns**
（一般責任指派軟體模式）

Larman 提出的 9 個經驗法則，用來回答 OO 設計裡最重要的問題：

> 「**這個責任（method、職責）應該指派給哪個類別？**」

它不是設計模式語法，而是「指派責任」時的判斷依據；GoF 模式很多都建立在這些原則之上。

## 3. 9 個元素的簡化定義

| # | 名稱 | 一句話說明 | 觸發時機 |
| --- | --- | --- | --- |
| 1 | **Information Expert** | 把責任交給「擁有完成它所需資訊的類別」。 | 不知道方法該放哪個類別時的預設答案。 |
| 2 | **Creator** | B 類包含/聚合/緊密使用 A，就由 B 來建立 A。 | 決定誰 `new` 出新物件。 |
| 3 | **Controller** | 系統事件由「代表整個系統的 façade」或「代表一個 use case 的 handler」接收。 | UI 或外部呼叫進入領域層的第一站。 |
| 4 | **Low Coupling** | 降低類別之間的依賴，讓變更不會擴散。 | 評估方案時當「裁判」。 |
| 5 | **High Cohesion** | 一個類別只做一件事，職責聚焦。 | 評估方案時當「裁判」。 |
| 6 | **Polymorphism** | 行為依型別而異時，用多型（覆寫）替代 if/switch。 | 出現「依類別判斷」的條件分支。 |
| 7 | **Pure Fabrication** | 為了內聚與低耦合，造一個現實世界沒有的「人工類別」。 | 例如 Repository、Service、Mapper 等基礎建設類。 |
| 8 | **Indirection** | 在兩個元件中間插一個中介者，讓彼此不直接依賴。 | Adapter、Proxy、事件匯流排等。 |
| 9 | **Protected Variations** | 在「會變動的點」用穩定介面包起來，避免變動外擴。 | 預期會替換/演化的部分，先抽介面。 |

## 4. hw5 裡的對照

作業 sequence diagram 上的 «by X» 是在標示「為什麼這條訊息該由這個類別負責」。常見對應：

- `:CheckCommand`、`:SuppressCommand` → **Controller**（代表 use case 的入口）
- `:CheckOrchestrator`、`:SuppressionService` → **Pure Fabrication**（領域沒有對應名詞，但需要協調者）
- `:RuleProfileLoader`、`:JavaParserAdapter` → **Indirection / Pure Fabrication**（包住外部 I/O、第三方 lib）
- `:Constraint` 子類別覆寫 `evaluate()` → **Polymorphism**
- `:Suppression` 由 service 建立 → **Creator**（service 持有建立所需資料）
- `:SuppressionRepository`、`:Reporter` 各做一件事 → **High Cohesion / Pure Fabrication**
- `:ViolationReport` 自己決定如何彙整自己的內容 → **Information Expert**
