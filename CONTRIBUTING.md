# Contributing

When contributing to this repository, please first discuss the change you wish to make on [Discussions area](https://github.com/LefterisXris/CodeTour/discussions) or via issue, email, or any other method with the owners of this repository before making a change.

**Note**: Please note we have a code of conduct, please follow it in all your interactions with the project.

## Search for potential issues for Contribution

To find new issues to contribute to, navigate to the [Issues section](https://github.com/LefterisXris/CodeTour/issues) of CodeTour's repository and select any of the available issues in `Backlog` state (you may use the filters).
If you want to contribute on a issue that is already in states [Sprint|In progress] please refrain from doing it, as the issue would have probably been broken down and planned for the upcoming release.

## Contributing on New Issues or New Bugs

If you have found a bug (or you want a new feature) that is not present on [CodeTour's Issues](https://github.com/LefterisXris/CodeTour/issues), please first double check the list to make sure there is not anything similar. 
If there isn't, proceed on opening the new issue following the structure of the available Github [Issue templates](https://github.com/LefterisXris/CodeTour/tree/main/.github/ISSUE_TEMPLATE).

## Pull Request Process

- To open Pull Requests, please use the available [Pull Request template](https://github.com/LefterisXris/CodeTour/tree/main/.github/PULL_REQUEST_TEMPLATE)
- Make sure your code is documented enough for the review process
- Format your code based on the Code Conventions mentioned below
- Commit only changes related to the context of this PR
- Ensure all automation status (build, test, compatibility, code analysis, signing) have passed
- Update CHANGELOG.md with a short description of your change
- Request review

## Code Conventions

### Code Style
- Max line limit: `120`
- Line Separator: `LF`
- Indent Spaces: `3`

### Development Guidelines
- Communication should be performed only through `Message Bus` in a publish-subscribe logic. It's preferable to use a new TOPIC for different features
- For utilizing IntelliJ SDK please refer to [IntelliJ Platform SDK DevGuide](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- To find SDK examples from other plugins please refer to [IntelliJ Platform Explorer](https://plugins.jetbrains.com/intellij-platform-explorer/extensions)
- For UI related issues, always check [IntelliJ Platform UI Guidelines](https://jetbrains.github.io/ui/)

### Type of Commits

- `feat`: A new feature adding to a particular application
- `fix`: A bug fix
- `style`: Feature and updates related to styling
- `refactor`: Refactoring a specific section of the codebase
- `test`: Everything related to testing
- `docs`: Everything related to documentation
- `chore`: Regular code maintenance
