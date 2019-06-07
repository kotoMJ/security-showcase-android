message "Dangerfile, processing started"

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

github.dismiss_out_of_range_messages

# AndroidLint
# android_lint.report_file = "app/build/reports/lint-results-debug.xml"
# android_lint.skip_gradle_task = true
# android_lint.severity = "Error"
# android_lint.lint(inline_mode: true)
# android_lint.gradle_task = "app:lintDebug"
# android_lint.report_file = "app/build/reports/lint-results-debug.xml"

message "Dangerfile, detekt.xml checking..."

if(File.exist?('/home/travis/build/kotomisak/security-showcase-android/build/reports/detekt/detekt.xml'))
	message "Dangerfile, detetk.xml detected..."
else
	message "Dangerfile, detetk.xml NOT on path specified!"
end

message "Dangerfile, detekt processing started..."

# Detekt
kotlin_detekt.report_file = "/home/travis/build/kotomisak/security-showcase-android/build/reports/detekt/detekt.xml"
#kotlin_detekt.skip_gradle_task = true
kotlin_detekt.gradle_task = "runChecksForDanger"
kotlin_detekt.severity = "error"
kotlin_detekt.detekt