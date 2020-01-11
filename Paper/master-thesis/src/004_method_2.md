# 手法II:耐戦略性を満たす手法

## アルゴリズム

手法I:パレート効率性を満たす手法のアルゴリズムについて説明する．

### 概要

2章で述べたように，ダブルオークション環境において，オークション主催者を含めた個人合理性，パレート効率性，耐戦略性の全ての性質を満たすオークションは存在しない．もし耐戦略性を満たすVCGオークションをダブルオークション 環境下に適用すると，オークション主催者の個人合理性が満たせなくなってしまう．つまり，売り手の報酬の合計が買い手の支払いの合計を上回ってしまう．

このオークション主催者の個人合理性を満たせない欠点を克服する為に，提案されたのがPadding Methodと呼ばれる方法である\cite{Chu2009}．Padding Methodとは，仮想的な買い手を用意し，均衡価格を引き上げることで買い手の支払い額を高めることでオークション主催者の個人合理性を満たすことを可能にした方法である．この考え方を適用したのが手法II:耐戦略性を満たす手法である．こうすることで，耐戦略性を満たすことはできるが，仮想的な買い手が勝者となった財は実際には取引が行われないので，その分パレート効率性を犠牲にしてしまう．

説明に用いる記号を以下に定義する．

+ $i$:リソース提供企業($i \in \boldsymbol{I}$)
+ $j$: リソース要求企業($j \in \boldsymbol{J}$)
+ $r$:オークションにかけられるリソース($r \in \boldsymbol{R}$)
+ $c_{i,r}$:提供企業$i$が提供するリソース$r$のコスト
+ $TP_{i,r}$:提供企業$i$がリソース$r$を提供する時間
+ $n$: 要求企業$j$の$n$番目の入札($n \in \boldsymbol{N}$)
+ $v_{j,n}$:要求企業$j$の$n$番目の入札の評価値
+ $TR_{j,n.r}$:要求企業$j$の$n$番目の入札においてリソース$r$を要求する時間
+ $\boldsymbol{Q}$:仮想的な買い手
+ $P(\boldsymbol{I},\boldsymbol{J})$:提供企業の集合が$\boldsymbol{J}$，要求企業の集合$\boldsymbol{J}$であるときの勝者決定問題
+ $V(\boldsymbol{I},\boldsymbol{J})$:問題$P(\boldsymbol{I},\boldsymbol{J})$の目的関数値
+ $P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$:提供企業の集合が$\boldsymbol{J}$，要求企業の集合$\boldsymbol{J}$，仮想的な買い手$\boldsymbol{Q}$を考慮した場合の勝者決定問題
+ $V(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$:問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$の目的関数値
+ $\boldsymbol{\tilde{J}}$:$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において勝者となった要求企業の集合
+ $pay_j$:要求企業$j$が勝者となった入札に対する支払い
+ $revenue_{i,r}$:提供企業$i$がリソース$r$を提供することによって得られる報酬
+ $p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$:問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において売手$i$がリソース$r$を提供する為の最大のコスト

以下に本手法のアルゴリズムの流れを示す．

1. 入札作成
   + リソース提供企業はオークション主催者に対して，入札を作成する．
   + リソース要求企業はオークション主催者に対して，入札を作成する．
2. 提供側と要求側の入札を元にした勝者決定問題$P(\boldsymbol{I},\boldsymbol{J})$に対し，仮想的な買い手$\boldsymbol{Q}$を考慮した問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$を定義する．
3. $P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$の最適解を求め，勝者となる入札を決める
4. $P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において勝者となった要求企業に対して支払い$pay_j$を決定する
5. $P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において勝者となった要求企業の集合を$\boldsymbol{\tilde{J}}$とし，また敗者となった入札の決定変数を0とした問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$を定義する
6. $P(\boldsymbol{I},\boldsymbol{\tilde{J}})$の最適解を求め，提供リソースの取引量を決める
7.  $P(\boldsymbol{I},\boldsymbol{\tilde{J}})$において勝者となったリソース提供企業に対して収入$revenue_{j,r}$を決定する

1が2章で説明をした入札を作成する部分，2-4が要求側の勝者と取引価格決定を決定する部分であり，5-7が提供企業の勝者と支払い価格を決める部分である．

### 要求側の勝者と支払いの決定

2-4の要求側の勝者と支払いの決定について説明する．まず勝者決定問題$P(\boldsymbol{I},\boldsymbol{J})$を作成する．これは\secref{method1-resorce}の定式化と同じである．以下に再掲する
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
$\eqref{max-provider}$は1提供企業が提供するリソース$r$の最大提供時間を表す．$\eqref{max-requester}$は1要求企業が要求するリソース$r$の最大要求時間を表す．よって$\eqref{max-time}$はリソース$r$の1企業が提供または要求する最大の時間を表す．仮想的な買い手$\boldsymbol{Q}$はこのように定まり，予算が0であるが満たさなければならない1要求企業として扱う．そうすることで均衡価格を引き上げることができる．この$\boldsymbol{Q}$を考慮した問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$を定義する．

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
$V(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})-v_{j,n}$は目的関数値から勝者となった要求企業$j$の入札$n$の予算を除いた値となっている．さらに，$V(\boldsymbol{I},\boldsymbol{J}\backslash\{j\},\boldsymbol{Q})$は要求企業$j$を除いた問題の目的関数値となっている．よって$\eqref{m2-pay}$は，VCGオークションと同様に，この値は要求企業$j$の予算に依存しておらず，問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において勝者となる為の最小の価格となっている．したがって提供企業側の耐戦略性を満たす．また$\boldsymbol{Q}$によって支払いが引き上がるのは，$\boldsymbol{Q}$が予算0であるが満たさなければならないので，コストの安いリソースが$\boldsymbol{Q}$に消費されてしまい，残りの要求企業は価格の高いリソースが割当てられてしまうからと捉えることができる．

