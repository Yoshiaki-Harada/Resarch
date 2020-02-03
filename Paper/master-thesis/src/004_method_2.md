# 手法II: 耐戦略性を満たす手法

本章では耐戦略性を満たす手法IIのアルゴリズムについて説明を提案し，その後計算機実験による特性評価を行う．

## アルゴリズム

耐戦略性を満たす手法IIのアルゴリズムについて説明する．

### 概要

\ref{double-auction}節で述べたように，ダブルオークション環境においてオークション主催者を含めた個人合理性，パレート効率性，耐戦略性の全ての性質を満たすオークションは存在しない．もし耐戦略性を満たすVCGオークションをダブルオークション 環境下に適用すると，オークション主催者の個人合理性が満たせなくなってしまう．つまり売り手の報酬の合計が買い手の支払いの合計を上回ってしまう．

そこで提案されたのがPadding Methodである\cite{Chu2009}．Padding Methodとは仮想的な買い手を用意し均衡価格を引き上げる，つまり買い手の支払い額を高めることでオークション主催者の個人合理性を満たすことを可能にした方法である．この考え方を適用した耐戦略性を満たす手法を提案し手法IIと呼ぶ．手法IIは耐戦略性を満たすことはできるが，仮想的な買い手が勝者となった財は実際には取引が行われないので，その分パレート効率性を犠牲にしてしまう．

記号の定義は\secref{symbol}節と同様のものを用いる．さらに使用する記号の定義を以下に示す．

