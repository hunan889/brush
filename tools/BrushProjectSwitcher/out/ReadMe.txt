该工具用于刷子项目进行各刷子之间的切换

switcher.jar是一个进行文件修改和替换的工具，读取config.xml里面的内容进行替换和修改操作

项目以体育刷子的dev分支为基础（所有代码都在这个分支上进行修改），其它刷子保留各自的配置文件（位置：https://svn.yy.com/dwjs/code/sportbrush/android/customize）

配置switcher_sportbrush2%PROJECT_NAME%.bat，以设置以下参数：
1. SVN_PATH： 体育刷子 dev分支的svn位置（固定为https://svn.yy.com/dwjs/code/sportbrush/android/branches/sportbrush-android_dev_feature）
2. TARGET_SVN_PATH： 需要merge回的工程路径（如游戏刷子的trunk：https://svn.yy.com/dwjs/code/jjww/android/trunk）
3. CUSTOME_SVN_PATH: 该工程的配置文件路径（如https://svn.yy.com/dwjs/code/sportbrush/android/customize/gamenews)




说明:
1. config.xml文件里面的字符需要转义，否则在parse的时候会出错
2. 有两种类型：modify类型会把目标路径下的文件中符合map中的key值换成value值。 replace类型会用配置文件夹下面的文件替换目标路径下的文件（整个文件夹替换，缺少的会删除）
3. 运行完成之后会生成target文件夹，此文件夹下的工程即为修改完成后的工程代码，检查后提交即可



如有新的刷子项目，请做以下事情：
1. 参考上面的配置，新建一个该刷子的bat文件，将路径改为相应的路径
2. 新增一份配置文件