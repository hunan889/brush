echo "copying file libUdbAndroidSdk.so(arm) ..." > pub.log 
copy /Y "..\UdbSdk\libs\armeabi\libUdbAndroidSdk.so" ".\libs\armeabi\libUdbAndroidSdk.so" >> pub.log 2>&1
echo "copying file libUdbAndroidSdk.so(x86) ..." >> pub.log 
copy /Y "..\UdbSdk\libs\x86\libUdbAndroidSdk.so" ".\libs\x86\libUdbAndroidSdk.so" >> pub.log 2>&1
echo "copying file udbsdk.jar ..." >> pub.log
copy /Y "..\UdbSdk\bin\udbsdk.jar" ".\libs\udbsdk.jar" >> pub.log 2>&1