+ $\boldsymbol{Q}$: 仮想的な買い手
+ $P(\boldsymbol{I},\boldsymbol{J})$: 提供企業の集合が$\boldsymbol{J}$，要求企業の集合$\boldsymbol{J}$であるときの勝者決定問題
+ $V(\boldsymbol{I},\boldsymbol{J})$: 問題$P(\boldsymbol{I},\boldsymbol{J})$の目的関数値
+ $P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$: 提供企業の集合が$\boldsymbol{J}$，要求企業の集合$\boldsymbol{J}$，仮想的な買い手$\boldsymbol{Q}$を考慮した場合の勝者決定問題
+ $V(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$: 問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$の目的関数値
+ $\boldsymbol{\tilde{J}}$: $P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において勝者となった要求企業の集合
+ $pay_j$: 要求企業$j$が勝者となった入札に対する支払い
+ $revenue_{i,r}$: 提供企業$i$がリソース$r$を提供することによって得られる報酬
+ $p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$: 問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において売手$i$がリソース$r$を提供する為の最大のコスト

以下に本手法のアルゴリズムの流れを示す．なお**STEP1**は手法Iの**STEP1**入札作成(\secref{make-bid})と同一となる．

<!-- tex --> 

\begin{description}

\item[STEP1:]リソース提供企業とリソース要求企業は入札を作成する．

\item[STEP2:]提供側と要求側の入札を元にした勝者決定問題$P(\boldsymbol{I},\boldsymbol{J})$に対し，仮想的な買い手$\boldsymbol{Q}$を考慮した問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$を定義し，最適解を求めることで勝者となる入札を決める．

\item[STEP3:] $P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において勝者となった要求企業に対して支払い$pay_j$を決定する．

\item[STEP4:] $P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において勝者となった要求企業の集合を$\boldsymbol{\tilde{J}}$とし，また敗者となった入札の決定変数を0とした問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$を定義し，最適解を求めることで提供側の勝者(提供量)を決定する．

\item[STEP5:]$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$において勝者となったリソース提供企業に対して収入$revenue_{j,r}$を決定する．

\end{description}

 <!-- tex -->

**STEP2**，**STEP3** が要求側の勝者と取引価格 決定を決定する部分であり，**STEP4**，**STEP5** が提供企業の勝者と支払い価格を決める部分である．それぞれについて次項以降で説明する．

### 要求側の勝者の決定

**STEP2**の要求側の勝者の決定について説明する．まず勝者決定問題$P(\boldsymbol{I},\boldsymbol{J})$を作成する．これは\secref{method1-resorce}の定式化と同じであり，以下に再掲する
$$
\begin{align}  
{\rm max}\quad  V(\boldsymbol{I},\boldsymbol{J})=&\sum_{j\in \boldsymbol{J}}\sum_{n\in\boldsymbol{N}}v_{j} \times y_{j,n} - \sum_{i\in\boldsymbol{I}}\sum_{r\in\boldsymbol{R}}\sum_{j\in\boldsymbol{J}}\sum_{n\in\boldsymbol{N}}c_{i,r} \times x_{i,r,j,n} \label{2-pij-obj}\\  
{\rm s.t.} \hspace{55pt} &\sum_{j\in \boldsymbol{J}}\sum_{n\in\boldsymbol{N}}x_{i,r,j,n} \leq TP_{i,r} \quad (\forall i, \forall r)\label{2-subto-time}\\
&\begin{cases} x_{i,r,j,n} = 0 \quad (\forall i, \forall r)&({\rm if} \ y_{j,n}=0) \\  
\sum_{j \in \boldsymbol{J}}\sum_{n\in\boldsymbol{N}} TR_{j,n,r} \times x_{i,r,j,n} = TR_{j,n,r}    \quad  (\forall i, \forall r)&({\rm if} \ y_{j,n}=1) \end{cases}\\ 
&\sum_{n\in \boldsymbol{N}}y_{j,n}  \leq 1 \quad (\forall j)\\   &x_{i,r,j,n} \in \boldsymbol{Z}\\    
&y_{j,n} \in {0,1}
\end{align}
$$
ここで手法IIでは，問題$P(\boldsymbol{I},\boldsymbol{J})$に対し仮想的な買い手$\boldsymbol{Q}$を考慮した問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$を作成する．その際に考慮する仮想的な買い手$\boldsymbol{Q}$について説明する．

#### 仮想的な買い手$\boldsymbol{Q}$

提供企業仮想的な買い手$\boldsymbol{Q}$は$Q=\{Q_1,Q_2,Q_r…Q_{|\boldsymbol{R}|}\}$で表現される．$Q_r$は$\boldsymbol{Q}$が要求するリソース$r$を要求する時間であり，以下のように定める\cite{Chu2009}．
$$
\begin{align}
Q_r^I&=\max \{TP_{i,r} |i \in \boldsymbol{I}\} \label{max-provider}\\
Q_r^J&=\max \{TR_{j,n,r} |j \in \boldsymbol{J},n \in \boldsymbol{N}\} \label{max-requester}\\
Q_r&=\max \{Q_r^J,Q_r^I\} \label{max-time}
\end{align}
$$
$\eqref{max-provider}$は1提供企業が提供するリソース$r$の最大提供時間を表す．$\eqref{max-requester}$は1要求企業が要求するリソース$r$の最大要求時間を表す．よって$\eqref{max-time}$は1企業が提供または要求するリソース$r$の最大の時間を表す．仮想的な買い手$\boldsymbol{Q}$はこのように定まり予算は0であるが満たさなければならない1要求企業として扱う．こうすることで均衡価格を引き上げることができる．この$\boldsymbol{Q}$を考慮した問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$を定義する．

#### 問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$の定式化

問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$の定式化を以下に示す．
$$
\begin{align}
  {\rm max} \quad V(\boldsymbol{I},\boldsymbol{J} ,\boldsymbol{Q})=&\sum_{j\in \boldsymbol{J}}\sum_{n\in\boldsymbol{N}}v_{j} \times y_{j,n} - \sum_{i\in\boldsymbol{I}}\sum_{r\in\boldsymbol{R}}\sum_{j\in\boldsymbol{J}}\sum_{n\in\boldsymbol{N}}c_{i,r} \times x_{i,r,j,n} - \sum_{i\in\boldsymbol{I}}\sum_{r\in\boldsymbol{R}}c_{i,r} \times q_{i,r} \label{pijq-obj}\\ 
  {\rm s.t.} \hspace{13pt} &\sum_{j\in \boldsymbol{J}}\sum_{n\in\boldsymbol{N}}x_{i,r,j,n}
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

決定変数$q_{i,r}$は提供企業$i$が仮想的な買い手$\boldsymbol {Q}$に提供するリソース$r$の量を表す整数変数である．そして$\boldsymbol{Q}$の要求を満たすための制約$\eqref{subto-q}$が追加される．それによって$P(\boldsymbol{I},\boldsymbol{J})$の提供企業の容量制約が$\eqref{2-subto-time}$から$\eqref{pijq-subto-time}$になる．そして$\boldsymbol{Q}$を満たした分のコストが目的関数に考慮されることで$P(\boldsymbol{I},\boldsymbol{J})$の$\eqref{2-pij-obj}$が$\eqref{pijq-obj}$になる．この問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$を解くことでまず勝者となる要求企業の入札を決定する．

### 支払い額の決定\label{sec:m2-pay}

**STEP3**の支払い額の決定について説明する．仮想的な買い手$\boldsymbol{Q}$を考慮した状態で，\secref{VCG}において説明したVCGオークションと同様の方法で価格を決定する．すなわち勝者となった提供企業$j$の入札$n$の支払いは以下の式で決定される．
$$
\begin{align}
pay_j=-\{V(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})-v_{j,n}\}+V(\boldsymbol{I},\boldsymbol{J}\backslash\{j\},\boldsymbol{Q}) \label{m2-pay}
\end{align}
$$
$V(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})-v_{j,n}$は目的関数値から勝者となった要求企業$j$の入札$n$の予算を除いた値となっている．さらに$V(\boldsymbol{I},\boldsymbol{J}\backslash\{j\},\boldsymbol{Q})$は要求企業$j$を除いた問題の目的関数値となっている．よって$\eqref{m2-pay}$はVCGオークションと同様にこの値は要求企業$j$の予算に依存しておらず，問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において勝者となる為の最小の価格，つまりcritical priceとなっており提供企業側の耐戦略性を満たす．また$\boldsymbol{Q}$によって支払い額が引き上がるのは$\boldsymbol{Q}$が予算0であるが満たさなければならないので，コストの安いリソースが$\boldsymbol{Q}$に消費されてしまい，残りの要求企業は価格の高いリソースが割当てられてしまうからと捉えることができる．

