# tekla-structures-material-list
做給家人用的，供參考
版本2021/8/3

執行sym轉excel
java auto

編譯(compile)sym轉excel
javac auto.java


執行統計材料
java sum

編譯(compile)統計材料
javac -encoding utf-8 sum.java

執行切割材料
java cut 專案名

編譯(compile)切割材料
javac -encoding utf-8 cut.java
---------------------------------

操作步驟(sym轉excel)
1.把全部的.sym檔丟入in資料夾
2.把範例excel檔命為in.xls(2003版的excel)丟入in資料夾，其中第一頁為材料資料，第二頁為輸出表格
3.去input.txt內一行打一個檔案名

  如:5001.sym與5002.sym則輸入
5001
5002   <---不需要留空格或enter鍵

4.按住shift鍵+右鍵後，滑鼠選取"在此處開起命令視窗"
5.終端機輸入java auto後按enter，看到End即為結束
6.去out資料夾開啟5001.xls(檔名為input裡第一行的文字)
7.第一頁隨意刪除一個字後重新輸入再按enter，讓excel重新讀取資料(切記不要更改資料)

操作步驟(統計材料數量)
1.input.txt第一行要跟out資料夾裡的檔案名稱一樣
2.執行java sum
3.去out資料夾開啟5001Sum.xls(檔名為input裡第一行的文字)

---------------------------------

移植其他電腦
1.C:\Program Files下面創建空資料夾，Java
2.把"安裝Java"裡的jdk-13.0.2資料夾，與"安裝軟體"裡的commons-collections4-4.4，commons-compress-1.20，commons-math3-3.6.1，poi-4.1.2放入Java中
3.按windoes鍵+R，搜索control選取"系統" -> "進階系統設定" -> "進階" -> "環境變數"
4.系統變數(S)的方格中找Path點選編輯,將安裝Java裡的安裝變數C:\Program Files\Java\jdk-13.0.2\bin貼到變數值(V)的最前面，並以 ; 分隔
(意即開頭貼上C:\Program Files\Java\jdk-13.0.2\bin;)
5.按下確認後按"新增(W)"，並將資料填入(大小寫要對)

變數名稱(N):CLASSPATH
變數值(V):C:\Program Files\Java\poi-4.1.2\poi-4.1.2.jar;C:\Program Files\Java\poi-4.1.2\poi-ooxml-schemas-4.1.2.jar;C:\Program Files\Java\poi-4.1.2\poi-ooxml-4.1.2.jar;C:\Program Files\Java\poi-4.1.2\ooxml-lib\xmlbeans-3.1.0.jar;C:\Program Files\Java\poi-4.1.2\ooxml-lib\curvesapi-1.06.jar;C:\Program Files\Java\commons-collections4-4.4\commons-collections4-4.4.jar;C:\Program Files\Java\commons-math3-3.6.1\commons-math3-3.6.1.jar;C:\Program Files\Java\commons-compress-1.20\commons-compress-1.20.jar;

6.確認後開啟終端機，打java -version，應該會顯示
openjdk version "13.0.2" 2020-1-14
OpenJDK Runtime Environment (build 13.0.2+8)
OpenJDK 64-Bit Server VM (build 13.0.2+8, mixed mode, sharing)
7.安裝完成

---------------------------------