### 提供側の勝者と報酬の決定

5-7の提供企業の勝者と支払い価格を決める部分について説明する．

#### 問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$の定式化

勝者となった要求企業の集合$\boldsymbol{\tilde{J}}$を定義し，また敗者となった入札の決定変数を0とした新たな問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$を定義する(4)．
$$
\begin{align}  
{\rm max}\quad V(\boldsymbol{I},\boldsymbol{\tilde{J}})=&\sum_{j\in \boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}}v_{j} \times y_{j,n} - \sum_{i\in\boldsymbol{I}}\sum_{r\in\boldsymbol{R}}\sum_{j\in\boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}}c_{i,r} \times x_{i,r,j,n}\\  
{\rm s.t.} \quad &\sum_{j\in \boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}}x_{i,r,j,n} \leq TP_{i,r} \quad (\forall i, \forall r)\\
&\begin{cases} x_{i,r,j,n} = 0 \quad (\forall i, \forall r)&({\rm if} \ y_{j,n}=0) \\  
\sum_{j \in \boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}} TR_{j,n,r} \times x_{i,r,j,n} = TR_{j,n,r}    \quad  (\forall i, \forall r)&({\rm if} \ y_{j,n}=1) \end{cases}\\ 
&\sum_{n\in \boldsymbol{N}}y_{j,n}  \leq 1 \quad (\forall j)\\  
&y_{j,n}=0 \quad ({\rm if} \ y_{j,n}=0 \ {\rm in}  \ P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q}))\label{subto-loser}\\
&x_{i,r,j,n} \in \boldsymbol{Z}\\    
&y_{j,n} \in {0,1}
\end{align}
$$
問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$は問題$P(\boldsymbol{I},\boldsymbol{J})$の$\boldsymbol{J}$を$\boldsymbol{\tilde{J}}$で置き換え，制約式$\eqref{subto-loser}$を追加したものとなっている．問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$の最適解を求めることで，提供企業の勝者，つまり各提供企業が提供するリソースの時間を決定する．よって問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$で敗者となった入札は，$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$で選ばれることはないので，仮想的な買い手$\boldsymbol{Q}$の分のリソースは取引は行われないが，利用されることもなくなってしまう．また提供企業数が増加すると，この仮想的な買い手$\boldsymbol{Q}$によって利用されないリソースの割合が全体の提供リソースに対して減少するので，小さくなると考えられる．

#### 報酬の決定\label{sec:m2-reward}

