#!/bin/sh

#变量声明

APP_NAME=BabySeePic
VERSION_CODE=$(grep versionCode AndroidManifest.xml | cut -d'"' -f 2)
VERSION_NAME=$(grep versionName AndroidManifest.xml | cut -d'"' -f 2)
OUTPUT_DIR=

CHANELID_NONE=CHANELID_NONE
CHANELID_1=$CHANELID_NONE
CHANELID_2=

DEBUG=debug
RELEASE=release
BUILD_MODE=$DEBUG

#初始化
init()
{
	echo
	echo [init]:
	echo
	OUTPUT_DIR=~/myapps/$APP_NAME/$VERSION_NAME/$BUILD_MODE
	echo 	create $OUTPUT_DIR folder
	mkdir -p $OUTPUT_DIR
	
	if [ $BUILD_MODE == $RELEASE ]; then
		sed -e s/'android:value="BAIDU_STAT_ID_NONE"'/'android:value="f0234c2f5c"'/g -i '' AndroidManifest.xml 
	fi

	check_ret
}

#重置
restore()
{
	echo
	echo [restore]:
	echo
	
	cp -fr bin/proguard $OUTPUT_DIR/proguard/
	
	if [ $BUILD_MODE == $RELEASE ]; then
		sed -e s/'android:value="f0234c2f5c"'/'android:value="BAIDU_STAT_ID_NONE"'/g -i '' AndroidManifest.xml
	else
		echo debug_mode
	fi
	
	#恢复渠道号的默认值
	CHANELID_2=$CHANELID_NONE
	echo CHANELID_1: $CHANELID_1 CHANELID_2: $CHANELID_2
	sed -e s/'android:value="'${CHANELID_1}'"'/'android:value="'${CHANELID_2}'"'/g -i '' AndroidManifest.xml 
	
	#clean
}

#检查命令是否执行成功
check_ret() {
    if [ $? != 0 ];then
        echo
        echo Failed!
        echo
        exit 1
    fi
}

clean()
{
	echo
	echo [clean]:
	echo
	
	ant clean
	check_ret
}

goToBuild()
{
	echo
	echo [goToBuild]:
	echo

	ant release
	check_ret
}	 

#根据传来的渠道号打包
build()
{
	echo
	echo [build]:
	echo 
	
	clean
	CHANELID_2=$1
	echo 	chanelId_1: $CHANELID_1 chanelId_2: $CHANELID_2
	
	#修改渠道号
	sed -e s/'android:value="'${CHANELID_1}'"'/'android:value="'${CHANELID_2}'"'/g -i '' AndroidManifest.xml 
	
	goToBuild
	
	cp bin/${APP_NAME}-release.apk $OUTPUT_DIR/$APP_NAME-$VERSION_NAME-$CHANELID_2.apk

	CHANELID_1=$CHANELID_2
}

#根据配置文件打包所有渠道
buildAll()
{
	echo
	echo [buildAll]:
	echo
	
	while read line
	do
	    CID=$(echo $line | grep -v ^# | cut -d':' -f1)
	    if [ x$CID = x ]; then
			continue
	    else
			build $CID
	    fi
	done < chanel_ids
}	

#初始化
if [ "x$1" = "x-r" ]; then
	BUILD_MODE=$RELEASE
else
	BUILD_MODE=$DEBUG
fi

init

#运行
if [ "x$1" = "x-r" ] && [ "x$2" = "x-all" ]; then
	buildAll
elif [ "x$1" = "x-r" ]; then
	build $2
else
	build debug
fi

#恢复
restore
