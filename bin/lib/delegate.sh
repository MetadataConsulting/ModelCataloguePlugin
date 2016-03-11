#!/usr/bin/env bash

###
# prints single command with it's description using the first line of the text file named same as the command
# but having .txt extension.
###
function print_command() {
    local PADDING='               '
    local FOLDER=$1
    local COMMAND=$2
    local DESC="(no description provided)"

    if [ -f "$FOLDER/$COMMAND.txt" ] ; then
        DESC=$(head -n 1 "$FOLDER/$COMMAND.txt")
    fi

    printf "%s %s %s\n" "$COMMAND" "${PADDING:${#COMMAND}}" "$DESC"
}

###
# prints usage for folder listing all *.sh files and directories (except lib directory)
# @param FOLDER folder to be listed
###
function print_usage_for_folder(){
    local ORIGINAL_DIRECTORY=$(pwd)
    cd "$1"
    echo "Usage: (run './catalogue <command> --help' to get help for specific command)"
    for f in * ; do
        if [ "$f" == "lib" ] ; then
            continue
        elif [ -d "$f" ]; then
            print_command $(pwd) "$f"
        elif [[ "$f" == *.sh ]]; then
            local COMMAND_NAME="${f/.sh/}"
            print_command $(pwd) "$COMMAND_NAME"
        fi
    done
    cd "$ORIGINAL_DIRECTORY"
}

###
# delegates to scripts inside folder
# @param FOLDER folder to be delegated to
# @param COMMAND name of the script to be delegated to
###
function delegate_to_folder() {
    local FOLDER="$1"
    local SCRIPT="$2"
    local COMMAND="$FOLDER""/""$SCRIPT"

    shift 2


    if [ "$1" == "--help" ] || [ "$1" == "--help" ] ; then
        if [ -f "$COMMAND.txt" ] ; then
            echo
            cat "$COMMAND.txt"
            if [ -d "$COMMAND" ]; then
                echo
                echo
                print_usage_for_folder "$COMMAND"
            fi
            echo
            echo
        fi
    elif [ -d "$COMMAND" ]; then
        delegate_to_folder "$COMMAND" "$@"
    elif [ -f "$COMMAND.sh" ]; then
        "$COMMAND.sh" "$@"
    else
        COMMAND=${COMMAND//bin\///}
        COMMAND=${COMMAND//\// }
        echo
        echo "Unknown command:$COMMAND"
        echo
        print_usage_for_folder "$FOLDER"
        echo
        exit 2
    fi
}
