#! /bin/bash

# update timeout(sec)
SO_TIMEOUT=60

rm -rf .hotX
rm -rf hotX.sh

# download from aliyunos
echo "downloading... ${TEMP_GREYS_FILE}";
wget http://076100.oss-cn-hangzhou.aliyuncs.com/hotX/hotX-agent.jar
wget http://076100.oss-cn-hangzhou.aliyuncs.com/hotX/hotX-core.jar
wget http://076100.oss-cn-hangzhou.aliyuncs.com/hotX/hotX-lib.jar
wget http://076100.oss-cn-hangzhou.aliyuncs.com/hotX/hotX.sh

mkdir .hotX

cp hotX-agent.jar ./.hotX/hotX-agent.jar
cp hotX-core.jar ./.hotX/hotX-core.jar
cp hotX-lib.jar ./.hotX/hotX-lib.jar

rm -f hotX-agent.jar
rm -f hotX-core.jar
rm -f hotX-lib.jar

chmod +x ./hotX.sh

# done
echo "hotX install successed."


