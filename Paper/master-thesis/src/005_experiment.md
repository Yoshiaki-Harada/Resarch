# 手法間の比較

3章では手法Iはパレート効率性を満たすが，耐戦略性を満たせないことを確認した．ただし参加する企業が均質化する(同じような評価値を持つ企業が増加する)と，正直な申告が支配戦略になることが確認できた．4章では手法IIは耐戦略性を満たすが，パレート効率性を満たせないことが確認できた．しかし提供企業数が増加するとパレート効率な状態に近づくことが確認できた．また手法IIではクラウドソースドマニュファクチャリング内の利益ではるが，誰の利益にも属さない余剰利益が発生してしまう．

本章では第3章，第4章で明らかにした特性を踏まえクラウドソースドマニュファクチャリングに参加する企業の均質性を変化させた状況で総利益，要求企業利益，提供企業利益に関して手法Iと手法IIを比較を行い，両手法の位置付けを明らかにする．

## 実験条件

本章で用いる実験条件を以下に示す．ただし[min, max]はminからmaxの範囲の一様乱数を表す．

+ リソースの種類:$|\boldsymbol{R}|=10$
+ 対象期間: 200[Ts]
+ 提供企業$|\boldsymbol{J}|=100$
  + 各企業2種類のリソースを遊休時間に提供する．
  + 遊休時間$TP_{i,r}$ [Ts]を[100, 200]で決定する．
  + コスト$c_{i,r}$を発生させる乱数の幅: 2.5，2.0，1.5，1.0
    - コストを[2.75,5.25]，[3.0,5.0]，[3.25,4.75]，[3.5,4.5]で生成する．
+ 要求企業$|\boldsymbol{I}|=30$
  + 各企業$|\boldsymbol{N}|$=5個の入札を作成する．
  + $|\boldsymbol{R}|$種類のリソースを各 [0, 200] [Ts]要求する．
  + 予算$v_{j,n}$は合計要求時間と重みの積[円]とする．
  + 予算を決める重みの乱数の幅: 2.5，2.0，1.5，1.0
    + 重みを[2.75,5.25]，[3.0,5.0]，[3.25,4.75]，[3.5,4.5]で生成する．
+ 施行回数:10回

コスト，予算の乱数の幅をそれぞれ3段階変化させ，合計9パターンの実験を行う．またこのとき手法Iは耐戦略性を満たせないので参加企業は虚偽申告を行うと考えられるが，\secref{3:cost-range}，\secref{3:budget-range}より参加企業が均質化する(同じような評価値の企業が集まる)ほど正直な評価値の申告が支配戦略となることが確認できた．よって手法Iにおいては参加企業のコストや予算を決める乱数の幅に対応した割合で虚偽申告を行うものとする．その対応を[@tbl:cost-false-rate-correspondence]，[@tbl:budget-false-rate-correspondence]に示す．

| Cost range | False rate |
| ---------- | ---------- |
| 1.0        | [0%, 10%]  |
| 1.5        | [0%, 20%]  |
| 2.0        | [0%, 30%]  |

