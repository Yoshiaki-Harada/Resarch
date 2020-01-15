# 手法I:パレート効率性を満たす手法

## アルゴリズム

手法I:パレート効率性を満たす手法のアルゴリズムについて説明する．

### 概要

以下に本手法のアルゴリズムの流れを示す．

1. 入札作成
   + リソース提供企業はオークション主催者に対して，入札を作成する．
   
   + リソース要求企業はオークション主催者に対して，入札を作成する．
2. オークション主催者は，入札を元に勝者決定問題を解くことでリソース配分を決定する．
3. オークション主催者は，リソースの取引価格を決定する．

1に関しては，前節の入札を作成をし，2，3について次節以降で説明する．説明に使用する記号の定義を以下に示す．

+ $i$:リソース提供企業($i \in \boldsymbol{I}$)
+ $j$: リソース要求企業($j \in \boldsymbol{J}$)
+ $r$:オークションにかけられるリソース($r \in \boldsymbol{R}$)
+ $c_{i,r}$:提供企業$i$が提供するリソース$r$のコスト
+ $TP_{i,r}$:提供企業$i$がリソース$r$を提供する時間
+ $n$: 要求企業$j$の入札($n \in \boldsymbol{N}$)
+ $v_{j,n}$:要求企業$j$の$n$番目の入札の評価値
+ $TR_{j,n.r}$:要求企業$j$の$n$番目の入札においてリソース$r$を要求する時間
+ $P(\boldsymbol{I},\boldsymbol{J})$:提供企業の集合が$\boldsymbol{J}$，要求企業の集合$\boldsymbol{J}$であるときの勝者決定問題
+ $V(\boldsymbol{I},\boldsymbol{J})$:問題$P(\boldsymbol{I},\boldsymbol{J})$の目的関数値
+ $trade_{i,r,j,n}$:提供企業$i$が要求企業$j$の勝者となった入札$n$に対してリソース$r$を$x_{i,r,j,n}$[Ts]提供するときの取引価格
+ $sumTR_{j,n,r}$:要求企業$j$が入札$n$におけるリソース要求時間の合計

### リソース配分の決定 \label{method1-resorce}

リソースの配分を決める勝者決定問題$P(\boldsymbol{I},\boldsymbol{J})$は組合せ最適化問題として定式化される．以下にその定式化を示す．

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

決定変数は$x_{i,r,j,n}$と$y_{j,n}$である．$x_{i,r,j,n}$は企業$i$と企業$j$がリソース$r$を取引する量を表す整数変数であり，$y_{j,n}$は企業$j$の入札$n$が選ばれるとき1，選ばれない時0となる変数である．$\eqref{pij-obj}$は目的関数であり，総利益最大化を表す．$\eqref{pij-subto-cap}$は提供企業のリソースの容量制約を表す．$\eqref{pij-subto-bundle}$は組合せ入札に関する制約であり，要求企業$j$の入札$n$が選ばれないときは，どのリソース要求も満たさず，要求企業$j$の入札$n$が選ばれるときは，その入札のリソース要求を全て満たすための制約である．$\eqref{pij-subto-1winner}$は勝者となる要求企業の入札は高々1つとする制約である．この組合せ最適化問題を解くことで勝者となる要求企業の入札と，それに対する提供リソースの配分を決定する．また，問題$P(\boldsymbol{I},\boldsymbol{J})$の最適解は総利益が最大化されているので，パレート効率な状態となっている．

### 取引価格決定

前節のリソース配分を元に取引価格$trade_{i,r,j,n}$を決定する．手法Iの取引価格はSamimiらの文献を参考にした\cite{Samimi2016}．この取引価格はお互いの評価値の平均の価格から決定され，．以下の式で表される．
$$
\begin{align}
trade_{i,r,j,n}=&\frac{c_{i,r}+\{v_{j,n}×\frac{TR_{j,n,r}}{sumTR_{j,n}}/TR_{j,n,r}\}}{2}\times x_{i,r,j,n} \label{1-trade}\\
sumTR_{j,n} = &\sum_{r  \in R} TR_{j,n,r} \label{sumtime}
\end{align}
$$
$sumTR_{j,n,r}$は要求企業$j$が入札$n$におけるリソース要求時間の合計である．よって$\eqref{1-trade}$の$v_{j,n}×\frac{TR_{j,n,r}}{sumTR_{j,n}}/TR_{j,n,r}$はリソース$r$の1[Ts]分の予算を表す．従って$\eqref{1-trade}$はお互いの評価値の平均で取引を行っていることとなる．

### 特徴

