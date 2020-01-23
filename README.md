# 実験用プログラム

## 実行方法
1. CPLEXをインストールする
2. `mkdir ./Bid`
3. `mkdir ./libs`
4. `./libs`に`cplex.jar`をコピーする
5. JVM Option に `-Djava.library.path=${CPLEX_PATH}`を指定して`kotlin/auction/Run.kt`を実行する

実験を行うにあたってconfig.jsonで設定すべき項目

```
bidDir // データセットのrootディレクトリ
targetData // データセットを複数指定する (bidDir/targetData)を回すことができる
targetAuction = ["PaddingMethod","利益最大化-平均"] //実行したいオークションを指定
targetDataIterate //施行回数
resource  //resourceの種類
provider　// provider数
requester // requester数
period // 対象期間
bidNumber // 1要求企業が作成する入札数
providerResourceNumber　//1提供企業が提供するリソースの種類
```

​	

## データセット生成

`kotlin/dataset/RunData.kt`を実行すればデータセットを`./$bidDir`に生成できる．以下の項目を調節できる．

データ生成にあたってconfig.jsonで設定すべき項目

```
"bidDir": "Bid/real-case/req4-pro6",
"targetDataIterate" : 10 ,

"provider": 150, 
"providerResourceNumber": 2,
"providerTimeMax": 200.0, //最大提供時間
"providerTimeMin": 100.0, //最小提供時間
"providerValueMax": 4.0,
"providerValueMin": 2.0, //1[Ts]あたりのコストの乱数の最小値

"requester": 30,
"bidNumber": 5,
"requesterTimeMax": 200.0, //1リソースあたりの最大要求時間
"requesterTimeMin": 0.0, //1リソースあたりの最小要求時間
"requesterValueMax": 5.0,
"requesterValueMin": 3.0, //1[Ts]あたりの予算の乱数の最小値
```

虚偽申告データを作成するなら，`Lie`がついているものを使用する．

## 結果集計

`kotlin/result/analysis/Analysis.kt`を実行すると結果を集計できる．

結果集計にあたってconfig.jsonで設定すべき項目

```
 "bidDir": "Bid/real-case/req4-pro6",
 "targetAuction": [
    "PaddingMethod"
  ],
  "targetData": [
    "truthful-data"
  ],
```

どのオークションのどのデータセットで結果を集計するかを指定する．