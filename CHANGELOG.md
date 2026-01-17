## v1.8.4 (2026-01-17)

### Fix

- **publish**: using git credential helper (avoids hygiene check false positives)

## v1.8.3 (2026-01-17)

### Fix

- **publish**: tag push did not trigger the publish workflow; trying with the PAT_FORGE_DEPLOY token

## v1.8.2 (2026-01-17)

### Fix

- **ci**: changing so that only tag pushes trigger a package deploy/publish

## v1.8.1 (2026-01-17)

### Refactor

- **spotbugs**: configuration issue after package rename

## v1.8.0 (2026-01-17)

### Feat

- **rename**: migration to metrics-api and package dirs now all include kit prefix

## v1.7.1 (2026-01-16)

### Fix

- **deps**: update aws sdk v2 monorepo to v2.41.9 (#7)

## v1.7.0 (2026-01-16)

### Feat

- **jwt**: final v minor jwt-related util

## v1.6.0 (2026-01-16)

### Feat

- **jwt**: refactoring so as to expose access to specifically service/user jwt token claims

## v1.5.1 (2026-01-16)

### Refactor

- **package**: rename to add .kit and avoid clash

## v1.5.0 (2026-01-15)

### Feat

- **throttle**: throttle impl migration

### Fix

- **mvn**: missed dependencies declaration (version inherited from quarkus bom)
- **pom**: version mismatch on pull rebase again

## v1.4.4 (2026-01-13)

### Fix

- **rebrand**: minor tweaks

## v1.4.3 (2026-01-13)

### Fix

- **rebrand**: missed reference to GitHub package repo for publishing

## v1.4.2 (2026-01-13)

### Fix

- **rebrand**: rename to forge-kit for lightweight contracts and avoid heavyweight platform connotations
- **deps**: update aws sdk v2 monorepo to v2.41.6 (#6)

## v1.4.1 (2026-01-13)

### Fix

- **pom**: busted version lagging again

### Refactor

- **metrics**: spliting dtos out to forge-api module so that implementations do not need to inherit the entire metrics framework, just the API where necessary

## v1.4.0 (2026-01-13)

### Feat

- **metrics**: migrating metrics out to public repo

### Fix

- **metrics**: pom version lagging

## v1.3.0 (2026-01-13)

### Feat

- **dotenv**: envrc required to run static analysis

### Fix

- **rename**: references to -core project remaining

## v1.2.28 (2026-01-13)

### Fix

- **pom**: removing transitive dependency duplication as not best-practive to redeclare

## v1.2.27 (2026-01-13)

### Fix

- **pom**: standardising on best-practice pom impl

## v1.2.26 (2026-01-12)

### Fix

- **publish**: changing the artifactId names to be consistent and without the forge- prefix

## v1.2.25 (2026-01-12)

### Fix

- **publish**: seems I need to publish parent poms after all as the usable jar is going to reference

## v1.2.24 (2026-01-12)

### Fix

- **publish**: reverting to my preferred artifactId after that wild goose chase

## v1.2.23 (2026-01-12)

### Fix

- **publish**: skip configuration is working so no need for the filter

## v1.2.22 (2026-01-12)

### Fix

- **publish**: okay child was inheriting parent skip

## v1.2.21 (2026-01-12)

### Fix

- **publish**: trying a skip publish on the parents

## v1.2.20 (2026-01-12)

### Fix

- **publish**: requires MODULE prefix

## v1.2.19 (2026-01-12)

### Fix

- **publish**: trying to filter on just the health package

## v1.2.18 (2026-01-12)

### Fix

- **publish**: hmmm, i think moving distributionManagement broke it; trying with just the filter

## v1.2.17 (2026-01-12)

### Fix

- **publish**: attempting to narrow to just the health module

## v1.2.16 (2026-01-12)

### Fix

- **publish**: ordering issue with the settings.xml

## v1.2.15 (2026-01-12)

### Fix

- **publish**: oi oi oi. might have finally found it; looks like expected .m2/settings-security.xml is required

## v1.2.14 (2026-01-12)

### Fix

- **publish**: adding some more debugging

## v1.2.13 (2026-01-12)

### Fix

- **publish**: trying to write auth direct to .m2/settings.xml

## v1.2.12 (2026-01-12)

### Fix

- **publish**: missed the env vars

## v1.2.11 (2026-01-12)

### Fix

- **publish**: feels like last ditch but username not being sent correctly

## v1.2.10 (2026-01-12)

### Fix

- **publish**: username

## v1.2.9 (2026-01-12)

### Fix

- **publish**: using a classic PAT now; also don't think we ever quite got the groupId right

## v1.2.8 (2026-01-12)

### Fix

- **publish**: pom issue
- **publish**: find replace error
- **publish**: mvn repo namespace needed to change also
- **publish**: not a token issue after all - seems to be a requirement that namespace starts com.github

## v1.2.7 (2026-01-12)

### Fix

- **publish**: trying a PAT instead

## v1.2.6 (2026-01-12)

### Fix

- **publish**: Github actor/token configuration
- **publish**: github actor/token not being set properly

## v1.2.5 (2026-01-12)

### Fix

- **build**: namespace change for mvn repo artefacts
- **release**: seems groupId must reflect the namespace to publish to GitHub packages

## v1.2.4 (2026-01-12)

### Fix

- **publish**: cursor leading me astray again

## v1.2.3 (2026-01-12)

### Fix

- **publish**: invalid tags command on workflow

## v1.2.2 (2026-01-12)

### Fix

- **publish**: reverting to manual mvn deploy

## v1.2.1 (2026-01-12)

### Fix

- **deps**: update dependency software.amazon.awssdk:bom to v2.41.5 (#1)
- **release**: reverting due to lack of clarity on GitHub branch protection rules

## v1.2.0 (2026-01-12)

### Feat

- **publish**: attempting to publish the public health dependency

### Fix

- **deploy**: wanting to auto-merge a version bump without relaxing rulesets too much
- **publish**: going with a marketplace action as far cleaner
- **publish**: configures Maven for GitHub Packages when credentials are provided
- **publish**: secrets not recognised by an actions include

## v1.1.1 (2026-01-11)

### Fix

- **pages**: jekyll config to ignore cursor

## v1.1.0 (2026-01-11)

### Feat

- **health**: adding health checks and a whole load of boilerplate to get CI up and running

### Fix

- **cz**: git history not deep enough yet to support the hygience check as configured
