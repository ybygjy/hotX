#! /bin/bash

# update timeout(sec)
SO_TIMEOUT=60

rm -rf .hotX
rm -rf start.sh

# download from aliyunos
echo "downloading... ${TEMP_GREYS_FILE}";
wget http://076100.oss-cn-hangzhou.aliyuncs.com/hotX/hotX-agent.jar
wget http://076100.oss-cn-hangzhou.aliyuncs.com/hotX/start.sh

mkdir .hotX

cp hotX-agent.jar ./.hotX/hotX-agent.jar

rm -f hotX-agent.jar

chmod +x ./start.sh

# done
echo "hotX install successed."


