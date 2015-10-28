#! /bin/bash

# exit shell with err_code
# $1 : err_code
# $2 : err_msg
exit_on_err()
{
    [[ ! -z "${2}" ]] && echo "${2}" 1>&2
    exit ${1}
}

# check greys permission
check_permission()
{
    [ ! -w ${HOME} ] \
        && exit_on_err 1 "permission denied, ${HOME} is not writeable."
}

reset_for_env()
{

    # if env define the JAVA_HOME, use it first
    # if is alibaba opts, use alibaba ops's default JAVA_HOME
    [ -z ${JAVA_HOME} ] && JAVA_HOME=/opt/taobao/java

    # check the jvm version, we need 1.6+
    local JAVA_VERSION=$(${JAVA_HOME}/bin/java -version 2>&1|awk -F '"' '/java version/&&$2>"1.5"{print $2}')
    [[ ! -x ${JAVA_HOME} || -z ${JAVA_VERSION} ]] && exit_on_err 1 "illegal ENV, please set \$JAVA_HOME to JDK6+"

    # reset BOOT_CLASSPATH
    [ -f ${JAVA_HOME}/lib/tools.jar ] && BOOT_CLASSPATH=-Xbootclasspath/a:${JAVA_HOME}/lib/tools.jar

    # reset CHARSET for alibaba opts, we use GBK
    [[ -x /opt/taobao/java ]]&& JVM_OPTS="${JVM_OPTS} -Dinput.encoding=GBK"

}

attach_jvm() {
    if [ x$1 == x ] || [ x$2 == x ]
    then
    echo 'please use ./start.sh [pid] [appName]'
    else
    sudo -u admin /opt/taobao/java/bin/java ${BOOT_CLASSPATH} ${JVM_OPTS} \
            -jar ./.hotX/hotX-agent.jar -pid $1 -appName $2
    fi
}

# the main
main()
{

    check_permission
    reset_for_env

    attach_jvm ${@}\
        || exit_on_err 1 "attach to target jvm(${1}) failed."

}



main "${@}"