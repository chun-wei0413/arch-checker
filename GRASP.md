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

### 第 7、8、9 點看不懂時：用 Larman 教材的例子

#### 7. Pure Fabrication —— NextGen POS 的 `PersistentStorage` / Monopoly 的 `Cup`

**例 1：把 Sale 存到資料庫該交給誰？**

照 Information Expert 的話，「資料在 Sale 上」→ 應該由 `Sale` 自己負責存。但這樣會發生：

- `Sale` 多出一堆跟 sale 無關的 DB 操作 → **內聚下降**
- `Sale` 直接依賴關聯式資料庫的 API → **耦合上升**
- 每個要存進 DB 的類別都得自己重寫一次 → **無法重用**

解法：捏一個現實世界沒有的類別 `PersistentStorage`，專門負責「把物件存進 DB」。它不是 domain concept，是純粹為了軟體方便而「捏造（fabricate）」出來的，所以叫 *Pure* Fabrication。

**例 2：Monopoly 擲骰子由誰負責？**

原本是 `Player.rollAll()` 自己擲、自己加總。問題：

- 擲骰邏輯被綁在 `Player` 裡，其他遊戲沒辦法重用
- 想單純查「目前點數」也得重擲一次

解法：捏一個 `Cup` 類別來持有所有骰子、負責 roll 與加總。Cup 不是 Monopoly 的領域概念，是設計者為了讓 Player 保持單純而「想出來的」類別。

> 重點：Pure Fabrication 是 **「behavioral decomposition」**——當「按領域名詞分類」會破壞內聚或耦合時，就照「行為」切一個工具類出來。GoF 裡的 Adapter / Strategy / Command 幾乎都是 Pure Fabrication。

#### 8. Indirection —— NextGen POS 的 `TaxCalculatorAdapter`

**情境**：Sale 結帳時要算稅，但稅算服務有好幾家外部廠商（Tax-Master、Good-As-Gold TaxPro……），每家 API 都不一樣。

如果 `Sale` 直接呼叫每一家：

```
Sale ──直接耦合──► TaxMaster
     ──直接耦合──► GoodAsGoldTaxPro
```

→ 換廠商就得改 `Sale`，而且 Sale 知道一堆與「銷售」無關的細節。

解法：在中間放一個 `TaxCalculatorAdapter` 當中介者：

```
Sale ──► TaxCalculatorAdapter ──► TaxMaster / GoodAsGoldTaxPro
```

`Sale` 只認 adapter，不認外部 API；廠商怎麼換，`Sale` 都不用動。這個「為了解耦而插在中間的物件」就是 Indirection。

> 補充：上面 `PersistentStorage` 同時也是 Indirection 的例子——它隔在 `Sale` 和 DB 之間。所以同一個類別常常是 *Pure Fabrication + Indirection*（hw5 的 SD 就是這樣標的）。

#### 9. Protected Variations —— 用 `ITaxCalculatorAdapter` 介面包住變動點

延續上面的稅算例子，再進一步：

把 adapter **抽出介面** `ITaxCalculatorAdapter`，每家廠商寫一個實作（`TaxMasterAdapter`、`GoodAsGoldAdapter`……）：

```
Sale ──► «interface» ITaxCalculatorAdapter
              ▲
   ┌──────────┼──────────┐
TaxMaster   GoodAs..   FutureCo
Adapter     Adapter    Adapter
```

這時：

- 「外部稅算 API 會變」是**已預測的變動點**
- `ITaxCalculatorAdapter` 是**穩定的介面（stable interface）**
- 不論將來插哪一家，`Sale` 端都完全不用改

這就是 Protected Variations：**找出會變的點 → 在它外面包一個穩定介面 → 變動就被擋在介面後面**。

> Larman 強調：data encapsulation、polymorphism、config file、虛擬機、作業系統……幾乎所有「隔離變動」的招式，本質上都是 Protected Variations 的具體實作。它的核心就是 **information hiding**（資訊隱藏）。

#### 三者怎麼區分？同一個稅算例子串起來

| 觀點 | 對應的 GRASP |
| --- | --- |
| 為什麼要造一個 `TaxCalculatorAdapter`？因為它不在領域裡，是為設計方便造的工具類 | **Pure Fabrication** |
| 為什麼要把它放在 `Sale` 和外部 API 之間？因為要解耦 | **Indirection** |
| 為什麼要再抽 `ITaxCalculatorAdapter` 介面？因為要保護 `Sale` 不受外部變動影響 | **Protected Variations** |

> 一句話記憶：**「捏一個（Pure Fab）→ 插在中間（Indirection）→ 外面再包介面（PV）」**——三層往往疊在同一個設計決策上。

## 4. hw5 裡的對照

作業 sequence diagram 上的 «by X» 是在標示「為什麼這條訊息該由這個類別負責」。常見對應：

- `:CheckCommand`、`:SuppressCommand` → **Controller**（代表 use case 的入口）
- `:CheckOrchestrator`、`:SuppressionService` → **Pure Fabrication**（領域沒有對應名詞，但需要協調者）
- `:RuleProfileLoader`、`:JavaParserAdapter` → **Indirection / Pure Fabrication**（包住外部 I/O、第三方 lib）
- `:Constraint` 子類別覆寫 `evaluate()` → **Polymorphism**
- `:Suppression` 由 service 建立 → **Creator**（service 持有建立所需資料）
- `:SuppressionStore`、`:Reporter` 各做一件事 → **High Cohesion / Pure Fabrication**
- `:ViolationReport` 自己決定如何彙整自己的內容 → **Information Expert**