問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$の解を元に，売り手$i$がリソース$r$を$\sum_{j\in\boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}}x_{i,r,j,n}$ [Ts]提供することで得られる報酬$revenue_{i,r}$を$\eqref{m2-reward}$で決定する．
$$
\begin{align}
revenue_{i,r}=\sum_{j\in\boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}}c_{i,r} \times x_{i,r,j,n} +V(\boldsymbol{I},\boldsymbol{\tilde{J}})-V(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}})\\-\{V(I|c_{i,r}=p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q}),\tilde{J})-  V(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}})\}
\label{m2-reward}
\end{align}
$$
$\eqref{m2-reward}$のの報酬決定方法が耐戦略性を示すことについて説明する．そこで$\eqref{m2-reward}$の前半部分と後半部分をそれぞれ以下のようにおく．
$$
\begin{align}
revenue_{i,r}’=&\sum_{j\in\boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}}c_{i,r} \times x_{i,r,j,n} +V(\boldsymbol{I},\boldsymbol{\tilde{J}})-V(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}})\\
ex_{i,r}=&V(I|c_{i,r}=p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q}),\tilde{J})-  V(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}})
\end{align}
$$
$revenue_{i,r}’$は問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$において提供企業$i$がリソース$r$を提供できる最大の価格(コストと時間の積)である．$revenue_{i,r}’$は，問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$の売り手側においてVCGメカニズムと同様の価格決定方法で求めている．$revenue_{i,r}’$について[@fig:m2-revenue-1]を用いて説明する．

![revenue-1](/Users/haradayoshiaki/Resarch/Paper/master-thesis/src/img/chapter-4/revenue-1.png){#fig:m2-revenue-1}

[@fig:m2-revenue-1]より，もし$revenue_{i,r}’$より大きくなるように，$c_{i,r}$を申告してしまうと，問題$P(\boldsymbol{I},\boldsymbol{J})$の解は，問題$P(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}})$の解に変わり，提供企業$i$はリソース$r$を提供できなくなってしまう．何故なら$V(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}})$の方が，$revenue_{i,r}’$より大きくなるように$c_{i,r}$を申告した$V(\boldsymbol{I},\boldsymbol{\tilde{J}})$より大きくなるからである．よって$revenue_{i,r}’$は問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$において，提供企業$i$がリソース$r$を提供できる最大の価格(コストと時間の積)である．$revenue_{i,r}’$において，提供企業$i$の評価値が使用されていないことも[@fig:m2-revenue-1]より確認できる．また$revenue_{i,r}’$は，$c_{i,r}$よりコストが高い企業が，安い順に$\sum_{j\in\boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}} x_{i,r,j,n}$[Ts]分リソースを提供したコストの和となっている．その提供企業の集合を$\boldsymbol{I'}$とする．

次に$ex_{i,r}$について説明する．この$p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$は問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において売手$i$がリソース$r$を提供する為の最大のコストである．つまり，問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において提供企業$i$が，リソース$r$を提供できる最大の価格を求め，それを提供時間[Ts]で割ったものである．$p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$は$revenue_{i,r}’$と同様の方法で求めることができる．ここで問題$P(I|c_{i,r}=p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q}),\boldsymbol{J})$と問題$P(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}})$に考えると，それぞれの解は先程定義した提供企業集合$\boldsymbol{I’}$のうち$p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$よりコストが低い企業が提供している部分の解が異なることになる．

よって$u=\sum_{\{i \in  \tilde{I}| c_{i,r}<p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})\}}\sum_{j\in\boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}}x_{i,r,j,n}$とおくと，
$$
\begin{align}
ex_{i,r}
=&V(I|c_{i,r}=p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q}),\tilde{J})-  V(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}}) \\
=&revenue_{i,r}’-p_{i,r} \times u \label{extra}
\end{align}
$$
となる．$\eqref{extra}$は，問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において，$revenue_{i,r}’$から計算されたコストを申告すると，提供企業$i$の勝者となれない時間分のコストを引くことになる．

以上より$\eqref{m2-reward}$は，問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$と問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$の両方で$\sum_{j\in\boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}} x_{i,r,j,n}$[Ts]を提供できる最大の価格を求めている．よって提供企業側においても耐戦略性が成り立つ．

### 特徴

