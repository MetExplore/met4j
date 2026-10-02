# Contributing to Met4J

Contributions are welcome! The reference repository is on the INRAE forge:
[forge.inrae.fr/metexplore/met4j](https://forge.inrae.fr/metexplore/met4j).
The GitHub repository ([MetExplore/met4j](https://github.com/MetExplore/met4j)) is a read-only mirror.

For major changes, please open an issue first to discuss what you would like to change.

## Development setup

Requirements: **JDK 17+** and **Maven 3.x**.

```bash
git clone https://forge.inrae.fr/metexplore/met4j.git
cd met4j
mvn clean install            # build and install all modules in your local repository
mvn clean install -DskipTests  # same, without running any test
```

The toolbox jar is generated in `met4j-toolbox/target/met4j-toolbox-0-SNAPSHOT.jar`.

The version is not written in the poms: it is given by the `revision` property (`0-SNAPSHOT` by default)
and set by the CI from the git tag. **Never edit the version in the poms.**
To build a given version locally: `mvn clean install -Drevision=2.5.0`.

## Running tests

| Tests             | Location                                            | Plugin   | Command                              |
|-------------------|-----------------------------------------------------|----------|--------------------------------------|
| Unit tests        | `*/src/test/java/**/*Test.java`, `Test*.java`       | surefire | `mvn clean test`                     |
| Integration tests | `met4j-toolbox/src/test/java/integration/**/*IT.java` | failsafe | `mvn clean verify -DskipUTs=true`    |
| All tests         |                                                     |          | `mvn clean verify`                   |

- Integration tests run the toolbox apps on real files, they need the `package` phase (included in `verify`).
- `-DskipUTs=true` skips unit tests only, `-DskipTests` skips both.
- Run a single test: `mvn test -pl met4j-core -Dtest=BioNetworkTest`
  (or `mvn verify -pl met4j-toolbox -am -DskipUTs=true -Dit.test=FbcToNotesIT` for an integration test).
- The aggregated coverage report is generated in `coverage/target/site/jacoco-aggregate/index.html`.

## Workflow

The project follows [git-flow](https://nvie.com/posts/a-successful-git-branching-model/):

| Branch        | Role                                                                 |
|---------------|----------------------------------------------------------------------|
| `master`      | Released code only. Each merge is tagged with a version `X.Y.Z`.     |
| `develop`     | Integration branch for the next version.                             |
| `feature/*`   | New features, created from and merged into `develop`.                |
| `release/*`   | Preparation of a new version, created from `develop`.                |
| `hotfix/*`    | Urgent fixes, created from `master`.                                 |

Before opening a merge request:

- [ ] the branch is up to date with its target branch (`develop`, or `master` for hotfixes);
- [ ] new code is covered by tests, and `mvn clean verify` passes;
- [ ] public methods have a Javadoc;
- [ ] a line is added to [CHANGELOG.md](CHANGELOG.md) (see below);
- [ ] for a new toolbox app, the module README is updated.

### Changelog

Each user-visible change gets a list item in [CHANGELOG.md](CHANGELOG.md), prefixed by the module:

```markdown
## 2.5.0

- [met4j-io] Fix SBML import of ...
- [met4j-toolbox] New app ...
```

The section title must be exactly `## X.Y.Z`: it is used by the CI to build the release notes.

## Continuous integration

The pipeline is defined in [.gitlab-ci.yml](.gitlab-ci.yml). What runs depends on what you push:

| Push                                  | test | integration_test | package | verifyRelease | Deploy & release |
|---------------------------------------|:----:|:----------------:|:-------:|:-------------:|:----------------:|
| Any branch (`feature/*`, `develop`, `master`...) | ✓ | ✓ | ✓ |   |   |
| `release/*` or `hotfix/*` branch (or its merge request) | ✓    | ✓                | ✓       | ✓             |                  |
| Other branches                   | ✓    | ✓                | ✓       |               |                  |
| Tag `X.Y.Z` (e.g. `2.5.0`)            | ✓    | ✓                | ✓       |               | ✓                |
| Any other tag (e.g. `changeVersion2_4_1`) |  |              |         |               |                  |

- Any other tag triggers no pipeline at all.
- When a merge request is open, pushing on its branch only runs the merge request pipeline (no duplicate).
- `develop` and `master` are not treated differently: **nothing is published until a version tag is pushed**.

### Jobs

- **test**: unit tests and coverage (shown in the merge request and in the badge).
- **integration_test**: integration tests of the toolbox apps.
- **package**: builds the toolbox jar (kept 1 day as an artifact).
- **verifyRelease**: dry run of deployCentral (javadoc, sources, no signature, no upload), to catch errors before tagging.
  It does not run on tags, where deployCentral performs the real build.

On a `X.Y.Z` tag, the version is set to the tag (`-Drevision=X.Y.Z`) and, if the tests pass:

1. **deployCentral**: signs and publishes all modules on [Maven Central](https://central.sonatype.com/namespace/fr.inrae.toulouse.metexplore);
2. **deployJar**: uploads the toolbox jar to the GitLab package registry;
3. **buildDocker\***, **buildSingularity**: build and push the `X.Y.Z` and `latest` images
   to the GitLab container registry and to Docker Hub;
4. **release**: creates the GitLab release, with the `## X.Y.Z` section of the changelog as release notes;
5. **releaseGithub**: creates the same release on the GitHub mirror, with the toolbox jar attached.

The bioconda recipe and the Galaxy instance are updated outside of this pipeline.

### Releasing a new version

```bash
git flow release start 2.5.0      # or: git flow hotfix start 2.4.4
# update CHANGELOG.md with a "## 2.5.0" section, commit, push: verifyRelease must pass
git flow release finish 2.5.0     # merges into master and develop, creates the tag 2.5.0
git push origin master develop 2.5.0
```

Without git-flow tools, merge the branch into `master` and `develop`, then tag `master` with `X.Y.Z`
and push the tag. The tag must contain only the version number (no `v` prefix).

### CI variables

Publishing jobs need these masked variables, set in the project settings (*Settings > CI/CD > Variables*):

| Variable                                 | Used by                     |
|------------------------------------------|-----------------------------|
| `CENTRAL_USERNAME`, `CENTRAL_PASSWORD`   | deployCentral (Sonatype token) |
| `GPG_PRIVATE_KEY`, `GPG_PASSPHRASE`      | deployCentral (signature)   |
| `DOCKERHUB_REGISTRY`, `DOCKERHUB_IMAGE`, `DOCKERHUB_USER`, `DOCKERHUB_PASSWORD` | buildDockerProdDockerhub |
| `GITHUB_TOKEN` (fine-grained PAT, *Contents: write*) | releaseGithub   |

## Reporting issues

Bugs and suggestions can be reported in the [GitHub issues](https://github.com/MetExplore/met4j/issues)
or by email at <contact-metexplore@inrae.fr>.

By contributing, you agree that your contributions will be licensed under the
[CeCILL-2.1](https://cecill.info/licences/Licence_CeCILL_V2.1-en.html) license.