手法Iは問題$P(\boldsymbol{I},\boldsymbol{J})$の最適解に従いリソースの配分を決めるので，パレート効率な配分が実現される．しかし取引価格は，お互いの評価値の平均をとるので，この価格は提供企業，要求企業ともにcritical priceとなっておらず耐戦略性を満たすことはできない．ただし同じような評価値を持つ企業が増加した場合，虚偽申告を行うと，オークションの敗者になる可能性が高くなる．従って同じような評価値を持つ企業が増加するほど正直な申告が支配戦略になっていく．

## 特性評価\label{exp-condition}

本節では計算機実験により手法Iの特性を評価する．問題$P(\boldsymbol{I},\boldsymbol{J})$の求解には数理計画ソルバーCPLEXを用いる．共通の実験条件を以下に示す．ただし[min, max]はminからmaxの一様乱数とする．

+ 提供企業$|\boldsymbol{J}|=25$
  + 各企業2種類のリソースを遊休時間に提供する
  + 遊休時間$TP_{i,r}$ [Ts]を[100, 200]で決定する
  + コスト$c_{i,r}$は[2.0, 4.0] [円]とする
+ 要求企業$|\boldsymbol{I}|=10$
  + 各企業$|\boldsymbol{N}|$=3個の入札を作成
  + R種類のリソースを各 [0, 200] [Ts]要求する
  + 予算$v_{j,n}$は合計要求時間と重み[3.0, 5.0]の積[円]とする

### 1提供企業の虚偽申告率の変更

本節では1提供企業の虚偽申告率を変更する実験を行う．手法Iは耐戦略性を満たせず，虚偽申告により利益を高められることを確認する．ここでの虚偽申告率とは，コストにある割合分上乗して入札値として申告するとした際の，その割合のことを指す．例えば，コストが10円，虚偽申告率が10%の場合は11円と入札する．コストをふっかけることで利益を上げようとする企業を想定している．

以下に本実験における実験条件を示す．

+ 1提供企業の虚偽申告率:0%，10%，20%，30%
  + 虚偽申告が0%のときは正直にコストを申告する．
+ 試行回数:1回

#### 実験結果

[@tbl:m1-1-total-profit]-[@tbl:m1-1-false-requester-total-profit]は，それぞれ総利益，総提供企業利益，総要求企業利益，虚偽申告を行った1提供企業の利益を示す．

| False reporting rate | 0%      | 10%     | 20%     | 30%     |
| -------------------- | ------- | ------- | ------- | ------- |
| Total Profit         | 9175.28 | 9175.22 | 9175.22 | 9049.41 |