### 提供側の勝者の決定

**STEP4**の提供企業の勝者を決める部分について説明する．

#### 問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$の定式化

勝者となった要求企業の集合$\boldsymbol{\tilde{J}}$を定義し，また敗者となった入札の決定変数を0とした新たな問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$を定義する．
$$
\begin{align}  
{\rm max}\quad V(\boldsymbol{I},\boldsymbol{\tilde{J}})=&\sum_{j\in \boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}}v_{j} \times y_{j,n} - \sum_{i\in\boldsymbol{I}}\sum_{r\in\boldsymbol{R}}\sum_{j\in\boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}}c_{i,r} \times x_{i,r,j,n}\\  
{\rm s.t.} \hspace{57pt} &\sum_{j\in \boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}}x_{i,r,j,n} \leq TP_{i,r} \quad (\forall i, \forall r)\\
&\begin{cases} x_{i,r,j,n} = 0 \ (\forall i, \forall r)&({\rm if} \ y_{j,n}=0) \\  
\sum_{j \in \boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}} TR_{j,n,r} \times x_{i,r,j,n} = TR_{j,n,r} (\forall i, \forall r)&({\rm if} \ y_{j,n}=1) \end{cases}\\ 
&\sum_{n\in \boldsymbol{N}}y_{j,n}  \leq 1 \quad (\forall j)\\  
&y_{j,n}=0 \quad ({\rm if} \ y_{j,n}=0 \ {\rm in}  \ P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q}))\label{subto-loser}\\
&x_{i,r,j,n} \in \boldsymbol{Z}\\    
&y_{j,n} \in {0,1}
\end{align}
$$
問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$は問題$P(\boldsymbol{I},\boldsymbol{J})$の$\boldsymbol{J}$を$\boldsymbol{\tilde{J}}$で置き換え，制約式$\eqref{subto-loser}$を追加したものとなっている．問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$の最適解を求めることで，提供企業の勝者つまり各提供企業が提供するリソースの時間を決定する．よって問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$で敗者となった入札は$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$で選ばれることはないので，仮想的な買い手$\boldsymbol{Q}$の分のリソースは取引は行われないが利用されることもなくなってしまう．また提供企業数が増加するとこの仮想的な買い手$\boldsymbol{Q}$によって利用されないリソースの割合が全体の提供リソースに対して減少するので，$\boldsymbol{Q}$の影響は小さくなると考えられる．

### 報酬額の決定\label{sec:m2-reward}

問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$の解を元に，売り手$i$がリソース$r$を$\sum_{j\in\boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}}x_{i,r,j,n}$ [Ts]提供することで得られる報酬$revenue_{i,r}$を$\eqref{m2-reward}$で決定する．
$$
\begin{align}
revenue_{i,r}=\sum_{j\in\boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}}c_{i,r} \times x_{i,r,j,n} +V(\boldsymbol{I},\boldsymbol{\tilde{J}})-V(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}}) \notag \\ - \{V(I|c_{i,r}=p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q}),\tilde{J})-  V(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}})\}
\label{m2-reward}
\end{align}
$$
$\eqref{m2-reward}$の報酬決定方法が耐戦略性を満たすことについて説明する．そこで$\eqref{m2-reward}$の前半部分と後半部分をそれぞれ以下のようにおく．
$$
\begin{align}
revenue_{i,r}’=&\sum_{j\in\boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}}c_{i,r} \times x_{i,r,j,n} +V(\boldsymbol{I},\boldsymbol{\tilde{J}})-V(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}})\\
extra_{i,r}=&V(I|c_{i,r}=p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q}),\tilde{J})-  V(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}})
\end{align}
$$
$revenue_{i,r}’$は問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$において提供企業$i$がリソース$r$を提供できる最大の価格(コストと時間の積)であり，売り手側においてVCGメカニズムと同様の価格決定方法で求めている．$revenue'_{i,r}$について[@fig:m2-revenue-1]を用いて説明する．

