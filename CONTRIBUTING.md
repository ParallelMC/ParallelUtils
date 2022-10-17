# Contributing to ParallelUtils

Thank you for considering contributing to ParallelUtils!

These guidelines help ensure that contributors can effectively make contributions to the project in a smooth manner,
minimizing the time taken by the developers and maintainers to address issues and assess changes.


**Table of Contents**

- [Code of Conduct](#code-of-conduct)
- [Environment](#environment)
- [How Can I Contribute?](#how-can-i-contribute)
  - [Reporting Issues](#reporting-issues)
  - [Feature Requests](#feature-requests)
  - [Your First Contribution](#your-first-contribution)
  - [Submitting Your Contribution](#submitting-your-contribution)
- [Styleguides](#styleguides)
  - [Git Commit Messages](#git-commit-messages) 

## Code of Conduct

This project and everyone participating in it is governed by the [ParallelMC Code of Conduct](https://github.com/ParallelMC/ParallelUtils/blob/main/CODE_OF_CONDUCT.md)

## Environment

The development is a work in progress, so it's kind of a mess right now and not in a final state
(we are always open to contributions to improve the development and build process!)

We use Gradle as our build system for its tight integration with Paper and helpful development tools.
Primarily, the gradle plugins `xyz.jpenilla.run-paper` and `io.papermc.paperweight.userdev` are used to run development servers
and to automatically create plugin jar files.

ParallelUtils is broken into two components: The API and the modules.

The API contains utility code, our command system,
and functionality to load and unload modules at runtime. This is all code needed for all plugins to run effectively.

The modules are the functional components of the plugin. The structure of modules allows for easy expansion of the plugin.

The root directory's gradle script includes gradle plugins and dependencies that are common between the API and modules.
This file rarely needs to be edited, such as when the Paper API is updated.

- `api/` - This directory contains the code and build script for the API. All non-plugin dependencies needed by modules
(those that need to be integrated into the plugin JAR), must be added here. This will likely be changed in the future.
This also contains the configuration information oif the plugin, which includes dependencies and commands. 
The commands will be removed from here in the future, favoring an API interface for modules to hook their own commands.
To build the API, run `./gradlew api:reobfJar`.
To run the development server, run `./gradlew api:runServer`.
- `docs/` - This directory contains a submodule of the [Parallel-Documentation](https://github.com/ParallelMC/Parallel-Documentation) repository. 
This repository will soon be removed in favor of the Wiki page on the ParallelUtils repo.
- `libs/` - This directory should contain libraries that are linked as jar files. Currently, this includes the Parallel
version of FractalForest (not currently public), [Pronouns](https://www.spigotmc.org/resources/pronouns.86199/), and
the Parallel version of VoteParty (also not currently public).
- `modules/` - This directory contains the code and build script for the modules. The build script here contains compileOnly dependencies for all modules
as well as custom code to automatically generate tasks to build each module separately.
To build a module, run `./gradlew modules:reobf-moduleName`, where moduleName is the directory name of the module.

## How Can I Contribute?

### Reporting Issues

If you believe you have found a bug with ParallelUtils or its modules, report it in an issue [here](https://github.com/ParallelMC/ParallelUtils/issues/new?assignees=jakebacker&labels=bug&template=bug_report.md&title=%5BBUG%5D+).
Please include a descriptive title and details of the bug, so we can properly evaluate and fix it.
Please do _not_ report issues in Github that are unrelated to ParallelUtils (such as bugs with other plugins on our server).
For these issues, create a ticket in our [Discord](https://discord.gg/7PSDuCbg7Y) server!

### Feature Requests

If you would like to request a new feature for ParallelUtils or its modules, create an issue [here](https://github.com/ParallelMC/ParallelUtils/issues/new?assignees=jakebacker&labels=enhancement&template=feature_request.md&title=%5BFEATURE%5D).
The developer team will evaluate the request and determine if it can be added to our workload.
If it cannot, but we still determine the feature is worthwhile, we will label the issue with `help-wanted`.

### Your First Contribution

Take a look at issues with the `good first issue` and `help wanted` labels:
- `good first issue` - Should only require a few lines of code or are otherwise very simple
- `help wanted` - A bit more involved than `good first issue`. These are generally issues that the development team 
doesn't have the time to pick up.

Setting up a development environment for ParallelUtils can be a bit tricky:

- If you are modifying an existing module, simply fork and clone ParallelUtils.
- First, ensure that you have Git installed. Second, create a public GitHub repository. This will house your module.
Next, fork and clone ParallelUtils. Now, you must add a submodule using `git submodule add repoURL modules/src/main/java/parallelmc/parallelutils/modules/<moduleName>`
Next, run `git submodule update --init modules/src/main/java/parallelmc/parallelutils/modules/<moduleName>`. 
These steps will allow you to use our development environment for creating the module and will prepare your fork for an eventual Pull Request.

### Submitting Your Contribution

Create a Pull Request! Please add a changelog of all features (new things), improvements (things that work better), bug fixes, and new known bugs
to the description. Also, link all relevant issues to the PR with `#issueNum`. The PR should include changes 
to existing modules or a git submodule to a new module. 

During review, the development team will:
1. Ensure any CI tests pass
2. At least one member will review all code and add comments or request changes
3. Once at least one team member approves the PR, we will merge it into the `staging` branch, where it will stay until
the next release.


## Styleguides

Coming Soon:tm:

### Git Commit Messages

- Use the present tense ("Add feature" not "Added feature")
- Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
- Limit the first line to 72 characters or less
- Reference issues and pull requests liberally after the first line
- When only changing documentation, include `[ci skip]` in the commit title