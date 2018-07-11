##Declaring Master version as 18.0 .
masterVersion=18.0
##Listing Current Git Branches showing current branch marked with *
echo "Below is the list of current branches. Current switched branch is marked with *."
git branch
##Taking Inputs for new branch to be cut. 
echo "Please enter branch name for cutting a new branch."
read branchName
echo "Please enter pom version for this branch"
read version
##Cutting and checking out new branch.
git checkout -b $branchName
##Displaying and replacing pom version number in new branch files .
echo "Going to replace branch version in text <version>$masterVersion</version> with <version>$version</version> in below listed pom files ."
grep -C3 $masterVersion pom.xml
grep -r -C4  $masterVersion ./**/pom.xml
echo "Enter y if you want to continue"
read contnue
if [ $contnue = "y" ] ; then
echo "Replacing"
sed -i s/$masterVersion/$version/g pom.xml
sed -i s/$masterVersion/$version/g ./**/pom.xml
##Opening Difftool
git difftool
##Inputs for pushing branch to remote.
echo "Enter y if you want to push the branch now?"
read push
if [ $push = "y" ] ; then 
git push --set-upstream origin $branchName
echo "Branch $branchName cut , switched and pushed to remote repository successfully."
else
echo "Branch $branchName cut and switched successfully."
fi
fi

