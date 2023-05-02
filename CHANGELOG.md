<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# CodeTour Changelog

## [Unreleased]

### Support

- Support for Jetbrains' products 2023.* releases

## [0.0.7]

### Support

- Support for Jetbrains' products 2022.3 and future releases

## [0.0.6]

### Added

- Enhance File path handling for more flexible Navigation (includes directory path as prefix)
- Tour's summary on Step's description including the current and the total number of Steps of the Tour
- Fix potential conflict with existence of files with the same name by prompting a picker dialog to user
- Disable Prev/Next buttons when no Prev/Next Step exists accordingly
- Introduce validation logic to make sure available tours are valid (currently checks for target file existence)
- Added a default Virtual Onboarding Assistant as a sample Tour. Enabled by default

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