手法II:耐戦略性を満たす手法は，以上で示したように耐戦略性を満たすことができる．しかし，仮想的な買い手$\boldsymbol{Q}$の分のリソースはどの要求企業にも提供できなくなるので無駄になってしまい，パレート効率性を満たすことができない．しかし，提供企業数が増えると仮想的な買い手$\boldsymbol{Q}$によって無駄になってしまうリソースの割合が減少し，パレート効率な状態に近づくと考えられる．また，手法IIは提供企業の支払いの合計と，提供側の報酬の合計が等しくなるとは限らず，提供企業の支払いの合計と提供側の報酬の合計余剰の差である余剰利益が発生する．この利益をクラウドソースドマニュファクチャリング内の利益ではあるので，総利益に加算するものとした．

## 特性評価

本節では手法IIにおいて特性評価を行う．実験条件は\secref{exp-condition}と同様である．

### 提供企業数の変更 

本節では提供企業数をを変更する実験を行う．手法IIは企業数が増加するごとに，パレート効率な状態に近づくことを確認する．手法Iはパレート効率性を満たすので，手法Iの総利益を用いて比較を行う．

以下に本実験における実験条件を示す．

+ 提供企業数$\boldsymbol{I}$:15,20,25,30
+ 試行回数:10回

#### 実験結果

[@tbl:m2-1-pareto-total-profit]にパレート効率な状態の総利益，[@tbl:m2-1-total-profit]に手法IIの総利益を示す．

| Provider  Number | 15      | 20       | 25       | 30       |
| ---------------- | ------- | -------- | -------- | -------- |
| AVE.             | 8007.79 | 10857.46 | 13721.85 | 14706.66 |
| S.D.             | 877.31  | 416.03   | 531.84   | 487.50   |

