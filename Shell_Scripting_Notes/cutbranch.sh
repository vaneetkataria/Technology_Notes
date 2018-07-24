##Listing Current Git Branches showing current branch marked with *
echo "Below is the list of current branches. Current switched branch is marked with *."
git branch

##Taking Input for changing current branch.
echo "Enter y if you want to switch to some other parent branch : "
read changeBranch
if [ $changeBranch = "y" ] ; then
echo "Enter Parent branch name to switch to :"
read changedBranch
git checkout $changedBranch
fi

##Taking Inputs for new branch to be cut. 
echo "Please enter pom version of presently switched parent branch : "
read parentPomVersion
echo "Please enter branch name for cutting a new branch : "
read branchName
echo "Please enter pom version of new  branch being cut : "
read version

##Cutting and checking out new branch.
git checkout -b $branchName

##Displaying and replacing pom version number in new branch files .
echo "Going to replace branch version in text <version>$parentPomVersion</version> with <version>$version</version> in below listed pom files ."
grep -C3 $parentPomVersion pom.xml
grep -r -C4  $parentPomVersion ./**/pom.xml

echo "Enter y if you want to continue : "
read contnue
if [ $contnue = "y" ] ; then
echo "Replacing ...."
sed -i s/$parentPomVersion/$version/g pom.xml
sed -i s/$parentPomVersion/$version/g ./**/pom.xml

##Opening Difftool
git difftool

##Inputs for pushing branch to remote.
echo "Enter y if you want to push the branch now?"
read push
if [ $push = "y" ] ; then 
git push --set-upstream origin $branchName
git add .
git commit -m "Chnaged pom version from $parentPomVersion to $version."
git push
echo "Branch $branchName cut , switched and pushed to remote repository successfully."
else
echo "Branch $branchName cut and switched successfully."
fi

fi



