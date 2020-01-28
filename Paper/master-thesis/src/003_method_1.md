# 手法I: パレート効率性を満たす手法

本章ではパレート効率性を満たす手法Iのアルゴリズムについて説明を行い，その後計算機実験による特性評価を行う．

## アルゴリズム

パレート効率性を満たす手法Iのアルゴリズムについて説明する．

### 概要\label{symbol}

以下に本手法のアルゴリズムの流れを示す．

<!-- tex --> 

\begin{description}

\item[STEP1:]リソース提供企業とリソース要求企業は入札を作成する．

\item[STEP2:]オークション主催者は，入札を元に勝者決定問題を解くことでリソース配分を決定する．

\item[STEP3:]オークション主催者は，リソースの取引価格を決定する．

\end{description}

 <!-- tex -->

各**STEP**については次項以降で説明する．また説明に使用する記号の定義を以下に示す．

+ $i$: リソース提供企業($i \in \boldsymbol{I}$)
+ $j$:  リソース要求企業($j \in \boldsymbol{J}$)
+ $r$: オークションにかけられるリソース($r \in \boldsymbol{R}$)
+ $c_{i,r}$: 提供企業$i$が提供するリソース$r$のコスト
+ $TP_{i,r}$: 提供企業$i$がリソース$r$を提供する時間
+ $n$:  要求企業$j$の$n$番目の入札($n \in \boldsymbol{N}$)
+ $v_{j,n}$: 要求企業$j$の$n$番目の入札の評価値
+ $TR_{j,n.r}$: 要求企業$j$の$n$番目の入札においてリソース$r$を要求する時間

### 入札作成\label{make-bid}

**STEP1**においてリソース提供企業とリソース要求企業はそれぞれ以下のような入札を作成する．

+ リソース提供企業: 提供するリソース$r \in \boldsymbol{R}$のコスト$c_{i,r}$と提供時間からなる入札を$|\boldsymbol{R}|$個作成する．
+ リソース提供企業:予算$v_{j,n}$と要求するリソース$r \in \boldsymbol{R}$の要求時間$TR_{j,n,r}$からなる入札$n$を$N$個提示する．
  + 勝者となる入札は1つとする．
  + 必要なリソースの組合せに対してリソースの入札を作成する．

この**STEP1**の入札作成は手法IIと共通である．作成され入札の例を[@fig:example-bid]に示す．