:Correspondence table between cost range and false rate in Method 1 {#tbl:cost-false-rate-correspondence} 

| Budget range | False rate |
| ------------ | ---------- |
| 1.0          | [0%, 10%]  |
| 1.5          | [0%, 20%]  |
| 2.0          | [0%, 30%]  |

:Correspondence table between budget range and false rate in Method 1 {#tbl:budget-false-rate-correspondence} 

[@tbl:cost-false-rate-correspondence]より，例えばコストを発生させる乱数の幅が1.0の場合提供企業は[0% 10%]分コストを過大申告する．同様に[@tbl:budget-false-rate-correspondence]より，要求企業は予算を決める重みの乱数の幅が1.0の場合[0% 10%]分予算を過小申告する．

手法IIは耐戦略性を満たすので参加する各企業は正直に評価値を申告する．

\newpage

## 結果

[@tbl:total-profit-cost-1.0]〜[@tbl:total-profit-cost-1.5]はそれぞれ，コストを発生させる乱数の幅が1.0，1.5，2.0の場合の総利益を表す．手法IIの括弧内の数字は余剰利益を示す．

| budget range |      | Method I | Method  II          |
| ------------ | ---- | -------- | ------------------- |
| 1.0          | AVE. | 34873.47 | 35321.95 (16465.96) |
|              | S.D. | 1695.84  | 1075.86             |
| 1.5          | AVE. |          |                     |
|              | S.D. |          |                     |
| 2.0          | AVE. | 34611.74 | 36457.68 (10351.46) |
|              | S.D. | 2030.83  | 2114.15             |

:Total profit: cost range = 1.0 {#tbl:total-profit-cost-1.0}

| budget range |      | Method I | Method  II          |
| ------------ | ---- | -------- | ------------------- |
| 1.0          | AVE. | 36902.72 | 37805.88 (16766.90) |
|              | S.D. | 2525.54  | 2225.28             |
| 1.5          | AVE. | 36902.72 | 38768.77 (15828.66) |
|              | S.D. | 2525.54  | 1764.1              |
| 2.0          | AVE. | 36607.47 | 40448.55 (10351.46) |
|              | S.D. | 1856.81  | 2866.84             |

:Total profit: cost range = 1.5 {#tbl:total-profit-cost-1.5}

| budget range |      | Method I | Method  II          |
| ------------ | ---- | -------- | ------------------- |
| 1.0          | AVE. | 38209.03 | 43851.34 (21754.78) |
|              | S.D. | 3554.65  | 3058.02             |
| 1.5          | AVE. | 39611.05 | 43919.75 (18751.78) |
|              | S.D. | 2846.27  | 2792.43             |
| 2.0          | AVE. | 38209.03 | 43851.34 (15367.51) |
|              | S.D. | 3554.65  | 3058.02             |

:Total profit: cost range = 2.0 {#tbl:total-profit-cost-2.0}

[@tbl:pareto-total-profit-cost-1.0]〜[@tbl:pareto-total-profit-cost-2.0]はそれぞれ，コストを発生させる乱数の幅が1.0，1.5，2.0の場合のパレート効率な状態の総利益を表す．

| budget range |      | Pareto efficient |
| ------------ | ---- | ---------------- |
| 1.0          | AVE. | 36036.98         |
|              | S.D. | 1518.70          |
| 1.5          | AVE. |                  |
|              | S.D. |                  |
| 2.0          | AVE. | 18710.05176      |
|              | S.D. | 1012.244173      |

:Pareto efficient total profit: cost range = 1.0 {#tbl:pareto-total-profit-cost-1.0}

| budget range |      | Pareto efficient |
| ------------ | ---- | ---------------- |
| 1            | AVE. | 45058.95         |
|              | S.D. | 3058.41          |
| 1.5          | AVE. | 45782.38         |
|              | S.D. | 2852.03          |
| 2            | AVE. | 41655.46         |
|              | S.D. | 2803.00          |

:Pareto efficient total profit: cost range = 2.0 {#tbl:pareto-total-profit-cost-1.5}

| budget range |      | Pareto efficient |
| ------------ | ---- | ---------------- |
| 1            | AVE. | 45058.95         |
|              | S.D. | 3058.41          |
| 1.5          | AVE. | 45782.38         |
|              | S.D. | 2852.03          |
| 2            | AVE. | 45291.11         |
|              | S.D. | 2717.13          |

:Pareto efficient total profit: cost range = 2.0 {#tbl:pareto-total-profit-cost-2.0}



[@tbl:total-providers-profit-cost-1.0]〜[@tbl:total-providers-profit-cost-2.0]はそれぞれ，コストを発生させる乱数の幅が1.0，1.5，2.0の場合の総提供企業利益を表す．

| budget range |      | Method I    | Method  II  |
| ------------ | ---- | ----------- | ----------- |
| 1.0          | AVE. | 17315.32261 | 11551.08103 |
|              | S.D. | 734.0264273 | 955.0962476 |
| 1.5          | AVE. |             |             |
|              | S.D. |             |             |
| 2.0          | AVE. | 19884.95    | 20568.41    |
|              | S.D. | 1051.93     | 2401.79     |

:Total providers profit: cost range = 1.0 {#tbl:total-providers-profit-cost-1.0}

| budget range |      | Method I | Method  II |
| ------------ | ---- | -------- | ---------- |
| 1.0          | AVE. | 17036.37 | 10374.55   |
|              | S.D. | 1254.36  | 852.64     |
| 1.5          | AVE. | 18536.52 | 14235.44   |
|              | S.D. | 973.67   | 1140.87    |
| 2.0          | AVE. | 19573.81 | 19505.66   |
|              | S.D. | 1078.97  | 2472.13    |

:Total providers profit: cost range = 1.5 {#tbl:total-providers-profit-cost-1.5}

| budget range |      | Method I | Method  II |
| ------------ | ---- | -------- | ---------- |
| 1.0          | AVE. | 17306.81 | 10313.82   |
|              | S.D. | 1401.15  | 1264.47    |
| 1.5          | AVE. | 18788.24 | 14421.84   |
|              | S.D. | 1387.68  | 1180.74    |
| 2.0          | AVE. | 19512.30 | 18204.50   |
|              | S.D. | 1646.21  | 2811.85    |

:Total providers profit: cost range = 2.0 {#tbl:total-providers-profit-cost-2.0}

[@tbl:total-requesters-profit-cost-1.0]〜[@tbl:total-requesters-profit-cost-2.0]はそれぞれ，コストを発生させる乱数の幅が1.0，1.5，2.0の場合の総要求企業利益を表す．

| budget range |      | Method I   | Method  II |
| ------------ | ---- | ---------- | ---------- |
| 1.0          | AVE. | 17558.1451 | 7304.90114 |
|              | S.D. | 1085.63837 | 744.305333 |
| 1.5          | AVE. |            |            |
|              | S.D. |            |            |
| 2.0          | AVE. | 14726.80   | 5537.81    |
|              | S.D. | 1110.12    | 898.17     |

:Total requesters profit: cost range = 1.0 {#tbl:total-requesters-profit-cost-1.0}

| budget range |      | Method I | Method II |
| ------------ | ---- | -------- | --------- |
| 1            | AVE. | 19866.35 | 10664.43  |
|              | S.D. | 1361.02  | 1665.16   |
| 1.5          | AVE. | 17958.01 | 8704.66   |
|              | S.D. | 1596.76  | 2094.6    |
| 2            | AVE. | 17033.66 | 8525.64   |
|              | S.D. | 975.22   | 1885.99   |

:Total requesters profit: cost range = 1.5 {#tbl:total-requesters-profit-cost-1.5}

| budget range |      | Method I | Method  II |
| ------------ | ---- | -------- | ---------- |
| 1.0          | AVE. | 21714.97 | 10946.02   |
|              | S.D. | 1698.46  | 4489.54    |
| 1.5          | AVE. | 20822.81 | 10746.13   |
|              | S.D. | 1576.64  | 2112.11    |
| 2.0          | AVE. | 18696.73 | 10279.33   |
|              | S.D. | 2154.17  | 1801.65    |

:Total requesters profit: cost range = 2.0 {#tbl:total-requesters-profit-cost-2.0}



## 考察

## まとめ

本章では手法Iと手法IIの比較を行った．手法Iの総利益に対する虚偽申告企業による損失が，手法IIの仮想的な買い手による総利益の損失より大きいことがわかった．従って虚偽申告を行う企業が発生する可能性がある場合は手法IIを用いる方が良いと考える．また手法IIでは余剰利益が発生するので総提供企業利益，総要求企業利益は手法Iより低い結果となった．また手法IIではこの余剰利益の運用を考える必要がある．
