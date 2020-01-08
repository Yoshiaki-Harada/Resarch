# 手法II:耐戦略性を満たす手法

## アルゴリズム

手法I:パレート効率性を満たす手法のアルゴリズムについて説明する．

### 概要

2章で述べたように，ダブルオークション環境において，オークション主催者を含めた個人合理性，パレート効率性，耐戦略性の全ての性質を満たすオークションは存在しない．もし耐戦略性を満たすVCGオークションをダブルオークション 環境下に適用すると，オークション主催者の個人合理性が満たせなくなってしまう．つまり，売り手の報酬の合計が買い手の支払いの合計を上回ってしまう．

このオークション主催者の個人合理性を満たせない欠点を克服する為に，提案されたのがPadding Methodと呼ばれる方法である．Padding Methodとは，仮想的な買い手を用意し，均衡価格を引き上げることで買い手の支払い額を高めることでオークション主催者の個人合理性を満たすことを可能にした方法である．この考え方を適用したのが手法II:耐戦略性を満たす手法である．こうすることで，耐戦略性を満たすことはできるが，仮想的な買い手が勝者となった財は実際には取引が行われないので，その分パレート効率性を犠牲にしてしまう．

説明に用いる記号を以下に定義する．

+ $i$:リソース提供企業($i \in \boldsymbol{I}$)
+ $j$: リソース要求企業($j \in \boldsymbol{J}$)
+ $r$:オークションにかけられるリソース($r \in \boldsymbol{R}$)
+ $c_{i,r}$:提供企業$i$が提供するリソース$r$のコスト
+ $TP_{i,r}$:提供企業$i$がリソース$r$を提供する時間
+ $n$: 要求企業$j$の入札($n \in \boldsymbol{N}$)
+ $v_{j,n}$:要求企業$j$の$n$番目の入札の評価値
+ $TR_{j,n.r}$:要求企業$j$の$n$番目の入札においてリソース$r$を要求する時間
+ $\boldsymbol{Q}$:仮想的な買い手
+ $P(\boldsymbol{I},\boldsymbol{J})$:提供企業の集合が$\boldsymbol{J}$，要求企業の集合$\boldsymbol{J}$であるときの勝者決定問題
+ $P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$:提供企業の集合が$\boldsymbol{J}$，要求企業の集合$\boldsymbol{J}$，仮想的な買い手$\boldsymbol{Q}$を考慮した場合の勝者決定問題
+ $V(\boldsymbol{I},\boldsymbol{J})$:問題$P(\boldsymbol{I},\boldsymbol{J})$の目的関数値
+ $V(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$:問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$の目的関数値
+ $\boldsymbol{\tilde{J}}$:$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において勝者となった要求企業の集合
+ $pay_j$:要求企業$j$が勝者となった入札に対する支払い
+ $revenue_{i,r}$:提供企業$i$がリソース$r$を提供することによって得られる報酬

以下に本手法のアルゴリズムの流れを示す．

1. 入札作成
   + リソース提供企業はオークション主催者に対して，入札を作成する．
   + リソース要求企業はオークション主催者に対して，入札を作成する．
2. 提供側と要求側の入札を元にした勝者決定問題$P(\boldsymbol{I},\boldsymbol{J})$に対し，仮想的な買い手$\boldsymbol{Q}$を考慮した問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$を定義する．
3. $P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$の最適解を求め，勝者となる入札を決める
4. $P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において勝者となった要求企業に対して支払い$pay_j$を決定する
5. $P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において勝者となった要求企業の集合を$\boldsymbol{\tilde{J}}$とし，問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$を定義する
6. $P(\boldsymbol{I},\boldsymbol{\tilde{J}})$の最適解を求め，提供リソースの取引量を決める
7.  $P(\boldsymbol{I},\boldsymbol{\tilde{J}})$において勝者となったリソース提供企業に対して収入$revenue_{j,r}$を決定する

1が2章で説明をした入札を作成する部分，2-4が要求側の勝者と取引価格決定を決定する部分であり，5-7が提供企業の勝者と支払い価格を決める部分である．

### 要求側の勝者と支払いの決定

2-4の要求側の勝者と支払いの決定について説明する．まず勝者決定問題$P(\boldsymbol{I},\boldsymbol{J})$を作成する．これは\ref{method1-resorce}の定式化と同じである．以下に再掲する
$$
\begin{align}  
{\rm max}\quad  V(\boldsymbol{I},\boldsymbol{J})=&\sum_{j\in \boldsymbol{J}}\sum_{n\in\boldsymbol{N}}v_{j} \times y_{j,n} - \sum_{i\in\boldsymbol{I}}\sum_{r\in\boldsymbol{R}}\sum_{j\in\boldsymbol{J}}\sum_{n\in\boldsymbol{N}}c_{i,r} \times x_{i,r,j,n} \label{2-pij-obj}\\  
{\rm s.t.} \quad &\sum_{j\in \boldsymbol{J}}\sum_{n\in\boldsymbol{N}}x_{i,r,j,n} \leq TP_{i,r} \quad (\forall i, \forall r)\label{2-subto-time}\\
&\begin{cases} x_{i,r,j,n} = 0 \quad (\forall i, \forall r)&({\rm if} \ y_{j,n}=0) \\  
\sum_{j \in \boldsymbol{J}}\sum_{n\in\boldsymbol{N}} TR_{j,n,r} \times x_{i,r,j,n} = TR_{j,n,r}    \quad  (\forall i, \forall r)&({\rm if} \ y_{j,n}=1) \end{cases}\\ 
&\sum_{n\in \boldsymbol{N}}y_{j,n}  \leq 1 \quad (\forall j)\\   &x_{i,r,j,n} \in \boldsymbol{Z}\\    
&y_{j,n} \in {0,1}
\end{align}
$$
問題$P(\boldsymbol{I},\boldsymbol{J})$に対して，仮想的な買い手$\boldsymbol{Q}$を考慮した問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$を作成する(2)．その際に考慮する仮想的な買い手$\boldsymbol{Q}$について説明する．

#### 仮想的な買い手$\boldsymbol{Q}$

提供企業仮想的な買い手$\boldsymbol{Q}$は$Q=\{Q_1,Q_2,Q_r…Q_{|\boldsymbol{R}|}\}$で表現される．$Q_r$は$\boldsymbol{Q}$が要求するリソース$r$を要求する時間である．文献に従い以下のように定める．


$$
\begin{align}
Q_r^I&=\max \{TP_{i,r} |i \in \boldsymbol{I}\} \label{max-provider}\\
Q_r^J&=\max \{TR_{j,n,r} |j \in \boldsymbol{J},n \in \boldsymbol{N}\} \label{max-requester}\\
Q_r&=\max \{Q_r^J,Q_r^I\} \label{max-time}
\end{align}
$$
$\eqref{max-provider}$は1提供企業が提供するリソース$r$の最大提供時間を表す．$\eqref{max-requester}$は1要求企業が要求するリソース$r$の最大要求時間を表す．よって$\eqref{max-time}$はリソース$r$の1企業が提供または要求する最大の時間を表す．仮想的な買い手$\boldsymbol{Q}$はこのように定まり，予算が0であるが，満たさなければならない1要求企業として扱う．そうすることで均衡価格を引き上げることができる．この$\boldsymbol{Q}$を考慮した問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$を定義する．

#### 問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$の定式化

問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$の定式化を以下に示す．
$$
\begin{align}
  {\rm max} \quad &V(\boldsymbol{I},\boldsymbol{J} ,\boldsymbol{Q})=\sum_{j\in \boldsymbol{J}}\sum_{n\in\boldsymbol{N}}v_{j} \times y_{j,n} - \sum_{i\in\boldsymbol{I}}\sum_{r\in\boldsymbol{R}}\sum_{j\in\boldsymbol{J}}\sum_{n\in\boldsymbol{N}}c_{i,r} \times x_{i,r,j,n} - \sum_{i\in\boldsymbol{I}}\sum_{r\in\boldsymbol{R}}c_{i,r} \times q_{i,r} \label{pijq-obj}\\ 
  {\rm s.t.} \quad &\sum_{j\in \boldsymbol{J}}\sum_{n\in\boldsymbol{N}}x_{i,r,j,n}
  +\sum_{i\in\boldsymbol{I}}\sum_{r\in\boldsymbol{R}} q_{i,r}\leq TP_{i,r} \quad (\forall i, \forall r) \label{pijq-subto-time}\\
  &\begin{cases}
    x_{i,r,j,n} = 0 \quad (\forall i,\forall r) \quad &({\rm if} \ y_{j,n}=0) \\
    \sum_{j \in \boldsymbol{J}}\sum_{n \in \boldsymbol{N}} x_{i,r,j,n} = TR_{j,n,r} \quad (\forall i, \forall r) 
    \quad  &({\rm if} \ y_{j,n}=1) 
  \end{cases}
  \\
  &\sum_{i\in\boldsymbol{I}} q_{i,r}=Q_{r} \quad (\forall r) \label{subto-q}\\
  &\sum_{n \in \boldsymbol{N}}y_{j,n}  \leq 1 \quad (\forall j) \\
  &x_{i,r,j,n}\in {0,1} (\forall i,\forall r\forall j,\forall n )\\
  &y_{j,n} \in \boldsymbol{Z} (\forall j,\forall n ) \\
  &q_{i,r} \in \boldsymbol{Z} (\forall i,\forall r )
\end{align}
$$
問題$P(\boldsymbol{I},\boldsymbol{J})$と問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$の異なる部分について説明をする．

まずどの提供企業が$\boldsymbol {Q}$にリソース決定変数$q_{i,r}$を用意する．そして$\boldsymbol{Q}$の要求を満たすための制約$\eqref{subto-q}$が追加される．

それによって$P(\boldsymbol{I},\boldsymbol{J})$の提供企業の容量制約が$\eqref{2-subto-time}$から$\eqref{pijq-subto-time}$になる．そして$\boldsymbol{Q}$を満たした分のコストが目的関数に考慮されることで，$P(\boldsymbol{I},\boldsymbol{J})$の$\eqref{2-pij-obj}$が$\eqref{pijq-obj}$になる．

この問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$を解くことで，まず勝者となる要求企業の入札を決定する(3)．

#### 支払いの決定

4の支払い価格の決定について説明する．仮想的な買い手$\boldsymbol{Q}$を考慮した状態で，\ref{VCG}において説明したVCGオークションと同様の方法で価格を決定する．勝者となった提供企業$j$の入札$n$の支払いは以下の式で決定される．
$$
\begin{align}
pay_j=-\{V(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})-v_{j,n}\}+V(\boldsymbol{I},\boldsymbol{J}\backslash\{j\},\boldsymbol{Q}) \label{m2-pay}
\end{align}
$$
$V(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})-v_{j,n}$は目的関数値から勝者となった要求企業$j$の入札$n$の予算を除いた値となっている．さらに，$V(\boldsymbol{I},\boldsymbol{J}\backslash\{j\},\boldsymbol{Q})$は要求企業$j$を除いた問題の目的関数値となっている．よって$\eqref{m2-pay}$は，VCGオークションと同様に，この値は要求企業$j$の予算に依存しておらず，問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において勝者となる為の最小の価格となっている．したがって提供企業側の耐戦略性を満たす．

### 提供側の勝者と報酬の決定

### 特徴

+ ***証明かな．．***

## 特性評価

### 提供企業数の変更 

### 要求企業数の変更 

### 1提供企業の虚偽申告率の変更 

### 1要求企業の虚偽申告率の変更 

### 提供側が申告するコストの幅の変更 

### 要求企業が申告する予算の幅の変更 