: Total Profit  in Method 1: A provider report false cost {#tbl:m1-1-total-profit}

| False reporting rate   | 0%      | 10%     | 20%     | 30%     |
| ---------------------- | ------- | ------- | ------- | ------- |
| Providers Total Profit | 4587.64 | 4613.49 | 4639.38 | 4524.70 |

: Providers Total Profit: in Method 1: A provider report false cost {#tbl:m1-1-providers-total-profit}

| False reporting rate    | 0%      | 10%     | 20%     | 30%     |
| ----------------------- | ------- | ------- | ------- | ------- |
| Requesters Total Profit | 4587.64 | 4561.72 | 4535.84 | 4524.70 |

: Requesters Total Profit in Method 1:A  provider report false cost  {#tbl:m1-1-requesters-total-profit}

| False reporting rate | 0%     | 10%    | 20%    | 30%  |
| -------------------- | ------ | ------ | ------ | ---- |
| Requester Profit     | 112.47 | 139.30 | 227.77 | 0    |

: False reporting Requester Profit in Method 1: A provider report false cost {#tbl:m1-1-false-requester-total-profit}

#### 考察

[@tbl:m1-1-false-requester-total-profit]において，虚偽申告率が20%まで利益が増加している．20%から30%にかけて利益が下がったのは，申告したコストが高くなり，リソースの配分が変わり，オークションにおいて勝者となることができなくなってしまったからだと考える．よってオークションの敗者になるまで，虚偽申告を行うことで利益を上げることが可能なことがわかり，耐戦略性を満たすことができないことが確認できる．また[@tbl:m1-1-total-profit]の虚偽申告率0から20%総利益があまり変化がないにも関わらず，[@tbl:m1-1-providers-total-profit]の総提供企業利益が増加し，[@tbl:m1-1-requesters-total-profit]の総要求企業利益が減少している．従って虚偽申告により，利益が提供企業に移転していることが確認できる．

### 1要求企業の虚偽申告率の変更

本節では1要求企業の虚偽申告率を変更する実験を行う．手法Iは耐戦略性を満たせず，虚偽申告により利益を高められることを確認する．また虚偽申告による影響も確認する．ここでの虚偽申告率は，入札の予算に対して減額する割合とする．例えば，虚偽申告率が10%のときは，予算が1000円の入札を900円と申告する．予算を過少申告することで，より安い価格でリソースを入手しようとする企業を想定する．

以下に本実験における実験条件を示す．

+ 1要求企業の虚偽申告率:0%，10%，20%，30%
  + 虚偽申告が0%のときは正直に予算を申告する．
+ 試行回数:1回

#### 結果

[@tbl:m1-2-total-profit]-[@tbl:m1-2-false-requester-profit]は，それぞれ総利益，総提供企業利益，総要求企業利益，虚偽申告を行った1要求企業の利益を示す．

| False reporting rate | 0%      | 10%     | 20%     | 30%     |
| -------------------- | ------- | ------- | ------- | ------- |
| Total Profit         | 9175.28 | 9175.28 | 9175.28 | 9175.28 |
: Total Profit  in Method 1: A requester report false budget {#tbl:m1-2-total-profit}

| False reporting rate   | 0%      | 10%     | 20%     | 30%     |
| ---------------------- | ------- | ------- | ------- | ------- |
| Providers Total Profit | 4587.64 | 4492.88 | 4398.11 | 4303.35 |

: Total Providers Profit  in Method 1: A requester report false budget {#tbl:m1-2-providers-total-profit}

| False reporting rate     | 0%      | 10%     | 20%     | 30%     |
| ------------------------ | ------- | ------- | ------- | ------- |
| Requesters Total  Profit | 4587.64 | 4682.41 | 4777.17 | 4871.93 |

: Total Providers Profit  in Method 1: A requester report false budget {#tbl:m1-2-requesters-total-profit}

| False reporting rate | 0%       | 10%      | 20%     | 30%      |
| -------------------- | -------- | -------- | ------- | -------- |
| Provider Profit      | 412.8505 | 457.5463 | 573.548 | 694.5275 |

: Total Providers Profit  in Method 1: A requester report false budget {#tbl:m1-2-false-requester-profit}

#### 考察

[@tbl:m1-2-false-requester-profit]より，申告率が増加するごとに利益が上がっていることが確認できる．よって手法Iが耐戦略性を満たせないことが確認できる．また[@tbl:m1-2-total-profit]において，総利益は変化がないが，[@tbl:m1-2-providers-total-profit]の提供企業利益は減少し，[@tbl:m1-2-requesters-total-profit]の総要求企業利益は増加している，これより提供側に利益が移転していることがわかる．

### 提供側が申告するコストの幅の変更 

本節では，提供側が申告するコストの幅を変更する実験を行う．幅を変更したそれぞれの場合において，ある提供企業が虚偽申告率を変更した場合に利益がどのように変化するかを確認する．各企業が申告するコストの幅が狭くなる，つまり同じようなコストの企業が集まると正直な申告が支配戦略になることを確認する．

+ コストを発生させる乱数の幅:2.5，2.0，1.5，1.0
  + コストを[1.75,4.25]，[2.0,4.0]，[2.25,3.75]，[2.5,3.5]で生成する．
+ 試行回数:10回

#### 実験結果

[@tbl:m1-3-2.5-false-provider-profit]-[@tbl:m1-3-1.0-false-provider-profit]は，それぞれコストの幅が2.5，2.0，1.5，1.0のときの虚偽申告提供企業の利益を表す．

| False reporting rate | 0%     | 10%    | 20%    | 30%    |
| -------------------- | ------ | ------ | ------ | ------ |
| AVE.                 | 173.07 | 194.66 | 184.64 | 143.98 |
| S.D.                 | 97.60  | 104.51 | 124.04 | 124.09 |

:  False reporting Provider Profit in Method 1:cost range=2.5 {#tbl:m1-3-2.5-false-provider-profit}

| False reporting rate | 0%     | 10%    | 20%    | 30%    |
| -------------------- | ------ | ------ | ------ | ------ |
| AVE.                 | 189.28 | 203.53 | 201.61 | 172.39 |
| S.D.                 | 105.08 | 103.87 | 79.45  | 88.61  |

:False reporting Provider Profit in Method 1:cost range=2.0 {#tbl:m1-3-2.0-false-provider-profit}

| False reporting rate | 0%     | 10%    | 20%    | 30%   |
| -------------------- | ------ | ------ | ------ | ----- |
| AVE.                 | 208.02 | 209.98 | 158.56 | 84.83 |
| S.D.                 | 89.21  | 116.48 | 125.35 | 88.38 |

:False reporting Provider Profit in Method 1:cost range=1.5 {#tbl:m1-3-1.5-false-provider-profit}

| False reporting rate | 0%     | 10%    | 20%   | 30%   |
| -------------------- | ------ | ------ | ----- | ----- |
| AVE.                 | 165.03 | 147.50 | 75.35 | 60.80 |
| S.D.                 | 89.19  | 90.53  | 94.68 | 69.13 |

:False reporting Provider Profit in Method 1:cost range=1.0 {#tbl:m1-3-1.0-false-provider-profit}

[@tbl:m1-3-2.5-false-provider-profit]-[@tbl:m1-3-1.0-false-provider-profit]の結果から，正直にコストを申告した場合に対しする虚偽申告を行った際の利益の変化に直したものを[@tbl:m1-3-profit-rate]に示す．

| False reporting rate | 10%    | 20%    | 30%    |
| -------------------- | ------ | ------ | ------ |
| cost range=2.5       | 12.5%  | 6.7%   | -16.8% |
| cost range=2.0       | 7.5%   | 6.5%   | -6.1%  |
| cost range=1.5       | 0.9%   | -23.8% | -59.2% |
| cost range=1.0       | -10.6% | -54.3% | -63.2% |

:Ratio of increased profit to the Profits for reporting truthful cost  {#tbl:m1-3-profit-rate}

例えばコストの幅が2.5のとき，虚偽申告10%を行うと，正直な申告より利益が12.5%と増加していることを表している．

#### 考察

[@tbl:m1-3-profit-rate]より，コストの幅が2.5のときは虚偽申告率が20%のときまで利益が増加しているが，コストの幅が1.5のときは利益が減少している．またコストの幅が1.0のときは，10%の虚偽申告でも利益が減少しており，コストの幅が狭くなるごとに正直な申告が支配戦略に近づくことがわかる．

### 要求企業が申告する予算の幅の変更 

本節では，提供側が申告する予算の幅を変更する実験を行う．幅を変更したそれぞれの場合において，ある要求企業が虚偽申告率を変更した場合に利益がどのように変化するかを確認する．前節と同様に，同じような予算の企業が集まると正直な申告が支配戦略になることを確認する．

+ コストを発生させる乱数の幅:2.5，2.0，1.5，1.0
  + コストを[2.75,5.25]，[3.0,5.0]，[3.25,4.75]，[3.5,4.5]で生成する．
+ 試行回数:10回

#### 実験結果

[@tbl:m1-4-2.5-requester-profit]-[@tbl:m1-4-1.0-requester-profit]は，それぞれ予算の幅が2.5，2.0，1.5，1.0のときの虚偽申告要求企業の利益を表す．

| False reporting rate | 0%     | 10%    | 20%    | 30%    |
| -------------------- | ------ | ------ | ------ | ------ |
| AVE.                 | 408.94 | 448.28 | 453.20 | 230.52 |
| S.D.                 | 293.34 | 376.17 | 477.45 | 466.90 |

:False reporting Requester Profit in Method 1:budget range=2.5 {#tbl:m1-4-2.5-requester-profit}

| False reporting rate | 0%     | 10%    | 20%    | 30%    |
| -------------------- | ------ | ------ | ------ | ------ |
| AVE.                 | 355.23 | 398.52 | 291.70 | 171.81 |
| S.D.                 | 244.25 | 354.22 | 366.58 | 351.41 |

:False reporting Requester Profit in Method 1:budget range=2.0 {#tbl:m1-4-2.0-requester-profit}

| False  reporting rate | 0%     | 10%     | 20%    | 30%  |
| --------------------- | ------ | ------- | ------ | ---- |
| AVE.                  | 329.75 | 249.590 | 123.20 | 0    |
| S.D.                  | 231.12 | 256.35  | 246.64 | 0    |

:False reporting Requester Profit in Method 1:budget range=1.5 {#tbl:m1-4–1.5-requester-profit}

| False  reporting rate | 0%     | 10%    | 20%    | 30%  |
| --------------------- | ------ | ------ | ------ | ---- |
| AVE.                  | 433.00 | 306.62 | 220.93 | 0    |
| S.D.                  | 159.78 | 317.43 | 352.38 | 0    |

:False reporting Requester Profit in Method 1:budget range=1.0 {#tbl:m1-4-1.0-requester-profit}

[@tbl:m1-4-2.5-requester-profit]-[@tbl:m1-4-1.0-requester-profit]から，正直にコストを申告した場合に対する虚偽申告を行った際の利益の変化の割合に直したものを[@tbl:m1-3-profit-rate]に示す．

| False reporting rate | 10.0%  | 20.0%  | 30.0%   |
| -------------------- | ------ | ------ | ------- |
| range=2.5            | 9.6%   | 10.8%  | -49.1%  |
| range=2.0            | 12.2%  | -17.9% | -41.1%  |
| range=1.5            | -24.3% | -62.6% | -100.0% |
| range=1.0            | -29.2% | -49.0% | -100.0% |

:Ratio of increased profit to the Profits for reporting truthful budget  {#tbl:m1-4-profit-rate}

#### 考察

[@tbl:m1-4-profit-rate]より，予算の幅が2.5，2.0の時は虚偽申告により利益を高めることができていることがわかる．しかし予算の幅が1.5より狭くなると虚偽申告によって利益を高めることができなくなっている．これにより予算の幅狭くなるほど，正直な申告が支配戦略に近づくことが確認できる．