:Pareto efficiency Total Profit {#tbl:m2-1-pareto-total-profit}

| Provider  Number | 15      | 20      | 25       | 30       |
| ---------------- | ------- | ------- | -------- | -------- |
| AVE.             | 6600.43 | 9457.75 | 12586.24 | 13785.36 |
| S.D.             | 650.81  | 340.77  | 559.70   | 723.15   |

:Total Profit in Method2 {#tbl:m2-1-total-profit}

[@tbl:m2-1-pareto-total-profit]と[@tbl:m2-1-total-profit]より，パレート効率な総利益に対する手法IIの総利益の減少割合を示す[@tbl:m2-1-profit-decreased]を作成する．

| Provider  Number | 15     | 20     | 25    | 30    |
| ---------------- | ------ | ------ | ----- | ----- |
| Decrese Rate     | 17.57% | 12.89% | 8.28% | 6.26% |

:Ratio of decreased Total Profit to the Profits for Pareto efficiency  {#tbl:m2-1-profit-decreased}

#### 考察

[@tbl:m2-1-profit-decreased]より提供企業数が15人のときは，パレート効率な状態の総利益より，17.57%減少してしまっているのに対して，提供企業数が30人のときは，6.25%の減少で留まっている．よって提供企業数が増加すると，仮想的な買い手$\boldsymbol{Q}$によって無駄になってしまうリソースの量が減少し，総利益はパレート効率な状態に近づくことが確認できた．

### 1提供企業の虚偽申告率の変更 

手法IIが耐戦略性を満たすことを確認する為に，1提供企業の虚偽申告率を変更させる実験を行う．また虚偽申告による影響も確認する．

以下に本実験における実験条件を示す．

+ 1提供企業の虚偽申告率:0%，10%，20%，30%
  + 虚偽申告が0%のときは正直にコストを申告する．
+ 試行回数:1回

#### 実験結果

[@tbl:m2-2-total-profit]-[@tbl:m2-2-false-provider-profit]は，それぞれ総利益，総提供企業利益，総要求企業利益，虚偽申告を行った1提供企業の利益を示す．

| False reporting rate | 0%      | 10%     | 20%     | 30%     |
| -------------------- | ------- | ------- | ------- | ------- |
| Total Profit         | 8654.35 | 8651.31 | 8651.31 | 8559.37 |

: Total Profit in Method 2: A provider report false cost {#tbl:m2-2-total-profit}

| False reporting rate | 0%      | 10%     | 20%     | 30%     |
| -------------------- | ------- | ------- | ------- | ------- |
| Total Provier Profit | 3204.51 | 3257.36 | 3328.47 | 3353.17 |

: Total  Provider Profit in Method 2: A provider report false cost {#tbl:m2-2-total-provider-profit}

| False reporting rate   | 0%       | 10%      | 20%      | 30%      |
| ---------------------- | -------- | -------- | -------- | -------- |
| Total Requester Profit | 3126.378 | 3119.517 | 3105.653 | 2995.902 |

: Total  Provider Profit in Method 2: A provider report false cost {#tbl:m2-2-total-requester-profit}

| False  reporting rate | 0%    | 10%   | 20%   | 30%  |
| --------------------- | ----- | ----- | ----- | ---- |
| Provier Profit        | 94.98 | 91.94 | 91.94 | 0.00 |

: False reporting Requester Profit in Method 2: A provider report false cost {#tbl:m2-2-false-provider-profit}

#### 考察

[@tbl:m2-2-false-provider-profit]より，正直な申告を行ったときの利益は94.98で，虚偽申告を行ったときの利益より高いことがわかる．よって耐戦略性を満たすことが確認できる．しかし，[@tbl:m2-2-total-provider-profit]より1提供企業の虚偽申告率が増加することで総提供企業利益が増加していることが確認できる．これは手法IIの報酬決定方法が，他企業の提供企業利益から決定されるので，虚偽申告企業の影響で，他の提供企業の利益を高めてしまっているからである．また虚偽申告率が10%から20%のときに，総利益が変わらないのに，総要求企業利益が変わっている．その理由は，$\eqref{m2-pay}$において，虚偽申告率が10%のときと虚偽申告率が20%の場合において，$V(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$は変化がないが，の$V(\boldsymbol{I},\boldsymbol{J}\backslash\{j\},\boldsymbol{Q})$が虚偽申告の影響で予算が足りない入札が発生することで，変わってしまう場合が発生したからだと考えられる．

### 1要求企業の虚偽申告率の変更 

手法IIが耐戦略性を満たすことを確認する為に，1要求企業の虚偽申告率を変更させる実験を行う．また虚偽申告による影響も確認する．

以下に本実験における実験条件を示す．

+ 1要求企業の虚偽申告率:0%，10%，20%，30%
  + 虚偽申告が0%のときは正直に予算を申告する．
+ 試行回数:1回

#### 実験結果

[@tbl:m2-3-total-profit]-[@tbl:m2-3-requesters-total-profit]は，それぞれ総利益，総提供企業利益，総要求企業利益，虚偽申告を行った1要求企業の利益を示す．

| False reporting rate | 0%      | 10%     | 20%     | 30%     |
| -------------------- | ------- | ------- | ------- | ------- |
| Total Profit         | 8654.35 | 8654.35 | 8654.35 | 8106.60 |

: Total Profit  in Method 2: A requester report false budget {#tbl:m2-3-total-profit}

| False  reporting rate  | 0%      | 10%     | 20%     | 30%     |
| ---------------------- | ------- | ------- | ------- | ------- |
| Total Providers Profit | 3204.51 | 3204.51 | 3204.51 | 2883.22 |

: Total Providers Profit  in Method 1: A requester report false budget {#tbl:m2–3providers-total-profit}

| False  reporting rate   | 0%      | 10%     | 20%     | 30%     |
| ----------------------- | ------- | ------- | ------- | ------- |
| Total Requesters Profit | 3126.38 | 3126.38 | 2832.30 | 2180.29 |

: Total Providers Profit  in Method 1: A requester report false budget {#tbl:m2-3-requesters-total-profit}

| False reporting rate    | 0%     | 10%    | 20%    | 30%  |
| ----------------------- | ------ | ------ | ------ | ---- |
| Total Requesters Profit | 468.09 | 468.09 | 468.09 | 0.00 |

: Total Providers Profit  in Method 1: A requester report false budget {#tbl:m2-3-false-requester-profit}

#### 考察

[@tbl:m2-3-requesters-total-profit]より虚偽申告率が0%から20%までは同じ利益であり，30%のとき利益が0となっており，耐戦略性を満たしていることが確認できた．またこのことから，手法IIの支払いが自身の評価値に依存していないことも確認できる．総利益，総提供企業利益に関して虚偽申告率が0%から20%まで変化がないのは，虚偽申告による問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$と$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$の解に対しては影響がなかったからである．総要求企業利益が虚偽申告率が10%から20%のときに，減少している．この理由は\eqref{m2-pay}において，虚偽申告率が10%から20%になるときの$V(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$の減少幅より，$V(\boldsymbol{I},\boldsymbol{J}\backslash {j},\boldsymbol{Q})$の減少幅の方が小さく，支払い価格が増加してしまう要求企業が存在したからだと考える．

この部分について，要求企業$2$の支払い決定のときの結果を用いて説明する．虚偽申告を行った要求企業を要求企業$1$とする．このとき要求企業$2$の勝者となった入札の予算は2449.17であった．．要求企業$1$の虚偽申告率が10%のときは，$V(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})=4571.30$，$V(\boldsymbol{I},\boldsymbol{J}\backslash{\{1\}},\boldsymbol{Q})=4128.26$となり，問題$P(\boldsymbol{I},\boldsymbol{J}\backslash{\{1\}},\boldsymbol{Q})$の勝者に要求企業$1$の入札が選ばれていた．要求企業$0$の虚偽申告率が20%のときは，$V(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})=4381.77$，$V(\boldsymbol{I},\boldsymbol{J}\backslash{\{1\}},\boldsymbol{Q})=4038.80$となり，問題$P(\boldsymbol{I},\boldsymbol{J}\backslash{\{2\}},\boldsymbol{Q})$において要求企業$1$の入札は敗者となっていた．その結果$V(\boldsymbol{I},\boldsymbol{J}\backslash{\{2\}},\boldsymbol{Q})$の減少分は$V(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$より小さくなり，支払いが要求企業$1$虚偽申告率が10%のときより増加してしまった．$pay_1=2006.13$から$pay_1=2106.21$に増加していた．

### 提供側が申告するコストの幅の変更 

本節では手法IIの価格決定方法の特性を確認する為に，提供側の申告するコストの幅を変更する実験を行う．$\eqref{m2-reward}$より手法IIのある提供企業の報酬は，他の提供企業のコストに依存した式になっているので，他企業のコストの幅が小さくなるごとに，報酬が減少し，1提供企業あたりの利益は減少すると考えられる．またそれによって余剰利益が減少すると考えられるので．その変化についても確認をする．

+ コストを発生させる乱数の幅:2.5，2.0，1.5，1.0
  + コストを[1.75,4.25]，[2.0,4.0]，[2.25,3.75]，[2.5,3.5]で生成する．
+ 試行回数:10回

#### 実験結果

[@tbl:m2-4-total-provider-profit]に総提供企業利益を示し，[@tbl:m2-4-provider-profit]に1企業あたりの利益の平均値を示す．[@tbl:m2-4-surplus-profit]に余剰利益を示す．

| Range | 2.5     | 2.0     | 1.5     | 1.0     |
| ----- | ------- | ------- | ------- | ------- |
| AVE.  | 3101.13 | 2536.34 | 1463.70 | 1456.30 |
| S.D.  | 1152.16 | 450.48  | 301.13  | 391.94  |

:Total Provider Profit in Method 2 {#tbl:m2-4-total-provider-profit}

|      | 2.5    | 2.0    | 1.5   | 1.0   |
| ---- | ------ | ------ | ----- | ----- |
| AVE. | 124.05 | 101.45 | 58.55 | 58.25 |
| S.D. | 46.09  | 18.02  | 12.05 | 15.68 |

:Average Requester Profit in Method 2  {#tbl:m2-4-provider-profit}

|      | 2.5     | 2.0     | 1.5     | 1.0     |
| ---- | ------- | ------- | ------- | ------- |
| AVE. | 2695.97 | 2858.49 | 3018.05 | 3410.49 |
| S.D. | 1094.76 | 919.96  | 859.63  | 904.73  |

:Surplus Profit in Method 2 {#tbl:m2-4-surplus-profit}

#### 考察

[@tbl:m2-4-total-provider-profit]，[@tbl:m2-4-provider-profit]より，コストの幅が狭くなるに連れ，総提供企業利益も提供企業利益の平均値も減少していることが確認できた．手法IIでは，同じようなコストの企業が集まると，提供企業の報酬が低くなり，利益が減少する傾向にある．また[@tbl:m2-4-surplus-profit]より，コストの幅が狭くなると，余剰利益が増加していることが確認できる．これは余剰利益が総支払い額と総報酬額の差であるからである．

### 要求企業が申告する予算の幅の変更 

本節では前節同様に，手法IIの価格決定方法の特性を確認する為に，要求側の申告するコストの幅を変更する実験を行う．$\eqref{m2-pay}$より手法IIの提供企業の報酬は，他企業のコストに依存した式になっているので，コストの幅が小さくなるごとに，要求企業の支払いが増加し，提供企業の利益は減少すると考えられる．その影響で余剰利益が変化すると考えらるので，余剰利益の確認をする．

- コストを発生させる乱数の幅:2.5，2.0，1.5，1.0
  - コストを[2.75,5.25]，[3.0,5.0]，[3.25,4.75]，[3.5,4.5]で生成する．
- 試行回数:10回

#### 実験結果

[@tbl:m2-5-total-provider-profit]-[@tbl:m2-5-surplus-profit]に総提供企業利益，総要求企業利益，1提供企業あたりの利益の平均値，余剰利益を示す．

| Range | 2.5     | 2.0     | 1.5     | 1.0     |
| ----- | ------- | ------- | ------- | ------- |
| AVE.  | 3176.27 | 2793.80 | 2069.44 | 1950.83 |
| S.D.  | 1163.93 | 902.33  | 687.54  | 629.26  |

:Total Requester Profit in Methd2 {#tbl:m2-5-total-requester-profit} 

| Range | 2.5     | 2.0     | 1.5     | 1.0     |
| ----- | ------- | ------- | ------- | ------- |
| AVE.  | 317.627 | 279.380 | 206.944 | 195.083 |
| S.D.  | 116.393 | 90.233  | 68.754  | 62.926  |

:Requester Profit in Method 2 {#tbl:m2-5-requester-profit}

| Rate | 2.5     | 2.0     | 1.5     | 1.0     |
| ---- | ------- | ------- | ------- | ------- |
| AVE. | 2573.33 | 2858.49 | 2593.19 | 2188.87 |
| S.D. | 781.67  | 919.96  | 698.38  | 660.86  |

:Surplus Profit {#tbl:m2-4-surplus-profit}  {#tbl:m2-5-surplus-profit}

#### 考察

[@tbl:m2-5-total-requester-profit]，[@tbl:m2-5-requester-profit]より，申告する予算をの幅を狭くするごとに，総提供企業利益，1提供企業の利益が減少していることがわかる．それに伴い，余剰利益が増加すると考えられたが，[@tbl:m2-5-surplus-profit]より，余剰利益は減少しなかった．この理由について詳しく考察をする．

[@tbl:m2-5-total-provider-profit]に総提供企業利益

| Range | 2.5     | 2.0     | 1.5     | 1.0     |
| ----- | ------- | ------- | ------- | ------- |
| AVE.  | 2618.77 | 2536.34 | 2970.47 | 3271.82 |
| S.D.  | 717.20  | 450.48  | 539.37  | 475.90  |

:Total Provider Profit in Method2 {#tbl:m2-5-total-provider-profit}

[@tbl:m2-5-total-provider-profit]予算の幅が増加すると，総提供企業利益が増加していることがわかる．総要求企業利益の増加の方が大きく余剰利益が増加しなかったと考える．

さらに，[@tbl:m2-5-availability]にリソース提供前に対するリソース提供後の稼働率の増加率を示す．

| Range          | 2.5    | 2      | 1.5    | 1      |
| -------------- | ------ | ------ | ------ | ------ |
| Incerase Ratio | 42.06% | 42.72% | 44.70% | 48.83% |

:Increase Ratio of Availability in Method 2 {#tbl:m2-5-availability}

[@tbl:m2-5-availability]より，稼働率の増加率も増加している．これより提供できているリソースの時間が増加していることがわかる．申告する予算の幅が広いときであると，より要求時間よりも1[Ts]あたりの予算が大きい入札が優先選ばれていたが，申告する予算の幅が狭くなると，1[Ts]あたりの予算の差が狭くなっていき，狭い時と比べて要求時間が長い入札が選ばれるようになったからである．よって提供側の報酬は相手の予算よりも，提供時間に依存すると考えられる．つまり，予算が高いが要求時間が短い入札より，予算が低くて要求時間が長い入札の方が提供企業側の利益としては高くなる．これは\secref{suc:m2-reward}の$\eqref{m2-reward}$のrevenue_{i,r}’$の部分からもわかる．

