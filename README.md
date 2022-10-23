logdog
========
一款用于实时展示Android应用程序中logcat日志的库, 特别适用于给用户展示软件运行过程中的关键日志信息.

Screenshots
--------------
![image](https://github.com/nondeepshit/logdog/blob/main/art/Screenshot_1.png)

Usage
-------------
To use Logdog Android library and get your logcat inside your app, follow these steps:
* 1. Add ``Logdog`` **to your layouts** and configure it as you wish.
```xml
<com.github.nodeepshit.LynxView xmlns:lynx="http://schemas.android.com/apk/res-auto"
        android:id="@+id/lv_logdog"
        android:layout_width="match_parent"
        android:layout_height="300dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        lynx:max_traces_to_show="1000"
        lynx:sampling_rate="0"
        lynx:text_size="12sp" />
```
* 2. Use ``Logdog`` to print any log you concern.
```java

Logdog.tip("this is tip message");
Logdog.debug("this is debug message");
Logdog.info("this is info message");
Logdog.warning("this is warning message");
Logdog.error("this is error message");

```
