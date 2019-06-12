# Sometimes it's a README fix, or something like that - which isn't relevant for
# including in a project's CHANGELOG for example
declared_trivial = github.pr_title.include? "#trivial"

# Make it more obvious that a PR is a work in progress and shouldn't be merged yet
warn("PR is classed as Work in Progress") if github.pr_title.include? "[WIP]"

# Warn when there is a big PR
warn("Big PR") if git.lines_of_code > 500

# Don't let testing shortcuts get into master by accident
fail("fdescribe left in tests") if `grep -r fdescribe specs/ `.length > 1
fail("fit left in tests") if `grep -r fit specs/ `.length > 1

# Do not show out of range issues, not caused by the current PR
#github.dismiss_out_of_range_messages()

# Detekt
#kotlin_detekt.report_file = "/home/travis/build/kotomisak/security-showcase-android/build/reports/detekt/detekt.xml"
#kotlin_detekt.gradle_task = "detekt"
#kotlin_detekt.severity = "warning"
#kotlin_detekt.detekt(inline_mode: true)


## AndroidLint
#android_lint.skip_gradle_task = true
#android_lint.severity = "warning"
android_lint.gradle_task = "runAndroidLintAndMergeResults"
android_lint.report_file = "./build/reports/android-lint/android-lint.xml"
android_lint.lint(inline_mode: true)
