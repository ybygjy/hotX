#! /bin/bash

# update timeout(sec)
SO_TIMEOUT=60

rm -rf .hotX

# download from aliyunos
echo "downloading...";
wget http://hotx.oss-cn-hangzhou-zmf.aliyuncs.com/hotX.tar.gz

tar -xzvf hotX.tar.gz

rm -f hotX.tar.gz

mv .hotX/hotX.sh hotX.sh

chmod +x hotX.sh

# done
echo "hotX install successed."


