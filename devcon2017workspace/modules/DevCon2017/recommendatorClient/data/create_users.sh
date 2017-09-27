#!/usr/bin/env zsh

echo ". /home/carlos/projects/liffey/shellscripts/functionsDXP.sh"
#. /home/carlos/projects/liffey/shellscripts/functionsDXP.sh

while read line 
do
 echo "liffey-add-user $line"
 #liffey-add-user $line
done < users.txt