![revenue-1](/Users/haradayoshiaki/Resarch/Paper/master-thesis/src/img/chapter-4/revenue-1.png){#fig:m2-revenue-1 width=70%}

[@fig:m2-revenue-1]より，もし$revenue_{i,r}’$より大きく$c_{i,r}\times x_{i,r}$を申告してしまうと$V(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}})>V(\boldsymbol{I},\boldsymbol{\tilde{J}})$となり問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$の解は問題$P(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}})$の解に変わり，提供企業$i$はリソース$r$を提供できなくなってしまう．よって$revenue_{i,r}’$は問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$において，提供企業$i$がリソース$r$を提供できる最大の価格である．また$V(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}})$には提供企業$i$のリソース$r$の予算は含まれておらず$\sum_{j\in\boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}}c_{i,r} \times x_{i,r,j,n} +V(\boldsymbol{I},\boldsymbol{\tilde{J}})$にも提供企業$i$のリソース$r$の予算含まれていないので，$revenue'_{i,r}$において提供企業$i$のリソース$r$の評価値が使用されていない．

また問題$P(\boldsymbol{I},\boldsymbol{J})$と問題$P(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}})$において勝者となる要求側の入札は変わらないので$revenue_{i,r}’$は$c_{i,r}$よりコストが高い企業が安い順に$\sum_{j\in\boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}} x_{i,r,j,n}$[Ts]分リソースを提供したコストの和となっている．その提供企業の集合を$\boldsymbol{I'}$とする．

次に$extra_{i,r}$について説明する．$p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$は問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において売手$i$がリソース$r$を提供するための最大コストである．つまり問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$における提供企業$i$のリソース$r$におけるcritical priceを提供時間[Ts]で割ったものである．ここで問題$P(I|c_{i,r}=p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q}),\boldsymbol{\tilde{J}})$と問題$P(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}})$では勝者となる要求側の入札は変わらないので，先ほど定義した提供企業集合$\boldsymbol{I’}$のうち$p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$よりコストが低い企業が提供している部分の解が異なる．ただし$p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$よりコストが低い企業が存在しなければ解は等しくなる．

