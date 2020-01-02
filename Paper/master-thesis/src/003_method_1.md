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

### リソース配分の決定

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

決定変数は$x_{i,r,j,n}$と$y_{j,n}$である．$x_{i,r,j,n}$は企業$i$と企業$j$がリソース$r$を取引する量を表す整数変数であり，$y_{j,n}$は企業$j$の入札$n$が選ばれるとき1，選ばれない時0となる変数である．$\eqref{pij-obj}$は目的関数であり，総利益最大化を表す．$\eqref{pij-subto-cap}$は提供企業のリソースの容量制約を表す．$\eqref{pij-subto-bundle}$は組合せ入札に関する制約であり，要求企業$j$の入札$n$が選ばれないときは，どのリソース要求も満たさないための制約，要求企業$j$の入札$n$が選ばれるときは，その入札のリソース要求を全て満たすための制約である．$\eqref{pij-subto-1winner}$は勝者となる要求企業の入札は高々1つとする制約である．この組合せ最適化問題を解くことで勝者となる要求企業の入札と，それに対する提供リソースの配分を決定する．また，問題$P(\boldsymbol{I},\boldsymbol{J})$の最適解は総利益が最大化されているので，パレート効率な状態となっている．

### 取引価格決定

前節のリソース配分を元に取引価格$trade_{i,r,j,n}$を決定する．手法Iの取引価格はお互いの評価値の平均の価格から決定され，以下の式で表される．
$$
\begin{align}
trade_{i,r,j,n}=&\frac{c_{i,r}+\{v_{j,n}×\frac{TR_{j,n,r}}{sumTR_{j,n}}/TR_{j,n,r}\}}{2}\times x_{i,r,j,n} \label{1-trade}\\
sumTR_{j,n} = &\sum_{r  \in R} TR_{j,n,r} \label{sumtime}
\end{align}
$$
$sumTR_{j,n,r}$は要求企業$j$が入札$n$におけるリソース要求時間の合計である．よって$\eqref{1-trade}$の$v_{j,n}×\frac{TR_{j,n,r}}{sumTR_{j,n}}/TR_{j,n,r}$はリソース$r$の1[Ts]分の予算を表す．従って$\eqref{1-trade}$はお互いの評価値の平均で取引を行っていることとなる．

### 特徴

手法Iは問題$P(\boldsymbol{I},\boldsymbol{J})$の最適解に従いリソースの配分を決めるので，パレート効率な配分が実現される．しかし取引価格は，お互いの評価値の平均をとるので，この価格は提供企業，要求企業ともにcritical priceとなっておらず耐戦略性を満たすことはできない．

## 特性評価

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

本節では1提供企業の虚偽申告率を変更する実験を行う．手法Iは耐戦略性を満たせず，真の評価値の申告が支配戦略でないことを確認する．虚偽申告率とは，コストにある割合分上乗して入札値として申告するとした際の，その割合のことを指す．例えば，コストが10円，虚偽申告率が10%の場合は11円と入札する．

以下に本実験における実験条件を示す．

+ 1提供企業の虚偽申告率:0%，10%，20%，30%
  + 虚偽申告が0%のときは正直にコストを申告する．
+ 試行回数:1回

#### 実験結果

[@tbl:m1-1-total-profit]~[@tbl:m1-1-false-requester-total-profit]はそれぞれ総利益，総提供企業利益，総要求企業利益，虚偽申告を行った1企業の利益を示す．

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

[@tbl:m1-1-false-requester-total-profit]において，虚偽申告率が20%まで利益が増加している．20%から30%にかけて利益が下がったのは，申告したコストが高くなり，リソースの配分が変わり，オークションにおいて勝者となることができなくなってしまったからだと考える．よってオークションの敗者になるまで，虚偽申告を行うことで利益を上げることが可能なことがわかる．また[@tbl:m1-1-total-profit]の虚偽申告率0から20%総利益があまり変化がないにも関わらず，[@tbl:m1-1-providers-total-profit]において総提供企業利益が増加し，[@tbl:m1-1-requesters-total-profit]において総提供企業総要求企業利益が減少している．従って虚偽申告により，利益が提供企業に移転していることが確認できる．

### 1要求企業の虚偽申告率の変更

### 虚偽申告を行う提供企業数の変更 

### 虚偽申告を行う要求企業数の変更 

### 提供側が申告するコストの幅の変更 

### 要求企業が申告する予算の幅の変更 

