@echo off
chcp 65001 >nul
echo 水浒Roguelite · 启动中...
java -cp target/classes -Dfile.encoding=UTF-8 com.shuihu.demo.Main
pause
