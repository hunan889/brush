@echo off
set PATH=%CD%;%PATH%;
set SVN_PATH="https://svn.yy.com/dwjs/code/sportbrush/android/branches/sportbrush-android_dev_feature"
set TARGET_SVN_PATH="https://svn.yy.com/dwjs/code/jjww/android/trunk"
set CUSTOME_SVN_PATH="https://svn.yy.com/dwjs/code/sportbrush/android/customize/gamenews"

set TEMP_PATH="temp"
set TARGET_TEMP_PATH="target"
set CUSTOMIZE_BASE="customization"
if exist %TEMP_PATH% rd %TEMP_PATH% /s /q
if exist %TARGET_TEMP_PATH% rd %TARGET_TEMP_PATH% /s /q
if exist %CUSTOMIZE_BASE% rd %CUSTOMIZE_BASE% /s /q
svn checkout %SVN_PATH% %TEMP_PATH%
svn checkout %TARGET_SVN_PATH% %TARGET_TEMP_PATH%
svn checkout %CUSTOME_SVN_PATH% %CUSTOMIZE_BASE%
java -jar "%~dp0\switcher.jar" -t %TEMP_PATH% -s %CUSTOMIZE_BASE%

rd %TEMP_PATH%\.svn /s /q
rem 备份svn文件
XCOPY /e /i /h /y %TARGET_TEMP_PATH%\.svn .svn
rem 删除目标文件夹下的文件（确保所有文件被新的文件覆盖，如果有新文件有删除，也同样可以删除）
del %TARGET_TEMP_PATH%\*.* /f /s /q /a
rem 拷贝文件到目标文件夹内
XCOPY /e /i /h /y %TEMP_PATH%\*.* %TARGET_TEMP_PATH%
rem 拷贝svn文件回目标文件夹（用于同步svn）
XCOPY /e /i /h /y .svn %TARGET_TEMP_PATH%\.svn
rd .svn /s /q
rd %TEMP_PATH% /s /q
rd %CUSTOMIZE_BASE% /s /q

pause