よって$u=\sum_{\{i \in  \boldsymbol{I'}| c_{i,r}<p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})\}}\sum_{j\in\boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}}x_{i,r,j,n}$とおくと，
$$
\begin{align}
extra_{i,r}
=&V(I|c_{i,r}=p_{i,r}(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q}),\tilde{J})-  V(\boldsymbol{I}|TP_{i,r}=0,\boldsymbol{\tilde{J}}) \\
=&revenue_{i,r}’-p_{i,r} \times u \label{extra} 
\end{align}
$$
となる．$\eqref{extra}$は，問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$において，$revenue_{i,r}’$から計算されたコストを申告すると提供企業$i$が勝者となれない時間分のコストを引くことになる．

以上より$\eqref{m2-reward}$は，問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$と問題$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$の両方で$\sum_{j\in\boldsymbol{\tilde{J}}}\sum_{n\in\boldsymbol{N}} x_{i,r,j,n}$[Ts]を提供できる最大の価格を求めている．つまりcritical priceになっており提供企業側においても耐戦略性が成り立つ．

### 手法IIの持つ特徴

\secref{sec:m2-pay}，\secref{sec:m2-reward}で示したように提案した手法IIは耐戦略性を満たす．しかし仮想的な買い手$\boldsymbol{Q}$への配分リソースは要求企業にも提供できなくなるので無駄になってしまい，パレート効率性を満たすことができない．一方で提供企業数が増えると仮想的な買い手$\boldsymbol{Q}$によって無駄になってしまうリソースの割合が減少し，パレート効率な状態に近づくと考えられる．また手法IIは提供企業の支払いの合計と提供側の報酬の合計が等しくなるとは限らず，提供企業の支払いの合計と提供側の報酬の合計余剰の差である余剰利益が発生する．この利益はクラウドソースドマニュファクチャリング内で閉じているので総利益に加算することとする．

## 特性評価

本節では手法IIに関する特性評価を行う．実験条件は\secref{exp-condition}と同様である．次項以降では以下の項目について変更を行った実験を行い結果ならびに考察を述べる．

+ 提供企業数
+ 1提供企業の虚偽申告率
+ 1要求企業の虚偽申告率
+ 提供側が申告するコストの幅
+ 要求企業が申告する予算の幅

### 提供企業数の変更 

本項では提供企業数を変更する実験を行う．手法IIは企業数が増加するごとに，パレート効率な状態に近づくことを確認する．パレート効率な総利益は手法Iの総利益を用いる．

以下に本実験における実験条件を示す．

+ 提供企業数$\boldsymbol{I}$:15,20,25,30
+ 試行回数:10回

#### 実験結果

[@tbl:m2-1-pareto-total-profit]にパレート効率な状態の総利益，[@tbl:m2-1-total-profit]に手法IIの総利益を示す．

| Number of Providers | 15      | 20       | 25       | 30       |
| ---------------- | :-------: | :--------: | :--------: | :--------: |
| AVE.             | 8007.79 | 10857.46 | 13721.85 | 14706.66 |
| S.D.             | 877.31  | 416.03   | 531.84   | 487.50   |

:Pareto efficient total profit {#tbl:m2-1-pareto-total-profit}

| Number of Providers | 15      | 20      | 25       | 30       |
| ---------------- | :-------: | :-------: | :--------: | :--------: |
| AVE.             | 6600.43 | 9457.75 | 12586.24 | 13785.36 |
| S.D.             | 650.81  | 340.77  | 559.70   | 723.15   |

:Total profit in Method II {#tbl:m2-1-total-profit}

以上よりパレート効率な総利益に対する手法IIの総利益の減少割合を[@tbl:m2-1-profit-decreased]に示す．

| Number of Providers | 15     | 20     | 25    | 30    |
| ---------------- | :------: | :------: | :-----: | :-----: |
| Decrese Rate     | 17.57% | 12.89% | 8.28% | 6.26% |

:Ratio of decreased total profit in Method II to pareto efficient total profit  {#tbl:m2-1-profit-decreased}

#### 考察

[@tbl:m2-1-profit-decreased]より，提供企業数が15のときはパレート効率な状態の総利益より17.57%減少してしまっているのに対して，提供企業数が30のときは6.25%の減少で留まっている．よって提供企業数が増加すると，仮想的な買い手$\boldsymbol{Q}$によって無駄になってしまうリソースの量が減少し，総利益はパレート効率な状態に近づくことが確認できた．これは提供企業数が増加すると，提供リソース全体における仮想的な買い手$\boldsymbol{Q}$に配分されることで無駄になるリソースの割合が減少するからである．

### 1提供企業の虚偽申告率の変更 

手法IIは耐戦略性を満たすが，本実験では意図的に1提供企業の虚偽申告率を変更させる実験を行い，正直な評価値の申告が支配戦略となることを確認する．また虚偽申告による影響も確認する．

以下に本実験における実験条件を示す．

+ 1提供企業の虚偽申告率:0%，10%，20%，30%
  + 虚偽申告が0%のときは正直にコストを申告する．
+ 試行回数:1回
  + ある1試行における虚偽申告企業の利益を見るために1試行とした．

#### 実験結果

[@tbl:m2-2-total-profit]〜[@tbl:m2-2-false-provider-profit]は，それぞれ総利益，総提供企業利益，総要求企業利益，虚偽申告を行った1提供企業の利益を示す．

| False rate | 0%      | 10%     | 20%     | 30%     |
| ---------- | ------- | ------- | ------- | ------- |
| AVE.       | 8188.63 | 8187.51 | 8157.33 | 8137.31 |
| S.D.       | 927.95  | 929.41  | 959.21  | 949.95  |

: Total profit in Method II: A provider report false cost {#tbl:m2-2-total-profit}

| False rate | 0%      | 10%     | 20%     | 30%     |
| ---------- | ------- | ------- | ------- | ------- |
| AVE.       | 2536.34 | 2592.67 | 2597.21 | 2623.43 |
| S.D.       | 450.48  | 480.56  | 518.26  | 570.34  |

: Total providers profit in Method II: A provider report false cost {#tbl:m2-2-total-provider-profit}

| False rate | 0%      | 10%     | 20%     | 30%     |
| ---------- | ------- | ------- | ------- | ------- |
| AVE.       | 2793.80 | 2766.17 | 2725.87 | 2704.01 |
| S.D.       | 902.33  | 915.71  | 935.06  | 927.98  |

: Total  requesters profit in Method II: A provider report false cost {#tbl:m2-2-total-requester-profit}

| False rate | 0%     | 10%    | 20%    | 30%    |
| ---------- | ------ | ------ | ------ | ------ |
| AVE.       | 142.60 | 141.48 | 127.48 | 107.46 |
| S.D.       | 99.02  | 98.29  | 86.94  | 92.87  |

: The false reporting provider profit in Method II: A provider report false cost {#tbl:m2-2-false-provider-profit}

#### 考察

[@tbl:m2-2-false-provider-profit]より，虚偽申告率が0%のとき利益が142.60と最大となっており耐戦略性を満たすことが確認できた．

[@tbl:m2-2-total-provider-profit]より，虚偽申告率が増加すると総提供企業利益も2623.43から増加している．これは虚偽申告提供企業が他の提供企業の利益を高めているためと考えられる．

[@tbl:m2-2-total-requester-profit]より，総提供企業利益は2536.34から2704.01に減少している．これは予算が足りず要求を満たして貰えない企業が増加したためと考える．

より詳しく考察を行うために[@tbl:m2-2-total-profit]〜[@tbl:m2-2-false-provider-profit]は，ある1試行における総利益，総提供企業利益，総要求企業利益，虚偽申告を行った1提供企業の利益を示す．

| False rate   |   0%    |   10%   |   20%   |   30%   |
| ------------ | :-----: | :-----: | :-----: | :-----: |
| Total Profit | 8654.35 | 8651.31 | 8651.31 | 8559.37 |

: Total profit in Method II: A provider report false cost, one tiral {#tbl:m2-2-total-profit-one-trial}

| False rate           |   0%    |   10%   |   20%   |   30%   |
| -------------------- | :-----: | :-----: | :-----: | :-----: |
| Total Provier Profit | 3204.51 | 3257.36 | 3328.47 | 3353.17 |

: Total providers profit in Method II: A provider report false cost, one tiral {#tbl:m2-2-total-provider-profit-one-trial}

| False rate             |    0%    |   10%    |   20%    |   30%    |
| ---------------------- | :------: | :------: | :------: | :------: |
| Total Requester Profit | 3126.378 | 3119.517 | 3105.653 | 2995.902 |

: Total  requesters profit in Method II: A provider report false cost, one tiral {#tbl:m2-2-total-requester-profit-one-trial}

| False rate     |  0%   |  10%  |  20%  | 30%  |
| -------------- | :---: | :---: | :---: | :--: |
| Provier Profit | 94.98 | 91.94 | 91.94 | 0.00 |

: The false reporting requester profit in Method II: A provider report false cost, one tiral {#tbl:m2-2-false-provider-profit-one-trial}

[@tbl:m2-2-false-provider-profit]より，正直な申告を行ったときの利益は94.98であり，虚偽申告を行ったときの利益は91.94，91.94，0.00と減少することが分かる．よって耐戦略性を満たしていることがある1試行においても確認できた．

しかし[@tbl:m2-2-total-provider-profit]より1提供企業の虚偽申告率が増加することで総提供企業利益が増加していることが確認できる．これは手法IIの報酬額の決定方法である$\eqref{m2-reward}$が他企業の提供企業利益から決定されるので，虚偽申告企業の影響で他の提供企業の利益を高めてしまっているからである．

また[@tbl:m2-2-total-profit]より虚偽申告率が増加すると総利益の値が3204.51から3353.17まで減少してしまっている．本実験では意図的に虚偽申告企業を発生させているので，虚偽申告の影響により予算が足りない入札が発生しリソースの割当が変わり総利益が減少している．

### 1要求企業の虚偽申告率の変更 

手法IIは耐戦略性を満たすが，意図的に1要求企業の虚偽申告率を変更させる実験を行い，正直な評価値の申告が支配戦略になることを確認する．また虚偽申告による影響も確認する．

以下に本実験における実験条件を示す．

+ 1要求企業の虚偽申告率: 0%，10%，20%，30%
  + 虚偽申告が0%のときは正直に予算を申告する．

#### 実験結果

[@tbl:m2-3-total-profit]〜[@tbl:m2-3-requesters-total-profit]は，それぞれ総利益，総提供企業利益，総要求企業利益，虚偽申告を行った1要求企業の利益を示す．

#### 考察

[@tbl:m2-3-false-requester-profit]より，虚偽申告率が0%のとき虚偽申告機

[@tbl:m2-3-total-profit-one-trial]〜[@tbl:m2-3-requesters-total-profit-one-trial]にある1試行における総利益，総提供企業利益，総要求企業利益，虚偽申告を行った1要求企業の利益を示す．

| False rate   |   0%    |   10%   |   20%   |   30%   |
| ------------ | :-----: | :-----: | :-----: | :-----: |
| Total Profit | 8654.35 | 8654.35 | 8654.35 | 8106.60 |

: Total profit  in Method II: A requester report false budget, one trial {#tbl:m2-3-total-profit-one-trial}

| False rate             |   0%    |   10%   |   20%   |   30%   |
| ---------------------- | :-----: | :-----: | :-----: | :-----: |
| Total Providers Profit | 3204.51 | 3204.51 | 3204.51 | 2883.22 |

:Total providers profit  in Method 1: A requester report false budget, one trial {#tbl:m2–3providers-total-profit-one-trial}

| False rate              |   0%    |   10%   |   20%   |   30%   |
| ----------------------- | :-----: | :-----: | :-----: | :-----: |
| Total Requesters Profit | 3126.38 | 3126.38 | 2832.30 | 2180.29 |

: Total providers profit  in Method II: A requester report false budget, one trial {#tbl:m2-3-requesters-total-profit-one-trial}

| False rate              |   0%   |  10%   |  20%   | 30%  |
| ----------------------- | :----: | :----: | :----: | :--: |
| Total Requesters Profit | 468.09 | 468.09 | 468.09 | 0.00 |

:The false reporting provider profit in Method II: A requester report false budget, one trial {#tbl:m2-3-false-requester-profit-one-trial}

[@tbl:m2-3-false-requester-profit]より虚偽申告率が0%から20%までは同じ利益468.09であり，30%のとき利益が0となっており，耐戦略性を満たしていることが確認できた．予算が変わっても利益が変わらないことから手法IIの支払い額が自身の評価値に依存していないことも確認できる．

総利益，総提供企業利益に関して虚偽申告率が0%から20%まで変化がないのは，虚偽申告による問題$P(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$と$P(\boldsymbol{I},\boldsymbol{\tilde{J}})$の解に対しては影響がなかったからである．総要求企業利益[@tbl:m2-3-total-profit]が虚偽申告率が10%から20%のときに減少している．この理由は，$\eqref{m2-pay}$において虚偽申告率が10%から20%になるときの$V(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$の減少幅より$V(\boldsymbol{I},\boldsymbol{J}\backslash \{j\},\boldsymbol{Q})$の減少幅の方が小さく，支払い額が増加してしまう要求企業が存在したためと考える．

この理由について要求企業$2$の支払い額の決定時の結果を用いて説明する．虚偽申告を行った要求企業を要求企業$1$とする．このとき要求企業$2$の勝者となった入札の予算は2449.17であった．要求企業$1$の虚偽申告率が10%のときは$V(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})=4571.30$，$V(\boldsymbol{I},\boldsymbol{J}\backslash{\{2\}},\boldsymbol{Q})=4128.26$となり，問題$P(\boldsymbol{I},\boldsymbol{J}\backslash{\{2\}},\boldsymbol{Q})$の勝者に要求企業$1$の入札が選ばれていた．要求企業$0$の虚偽申告率が20%のときは$V(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})=4381.77$，$V(\boldsymbol{I},\boldsymbol{J}\backslash{\{2\}},\boldsymbol{Q})=4038.80$となり，問題$P(\boldsymbol{I},\boldsymbol{J}\backslash{\{2\}},\boldsymbol{Q})$において要求企業$1$の入札は敗者となっていた．その結果$V(\boldsymbol{I},\boldsymbol{J}\backslash{\{2\}},\boldsymbol{Q})$の減少分は89.46となり$V(\boldsymbol{I},\boldsymbol{J},\boldsymbol{Q})$減少分189.53より小さい値となった．その結果支払い額が要求企業$1$虚偽申告率が10%のときより増加してしまった．

以上より前項の実験と同様に本実験が意図的に虚偽申告企業を発生させているので，[@tbl:m2-3-total-profit]のように総利益は減少してしまう．

### 提供側が申告するコストの幅の変更 

本項では手法IIの価格決定方法の特性を確認する為に，提供側の申告するコストの幅を変更する実験を行う．$\eqref{m2-reward}$より手法IIのある提供企業の報酬は他の提供企業のコストに依存した式になっているので，参加企業が均質化し申告するコストの幅が小さくなるごとに報酬が減少し，1提供企業あたりの利益は減少すると考えられる．またそれによって余剰利益が減少すると考えられるので．その変化についても確認をする．

+ コストを発生させる乱数の幅: 2.5，2.0，1.5，1.0
  + コストを[1.75,4.25]，[2.0,4.0]，[2.25,3.75]，[2.5,3.5]で生成する．
+ 試行回数: 10回

#### 実験結果

[@tbl:m2-4-total-provider-profit]に総提供企業利益を示し[@tbl:m2-4-provider-profit]に1企業あたりの利益の平均値を示す．[@tbl:m2-4-surplus-profit]に余剰利益を示す．

| Range | 2.5     | 2.0     | 1.5     | 1.0     |
| :-----: | :-------: | :-------: | :-------: | :-------: |
| AVE.  | 3101.13 | 2536.34 | 1463.70 | 1456.30 |
| S.D.  | 1152.16 | 450.48  | 301.13  | 391.94  |

:Total Provider profit in Method II:Change cost range {#tbl:m2-4-total-provider-profit}

|      | 2.5    | 2.0    | 1.5   | 1.0   |
| ---- | :------: | :------: | :-----: | :-----: |
| AVE. | 124.05 | 101.45 | 58.55 | 58.25 |
| S.D. | 46.09  | 18.02  | 12.05 | 15.68 |

:A requester profit in Method II:Change cost range {#tbl:m2-4-provider-profit}

|      | 2.5     | 2.0     | 1.5     | 1.0     |
| ---- | :-------: | :-------: | :-------: | :-------: |
| AVE. | 2695.97 | 2858.49 | 3018.05 | 3410.49 |
| S.D. | 1094.76 | 919.96  | 859.63  | 904.73  |

:Surplus profit in Method II:Change cost range {#tbl:m2-4-surplus-profit}

#### 考察

[@tbl:m2-4-total-provider-profit]，[@tbl:m2-4-provider-profit]より，コストの幅が狭くなるに連れ総提供企業利益は3101.13から1456.30まで減少し，1提供企業利益も124.05から58.25に減少していることが確認できた．手法IIでは$\eqref{m2-reward}$より報酬が他の提供企業のコストによって決まるので，同じようなコストの企業が集まると報酬が低くなり利益が減少する傾向にある．また[@tbl:m2-4-surplus-profit]よりコストの幅が狭くなると余剰利益が増加していることが確認できる．これは余剰利益が総支払い額と総報酬額の差であるからである．

### 要求企業が申告する予算の幅の変更 

本項では，前項と同様に手法IIの価格決定方法の特性を確認するために要求側の申告する予算の幅を変更する実験を行う．$\eqref{m2-pay}$より，手法IIの提供企業の報酬は他企業のコストに依存した式になっているので，参加企業が均質化し申告する予算の幅が小さくなるごとに要求企業の支払いが増加し提供企業の利益は減少すると考えられる．その影響で余剰利益が変化すると考えられこの確認を行う．

- 予算を決める重みの乱数の幅: 2.5，2.0，1.5，1.0
  + 重みを[2.75,5.25]，[3.0,5.0]，[3.25,4.75]，[3.5,4.5]で生成する．
- 試行回数: 10回

#### 実験結果

[@tbl:m2-5-total-requester-profit]-[@tbl:m2-5-surplus-profit]に総提供企業利益，総要求企業利益，1提供企業あたりの利益の平均値，余剰利益を示す．

| Range | 2.5     | 2.0     | 1.5     | 1.0     |
| ----- | :-------: | :-------: | :-------: | :-------: |
| AVE.  | 3176.27 | 2793.80 | 2069.44 | 1950.83 |
| S.D.  | 1163.93 | 902.33  | 687.54  | 629.26  |

:Total requester profit in Methd2: Change budget range {#tbl:m2-5-total-requester-profit} 

| Range | 2.5     | 2.0     | 1.5     | 1.0     |
| ----- | :-------: | :-------: | :-------: | :-------: |
| AVE.  | 317.627 | 279.380 | 206.944 | 195.083 |
| S.D.  | 116.393 | 90.233  | 68.754  | 62.926  |

:A requester profit in Method II: Change budget range {#tbl:m2-5-requester-profit}

| Rate | 2.5     | 2.0     | 1.5     | 1.0     |
| ---- | :-------: | :-------: | :-------: | :-------: |
| AVE. | 2573.33 | 2858.49 | 2593.19 | 2188.87 |
| S.D. | 781.67  | 919.96  | 698.38  | 660.86  |

:Surplus profit :Change budget range  {#tbl:m2-5-surplus-profit}

#### 考察

[@tbl:m2-5-total-requester-profit]，[@tbl:m2-5-requester-profit]より，申告する予算の幅を狭くするごとに総提供企業利益が3176.27から1950.83まで減少し，1提供企業の利益が317.627から195.083まで減少していることが分かる．それに伴い余剰利益が増加すると考えられたが，[@tbl:m2-5-surplus-profit]より余剰利益は減少しなかった．この理由について詳しく考察をする．

[@tbl:m2-5-total-provider-profit]に総提供企業利益を示す．

| Range | 2.5     | 2.0     | 1.5     | 1.0     |
| ----- | :-------: | :-------: | :-------: | :-------: |
| AVE.  | 2618.77 | 2536.34 | 2970.47 | 3271.82 |
| S.D.  | 717.20  | 450.48  | 539.37  | 475.90  |

:Total provider profit in Method II: Change budget range {#tbl:m2-5-total-provider-profit}

[@tbl:m2-5-total-provider-profit]予算の幅が増加すると総提供企業利益が増加していることが分かる．この結果から余剰利益が増加しなかった理由が総要求企業利益の増加の方が総要求企業利益の減少より大きくなったためと考える．

さらに[@tbl:m2-5-availability]にリソース提供前に対するリソース提供後の稼働率の増加率を示す．

| Range          | 2.5    | 2      | 1.5    | 1      |
| -------------- | :------: | :------: | :------: | :------: |
| Incerase Ratio | 42.06% | 42.72% | 44.70% | 48.83% |

:Increase ratio of availability in Method II: Change budget range {#tbl:m2-5-availability}

[@tbl:m2-5-availability]より，稼働率の増加率も増加しており提供できているリソースの時間が増加していることが分かる．申告する予算の幅が広いときは，要求時間よりも1[Ts]あたりの予算が大きい入札が選ばれていたが，申告する予算の幅が狭くなると1[Ts]あたりの予算の差が狭くなっていき要求時間が長い入札が選ばれるようになったからである．よって提供側の報酬額は相手の予算よりも提供時間に依存すると考えられる．つまり予算が高いが要求時間が短い入札より，予算が低くて要求時間が長い入札の方が提供企業側の利益としては高くなる．これは\secref{sec:m2-reward}の$\eqref{m2-reward}$の$revenue_{i,r}’$からも分かる．

## まとめ

本章ではパレート効率性を満たす手法IIのアルゴリズムの説明と特性評価を行った．耐戦略性を満たすことが計算機実験においても確認できた．手法IIの支払い額，報酬額の決定が同じ側の他の企業に依存するので，参加企業が均質化していき予算やコストなど入札値の幅が狭くなると1企業あたりの利益が減少することが確認できた．また提供企業数が増加するごとに総利益がパレート効率な状態に近づくことも確認ができた．



