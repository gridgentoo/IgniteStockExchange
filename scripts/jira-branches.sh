GIT_HOME=$1

if [ "${GIT_HOME}" != "" ]; then
	cd $GIT_HOME
fi

echo "At git home: $(pwd)"

git fetch --all

echo Delete stale remote-tracking branches under 'origin' repository.
git remote prune origin

FILE=$(cd $(dirname "$0"); pwd)/jira-branches.js

echo Update git branches list file.

echo "var \$branches = \"\\" > $FILE

git branch -a >> $FILE

echo '";' >> $FILE

echo "var \$branchesDate = '$(date +"%Y-%m-%d")';" >> $FILE
