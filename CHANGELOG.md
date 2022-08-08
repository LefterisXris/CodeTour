<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# CodeTour Changelog

## [Unreleased]
### Added
- navigation support for directory attribute in step
- navigation support for file path in step file attribute

## [0.0.5]
### Added
- Compatibility with intelliJ 2022-*

## [0.0.4]
### Added
- Description-Only Step (file:line made optional for a Step) so that a Step can render only the related description,
  with no navigation at all

## [0.0.3]
### Added
- New Icons
- New Step's Description default value is pre-selected for easier user input
- Added markdown support
- Sample Tours (for demonstration)
- Steps Reorder support (moveUp/moveDown)
- Preview mode on Step Description Editor
- Implemented a new enhanced Step Editor

## [0.0.2]
### Implemented
- Load from json, Show In Tool Pane, Navigate with click
- Navigate with Previous/Next buttons on Tool Pane
- Create|Delete Tour from Tool Pane
- Add|Delete Steps to a Tour (just file and line)
- Edit steps (CRUD + add text)
- Preview Step description on the code (as a resizable/movable modal, after Navigation)
- HTML support on steps
- Persisting user preferences on Size/Location of Step Documentation modal
- Gutter Icons for Steps on code
- Jump to Tour source file (via Tree click)
- Steps navigation through Step Documentation modal (arrow buttons)
- Added sample Tours on codebase for demonstration
- Added demo content on README (.gif and screenshots)
