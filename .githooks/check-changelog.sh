#!/bin/sh
# Check the structure of CHANGELOG.md (staged version, or the file given as argument)
if [ -n "$1" ]; then
  content=$(cat "$1") || exit 1
else
  content=$(git show :CHANGELOG.md) || exit 1
fi
printf '%s\n' "$content" | awk '
  function err(msg) { print "CHANGELOG.md:" NR ": " msg; bad = 1 }
  NR == 1 && $0 != "# Changelog" { err("first line must be \"# Changelog\"") }
  /^## / {
    if ($0 ~ /[ \t]$/) err("trailing whitespace in heading \"" $0 "\"")
    if (seen[$0]++) err("duplicate heading \"" $0 "\"")
    sections++
    if (sections == 2 && !entries) err("empty section \"" first "\"")
    if (sections == 1) first = $0
    next
  }
  sections == 1 && NF {
    if (/^###/) err("sub-headings (###) are not allowed")
    else if (/^- (\[[^]]+\])+ [^ ]/ || /^  +- [^ ]/) entries++
    else err("entry must be \"- [tag] text\" or an indented \"  - text\": " $0)
  }
  END {
    if (sections == 1 && !entries) err("empty section \"" first "\"")
    exit bad
  }
' || { echo "pre-commit: invalid CHANGELOG.md structure (bypass with git commit --no-verify)"; exit 1; }