![Example of bids](/Users/haradayoshiaki/Resarch/Paper/master-thesis/src/img/chapter-3/bid-example.png){#fig:example-bid width=90%}

提供企業$1$はリソース1をコスト0.1[円]で150[Ts]提供し，リソース2をコスト0.2円で100[Ts]提供していることを表す．同様に，提供企業$2$はリソース2をコスト0.5[円]で200[Ts]提供する．要求企業$1$は入札$1$において予算150円で，リソース$2$とリソースを50[Ts]要求している．同様に要求企業$2$は入札$1$において予算200で，リソース1を100[Ts]，リソース2を50[Ts]要求する入札を作成している．

### リソース配分の決定 \label{method1-resorce}

**STEP2**において，リソースの配分を決める勝者決定問題$P(\boldsymbol{I},\boldsymbol{J})$は組合せ最適化問題として定式化される．以下にその定式化を示す．
$$
\begin{align}
  {\rm max}\quad  V(\boldsymbol{I},\boldsymbol{J})=&\sum_{j\in \boldsymbol{J}}\sum_{n\in\boldsymbol{N}}v_{j} \times y_{j,n} - \sum_{i\in\boldsymbol{I}}\sum_{r\in\boldsymbol{R}}\sum_{j\in\boldsymbol{J}}\sum_{n\in\boldsymbol{N}}c_{i,r} \times x_{i,r,j,n} \label{pij-obj}\\
	{\rm s.t.} \quad &\sum_{j\in \boldsymbol{J}}\sum_{n\in\boldsymbol{N}}x_{i,r,j,n} \leq TP_{i,r} \quad (\forall i, \forall r) \label{pij-subto-cap}\\
  &\begin{cases}
    x_{i,r,j,n} = 0 \quad (\forall i, \forall r)&({\rm if} \ y_{j,n}=0) \\
    \sum_{j \in \boldsymbol{J}}\sum_{n\in\boldsymbol{N}} TR_{j,n,r} \times x_{i,r,j,n} = TR_{j,n,r}
    \quad  (\forall i, \forall r)&({\rm if} \ y_{j,n}=1) 
  \end{cases}\label{pij-subto-bundle}\\
    &\sum_{n\in \boldsymbol{N}}y_{j,n}  \leq 1 \quad (\forall j) \label{pij-subto-1winner}\\
    &x_{i,r,j,n} \in \boldsymbol{Z}\label{pij-decision-x}\\
    &y_{j,n} \in {0,1} \label{pij-decision-y}
\end{align}
$$

決定変数は$x_{i,r,j,n}$と$y_{j,n}$である．$x_{i,r,j,n}$は企業$i$と企業$j$がリソース$r$を取引する量を表す整数変数，$y_{j,n}$は企業$j$の入札$n$が選ばれるとき1，選ばれない時0となる変数である．$\eqref{pij-obj}$は目的関数であり，総利益最大化を表す．$\eqref{pij-subto-cap}$は提供企業のリソース容量制約を表す．$\eqref{pij-subto-bundle}$は組合せ入札に関する制約であり，要求企業$j$の入札$n$が選ばれないときはどのリソース要求も満たさず，要求企業$j$の入札$n$が選ばれるときはその入札のリソース要求を全て満たすための制約である．$\eqref{pij-subto-1winner}$は勝者となる要求企業の入札は高々1つとする制約である．この組合せ最適化問題$P(\boldsymbol{I},\boldsymbol{J})$を解くことで勝者となる要求企業の入札と，それに対する提供リソースの配分を決定する．また問題$P(\boldsymbol{I},\boldsymbol{J})$の最適解は総利益が最大化されており，パレート効率な状態となっている．

### 取引価格決定

**STEP3**では前項のリソース配分を元に取引価格$trade_{i,r,j,n}$を決定する．手法Iの取引価格はSamimiらの文献を参考にした\cite{Samimi2016}．この取引価格はお互いの入札値の平均の価格から決定され，以下の式で表される．
$$
\begin{align}
trade_{i,r,j,n}=&\frac{c_{i,r}+\{v_{j,n}×\frac{TR_{j,n,r}}{sumTR_{j,n}}/TR_{j,n,r}\}}{2}\times x_{i,r,j,n} \label{1-trade}\\
sumTR_{j,n} = &\sum_{r  \in R} TR_{j,n,r} \label{sumtime}
\end{align}
$$
$sumTR_{j,n,r}$は要求企業$j$が入札$n$におけるリソース要求時間の合計であり，$\eqref{1-trade}$の$v_{j,n}×\frac{TR_{j,n,r}}{sumTR_{j,n}}/TR_{j,n,r}$はリソース$r$の1[Ts]分の予算を表す．従って$\eqref{1-trade}$はお互いの入札値の平均で取引を行っていることとなる．

### 特徴

手法Iは問題$P(\boldsymbol{I},\boldsymbol{J})$の最適解に従いリソースの配分を決めるので，パレート効率な配分が実現される．しかし取引価格はお互いの入札値の平均をとるので，この価格は提供企業，要求企業ともにcritical priceとなっておらず耐戦略性を満たすことはできない．ただし同じような入札値を持つ企業が増加した場合，虚偽申告を行うとオークションの敗者になる可能性が高くなるので，正直な入札値の申告が支配戦略になっていくことが考えられる．

## 特性評価\label{exp-condition}

本節では計算機実験により手法Iの特性を評価する．問題$P(\boldsymbol{I},\boldsymbol{J})$の求解には数理計画ソルバーCPLEXを用いる．共通の実験条件を以下に示す．ただし[min, max]はminからmaxの一様乱数とする．

+ リソース$|\boldsymbol{R}|$=6
+ 対象期間: 200[Ts]
+ 提供企業$|\boldsymbol{J}|=25$
  + 各企業2種類のリソースを遊休時間に提供する
  + 遊休時間$TP_{i,r}$ [Ts]を[100, 200]で決定する
  + コスト$c_{i,r}$は[2.0, 4.0] [円]とする
+ 要求企業$|\boldsymbol{I}|=10$
  + 各企業$|\boldsymbol{N}|$=3個の入札を作成
  + R種類のリソースを各 [0, 200] [Ts]要求する
  + 予算$v_{j,n}$は合計要求時間と重み[3.0, 5.0]の積[円]とする

次項以降では以下の項目について変更を行った実験を行い，結果ならびに考察を述べる．

+ 1提供企業の虚偽申告率
+ 1要求企業の虚偽申告率
+ 提供側が申告するコストの幅
+ 要求企業が申告する予算の幅 

### 1提供企業の虚偽申告率の変更

本稿では1提供企業の虚偽申告率を変更する実験を行う．手法Iは耐戦略性を満たせず虚偽申告により利益を高められることを確認する．ここでの虚偽申告率とは，コストにある割合分上乗して入札値として申告するとした際のその割合のことを指す．例えばコストが10円，虚偽申告率が10%の場合は11円と入札する．コストを過剰申告することで利益を上げようとする企業を想定する．以下に本実験における実験条件を示す．

+ 1提供企業の虚偽申告率: 0%，10%，20%，30%
  + 虚偽申告が0%のときは正直にコストを申告する．
+ 試行回数: 1回(ある1試行における虚偽申告企業の利益を見るために1試行とした)

#### 実験結果

[@tbl:m1-1-total-profit]〜[@tbl:m1-1-false-requester-total-profit]はそれぞれ総利益，総提供企業利益，総要求企業利益，虚偽申告を行った1提供企業の利益を示す．

| False rate   | 0%      | 10%     | 20%     | 30%     |
| ------------ | :-------: | :-------: | :-------: | :-------: |
| Total Profit | 9175.28 | 9175.22 | 9175.22 | 9049.41 |

: Total profit  in Method I: A provider report false cost {#tbl:m1-1-total-profit}

| False rate             | 0%      | 10%     | 20%     | 30%     |
| ---------------------- | :-------: | :-------: | :-------: | :-------: |
| Providers Total Profit | 4587.64 | 4613.49 | 4639.38 | 4524.70 |

: Providers total profit: in Method I: A provider report false cost {#tbl:m1-1-providers-total-profit}

| False rate              | 0%      | 10%     | 20%     | 30%     |
| ----------------------- | :-------: | :-------: | :-------: | :-------: |
| Requesters Total Profit | 4587.64 | 4561.72 | 4535.84 | 4524.70 |

: Requesters total profit in Method I: A  provider report false cost  {#tbl:m1-1-requesters-total-profit}

| False rate       | 0%     | 10%    | 20%    | 30%  |
| ---------------- | :------: | :------: | :------: | :----: |
| Requester Profit | 112.47 | 139.30 | 227.77 | 0    |

:The false reporting provider profit in Method I: A provider report false cost {#tbl:m1-1-false-requester-total-profit}

#### 考察

[@tbl:m1-1-false-requester-total-profit]において，偽申告提供企業の利益は虚偽申告率が0%のとき112.47であるが20%のときに227.77であり，虚偽申告率が20%まで利益が増加している．20%から30%にかけて利益が下がったのは，申告したコストが高くなりリソースの配分が変わってしまいオークションにおいて勝者となることができなくなってしまったためと考える．よってオークションの敗者になるまで，虚偽申告を行うことで利益を上げることが可能であり，耐戦略性を満たせないことが確認できる．また[@tbl:m1-1-total-profit]の虚偽申告率0から20%まで総利益があまり変化がないにも関わらず，[@tbl:m1-1-providers-total-profit]の総提供企業利益が増加し[@tbl:m1-1-requesters-total-profit]の総要求企業利益が減少していることから，虚偽申告提供企業に利益が移動していることが確認できる．

また[@tbl:m1-1-total-profit]より，総利益は虚偽申告率が0%のときが最も高い9175.28であり，虚偽申告率が30%のときが最も低い9049.41である．このように虚偽申告企業が存在してしまうとパレート効率な状態を導けなくなることが確認できる．

### 1要求企業の虚偽申告率の変更

次に1要求企業の虚偽申告率を変更する実験を行う．手法Iは耐戦略性を満たせず虚偽申告により利益を高められることを確認する．また虚偽申告による影響も確認する．ここでの虚偽申告率は入札の予算に対して減額する割合とする．例えば虚偽申告率が10%のときは，予算が1000円の入札を900円と申告する．予算を過少申告することでより安い価格でリソースを入手しようとする企業を想定する．以下に本実験における実験条件を示す．

+ 1要求企業の虚偽申告率:0%，10%，20%，30%
  + 虚偽申告が0%のときは正直に予算を申告する．
+ 試行回数:1回(ある1試行における虚偽申告企業の利益を見るために1試行とした)

#### 結果

[@tbl:m1-2-total-profit]〜[@tbl:m1-2-false-requester-profit]は，それぞれ総利益，総提供企業利益，総要求企業利益，虚偽申告を行った1要求企業の利益を示す．

| False rate   | 0%      | 10%     | 20%     | 30%     |
| ------------ | :-------: | :-------: | :-------: | :-------: |
| Total Profit | 9175.28 | 9175.28 | 9175.28 | 9175.28 |
: Total profit  in Method I: A requester report false budget {#tbl:m1-2-total-profit}

| False rate             | 0%      | 10%     | 20%     | 30%     |
| ---------------------- | :-------: | :-------: | :-------: | :-------: |
| Providers Total Profit | 4587.64 | 4492.88 | 4398.11 | 4303.35 |

: Total providers profit  in Method I: A requester report false budget {#tbl:m1-2-providers-total-profit}

| False rate               | 0%      | 10%     | 20%     | 30%     |
| ------------------------ | :-------: | :-------: | :-------: | :-------: |
| Requesters Total  Profit | 4587.64 | 4682.41 | 4777.17 | 4871.93 |

: Total requesters profit  in Method I: A requester report false budget {#tbl:m1-2-requesters-total-profit}

| False rate      | 0%     | 10%    | 20%    | 30%    |
| --------------- | :------: | :------: | :------: | :------: |
| Provider Profit | 412.85 | 457.55 | 573.55 | 694.53 |

 : The false reporting requester profit in Method I: A requester report false budget {#tbl:m1-2-false-requester-profit}

#### 考察

[@tbl:m1-2-false-requester-profit]より，虚偽申告要求企業の利益は0%のとき412.85であり，30%のとき694.53となっており，虚偽申告率が増加するごとに利益が上がっていることが確認できる．よって手法Iが耐戦略性を満たせないことが確認できた．また[@tbl:m1-2-total-profit]において，総利益は虚偽申告率が0%から30%のとき9175.28と変化がないが，[@tbl:m1-2-providers-total-profit]の提供企業利益は減少し[@tbl:m1-2-requesters-total-profit]の総要求企業利益は増加している．これより虚偽申告企業に利益が移動していることが分かる．

### 提供側が申告するコストの幅の変更 

次に提供側が申告するコストの幅を変更する実験を行う．幅を変更したそれぞれの場合において，ある提供企業が虚偽申告率を変更した場合に利益がどのように変化するかを確認する．各企業が申告するコストの幅が狭くなる，つまり同じようなコストの企業が集まると正直な申告が支配戦略になることを確認する．

+ コストを発生させる乱数の幅: 2.5，2.0，1.5，1.0
  + コストを[1.75,4.25]，[2.0,4.0]，[2.25,3.75]，[2.5,3.5]で生成する．
+ 試行回数: 10回

#### 実験結果

[@tbl:m1-3-2.5-false-provider-profit]〜[@tbl:m1-3-1.0-false-provider-profit]は，それぞれコストの幅が2.5，2.0，1.5，1.0のときの虚偽申告提供企業の利益を表す．

| False rate | 0%     | 10%    | 20%    | 30%    |
| ---------- | :------: | :------: | :------: | :------: |
| AVE.       | 173.07 | 194.66 | 184.64 | 143.98 |
| S.D.       | 97.60  | 104.51 | 124.04 | 124.09 |

: The false reporting provider profit in Method I: cost range=2.5 {#tbl:m1-3-2.5-false-provider-profit}

| False rate | 0%     | 10%    | 20%    | 30%    |
| ---------- | :------: | :------: | :------: | :------: |
| AVE.       | 189.28 | 203.53 | 201.61 | 172.39 |
| S.D.       | 105.08 | 103.87 | 79.45  | 88.61  |

:The false reporting provider profit in Method I: cost range=2.0 {#tbl:m1-3-2.0-false-provider-profit}

| False rate | 0%     | 10%    | 20%    | 30%   |
| ---------- | :------: | :------: | :------: | ----- |
| AVE.       | 208.02 | 209.98 | 158.56 | 84.83 |
| S.D.       | 89.21  | 116.48 | 125.35 | 88.38 |

:The false reporting provider profit in Method I: cost range=1.5 {#tbl:m1-3-1.5-false-provider-profit}

| False rate | 0%     | 10%    | 20%   | 30%   |
| ---------- | :------: | :------: | ----- | ----- |
| AVE.       | 165.03 | 147.50 | 75.35 | 60.80 |
| S.D.       | 89.19  | 90.53  | 94.68 | 69.13 |

:The false reporting provider profit in Method I: cost range=1.0 {#tbl:m1-3-1.0-false-provider-profit}

[@tbl:m1-3-2.5-false-provider-profit]〜[@tbl:m1-3-1.0-false-provider-profit]の結果から，正直にコストを申告した場合に対する虚偽申告を行った際の利益の変化を[@tbl:m1-3-profit-rate]に示す．

| False rate     | 10%    | 20%    | 30%    |
| -------------- | :------: | :------: | :------: |
| cost range=2.5 | 12.5%  | 6.7%   | -16.8% |
| cost range=2.0 | 7.5%   | 6.5%   | -6.1%  |
| cost range=1.5 | 0.9%   | -23.8% | -59.2% |
| cost range=1.0 | -10.6% | -54.3% | -63.2% |

:Ratio of increased the false reporting provider profit to the profit for reporting truthful cost  {#tbl:m1-3-profit-rate}

例えばコストの幅が2.5のとき，虚偽申告10%を行うと正直な申告より利益が12.5%と増加していることを表している．

#### 考察

[@tbl:m1-3-profit-rate]より，コストの幅が2.5のときは虚偽申告率が20%のときまで利益が増加しているが，コストの幅が1.5のとき虚偽申告率を20%とする利益は-6.1%となり減少している．またコストの幅が1.0のときは10%の虚偽申告でも-10.6%と利益が減少しており，コストの幅が狭くなるごとに正直な申告が支配戦略に近づくことがわかる．これは各提供企業が申告するコストの幅が狭い状況で虚偽申告を行うと勝者決定問題$P(\boldsymbol{I},\boldsymbol{J})$において敗者となってしまい，利益を得られないからである．

### 要求企業が申告する予算の幅の変更 

次に提供側が申告する予算の幅を変更する実験を行う．幅を変更したそれぞれの場合において，ある要求企業が虚偽申告率を変更した場合に利益がどのように変化するかを確認する．前項と同様に同じような予算の企業が集まると正直な申告が支配戦略になることを確認する．

+ 予算を決める重みの乱数の幅: 2.5，2.0，1.5，1.0
  + 重みを[2.75,5.25]，[3.0,5.0]，[3.25,4.75]，[3.5,4.5]で生成する．
+ 試行回数:10回

#### 実験結果

[@tbl:m1-4-2.5-requester-profit]〜[@tbl:m1-4-1.0-requester-profit]は，それぞれ予算の幅が2.5，2.0，1.5，1.0のときの虚偽申告要求企業の利益を表す．

| False rate | 0%     | 10%    | 20%    | 30%    |
| ---------- | :------: | :------: | :------: | :------: |
| AVE.       | 408.94 | 448.28 | 453.20 | 230.52 |
| S.D.       | 293.34 | 376.17 | 477.45 | 466.90 |

:The false reporting requester profit in Method I: budget range=2.5 {#tbl:m1-4-2.5-requester-profit}

| False rate | 0%     | 10%    | 20%    | 30%    |
| ---------- | :------: | :------: | :------: | :------: |
| AVE.       | 355.23 | 398.52 | 291.70 | 171.81 |
| S.D.       | 244.25 | 354.22 | 366.58 | 351.41 |

:The false reporting requester profit in Method I: budget range=2.0 {#tbl:m1-4-2.0-requester-profit}

| False rate | 0%     | 10%     | 20%    | 30%  |
| ---------- | :------: | :-------: | :------: | :----: |
| AVE.       | 329.75 | 249.590 | 123.20 | 0    |
| S.D.       | 231.12 | 256.35  | 246.64 | 0    |

:The false reporting requester profit in Method I: budget range=1.5 {#tbl:m1-4–1.5-requester-profit}

| False rate | 0%     | 10%    | 20%    | 30%  |
| ---------- | :------: | :------: | :------: | :----: |
| AVE.       | 433.00 | 306.62 | 220.93 | 0    |
| S.D.       | 159.78 | 317.43 | 352.38 | 0    |

:The false reporting requester profit in Method I: budget range=1.0 {#tbl:m1-4-1.0-requester-profit}

[@tbl:m1-4-2.5-requester-profit]〜[@tbl:m1-4-1.0-requester-profit]から，正直にコストを申告した場合に対する虚偽申告を行った際の利益の変化を[@tbl:m1-3-profit-rate]に示す．

| False rate | 10.0%  | 20.0%  | 30.0%   |
| ---------- | :------: | :------: | :-------: |
| range=2.5  | 9.6%   | 10.8%  | -49.1%  |
| range=2.0  | 12.2%  | -17.9% | -41.1%  |
| range=1.5  | -24.3% | -62.6% | -100.0% |
| range=1.0  | -29.2% | -49.0% | -100.0% |

:Ratio of increased false reporting provider profit to the profit for reporting truthful budget {#tbl:m1-4-profit-rate}

#### 考察

[@tbl:m1-4-profit-rate]より，予算の幅が2.5のときは虚偽申告率が20%のときまで利益が増加し，2.0のときは10%まで虚偽申告を行うことにより利益を高めることができている．しかし予算の幅が1.5より狭くなると虚偽申告率が10%でも利益を高めることができなくなっている．これにより予算の幅狭くなるほど，正直な申告が支配戦略に近づくことが確認できる．これは各提供企業が申告する予算の幅が狭い状況で虚偽申告を行うと勝者決定問題$P(\boldsymbol{I},\boldsymbol{J})$において敗者となってしまい，利益を得られないからである．

## まとめ

本章ではパレート効率性を満たす手法Iのアルゴリズムの説明と特性評価を行った．総利益が最大化される解でリソースの配分を決定するのでパレート効率性を満たすことができるが，取引価格がcritical priceとはなっておらず，耐戦略性を満たせないことを計算機実験において確認した．一方で各企業が申告する評価値の幅が狭くなると虚偽申告企業は損をするので，真の評価値を入札値とすることが支配戦略に近づくことが確認できた．

