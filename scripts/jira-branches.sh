git fetch --all

echo Delete stale remote-tracking branches under 'origin' repository.
git remote prune origin

FILE=$(cd $(dirname "$0"); pwd)/jira-branches.js

echo Update git branches list file.

echo "var \$branches = \"\\" > $FILE

git branch -a | grep remotes/origin/ignite- | sed 's/remotes\/origin\/ignite\-\(.*\)$/IGNITE-\1 \\/g' >> $FILE

echo '";' >> $FILE

echo "var \$branchesDate = '$(date +"%Y-%m-%d")';" >> $FILE

echo Open in browser $(dirname $FILE)/jira-branches.html
