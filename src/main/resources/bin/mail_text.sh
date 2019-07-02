#!/bin/bash - 
#===============================================================================
#
#          FILE: mail_text.sh
# 
#         USAGE: ./mail_text.sh 
# 
#   DESCRIPTION: 
# 
#       OPTIONS: ---
#  REQUIREMENTS: ---
#          BUGS: ---
#         NOTES: ---
#        AUTHOR: zhaoj (Admin), zj_400@163.com
#  ORGANIZATION: 
#       CREATED: 07/19/2018 06:34:03 PM
#      REVISION:  ---
#===============================================================================

set -o nounset                              # Treat unset variables as an error

serverName=mstore_inhdfs

ps -ef|grep ${serverName}|grep -v grep

if [ $? -eq 0 ] ; then
    exit 0
fi


ip=`/sbin/ifconfig -a|grep inet|grep -v 127.0.0.1|grep -v inet6|awk '{print $2}'|tr -d "addr:"`

time=$(date "+%Y-%m-%d %H:%M:%S")

export JAVA_HOME=/disk2/shell/jdk1.8.0_151
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar

target_path=$(cd `dirname $0`; pwd)
cd $target_path

java -cp script-mail.jar com.script.mail.TextMailUtil email.txt -serverName $serverName -ip $ip --time="$time"

exit 0
