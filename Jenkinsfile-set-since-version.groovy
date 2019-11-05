println "NXRM version: ${params.nxrm_version}"
println "release branch: ${params.release_branch}"
println "tracking_branch: ${params.tracking_branch}"

deleteDir()
checkout scm

runSafely "git checkout ${params.release_branch}"

runSafely "git checkout -b jenkins_work"

String versionUpdater = "update-since-version.groovy"

if (!runGroovy(versionUpdater, params.nxrm_version)) {
  throw new RuntimeException("Version update failed")
}

runSafely "git commit -m \"updated source version to ${params.nxrm_version}\""

commitAndPushToExistingBranch(params.release_branch)

commitAndPushToExistingBranch(params.tracking_branch)


void commitAndPushToExistingBranch(String branch) {
  println "going to push changes to ${branch}"

  runSafely "git checkout ${branch}"
  runSafely "git pull"
  runSafely "git cherry-pick jenkins_work"

  try {
    runSafely "git push"
  } catch (Exception e) {
    runSafely "git reset --hard"
    runSafely "git pull"
    runSafely "git cherry-pick jenkins_work"
    runSafely "git push"
  }

  println "push to ${branch}' - OK"
}